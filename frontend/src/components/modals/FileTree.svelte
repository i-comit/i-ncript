<script lang="ts">
    // import { slide } from 'svelte/transition'
    import { onMount, onDestroy, afterUpdate } from "svelte";
    import { get } from "svelte/store";
    import {
        EventsOn,
        LogInfo,
    } from "../../../wailsjs/runtime/runtime";

    import {
        FolderOpenSolid,
        FolderSolid,
        ExpandOutline,
        CompressOutline,
    } from "flowbite-svelte-icons";
    import {
        SpeedDial,
        SpeedDialButton,
        Tooltip,
        Popover,
    } from "flowbite-svelte";

    import {
        clearHeldBtns,
        currentFilePath,
        expandRoot,
        getCurrentPageStore,
        buildFileTree,
        currentDirPath,
        clearHeldBtnsFromContainer,
        checkFileDragDirectory,
        handleFileClick,
        FileNode,
    } from "../../tools/fileTree.ts";

    import {
        pageName,
        getFileProperties,
        basePath,
        formatFileSize,
        moveFilesToRelPath,
        heldDownBtns,
        removeFileName,
        leftCtrlDown,
        pointerDown,
        getRootDir,
    } from "../../tools/utils.ts";

    import { darkLightTextOnElement } from "../../tools/themes.ts";

    import {
        darkBGColor,
        darkTextColor,
        lightBGColor,
        lightTextColor,
        tooltipTailwindClass,
    } from "../../stores/constantVariables.ts";
    import {
        ctrlLeftDown,
        ctrlLeftUp,
        onMouseDown,
        onMouseUp,
        onTouchStart,
        onTouchEnd,
    } from "../../tools/inputs.ts";
    import FileIcon from "../widgets/FileIcon.svelte";
    import {
        darkLightMode,
        accentColor,
    } from "../../stores/dynamicVariables.ts";
    import { FileTasks, currentFileTask } from "../../enums/FileTasks.ts";
    import { OpenDirectory } from "../../../wailsjs/go/main/FileUtils";
    import { GetTotalDirSize } from "../../../wailsjs/go/main/Getters";

    export let _fileTree: FileNode;

    $: _label = basePath(_fileTree.relPath);
    let buttonRef: HTMLButtonElement;
    let clickedOnButtonRef: HTMLButtonElement;

    let _heldDownBtns: { [key: string]: HTMLButtonElement };
    heldDownBtns.subscribe((value) => {
        _heldDownBtns = value;
    });
    let expanded = false;
    let _filePropsTooltip: string;
    let ulElement: HTMLUListElement;

    let _isFile = isFile();
    let _dirSize = getDirSize();
    let _canMoveToDir = checkFileDragDirectory(_fileTree.relPath);

    $: folderStyle = `${
        _fileTree.relPath === getRootDir()
            ? `position: sticky; top: 1px; left: 0px; 
               background-color: ${get(darkLightMode) ? darkBGColor : lightBGColor}; 
               color: ${$accentColor}; 
               font-weight: 600; z-index: 42`
            : `border-left: 1.5px solid ${get(darkLightMode) ? lightBGColor : darkBGColor};
               position: relative;
               color: ${get(darkLightMode) ? lightTextColor : darkTextColor}; `
    }`;

    const toggleExpansion = () => {
        expanded = !expanded;
        const currentPageStore = getCurrentPageStore();
        currentPageStore.update((currentState) => {
            currentState[_fileTree.relPath] = expanded;
            return currentState;
        });
    };

    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightTextOnElement(!value, ulElement);
    });

    onMount(() => {
        const currentPageStore = getCurrentPageStore();
        const unsubscribe = currentPageStore.subscribe((state) => {
            const basePathKey = _fileTree.relPath;
            if (state[basePathKey] !== undefined) {
                expanded = state[basePathKey];
                _label = basePath(_fileTree.relPath); //sets the rootdir Name
            }
        });
        darkLightTextOnElement(!get(darkLightMode), ulElement);
        _label = basePath(_fileTree.relPath);
        EventsOn("rebuildFileTree", buildFileTree);
        return unsubscribe; // Unsubscribe when the component unmounts
    });

    onDestroy(() => {
        unsub_darkLightMode();
    });

    function expandCollapseAllNodes(node: FileNode, expand: boolean) {
        const currentPageStore = getCurrentPageStore();
        currentPageStore.update((currentState) => {
            const basePathKey = node.relPath;
            currentState[basePathKey] = expand;
            return currentState;
        });
        // Recurse through children if any
        if (node.children) {
            for (const child of node.children) {
                expandCollapseAllNodes(child, expand);
            }
        }
        expandRoot();
    }

    async function isFile(): Promise<boolean> {
        const fileProps = await getFileProperties(
            getRootDir() + _fileTree.relPath,
        );
        return fileProps.fileSize > 0; // Return true if fileSize > 0, indicating a file
    }

    async function getDirSize(): Promise<string> {
        const dirSize = await GetTotalDirSize(getRootDir() + _fileTree.relPath);
        return `${formatFileSize(dirSize)}`;
    }

    function handleMouseEnter() {
        if (_fileTree.relPath === getRootDir()) return;
        getFileProperties(getRootDir() + _fileTree.relPath).then(
            (fileProps) => {
                _filePropsTooltip = `${fileProps.modifiedDate} | ${formatFileSize(fileProps.fileSize)}`;
                // LogPrint("RelativePath " + tree.relPath);
                if (!_fileTree.children) {
                    isFile().then((_isFile) => {
                        if (_isFile) currentFilePath.set(_fileTree.relPath);
                    });
                } else {
                    currentDirPath.set(_fileTree.relPath);
                    LogInfo("current Dir Path " + get(currentDirPath));
                }
            },
        );
    }

    function handleMouseLeave() {
        currentFilePath.set("");
        currentDirPath.set("");
        leftCtrlDown.set(false);
    }

    function moveFilesFromFileNode() {
        if (_fileTree.children && _fileTree.children.length > 0) {
            if (Object.keys(_heldDownBtns).length > 0) {
                moveFilesToRelPath(_fileTree.relPath);
                LogInfo("Moved Files to Dir " + _fileTree.relPath);
            }
        } else {
            if (_fileTree.relPath !== get(currentFilePath)) {
                var dirPath = removeFileName(_fileTree.relPath);
                moveFilesToRelPath(dirPath);
            }
        }
    }

    function checkHeldDownBtnsTooltip(): boolean {
        return (
            Object.keys($heldDownBtns).length > 0 &&
            basePath(_fileTree.relPath) !== pageName()
        );
    }
