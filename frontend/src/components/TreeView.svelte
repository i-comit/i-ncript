<script context="module" lang="ts">
    // retain module scoped expansion state for each tree node
    import { writable } from "svelte/store";
    // Store for expansion states, keyed by node label or a unique ID
    export const vaultExpansionState = writable<{ [key: string]: boolean }>({});
    export const nBoxExpansionState = writable<{ [key: string]: boolean }>({});
    export const oBoxExpansionState = writable<{ [key: string]: boolean }>({});
</script>

<script lang="ts">
    // import { slide } from 'svelte/transition'
    import { onMount } from "svelte";

    import {
        FolderOpenSolid,
        FolderSolid,
        SearchOutline,
        ShareNodesSolid,
        PrinterOutline,
        DownloadSolid,
        FileCopySolid,
    } from "flowbite-svelte-icons";
    import { SpeedDial, SpeedDialButton } from "flowbite-svelte";

    // import {pageName, toggleExpansion} from "../stores/treeViewStates"

    import {
        printFrontendMsg,
        getFilePath,
        getFileProperties,
        basePath,
    } from "../utils.ts";

    interface FileNode {
        relPath: string;
        children?: FileNode[]; // Make children optional to match the Go structure
    }
    export let tree: FileNode;
    import { currentPage } from "../stores/currentPage.ts";
    import { AppPage } from "../enums/AppPage.ts";
    let _appPage: AppPage;
    _appPage = AppPage.Vault;
    currentPage.subscribe((value) => {
        _appPage = value;
    });
    // _appPage = $currentPage
    let expanded = false;

    function getCurrentPageStore() {
        switch (
            _appPage // Assuming _appPage holds the current page enum value
        ) {
            case AppPage.Vault:
                return vaultExpansionState;
            case AppPage.NBox:
                return nBoxExpansionState;
            case AppPage.OBox:
                return oBoxExpansionState;
            default:
                throw new Error("Unrecognized page");
        }
    }

    const toggleExpansion = () => {
        expanded = !expanded;
        const currentPageStore = getCurrentPageStore();
        currentPageStore.update((currentState) => {
            currentState[basePath(tree.relPath)] = expanded;
            return currentState;
        });
    };

    onMount(() => {
        expandRoot();
        printFrontendMsg("AMOGUS");
        const currentPageStore = getCurrentPageStore();
        const unsubscribe = currentPageStore.subscribe((state) => {
            const basePathKey = basePath(tree.relPath);
            if (state[basePathKey] !== undefined) {
                expanded = state[basePathKey];
            }
        });
        return unsubscribe; // Unsubscribe when the component unmounts
    });

    function updateExpansionForNode(node: FileNode, expand: boolean) {
        // Recursive function to update the expansion state for a node and its children
        // const currentPageStore = getCurrentPageStore();
        // Update the current node's expansion state
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
    }
    const currentPageStore = getCurrentPageStore();

    const collapseAll = () => {
        updateExpansionForNode(tree, false);
    };
    const expandRoot: () => void = () => {
        // const currentPageStore = getCurrentPageStore();
        currentPageStore.update((currentState) => {
            currentState[pageName()] = true;
            printFrontendMsg(" REL PATH " + tree.relPath);
            return currentState; //returns the currentState to currentPageStore
        });
    };

    const pageName: () => string = () => {
        switch (
            _appPage // Assuming _appPage holds the current page enum value
        ) {
            case AppPage.Vault:
                return "VAULT";
            case AppPage.NBox:
                return "N-BOX";
            case AppPage.OBox:
                return "O-BOX";
            default:
                throw new Error("Unrecognized page");
        }
    };

    function logFilePath(treeLabel: string) {
        getFilePath(treeLabel).then((filePath) => {
            printFrontendMsg(filePath.toString() + treeLabel);
        });
    }

    function isFile(node: FileNode) {
        getFilePath(basePath(tree.relPath)).then((filePath) => {
            getFileProperties(filePath + node.relPath).then((fileProps) => {
                printFrontendMsg(fileProps.fileSize.toString());
                return fileProps.fileSize > 0;
            });
        });
        return !node.children;
    }
</script>

<!-- class={basePath(tree.relPath) === pageName() ? "pl-0.5" : "pl-3"}
style={basePath(tree.relPath) === pageName()
    ? "margin-top: -22px;"
    : "margin-top: 1px;"} -->
<div>
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
                <button on:click={toggleExpansion} class="flex">
                    {#if !expanded}
                        <FolderSolid class="w-3 mr-1"></FolderSolid>
                    {:else}
                        <FolderOpenSolid class="w-3 mr-1"></FolderOpenSolid>
                    {/if}
                    {basePath(tree.relPath)}
                </button>
                {#if expanded}
                    <ul>
                        {#each tree.children as child}
                            <svelte:self tree={child} />
                        {/each}
                    </ul>
                {/if}
            {:else if isFile(tree)}
                <button
                    class="bg-gray-800"
                    on:click={() => logFilePath(tree.relPath)}
                >
                    {basePath(tree.relPath)}
                </button>
            {:else}
                <span class="flex">
                    <FolderSolid class="w-3 mr-1"></FolderSolid>
                    {basePath(tree.relPath)}
                </span>
            {/if}
        </li>
    </ul>
    <div id="dial" class="fixed">
        <SpeedDial
            defaultClass="fixed end-0"
            class="bg-gray-800 rounded-full bg-white"
        >
            <SpeedDialButton name="By Size " class="h-10 w-14">
                <DownloadSolid class="w-6 h-6" />
            </SpeedDialButton>
            <SpeedDialButton name="By Date " class="h-10 w-14">
                <FileCopySolid class="w-6 h-6" />
            </SpeedDialButton>
            <SpeedDialButton
                name="Collapse "
                class="h-10 w-14 right-10"
            >
                <ShareNodesSolid class="w-6 h-6" />
            </SpeedDialButton>
            <SpeedDialButton
                name="Expand "
                class="h-10 w-14 text-lg"
                on:click={expandRoot}
            >
                <PrinterOutline class="w-6 h-6" />
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
</style>
