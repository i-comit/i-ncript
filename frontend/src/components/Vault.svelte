<!-- Vault.svelte -->
<script lang="ts">
    import { onMount, onDestroy } from "svelte";
    import { get } from "svelte/store";
    import { Progressbar, Button } from "flowbite-svelte";
    import Toggle from "../elements/FlatToggle.svelte";

    import { sineOut } from "svelte/easing";

    import {
        InfoCircleOutline,
        AdjustmentsVerticalOutline,
        CirclePauseSolid,
        BarsFromLeftOutline,
        PauseSolid,
    } from "flowbite-svelte-icons";

    import { AppPage, currentPage } from "../enums/AppPage.ts";
    import { Modals, currentModal } from "../enums/Modals.ts";

    import { height, width, lightBGColor } from "../stores/globalVariables.ts";
    import { encryptProgress as fileCt } from "../stores/encryptProgress";

    import {
        buildFileTree,
        fileTree,
        setIsInFileTask,
        clearHeldBtns,
    } from "../tools/fileTree.ts";

    import {
        EncryptFilesInDir,
        DecryptFilesInDir,
        InterruptEncryption,
    } from "../../wailsjs/go/main/App";

    import { switchPages, switchModals } from "../tools/utils.ts";

    import NeuButton from "../elements/NeuButton.svelte";

    import Frame from "../elements/Frame.svelte";
    import Info from "./Info.svelte";
    import Settings from "./Settings.svelte";
    import TreeView from "./FileTree.svelte";
    import Logger from "./Logger.svelte";

    import { EventsOn, LogInfo } from "../../wailsjs/runtime/runtime";
    import { GetFilesByType } from "../../wailsjs/go/main/Getters";
    import DirSize from "../elements/DirectorySizeBar.svelte";
    import RadialProgress from "../elements/RadialProgress.svelte";
    import { FileTasks, currentFileTask } from "../enums/FileTasks.ts";
    import NeuSearch from "../elements/NeuSearch.svelte";
    import PanelDivider from "../elements/PanelDivider.svelte";
    import WaveProgress from "../elements/WaveProgress.svelte";
    import NeuButtonFake from "../elements/NeuButtonFake.svelte";
    import TaskDisplay from "../elements/TaskDisplay.svelte";

    let _totalFileCt: number;
    _totalFileCt = 0;
    let _fileCount: number;
    fileCt.subscribe((value) => {
        _fileCount = value;
    });
    let _fileTaskPercent: number;

    onMount(() => {
        buildFileTree();
        EventsOn("fileProcessed", (fileCtEvt) => {
            fileCt.set(fileCtEvt);
            _fileTaskPercent = Math.round((_fileCount / _totalFileCt) * 100);
            if (fileCtEvt === 0) currentFileTask.set(FileTasks.None);
        });
    });

    let _currentFileTask: FileTasks;
    currentFileTask.subscribe((value) => {
        _currentFileTask = value;
    });

    let _modal: Modals;
    currentModal.subscribe((value) => {
        _modal = value;
    });

    function encrypt() {
        setIsInFileTask(true).then(() => {
            GetFilesByType(0, false).then((filePaths) => {
                LogInfo("Began encrypting");
                if (filePaths.length > 0) {
                    currentFileTask.set(FileTasks.Encrypt);
                    _totalFileCt = filePaths.length;
                    EncryptFilesInDir(0);
                }
            });
        });
    }

    function decrypt() {
        setIsInFileTask(true).then(() => {
            GetFilesByType(0, true).then((filePaths) => {
                if (filePaths.length > 0) {
                    currentFileTask.set(FileTasks.Decrypt);
                    _totalFileCt = filePaths.length;
                    DecryptFilesInDir(0);
                }
            });
        });
    }
</script>

<div class="flex h-screen !rounded-lg">
    <Frame />
    <div
        id="left-panel"
        style="background-color:{lightBGColor}"
        role="none"
        on:mousedown={clearHeldBtns}
    >
        <DirSize />
        <div class="buttons">
            {#if _currentFileTask === FileTasks.None}
                <div class="row space-x-2">
                    <NeuButton
                        on:click={() => encrypt()}
                        _style="font-size: 14px;">ENCRYPT</NeuButton
                    >
                    <NeuButton
                        on:click={() => decrypt()}
                        _style="font-size: 14px;">DECRYPT</NeuButton
                    >
                </div>
            {:else}
                <TaskDisplay />
            {/if}

            <div class="relative h-14">
                {#if _currentFileTask === FileTasks.None}
                    <p class="absolute bottom-0 right-0 leading-none text-sm">
                        HOT FILER
                    </p>
                {:else}
                    <div class="h-0.5" />
                    <WaveProgress dataProgress={_fileTaskPercent}
                    ></WaveProgress>
                {/if}
            </div>

            <div class="h-1" />
            <div class="row space-x-0">
                <NeuButton
                    on:click={() => switchPages(AppPage.Mbox)}
                    _class="!w-20">M-BOX</NeuButton
                >
                <Toggle />
            </div>
        </div>
    </div>

    <PanelDivider />

    <div
        id="right-panel"
        role="none"
        style="background-color:{lightBGColor}"
        on:mouseleave={onmouseleave}
        on:click={clearHeldBtns}
    >
        <NeuSearch />
        <RadialProgress _style="right: 3.6rem" dataProgress={30} />
        {#if _modal === Modals.None}
            <TreeView _fileTree={$fileTree} />
        {:else if _modal === Modals.Settings}
            <Settings />
        {:else if _modal === Modals.Info}
            <Info />
        {:else if _modal === Modals.Logger}
            <Logger />
        {/if}
    </div>
</div>

<style>
    p {
        color: black;
    }

    #left-panel {
        background-color: #e8e8e8;
    }

    .buttons .row {
        display: flex;
        justify-content: space-between;
        margin-bottom: 4px;
    }
</style>
