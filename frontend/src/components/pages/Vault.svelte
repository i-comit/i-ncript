<!-- Vault.svelte -->
<script lang="ts">
    import { onMount, onDestroy } from "svelte";
    import { get } from "svelte/store";
    import Toggle from "../widgets/FlatToggle.svelte";

    import {
        EventsOff,
        EventsOn,
        LogInfo,
    } from "../../../wailsjs/runtime/runtime";

    import {
        EncryptFilesInDir,
        DecryptFilesInDir,
        EncryptFilesInArr,
        DecryptFilesInArr,
    } from "../../../wailsjs/go/main/App";
    import {
        GetDirectoryPath,
        GetFilesByType,
    } from "../../../wailsjs/go/main/Getters";

    import { AppPage, currentPage } from "../../enums/AppPage.ts";
    import { Modals, currentModal } from "../../enums/Modals.ts";
    import { FileTasks, currentFileTask } from "../../enums/FileTasks.ts";

    import {
        darkLightMode,
        fileCount,
        fileTaskPercent,
        largeFileName,
        largeFilePercent,
        totalFileCt,
    } from "../../stores/dynamicVariables.ts";

    import {
        buildFileTree,
        fileTree,
        setIsInFileTask,
        clearHeldBtnsFromContainer,
    } from "../../tools/fileTree.ts";

    import {
        switchPages,
        pointerDown,
        heldDownBtns,
        prependAbsPathToRelPaths,
        checkFileTypesinHeldDownBtns,
        retrieveDuplicateFiles,
    } from "../../tools/utils.ts";

    import { darkLightBGOnId } from "../../tools/themes";

    import TaskDisplay from "../widgets/TaskDisplay.svelte";
    import NeuButton from "../widgets/NeuButton.svelte";
    import WaveProgress from "../widgets/WaveProgress.svelte";
    import RadialProgress from "../widgets/RadialProgress.svelte";

    import Info from "../modals/Info.svelte";
    import Settings from "../modals/Settings.svelte";
    import TreeView from "../modals/FileTree.svelte";
    import Logger from "../modals/Logger.svelte";

    import Frame from "../widgets/Frame.svelte";
    import DirSize from "../widgets/DirectorySize.svelte";
    import PanelDivider from "../widgets/PanelDivider.svelte";
    import FileTools from "../widgets/FileTools.svelte";
    import { startDisplay } from "../../tools/logger.ts";
    import DuplicateFiles from "../modals/DuplicateFiles.svelte";

    let _currentFileTask: FileTasks;
    currentFileTask.subscribe((value) => {
        _currentFileTask = value;
    });

    let _modal: Modals;
    currentModal.subscribe((value) => {
        _modal = value;
    });

    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightBGOnId(value, "right-panel");
        darkLightBGOnId(value, "left-panel");
    });

    onMount(() => {
        buildFileTree();
        EventsOn("fileProcessed", (fileCtEvt: number) => {
            fileCount.set(fileCtEvt);
            fileTaskPercent.set(
                Math.round(($fileCount / get(totalFileCt)) * 100),
            );
            if (fileCtEvt === 0) currentFileTask.set(FileTasks.None);
        });
        retrieveDuplicateFiles();
        darkLightBGOnId($darkLightMode, "right-panel");
        darkLightBGOnId($darkLightMode, "left-panel");
    });

    onDestroy(() => {
        unsub_darkLightMode();
        EventsOff("fileProcessed");
    });

    function encrypt() {
        if (
            checkFileTypesinHeldDownBtns(true) > 0 &&
            Object.keys(get(heldDownBtns)).length > 0
        )
            setIsInFileTask(true).then(() => {
                currentFileTask.set(FileTasks.Encrypt);
                currentModal.set(Modals.Logger);
                totalFileCt.set(checkFileTypesinHeldDownBtns(true));
                prependAbsPathToRelPaths(0).then((prependedRelPaths) => {
                    EncryptFilesInArr(prependedRelPaths);
                });
            });
        else
            GetFilesByType(0, false).then((filePaths) => {
                if (!filePaths) {
                    startDisplay("no files to encrypt..");
                    return;
                }
                if (filePaths.length > 0) {
                    setIsInFileTask(true).then(() => {
                        currentFileTask.set(FileTasks.Encrypt);
                        currentModal.set(Modals.Logger);
                        totalFileCt.set(filePaths.length);
                        EncryptFilesInDir(0);
                    });
                } else {
                    startDisplay("no files to encrypt..");
                    setIsInFileTask(false);
                }
            });
    }

    function decrypt() {
        if (
            checkFileTypesinHeldDownBtns(false) > 0 &&
            Object.keys(get(heldDownBtns)).length > 0
        )
            setIsInFileTask(true).then(() => {
                currentFileTask.set(FileTasks.Decrypt);
                currentModal.set(Modals.Logger);
                totalFileCt.set(checkFileTypesinHeldDownBtns(false));
                prependAbsPathToRelPaths(0).then((prependedRelPaths) => {
                    DecryptFilesInArr(prependedRelPaths);
                });
            });
        else
            GetFilesByType(0, true).then((filePaths) => {
                if (!filePaths) {
                    startDisplay("no files to decrypt..");
                    return;
                }
                if (filePaths.length > 0) {
                    setIsInFileTask(true).then(() => {
                        currentFileTask.set(FileTasks.Decrypt);
                        currentModal.set(Modals.Logger);
                        totalFileCt.set(filePaths.length);
                        DecryptFilesInDir(0);
                    });
                } else {
                    startDisplay("no files to decrypt..");
                    setIsInFileTask(false);
                }
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
</script>

<div class="flex h-screen !rounded-lg">
    <DuplicateFiles />
    <Frame />
    <div
        id="left-panel"
        role="none"
        on:click={clearHeldBtnsFromContainer}
        on:mouseenter={checkMouseEnter}
    >
        <DirSize />
        {#if _currentFileTask === FileTasks.None}
            <div class="row space-x-2">
                <NeuButton on:click={() => encrypt()} _style="font-size: 14px;"
                    >ENCRYPT</NeuButton
                >
                <NeuButton on:click={() => decrypt()} _style="font-size: 14px;"
                    >DECRYPT</NeuButton
                >
            </div>
        {:else}
            <TaskDisplay />
        {/if}

        <div class="relative h-14">
            {#if _currentFileTask === FileTasks.None}
                <div style="padding-top: 0.325rem">
                    <FileTools />
                </div>
                <!-- <p
                    class="absolute bottom-0 right-0 leading-none text-sm select-none"
                >
                    HOT FILER
                </p> -->
            {:else}
                <WaveProgress dataProgress={$fileTaskPercent}></WaveProgress>
            {/if}
        </div>

        <div class={_currentFileTask === FileTasks.None ? "h-1" : "h-0.5"} />

        <div class="row space-x-0">
            <NeuButton on:click={() => switchPages(AppPage.Mbox)} _class="!w-20"
                >M-BOX</NeuButton
            >
            <Toggle />
        </div>
    </div>

    <PanelDivider />
    <div
        id="right-panel"
        role="none"
        on:mouseleave={onmouseleave}
        on:mouseup={clearHeldBtnsFromContainer}
    >
        <!-- <NeuSearch /> -->
        <RadialProgress
            _style="right: 3.6rem"
            dataProgress={$largeFilePercent}
            overlayText={$largeFileName}
        />
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
    .row {
        display: flex;
        justify-content: space-between;
        margin-bottom: 4px;
    }
</style>
