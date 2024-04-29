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

    import { EncryptFiles, DecryptFiles } from "../../../wailsjs/go/main/App";
    import { GetFilesByType } from "../../../wailsjs/go/main/Getters";

    import { AppPage, currentPage } from "../../enums/AppPage.ts";
    import { Modals, currentModal } from "../../enums/Modals.ts";
    import { FileTasks, currentFileTask } from "../../enums/FileTasks.ts";

    import {
        fileTaskPercent,
        hotFiler,
        largeFileName,
        largeFilePercent,
        pageLoading,
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

    import TaskDisplay from "../widgets/TaskDisplay.svelte";
    import NeuButton from "../widgets/NeuButton.svelte";
    import WaveProgress from "../widgets/WaveProgress.svelte";
    import RadialProgress from "../widgets/RadialProgress.svelte";

    import Info from "../modals/Info.svelte";
    import Settings from "../modals/Settings.svelte";
    import TreeView from "../modals/FileTree.svelte";
    import Logger from "../modals/Logger.svelte";

    import TitleBar from "../widgets/TitleBar.svelte";
    import DirSize from "../widgets/DirectorySize.svelte";
    import PanelDivider from "../widgets/PanelDivider.svelte";
    import FileTools from "../widgets/FileTools.svelte";
    import { startDisplay } from "../../tools/logger.ts";
    import DuplicateFiles from "../modals/DuplicateFiles.svelte";
    import OvalSpinner from "../widgets/OvalSpinner.svelte";
    import ModalButtons from "../widgets/ModalButtons.svelte";
    import Chronograph from "../widgets/Chronograph.svelte";

    let _currentFileTask: FileTasks;
    currentFileTask.subscribe((value) => {
        _currentFileTask = value;
    });

    let _hotFiler: boolean;
    hotFiler.subscribe((value) => {
        _hotFiler = value;
    });

    let _modal: Modals;
    currentModal.subscribe((value) => {
        _modal = value;
    });

    onMount(() => {
        pageLoading.set(true);
        buildFileTree();
        if (_currentFileTask === FileTasks.None) retrieveDuplicateFiles();
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
                    EncryptFiles(prependedRelPaths);
                });
            });
        else
            GetFilesByType(0, true).then((filePaths) => {
                if (!filePaths) {
                    startDisplay("no files to encrypt..");
                    return;
                }
                if (filePaths.length > 0) {
                    setIsInFileTask(true).then(() => {
                        currentFileTask.set(FileTasks.Encrypt);
                        currentModal.set(Modals.Logger);
                        totalFileCt.set(filePaths.length);
                        EncryptFiles(filePaths);
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
                    DecryptFiles(prependedRelPaths);
                });
            });
        else
            GetFilesByType(0, false).then((filePaths) => {
                if (!filePaths) {
                    startDisplay("no files to decrypt..");
                    return;
                }
                if (filePaths.length > 0) {
                    setIsInFileTask(true).then(() => {
                        currentFileTask.set(FileTasks.Decrypt);
                        currentModal.set(Modals.Logger);
                        totalFileCt.set(filePaths.length);
                        DecryptFiles(filePaths);
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

<TitleBar />
<OvalSpinner />
<div
    class="flex h-screen !rounded-lg {$pageLoading
        ? 'pointer-events-none opacity-40'
        : ''}"
>
    <DuplicateFiles />
    <div
        id="left-panel"
        role="none"
        class="bg-primary-400 dark:bg-primary-300"
        on:click={clearHeldBtnsFromContainer}
        on:pointerenter={checkMouseEnter}
        on:pointerdown={() => {
            currentModal.set(Modals.None);
        }}
    >
        <DirSize />
        <div class="h-1/3">
            {#if _currentFileTask === FileTasks.None}
                {#if _hotFiler}
                    <Chronograph />
                {:else}
                    <div class="flex justify-between mb-[4px] space-x-2.5 pt-1">
                        <NeuButton
                            on:click={() => encrypt()}
                            _style="font-size: 14px;">ENCRYPT</NeuButton
                        >
                        <NeuButton
                            on:click={() => decrypt()}
                            _style="font-size: 14px;">DECRYPT</NeuButton
                        >
                    </div>
                {/if}
            {:else}
                <TaskDisplay />
            {/if}
            <div>
                {#if _currentFileTask === FileTasks.None}
                    <div>
                        <FileTools />
                    </div>
                {:else}
                    <WaveProgress dataProgress={$fileTaskPercent} />
                {/if}
            </div>
            <div class="h-0.5" />
        </div>

        <div class="h-1/2">
            <div class="relative top-[2.25rem]">
                <ModalButtons />
            </div>
            <div class="flex justify-between relative top-[2.85rem]">
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
        class="bg-primary-400 dark:bg-primary-300"
        on:mouseleave={onmouseleave}
        on:pointerup={clearHeldBtnsFromContainer}
    >
        <RadialProgress
            _style="right: 3.4rem"
            dataProgress={$largeFilePercent}
            overlayText={$largeFileName}
        />
        {#if _modal === Modals.None}
            {#if !$pageLoading}
                <TreeView _fileTree={$fileTree} />
            {/if}
        {:else if _modal === Modals.Settings}
            <Settings />
        {:else if _modal === Modals.Info}
            <Info />
        {:else if _modal === Modals.Logger}
            <Logger />
        {/if}
    </div>
</div>
