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

    import {
        EventsOn,
        LogInfo,
    } from "../../wailsjs/runtime/runtime";
    import {
        GetDirectoryPath,
        GetFilesByType,
    } from "../../wailsjs/go/main/Getters";
    import DirSize from "../elements/DirectorySizeBar.svelte";
    import RadialProgress from "../elements/RadialProgress.svelte";
    import { FileTasks, currentFileTask } from "../enums/FileTasks.ts";
    import NeuSearch from "../elements/NeuSearch.svelte";
    import PanelDivider from "../elements/PanelDivider.svelte";

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
        {#if _currentFileTask !== FileTasks.None}
            <RadialProgress className="left-5 " dataProgress={_fileTaskPercent}>
                <div class="absolute inset-5 flex flex-col align-center top-7">
                    <p class="h-4 text-center leading-none text-sm select-none">
                        {_currentFileTask}
                    </p>
                    <button class="self-center" on:click={InterruptEncryption}>
                        <PauseSolid class="w-12 h-12" color="dark" />
                    </button>
                    <p
                        class="h-4 text-center leading-none select-none truncate"
                    >
                        {_fileCount} / {_totalFileCt}
                    </p>
                </div></RadialProgress
            >
        {/if}
        <!-- <div id="page-info" class="static">
            <p>VAULT</p>
            <Button
                pill={true}
                outline={true}
                class="z-20 h-0"
                color="dark"
                style="--wails-draggable:drag"
                on:click={() => switchModals(Modals.Info)}
                ><InfoCircleOutline class="w-5 h-5" color="white" /></Button
            >
            <p>32GB</p>
        </div> -->
        <div class="h-6"></div>

        <DirSize />
        <div class=" !flex !justify-start row center bg-white mb-1">
            <p>encrypted 999999 files</p>
        </div>
        <div class="buttons">
            <div class="row space-x-2">
                <NeuButton on:click={() => encrypt()}>ENCRYPT</NeuButton>
                <NeuButton on:click={() => decrypt()}>DECRYPT</NeuButton>
            </div>
            <div class="h-1"></div>
            <div class="flex justify-between items-center">
                <div class="flex space-x-1">
                    <button
                        class="!p-1 !px-0 !mb-2"
                        color="dark"
                        on:click={() => switchModals(Modals.Logger)}
                        ><BarsFromLeftOutline
                            class="w-5 h-5"
                            color="dark"
                        /></button
                    >
                </div>
                <div>
                    <Toggle />
                    <p class="text-sm">HOT FILER</p>
                </div>
            </div>

            <div class="h-1"></div>
            <div class="row space-x-2">
                <NeuButton on:click={() => switchPages(AppPage.OBox)}
                    >O-BOX</NeuButton
                >
                <Button
                    pill={true}
                    outline={true}
                    class="!p-1 h-7"
                    color="dark"
                    on:click={() => switchModals(Modals.Settings)}
                    ><AdjustmentsVerticalOutline
                        class="w-5 h-5"
                        color="white"
                    /></Button
                >
                <NeuButton on:click={() => switchPages(AppPage.NBox)}
                    >N-BOX</NeuButton
                >
            </div>
        </div>
    </div>
    <PanelDivider/>

    <div
        id="right-panel"
        role="none"
        class="bg-gray-500"
        style="background-color:{lightBGColor}"
        on:mouseleave={onmouseleave}
        on:click={clearHeldBtns}
    >
        <NeuSearch  />
        <RadialProgress className="right-10" dataProgress={30} />
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
        margin-bottom: 10px;
    }
</style>