</script>

<svelte:window
    on:keydown={ctrlLeftDown}
    on:keyup={ctrlLeftUp}
    on:mousedown={onMouseDown}
    on:mouseup={onMouseUp}
    on:touchstart={onTouchStart}
    on:touchend={onTouchEnd}
/>
<div
    id="filetree"
    class="rounded-lg"
    role="none"
    on:mouseup|stopPropagation={clearHeldBtnsFromContainer}
>
    <ul
        bind:this={ulElement}
        class={_fileTree.relPath === getRootDir() ? "pl-0" : "pl-3"}
        style={`${
            _fileTree.relPath === getRootDir()
                ? "padding-top: 1px; font-size: 15px; --wails-draggable: drag;"
                : `margin-top: -2px; --wails-draggable: no-drag; margin-bottom: 0px`
        }`}
    >
        <li>
            {#if _fileTree.children && _fileTree.children.length > 0}<!-- Folder with children -->
                <button
                    disabled={$currentFileTask !== FileTasks.None}
                    class="flex hover:underline {_fileTree.relPath ===
                    getRootDir()
                        ? 'rounded-md px-1 mb-0.5'
                        : 'pl-1.5'}"
                    style={folderStyle}
                    on:click|stopPropagation={() => {
                        toggleExpansion();
                        clearHeldBtns();
                    }}
                    on:mouseenter={() => handleMouseEnter()}
                    on:mouseup={() => moveFilesFromFileNode()}
                >
                    {#if !expanded}
                        <FolderSolid class="w-3 mr-1"></FolderSolid>
                    {:else}
                        <FolderOpenSolid class="w-3 mr-1"></FolderOpenSolid>
                    {/if}
                    {_label}
                </button>
                {#if $pointerDown}
                    {#if checkHeldDownBtnsTooltip()}
                        <Tooltip class={tooltipTailwindClass} offset={1}
                            >move {Object.keys(_heldDownBtns).length} files to {basePath(
                                _fileTree.relPath,
                            )}</Tooltip
                        >
                    {/if}
                {:else if _fileTree.relPath !== getRootDir()}
                    {#await _dirSize then dirSize}
                        {#if dirSize}
                            <Tooltip class={tooltipTailwindClass} offset={-1}
                                >{dirSize}</Tooltip
                            >
                        {/if}
                    {:catch}
                        <Tooltip class={tooltipTailwindClass} offset={-1}
                            >{basePath(_fileTree.relPath)}</Tooltip
                        >
                    {/await}
                {/if}
                {#if expanded}
                    <ul>
                        {#each _fileTree.children as child}
                            <svelte:self _fileTree={child} />
                        {/each}
                    </ul>
                {/if}
            {:else if !_fileTree.children}<!-- File -->
                {#await _isFile then isFile}
                    {#if isFile}
                        <button
                            class="flex rounded-md px-0.5 ml-1 my-0.5"
                            bind:this={buttonRef}
                            disabled={$currentFileTask !== FileTasks.None}
                            on:mousedown={() => {
                                handleFileClick(_fileTree.relPath, buttonRef);
                            }}
                            on:mouseenter={() => {
                                handleMouseEnter();
                            }}
                            on:mouseleave={() => {
                                handleMouseLeave();
                            }}
                            on:mouseup={() => {
                                moveFilesFromFileNode();
                            }}
                        >
                            <FileIcon {_fileTree} />
                            {_label}
                        </button>
                    {:else}
                        <button
                            class="flex"
                            on:mouseenter={() => {
                                handleMouseEnter();
                            }}
                            on:mouseup|stopPropagation={() =>
                                moveFilesFromFileNode()}
                        >
                            <FolderSolid class="w-3 mr-1"></FolderSolid>
                            {_label} &#40;empty&#41;
                        </button>
                    {/if}
                {:catch error}
                    <button
                        on:click={() =>
                            OpenDirectory( _fileTree.relPath)}
                        > nothing here yet.</button
                    >

                    <!-- <button
                        class="absolute top-1/3 left-1/2 z-20 p-2 !py-0.5 text-xs font-semibold rounded-lg"
                        style={`padding-left: 0.1rem; 
                        border: 1px solid black;
                        outline-style: solid;
                        outline-width: thin;
                        color: ${$accentColor}`}
                        on:click={() => OpenDirectory(_fileTree.relPath)}
                    >
                        {pageName()}
                        <FolderSolid class="mr-1" size="xl"></FolderSolid>
                    </button> -->
                {/await}

                {#if $pointerDown}
                    {#if checkHeldDownBtnsTooltip()}
                        {#await _canMoveToDir then canMove}
                            {#if canMove}
                                <Tooltip
                                    class={tooltipTailwindClass}
                                    offset={1}
                                    arrow={true}
                                    >move {Object.keys(_heldDownBtns).length} files
                                    to
                                    {basePath(_fileTree.relPath)}</Tooltip
                                >
                            {/if}
                        {/await}
                    {/if}
                {:else}
                    <Tooltip
                        class={tooltipTailwindClass}
                        offset={-1}
                        arrow={true}>{_filePropsTooltip}</Tooltip
                    >
                {/if}
            {/if}
        </li>
    </ul>
    <div id="dial" class="fixed">
        <SpeedDial
            class="flex items-center justify-center h-8 w-14"
            popperDefaultClass="flex items-center !mb-0 gap-0.5"
            style={`background-color: ${$accentColor}; border-radius: 50% 0% 50% 0%;`}
        >
            <SpeedDialButton
                name="Collapse "
                class="h-10 w-14 right-10"
                on:click={() => {
                    expandCollapseAllNodes(_fileTree, false);
                }}
            >
                <CompressOutline class="w-6 h-6" />
            </SpeedDialButton>
            <SpeedDialButton
                name="Expand "
                class="h-10 w-14 text-lg"
                on:click={() => {
                    expandCollapseAllNodes(_fileTree, true);
                }}
            >
                <ExpandOutline class="w-6 h-6" />
            </SpeedDialButton>
        </SpeedDial>
    </div>
</div>

<style>
    #dial {
        right: -0.7rem !important;
        bottom: -9vh !important;
        transform: scale(0.55) !important;
        z-index: 35;
    }

    ul {
        --text-color: #757575;
        color: var(--text-color);
        margin: 0px;
        z-index: 25;
        list-style: none;
        user-select: none;
        text-align: justify;
        font-size: small;
    }
    button {
        text-align: justify;
        max-width: 20rem;

        overflow: hidden;
        white-space: nowrap;
    }
</style>
