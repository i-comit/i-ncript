<script context="module" lang="ts">
</script>

<script lang="ts">
    // import { slide } from 'svelte/transition'
    import { onMount } from "svelte";
    import { get } from "svelte/store";
    import {
        EventsOn,
        EventsOnce,
        LogError,
        LogInfo,
        LogPrint,
    } from "../../wailsjs/runtime/runtime";

    import {
        FolderOpenSolid,
        FolderSolid,
        ExpandOutline,
        CompressOutline,
        LockSolid,
        FileImageSolid,
        FileVideoSolid,
        FileMusicSolid,
        FileOutline,
        SearchOutline,
        MailBoxSolid,
        ShieldSolid,
    } from "flowbite-svelte-icons";
    import {
        SpeedDial,
        SpeedDialButton,
        Tooltip,
        Popover,
        Search,
        Button,
    } from "flowbite-svelte";

    import {
        clearHeldBtns,
        currentFilePath,
        expandRoot,
        getCurrentPageStore,
        buildFileTree,
        setHeldBtnsStyle,
        openDirectory,
        openFile,
        currentDirPath,
        clearHeldBtnsFromContainer,
        checkFileDragDirectory,
    } from "../tools/fileTree.ts";

    import {
        pageName,
        pageIndex,
        getFileProperties,
        basePath,
        formatFileSize,
        moveFilesToRelPath,
        addToHeldFileBtnsArr,
        heldDownBtns,
        removeFileName,
        checkIfRelPathIsInHeldDownBtns,
        leftCtrlDown,
        pointerDown,
    } from "../tools/utils.ts";

    interface FileNode {
        relPath: string;
        children?: FileNode[]; // Make children optional to match the Go structure
    }
    export let _fileTree: FileNode;

    import { AppPage, currentPage } from "../enums/AppPage.ts";
    import { GetDirectoryPath } from "../../wailsjs/go/main/Getters";
    import {
        darkTextColor,
        lightBGColor,
        tooltipTailwindClass,
    } from "../stores/globalVariables.ts";
    import { FileTypes, getFileType } from "../enums/FileTypes.ts";
    import SpringyPointer from "../elements/SpringyPointer.svelte";

    let _label: string;
    let _heldDownBtns: { [key: string]: HTMLButtonElement };
    let buttonRef: HTMLButtonElement;
    let clickedOnButtonRef: HTMLButtonElement;

    heldDownBtns.subscribe((value) => {
        _heldDownBtns = value;
    });
    let expanded = false;

    const toggleExpansion = () => {
        expanded = !expanded;
        const currentPageStore = getCurrentPageStore();
        currentPageStore.update((currentState) => {
            currentState[basePath(_fileTree.relPath)] = expanded;
            return currentState;
        });
    };

    function ctrlLeftDown(event: KeyboardEvent) {
        if (event.code === "ControlLeft") {
            leftCtrlDown.set(true);
        }
    }

    function ctrlLeftUp(event: KeyboardEvent) {
        if (event.code === "ControlLeft") {
            leftCtrlDown.set(false);
        }
    }
    function onMouseDown(event: MouseEvent) {
        if (event.button === 0) {
            pointerDown.set(true);
            // LogInfo("Mouse down");
        }
    }
    function onMouseUp(event: MouseEvent) {
        if (event.button === 0) {
            pointerDown.set(false);
            // LogInfo("Mouse up");
        }
    }
    function onTouchStart(event: TouchEvent) {
        pointerDown.set(true);
        // LogInfo("Pointer down");
    }

    function onTouchEnd(event: TouchEvent) {
        pointerDown.set(false);
        // LogInfo("Pointer up");
    }

    function handleFileClick(relPath: string, _buttonRef: HTMLButtonElement) {
        if (!get(leftCtrlDown) && checkIfRelPathIsInHeldDownBtns(relPath)) {
            clearHeldBtns();
        }
        addToHeldFileBtnsArr(relPath, _buttonRef);
        Object.keys(get(heldDownBtns)).forEach((key) => {
            LogInfo("Held down node moveFiles: " + key);
        });
        setHeldBtnsStyle();
    }

    onMount(() => {
        const currentPageStore = getCurrentPageStore();
        const unsubscribe = currentPageStore.subscribe((state) => {
            const basePathKey = basePath(_fileTree.relPath);
            if (state[basePathKey] !== undefined) {
                expanded = state[basePathKey];
                _label = basePath(_fileTree.relPath); //This sets the rootdir Name
            }
        });
        _label = basePath(_fileTree.relPath);
        EventsOn("rebuildFileTree", buildFileTree);
        return unsubscribe; // Unsubscribe when the component unmounts
    });

    function updateExpansionForNode(node: FileNode, expand: boolean) {
        const currentPageStore = getCurrentPageStore();
        currentPageStore.update((currentState) => {
            const basePathKey = basePath(node.relPath);
            currentState[basePathKey] = expand;
            return currentState;
        });
        // Recurse through children if any
        if (node.children) {
            for (const child of node.children) {
                updateExpansionForNode(child, expand);
            }
        }
        expandRoot();
    }
    let _filePropsTooltip: string;

    async function isFile(): Promise<boolean> {
        const filePath = await GetDirectoryPath(pageIndex());
        const fileProps = await getFileProperties(filePath + _fileTree.relPath);
        // Return true if fileSize > 0, indicating it's a file
        return fileProps.fileSize > 0;
    }

    function handleMouseEnter() {
        if (basePath(_fileTree.relPath) === pageName()) return;
        GetDirectoryPath(pageIndex()).then((filePath) => {
            getFileProperties(filePath + _fileTree.relPath).then(
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
        });
    }

    function handleMouseLeave() {
        currentFilePath.set("");
        currentDirPath.set("");
        leftCtrlDown.set(false);
        // LogError("currentRelpath null");
    }

    function moveFilesFromFileNode() {
        if (_fileTree.children && _fileTree.children.length > 0) {
            if (Object.keys(_heldDownBtns).length > 0) {
                moveFilesToRelPath(_fileTree.relPath);
                LogInfo("Moved Files to Dir " + _fileTree.relPath);
            }
        } else {
            LogInfo(
                "moveFilesFromFileNode " +
                    _fileTree.relPath +
                    " " +
                    get(currentFilePath) +
                    " " +
                    get(currentDirPath),
            );
            if (_fileTree.relPath !== get(currentFilePath)) {
                var dirPath = removeFileName(_fileTree.relPath);
                moveFilesToRelPath(dirPath);
            }
        }
    }

    function handleDblClick(relPath: string) {
        openFile(relPath);
    }

    function checkHeldDownBtnsTooltip(): boolean {
        return (
            Object.keys(_heldDownBtns).length > 0 &&
            basePath(_fileTree.relPath) !== pageName()
        );
    }

    let _isFile = isFile();
    let _canMoveToDir = checkFileDragDirectory(_fileTree.relPath);
</script>

<svelte:window
    on:keydown={ctrlLeftDown}
    on:keyup={ctrlLeftUp}
    on:mousedown={onMouseDown}
    on:mouseup={onMouseUp}
    on:touchstart={onTouchStart}
    on:touchend={onTouchEnd}
/>
<div class="rounded-lg" role="none" on:click={clearHeldBtnsFromContainer}>
    <ul
        class={basePath(_fileTree.relPath) === pageName() ? "pl-0" : "pl-3"}
        style={`color: ${darkTextColor}; ${
            basePath(_fileTree.relPath) === pageName()
                ? "padding-top: 1px; font-size: 15px; --wails-draggable: drag;"
                : `margin-top: -2px; --wails-draggable: no-drag; margin-bottom: 0px`
        }`}
    >
        <li>
            {#if _fileTree.children && _fileTree.children.length > 0}<!-- Folder with children -->
                <button
                    class="flex {basePath(_fileTree.relPath) === pageName()
                        ? 'rounded-md px-1 underline mb-0.5'
                        : 'pl-1.5'}"
                    style="border-left: 1px solid #eeeeee;
                    {basePath(_fileTree.relPath) === pageName()
                        ? `position: sticky; top: 1px; left:0px; background-color: ${lightBGColor}; z-index: 45`
                        : 'position: relative;'}"
                    on:click={() => {
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
                {:else if basePath(_fileTree.relPath) !== pageName()}
                    <Tooltip class={tooltipTailwindClass} offset={1}
                        >{basePath(_fileTree.relPath)}</Tooltip
                    >
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
                            on:mousedown={() => {
                                handleFileClick(_fileTree.relPath, buttonRef);
                                getFileType(_fileTree.relPath);
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
                            {#if getFileType(_fileTree.relPath) === FileTypes.Encrypted}
                                <LockSolid class="w-3 mr-0.5"></LockSolid>
                            {:else if getFileType(_fileTree.relPath) === FileTypes.Image}
                                <FileImageSolid class="w-3 mr-0.5"
                                ></FileImageSolid>
                            {:else if getFileType(_fileTree.relPath) === FileTypes.Video}
                                <FileVideoSolid class="w-3 mr-0.5"
                                ></FileVideoSolid>
                            {:else if getFileType(_fileTree.relPath) === FileTypes.Audio}
                                <FileMusicSolid class="w-3 mr-0.5"
                                ></FileMusicSolid>
                            {:else}
                                <FileOutline class="w-3 mr-1"></FileOutline>
                            {/if}
                            {_label}
                        </button>
                    {:else}
                        <button
                            class="flex"
                            on:mouseenter={() => {
                                handleMouseEnter();
                            }}
                            on:mouseup={() => moveFilesFromFileNode()}
                        >
                            <FolderSolid class="w-3 mr-1"></FolderSolid>
                            {_label} &#40;empty&#41;
                        </button>
                    {/if}
                {:catch error}
                    <p>nothing here yet.</p>
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
                {:else if clickedOnButtonRef === buttonRef}
                    <Popover
                        class="w-30 text-xs font-light m-0 p-0"
                        arrow={true}
                        offset={1}
                        placement="bottom-end"
                    >
                        <FolderSolid class="w-3 m-0"></FolderSolid>
                        <FolderSolid class="w-3 m-0"></FolderSolid>
                    </Popover>
                    <Tooltip
                        class={tooltipTailwindClass}
                        offset={-1}
                        arrow={true}>{_filePropsTooltip}</Tooltip
                    >
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
            class="flex items-center justify-center bg-gray-600 !rounded-br-xl !rounded-tl-3xl h-8 w-14"
        >
            <SpeedDialButton
                name="Collapse "
                class="h-10 w-14 right-10"
                on:click={() => {
                    updateExpansionForNode(_fileTree, false);
                }}
            >
                <CompressOutline class="w-6 h-6" />
            </SpeedDialButton>
            <SpeedDialButton
                name="Expand "
                class="h-10 w-14 text-lg"
                on:click={() => {
                    updateExpansionForNode(_fileTree, true);
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
        bottom: -8vh !important;
        transform: scale(0.55) !important;
        z-index: 5;
    }

    ul {
        margin: 0px;
        z-index: 25;
        list-style: none;
        user-select: none;
        text-align: justify;
        font-size: small;
        text-overflow: ellipsis;
    }
    button {
        text-align: justify;
        max-width: 12rem;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }
</style>
