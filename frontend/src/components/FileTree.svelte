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
        setHeldBtnsStyle,
        openDirectory,
        openFile,
        currentDirPath,
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
    } from "../tools/utils.ts";

    interface FileNode {
        relPath: string;
        children?: FileNode[]; // Make children optional to match the Go structure
    }
    export let _fileTree: FileNode;

    import { AppPage, currentPage } from "../enums/AppPage.ts";
    import { GetDirectoryPath } from "../../wailsjs/go/main/Getters";
    import { tooltipTailwindClass } from "../stores/globalVariables.ts";
    import { FileTypes, getFileType } from "../enums/FileTypes.ts";

    let _label: string;
    let _heldDownBtns: { [key: string]: HTMLButtonElement };
    let buttonRef: HTMLButtonElement;
    let clickedOnButtonRef: HTMLButtonElement;

    let clickedOnButton2: { [key: string]: HTMLButtonElement };

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

    let leftCtrlDown = false;
    let pointerDown = false; // Previously mouseDown

    function ctrlLeftDown(event: KeyboardEvent) {
        if (event.code === "ControlLeft") {
            leftCtrlDown = true;
        }
    }

    function ctrlLeftUp(event: KeyboardEvent) {
        if (event.code === "ControlLeft") {
            leftCtrlDown = false;
        }
    }
    function onMouseDown(event: MouseEvent) {
        if (event.button === 0) {
            pointerDown = true;
            // LogInfo("Mouse down");
        }
    }
    function onMouseUp(event: MouseEvent) {
        if (event.button === 0) {
            pointerDown = false;
            // LogInfo("Mouse up");
        }
    }
    function onTouchStart(event: TouchEvent) {
        pointerDown = true;
        // LogInfo("Pointer down");
    }

    function onTouchEnd(event: TouchEvent) {
        pointerDown = false;
        // LogInfo("Pointer up");
    }

    function handleFileClick(relPath: string, _buttonRef: HTMLButtonElement) {
        if (!leftCtrlDown && !checkIfRelPathIsInHeldDownBtns(relPath)) {
            clearHeldBtns();
        }
        addToHeldFileBtnsArr(relPath, _buttonRef);
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
        LogError("currentRelpath null");
    }

    function moveFilesFromFileNode() {
        if (_fileTree.children && _fileTree.children.length > 0) {
            if (Object.keys(_heldDownBtns).length > 0) {
                moveFilesToRelPath(_fileTree.relPath);
                LogInfo("Moved Files to Dir");
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

    let promise = isFile();
</script>

<svelte:window
    on:keydown={ctrlLeftDown}
    on:keyup={ctrlLeftUp}
    on:mousedown={onMouseDown}
    on:mouseup={onMouseUp}
    on:touchstart={onTouchStart}
    on:touchend={onTouchEnd}
/>
<div class="bg-gray-300 rounded-lg" role="none" on:click={clearHeldBtns}>
    <button
        class="z-10 fixed top-0 left-1/2 transform -translate-x-1/2 mt-0.5"
        style="--wails-draggable:drag"
    >
        <!-- <SearchOutline class="w-5 h-5" color="dark" /> -->
    </button>
    <!-- style={basePath(_fileTree.relPath) === pageName()
        ? "position: sticky; top: 0; z-index: 10; background-color: green !important"
        : "position: relative;"}  -->
    <ul
        class={basePath(_fileTree.relPath) === pageName() ? "pl-1" : "pl-3"}
        style={basePath(_fileTree.relPath) === pageName()
            ? "padding-top: 1px;font-size: 15px;color:black;--wails-draggable:drag;"
            : "margin-top: -2px;color:white;--wails-draggable:no-drag; margin-bottom: 0px"}
    >
        <li>
            {#if _fileTree.children && _fileTree.children.length > 0}<!-- Folder with children -->
                <button
                    style={basePath(_fileTree.relPath) === pageName()
                        ? "position: sticky; top: 1px; z-index: 5; background-color:blue"
                        : "position: relative;"}
                    on:click={() => {
                        toggleExpansion();
                        clearHeldBtns();
                    }}
                    class="flex"
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
                {#if pointerDown}
                    {#if Object.keys(_heldDownBtns).length > 0}
                        <Tooltip class={tooltipTailwindClass} offset={1}
                            >Move {Object.keys(_heldDownBtns).length} files to {basePath(
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
                {#await promise then isFile}
                    {#if isFile}
                        <button
                            class="flex rounded-md px-0.5"
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
                            on:dblclick={() =>
                                handleDblClick(_fileTree.relPath)}
                        >
                            {#if getFileType(_fileTree.relPath) === FileTypes.Encrypted}
                                <LockSolid class="w-3 mr-1"></LockSolid>
                            {:else if getFileType(_fileTree.relPath) === FileTypes.Image}
                                <FileImageSolid class="w-3 mr-1"
                                ></FileImageSolid>
                            {:else if getFileType(_fileTree.relPath) === FileTypes.Video}
                                <FileVideoSolid class="w-3 mr-1"
                                ></FileVideoSolid>
                            {:else if getFileType(_fileTree.relPath) === FileTypes.Audio}
                                <FileMusicSolid class="w-3 mr-1"
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
                    <p>nothing here yet..</p>
                {/await}

                {#if pointerDown}
                    <Tooltip
                        class={tooltipTailwindClass}
                        offset={1}
                        arrow={true}
                        >Move {Object.keys(_heldDownBtns).length} files to {basePath(
                            removeFileName(_fileTree.relPath),
                        )}</Tooltip
                    >
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
            defaultClass="fixed end-0"
            class="bg-gray-800 rounded-full bg-white"
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
            <!-- {#if expanded}
                <SpeedDialButton
                    name="Expand "
                    class="h-10 w-14 text-lg"
                    on:click={() => {
                        updateExpansionForNode(tree, true);
                    }}
                >
                    <ExpandOutline class="w-6 h-6" />
                </SpeedDialButton>
            {/if} -->
        </SpeedDial>
    </div>
</div>

<style>
    #dial {
        right: -12px !important;
        bottom: 10vh !important;
        transform: scale(0.55) !important;
        z-index: 5;
    }

    ul {
        margin: 0px;
        list-style: none;
        user-select: none;
        text-align: justify;
        font-size: small;
        text-overflow: clip;
    }
    button {
        text-align: justify;
        max-width: 12rem;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }
</style>
