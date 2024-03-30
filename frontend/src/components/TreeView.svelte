<script context="module" lang="ts">
</script>

<script lang="ts">
    // import { slide } from 'svelte/transition'
    import { onMount, afterUpdate, createEventDispatcher } from "svelte";
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
        SearchOutline,
        ExpandOutline,
        CompressOutline,
    } from "flowbite-svelte-icons";
    import { SpeedDial, SpeedDialButton, Tooltip } from "flowbite-svelte";

    import {
        clearHeldBtns,
        currentRelPath,
        expandRoot,
        getCurrentPageStore,
        handleDragOver,
        buildFileTree,
        setHeldBtnsStyle,
    } from "../stores/treeView.ts";

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
    export let tree: FileNode;

    import { AppPage, currentPage } from "../enums/AppPage.ts";
    import { GetDirectoryPath } from "../../wailsjs/go/main/Getters";
    import { EventsOff } from "../../wailsjs/runtime/runtime.js";

    let _appPage: AppPage;
    _appPage = AppPage.Vault;
    currentPage.subscribe((value) => {
        _appPage = value;
    });

    let _label: string;
    let _heldDownBtns: { [key: string]: HTMLButtonElement };

    heldDownBtns.subscribe((value) => {
        _heldDownBtns = value;
    });
    let expanded = false;

    const toggleExpansion = () => {
        expanded = !expanded;
        const currentPageStore = getCurrentPageStore();
        currentPageStore.update((currentState) => {
            currentState[basePath(tree.relPath)] = expanded;
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
            // Check if the left mouse button was pressed
            pointerDown = true;
            // LogInfo("Mouse down");
        }
    }
    function onMouseUp(event: MouseEvent) {
        if (event.button === 0) {
            // Check if the left mouse button was released
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
        var _heldDownBtns = get(heldDownBtns);
    }

    onMount(() => {
        const currentPageStore = getCurrentPageStore();
        const unsubscribe = currentPageStore.subscribe((state) => {
            const basePathKey = basePath(tree.relPath);
            if (state[basePathKey] !== undefined) {
                expanded = state[basePathKey];
                _label = basePath(tree.relPath); //This sets the rootdir Name
            }
        });
        _label = basePath(tree.relPath);
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

    function isFile(): boolean {
        return !tree.children; //This needs to be async
    }
    let buttonRef: HTMLButtonElement;
    function handleMouseEnter() {
        GetDirectoryPath(pageIndex()).then((filePath) => {
            getFileProperties(filePath + tree.relPath).then((fileProps) => {
                _filePropsTooltip = `${fileProps.modifiedDate} | ${formatFileSize(fileProps.fileSize)}`;
                // LogPrint("RelativePath " + tree.relPath);
                if (isFile()) currentRelPath.set(tree.relPath);
            });
        });
    }

    function handleMouseLeave() {
        currentRelPath.set("");
    }

    function moveFilesFromFileNode() {
        if (tree.children && tree.children.length > 0) {
            if (Object.keys(_heldDownBtns).length > 0) {
                moveFilesToRelPath(tree.relPath);
                LogInfo("Moved Files to Dir");
            }
        } else {
            LogInfo("moveFilesFromFileNode " + tree.relPath);
            if (tree.relPath !== get(currentRelPath)) {
                var dirPath = removeFileName(tree.relPath);
                moveFilesToRelPath(dirPath);
            }
        }
        LogInfo("OH LAWD ");
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
<div class="bg-gray-300" role="none">
    <button
        class="z-10 fixed top-0 left-1/2 transform -translate-x-1/2 mt-0.5"
        style="--wails-draggable:drag"
    >
        <!-- <SearchOutline class="w-5 h-5" color="dark" /> -->
    </button>
    <!-- <div
        class="w-full absolute top-0 bg-gray-600 h-6"
        style="--wails-draggable:drag"
    ></div> -->
    <ul
        class={basePath(tree.relPath) === pageName() ? "pl-0.5" : "pl-3"}
        style={basePath(tree.relPath) === pageName()
            ? "padding-top: 1px;font-size: 15px;color:black;--wails-draggable:drag"
            : "margin-top: -2px;color:white;--wails-draggable:no-drag"}
    >
        <li>
            {#if tree.children && tree.children.length > 0}<!-- Folder with children -->
                <button
                    on:click={toggleExpansion}
                    class="flex"
                    on:mouseenter={() => handleMouseEnter()}
                    on:mouseup={() => moveFilesFromFileNode()}
                    on:drop={() => LogError("AMOGUS")}
                    on:dragover={(event) => handleDragOver(tree.relPath, event)}
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
                        <Tooltip class="p-1 m-1 text-xs bg-gray-400"
                            >Move {Object.keys(_heldDownBtns).length} files to {basePath(
                                tree.relPath,
                            )}</Tooltip
                        >
                    {/if}
                {:else}
                    <Tooltip class="p-1 m-1 text-xs bg-gray-400"
                        >{basePath(tree.relPath)}</Tooltip
                    >
                {/if}

                {#if expanded}
                    <ul>
                        {#each tree.children as child}
                            <svelte:self tree={child} />
                        {/each}
                    </ul>
                {/if}
            {:else if isFile()}<!-- File -->
                <button
                    bind:this={buttonRef}
                    on:mousedown={() =>
                        handleFileClick(tree.relPath, buttonRef)}
                    on:mouseenter={() => {
                        handleMouseEnter();
                    }}
                    on:mouseleave={() => {
                        handleMouseLeave();
                    }}
                    on:mouseup={() => {
                        moveFilesFromFileNode();
                    }}
                    on:dragover={(event) => handleDragOver(tree.relPath, event)}
                >
                    {_label}
                </button>
                {#if pointerDown}
                    <Tooltip class="p-1 m-1 text-xs bg-gray-400"
                        >Move {Object.keys(_heldDownBtns).length} files to {basePath(
                            removeFileName(tree.relPath),
                        )}</Tooltip
                    >
                {:else}
                    <Tooltip class="p-0 m-0 text-xs bg-gray-400"
                        >{_filePropsTooltip}</Tooltip
                    >
                {/if}
            {:else}
                <!-- Folder with no children (empty) -->
                <button
                    class="flex"
                    on:mouseenter={() => {
                        handleMouseEnter();
                    }}
                    on:mouseup={() => moveFilesFromFileNode()}
                >
                    <FolderSolid class="w-3 mr-1"></FolderSolid>
                    {_label} AMOGUS
                </button>
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
                    updateExpansionForNode(tree, false);
                }}
            >
                <CompressOutline class="w-6 h-6" />
            </SpeedDialButton>
            <SpeedDialButton
                name="Expand "
                class="h-10 w-14 text-lg"
                on:click={() => {
                    updateExpansionForNode(tree, true);
                }}
            >
                <ExpandOutline class="w-6 h-6" />
            </SpeedDialButton>
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
