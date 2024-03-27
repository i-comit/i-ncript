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
    } from "flowbite-svelte-icons";
    import {
        logFrontendMessage,
        getFilePath,
        getFileProperties,
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
        const currentPageStore = getCurrentPageStore();
        const unsubscribe = currentPageStore.subscribe((state) => {
            const basePathKey = basePath(tree.relPath);
            if (state[basePathKey] !== undefined) {
                expanded = state[basePathKey];
            }
        });

        return unsubscribe; // Unsubscribe when the component unmounts
    });

    function basePath(path: string): string {
        const separator = path.includes("\\") ? "\\" : "/";
        return path.split(separator).pop() || path;
    }

    function logFilePath(treeLabel: string) {
        getFilePath(treeLabel).then((filePath) => {
            logFrontendMessage(filePath.toString() + treeLabel);
        });
    }

    function isFile(node: FileNode) {
        getFilePath(basePath(tree.relPath)).then((filePath) => {
            getFileProperties(filePath + node.relPath).then((fileProps) => {
                logFrontendMessage(fileProps.fileSize.toString());
                return fileProps.fileSize > 0;
            });
        });
        return !node.children;
    }
</script>

<div>
    <button
        class="z-20 fixed top-0 left-1/2 transform -translate-x-1/2 mt-0.5"
        style="--wails-draggable:drag"
    >
        <SearchOutline class="w-5 h-5" color="dark" />
    </button>

    <ul>
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
</div>

<style>
    ul {
        margin: 0px;
        list-style: none;
        padding-left: 0.4rem;
        user-select: none;
        background-color: gray;
        text-align: justify;
        font-size: small;
    }
    .hide-root {
        display: none;
    }
</style>
