<!-- Vault.svelte -->
<script lang="ts">
    import { onMount, onDestroy } from "svelte";
    import { get } from "svelte/store";
    import Toggle from "../elements/FlatToggle.svelte";

    import { AppPage, currentPage } from "../enums/AppPage.ts";
    import { Modals, currentModal } from "../enums/Modals.ts";
    import { FileTasks, currentFileTask } from "../enums/FileTasks.ts";

    import { height, width, lightBGColor } from "../stores/globalVariables.ts";
    import { encryptProgress as fileCt } from "../stores/encryptProgress";

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

    //  let _totalFileCt: number;
    // _totalFileCt = 0;
    let _fileCount: number;
    fileCt.subscribe((value) => {
        _fileCount = value;
    });
    let _fileTaskPercent: number;
    let _largeFilePercent: number;

    onMount(() => {
        buildFileTree();
        EventsOn("fileProcessed", (fileCtEvt: number) => {
            fileCt.set(fileCtEvt);
            _fileTaskPercent = Math.round(
                (_fileCount / get(totalFileCt)) * 100,
            );
            if (fileCtEvt === 0) currentFileTask.set(FileTasks.None);
        });
        EventsOn("largeFilePercent", (largeFilePercent: number) => {
            _largeFilePercent = largeFilePercent;
            LogInfo("large file percent " + _largeFilePercent);
            if (_largeFilePercent === 0) EventsOff("largeFilePercent");
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
        var _heldDownBtns = get(heldDownBtns);
        if (Object.keys(_heldDownBtns).length > 0)
            setIsInFileTask(true).then(() => {
                currentFileTask.set(FileTasks.Encrypt);
                totalFileCt.set(Object.keys(_heldDownBtns).length);
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
        var _heldDownBtns = get(heldDownBtns);
        if (Object.keys(_heldDownBtns).length > 0)
            setIsInFileTask(true).then(() => {
                currentFileTask.set(FileTasks.Decrypt);
                totalFileCt.set(Object.keys(_heldDownBtns).length);
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
        style="background-color:{lightBGColor}"
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
        style="background-color:{lightBGColor}"
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
