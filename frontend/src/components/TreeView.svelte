<script context="module" lang="ts">
</script>

<script lang="ts">
    // import { slide } from 'svelte/transition'
    import { onMount, afterUpdate, createEventDispatcher } from "svelte";
    import { get } from "svelte/store";
    import {
        EventsOnce,
        LogError,
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
        expandRoot,
        getCurrentPageStore,
        setFileStyle,
        handleDragLeave,
        handleDragOver,
        handleDrop,
        loadFileTree,
        addButtonRefToStore,
        allTreeViewBtns,
        setHeldBtnsStyle,
    } from "../stores/treeView.ts";

    import {
        pageName,
        pageIndex,
        getFileProperties,
        basePath,
        formatFileSize,
        addFileToHeldFilesArr,
        moveFilesToRelPath,
        heldDownFiles,
        clearHeldDownFiles,
        addToHeldBtnsArr,
    } from "../tools/utils.ts";

    interface FileNode {
        relPath: string;
        children?: FileNode[]; // Make children optional to match the Go structure
    }
    export let tree: FileNode;

    import { AppPage, currentPage } from "../enums/AppPage.ts";
    import { GetDirectoryPath } from "../../wailsjs/go/main/Getters";

    import App from "../App.svelte";
    let _appPage: AppPage;
    _appPage = AppPage.Vault;
    currentPage.subscribe((value) => {
        _appPage = value;
    });

    let _label: string;
    let expanded = false;

    const toggleExpansion = () => {
        expanded = !expanded;
        const currentPageStore = getCurrentPageStore();
        currentPageStore.update((currentState) => {
            currentState[basePath(tree.relPath)] = expanded;
            return currentState;
        });
    };
    function logFilePath(relPath: string, _buttonRef: HTMLButtonElement) {
        addToHeldBtnsArr(relPath, _buttonRef);
        setHeldBtnsStyle()
        var _heldDownFiles = get(heldDownFiles);
        _heldDownFiles.forEach((node) => {
            LogError("held down node moveFiles: " + node);
        });
    }

    onMount(() => {
        const currentPageStore = getCurrentPageStore();
        const unsubscribe = currentPageStore.subscribe((state) => {
            const basePathKey = basePath(tree.relPath);
            if (state[basePathKey] !== undefined) {
                expanded = state[basePathKey];
            }
        });
        _label = basePath(tree.relPath);
        EventsOnce("rebuildFileTree", () => {
            loadFileTree(pageIndex());
        });
        // addButtonRefToStore(tree.relPath, buttonRef);
        // printAllButtonRefs()
        return unsubscribe; // Unsubscribe when the component unmounts
    });

    // function printAllButtonRefs() {
    //     const btns = get(allTreeViewBtns);
    //     Object.entries(btns).forEach(([key, value]) => {
    //         LogError(`Path: ${key}, Button: ${value}`);
    //     });
    // }

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

    function isFile() {
        GetDirectoryPath(pageIndex()).then((filePath) => {
            getFileProperties(filePath + tree.relPath).then((fileProps) => {
                return fileProps.fileSize > 0;
            });
        });
        return !tree.children;
    }
    let buttonRef: HTMLButtonElement;
    let currentRelPath: string;
    function handleMouseEnter() {
        GetDirectoryPath(pageIndex()).then((filePath) => {
            getFileProperties(filePath + tree.relPath).then((fileProps) => {
                _filePropsTooltip = `${fileProps.modifiedDate} | ${formatFileSize(fileProps.fileSize)}`;
                LogPrint("RelativePath " + tree.relPath);
                currentRelPath = tree.relPath;
            });
        });
    }

    function handleKeyDown(event: KeyboardEvent) {
        if (event.key === "Shift") {
            addFileToHeldFilesArr(tree.relPath);
        }
    }

    function handleKeyUp(event: KeyboardEvent) {
        if (event.key === "Shift") {
        }
    }

    function handleMouseLeave() {
        currentRelPath = "";
        LogPrint("RelativePath Leave " + currentRelPath);
    }

    let backgroundBG: string;
    function setBackgroundColor(relPath: string) {
        var _heldDownFiles = get(heldDownFiles);
        LogError(
            "REL PATH " +
                relPath +
                "       \t" +
                _heldDownFiles[0] +
                " currentPath " +
                currentRelPath,
        );
        if (_heldDownFiles.includes(relPath)) {
            backgroundBG = "background-color: blue;";
        } else {
            backgroundBG = "background-color: none;";
        }
        // else if (relPath === currentRelPath) {
        //     backgroundBG = "background-color: green;";
        // } else if (relPath !== currentRelPath) {
        //     backgroundBG = "background-color: red;";
        // }
    }
    const dispatch = createEventDispatcher();
    let fileTreeMap;
</script>

<svelte:window on:keydown={handleKeyDown} on:keyup={handleKeyUp} />
<div class="bg-gray-300">
    <button
        class="z-10 fixed top-0 left-1/2 transform -translate-x-1/2 mt-0.5"
        style="--wails-draggable:drag"
    >
        <SearchOutline class="w-5 h-5" color="dark" />
    </button>
    <!-- <div
        class="w-full absolute top-0 bg-gray-600 h-6"
        style="--wails-draggable:drag"
    ></div> -->
    <ul
        class={basePath(tree.relPath) === pageName() ? "pl-0.5" : "pl-3"}
        style={basePath(tree.relPath) === pageName()
            ? "margin-top: -22px;"
            : "margin-top: 1px;"}
    >
        <li>
            {#if tree.children && tree.children.length > 0}
                <button
                    on:click={toggleExpansion}
                    class="flex"
                    on:mouseenter={() => handleMouseEnter()}
                    on:mouseup={() => moveFilesToRelPath(tree.relPath)}
                    on:dragover={(event) => handleDragOver(tree.relPath, event)}
                    on:drop={(event) => handleDrop(tree.relPath, event)}
                >
                    {#if !expanded}
                        <FolderSolid class="w-3 mr-1"></FolderSolid>
                    {:else}
                        <FolderOpenSolid class="w-3 mr-1"></FolderOpenSolid>
                    {/if}
                    {_label}
                </button>
                {#if expanded}
                    <ul>
                        {#each tree.children as child}
                            <svelte:self tree={child} />
                        {/each}
                    </ul>
                {/if}
            {:else if isFile()}
                <button
                    bind:this={buttonRef}
                    on:click={() => logFilePath(tree.relPath, buttonRef)}
                    on:mouseenter={() => {
                        handleMouseEnter();
                    }}
                    on:mouseleave={() => {
                        handleMouseLeave();
                    }}
                    on:dragover={(event) => handleDragOver(tree.relPath, event)}
                    on:dragleave={handleDragLeave}
                    on:drop={(event) => handleDrop(tree.relPath, event)}
                >
                    {_label}
                </button>
                <Tooltip class="p-0 m-0 text-xs bg-gray-400"
                    >{_filePropsTooltip}</Tooltip
                >
            {:else}
                <button
                    class="flex"
                    on:dragover={(event) => handleDragOver(tree.relPath, event)}
                    on:drop={(event) => handleDrop(tree.relPath, event)}
                >
                    <FolderSolid class="w-3 mr-1"></FolderSolid>
                    {_label}
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
