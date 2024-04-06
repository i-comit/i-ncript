<!-- Vault.svelte -->
<script lang="ts">
    import { onMount, onDestroy } from "svelte";
    import { get } from "svelte/store";
    import Toggle from "../elements/FlatToggle.svelte";

    import { AppPage, currentPage } from "../enums/AppPage.ts";
    import { Modals, currentModal } from "../enums/Modals.ts";
    import { FileTasks, currentFileTask } from "../enums/FileTasks.ts";

    import {
        height,
        width,
        lightBGColor,
        darkBGColor,
    } from "../stores/constantVariables.ts";

    import {
        buildFileTree,
        fileTree,
        setIsInFileTask,
        clearHeldBtnsFromContainer,
    } from "../tools/fileTree.ts";

    import {
        EncryptFilesInDir,
        DecryptFilesInDir,
        InterruptEncryption,
        EncryptFilesInArr,
        DecryptFilesInArr,
    } from "../../wailsjs/go/main/App";

    import {
        switchPages,
        switchModals,
        pointerDown,
        heldDownBtns,
        prependAbsPathToRelPaths,
        totalFileCt,
        checkFileTypesinHeldDownBtns,
        toggleDarkLightMode,
    } from "../tools/utils.ts";

    import NeuButton from "../elements/NeuButton.svelte";

    import Frame from "../elements/Frame.svelte";
    import Info from "./Info.svelte";
    import Settings from "./Settings.svelte";
    import TreeView from "./FileTree.svelte";
    import Logger from "./Logger.svelte";

    import {
        EventsOff,
        EventsOn,
        LogInfo,
    } from "../../wailsjs/runtime/runtime";
    import {
        GetDirectoryPath,
        GetFilesByType,
    } from "../../wailsjs/go/main/Getters";
    import DirSize from "../elements/DirectorySizeBar.svelte";
    import RadialProgress from "../elements/RadialProgress.svelte";
    import NeuSearch from "../elements/NeuSearch.svelte";
    import PanelDivider from "../elements/PanelDivider.svelte";
    import WaveProgress from "../elements/WaveProgress.svelte";
    import NeuButtonFake from "../elements/NeuButtonFake.svelte";
    import TaskDisplay from "../elements/TaskDisplay.svelte";
    import FileTools from "../elements/FileTools.svelte";
    import { darklightMode } from "../stores/dynamicVariables.ts";

    //  let _totalFileCt: number;
    // _totalFileCt = 0;
    let _fileCount: number;

    let _fileTaskPercent: number;
    let _largeFilePercent: number;

    let _currentFileTask: FileTasks;
    currentFileTask.subscribe((value) => {
        _currentFileTask = value;
    });

    let _modal: Modals;
    currentModal.subscribe((value) => {
        _modal = value;
    });

    darklightMode.subscribe((value) => {
        toggleDarkLightMode(value);
    });

    onMount(() => {
        buildFileTree();
        EventsOn("fileProcessed", (fileCtEvt: number) => {
            _fileCount = fileCtEvt;
            _fileTaskPercent = Math.round(
                (_fileCount / get(totalFileCt)) * 100,
            );
            if (fileCtEvt === 0) currentFileTask.set(FileTasks.None);
        });
        toggleDarkLightMode(get(darklightMode));
        EventsOn("largeFilePercent", (largeFilePercent: number) => {
            _largeFilePercent = largeFilePercent;
            LogInfo("large file percent " + _largeFilePercent);
            if (largeFilePercent === 0) EventsOff("largeFilePercent");
        });
    });

    function encrypt() {
        if (
            checkFileTypesinHeldDownBtns(true) > 0 ||
            Object.keys(get(heldDownBtns)).length > 0
        )
            setIsInFileTask(true).then(() => {
                currentFileTask.set(FileTasks.Encrypt);
                totalFileCt.set(checkFileTypesinHeldDownBtns(true));
                prependAbsPathToRelPaths(0).then((prependedRelPaths) => {
                    EncryptFilesInArr(prependedRelPaths);
                });
            });
        else
            GetFilesByType(0, false).then((filePaths) => {
                if (filePaths.length > 0) {
                    setIsInFileTask(true).then(() => {
                        currentFileTask.set(FileTasks.Encrypt);
                        totalFileCt.set(filePaths.length);
                        EncryptFilesInDir(0);
                    });
                } else setIsInFileTask(false);
            });
    }

    function decrypt() {
        if (
            checkFileTypesinHeldDownBtns(false) > 0 ||
            Object.keys(get(heldDownBtns)).length > 0
        )
            setIsInFileTask(true).then(() => {
                currentFileTask.set(FileTasks.Decrypt);
                totalFileCt.set(checkFileTypesinHeldDownBtns(false));
                prependAbsPathToRelPaths(0).then((prependedRelPaths) => {
                    DecryptFilesInArr(prependedRelPaths);
                });
            });
        else
            GetFilesByType(0, true).then((filePaths) => {
                if (filePaths.length > 0) {
                    setIsInFileTask(true).then(() => {
                        currentFileTask.set(FileTasks.Decrypt);
                        totalFileCt.set(filePaths.length);
                        DecryptFilesInDir(0);
                    });
                } else setIsInFileTask(false);
            });
    }

    let isFilesDraggedOver: boolean;

    function checkMouseEnter(): boolean {
        if ($pointerDown) {
            isFilesDraggedOver = true;
            return true;
        }
        return false;
    }

    function checkMouseLeave() {}
</script>

<div class="flex h-screen !rounded-lg">
    <Frame />
    <div
        id="left-panel"
        role="none"
        on:click={clearHeldBtnsFromContainer}
        on:mouseenter={checkMouseEnter}
        on:mouseleave={checkMouseLeave}
    >
        <DirSize />
        <div class="buttons">
            {#if _currentFileTask === FileTasks.None}
                <div class="row space-x-2">
                    <NeuButton
                        on:mousedown={() => encrypt()}
                        _style="font-size: 14px;">ENCRYPT</NeuButton
                    >
                    <NeuButton
                        on:mousedown={() => decrypt()}
                        _style="font-size: 14px;">DECRYPT</NeuButton
                    >
                </div>
            {:else}
                <div class="h-7">
                    <TaskDisplay />
                </div>
            {/if}

            <div class="relative h-14">
                {#if _currentFileTask === FileTasks.None}
                    <div style="padding-top: 0.325rem">
                        <FileTools />
                    </div>
                    <p
                        class="absolute bottom-0 right-0 leading-none text-sm select-none"
                    >
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
        on:mouseleave={onmouseleave}
        on:click={clearHeldBtnsFromContainer}
    >
        <NeuSearch />
        {#if _largeFilePercent > 0}
            <RadialProgress
                _style="right: 3.6rem"
                dataProgress={_largeFilePercent}
            />
        {/if}
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
    :root {
        --bg-color: #757575;
    }
    p {
        color: black;
    }

    #left-panel,
    #right-panel {
        background-color: var(--bg-color);
    }

    .buttons .row {
        display: flex;
        justify-content: space-between;
        margin-bottom: 4px;
    }
</style>
