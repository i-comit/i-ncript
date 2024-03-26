<script context="module">
    // retain module scoped expansion state for each tree node
    const _expansionState = {
        /* treeNodeId: expanded <boolean> */
    };
</script>

<script lang="ts">
    // import { slide } from 'svelte/transition'
    import { onMount } from "svelte";

    import { FolderOpenSolid, FolderSolid } from "flowbite-svelte-icons";
    import {
        logFrontendMessage,
        getFilePath,
        getFileProperties,
    } from "../utils.ts";
    interface FileNode {
        relPath: string;
        label: string;
        children?: FileNode[]; // Make children optional to match the Go structure
    }
    export let tree: FileNode;

    let expanded = false;
    const toggleExpansion = () => {
        expanded = !expanded;
    };

    function logFilePath(treeLabel: string) {
        getFilePath(treeLabel).then((filePath) => {
            logFrontendMessage(filePath.toString() + treeLabel);
        });
    }

    function isFile(node: FileNode) {
        getFilePath(node.label).then((filePath) => {
            getFileProperties(filePath + node.relPath).then((fileProps) => {
                logFrontendMessage(fileProps.fileSize.toString());
                return fileProps.fileSize > 0;
            });
        });
        return !node.children;
    }
</script>

<ul>
    <li>
        {#if tree.children && tree.children.length > 0}
            <button on:click={toggleExpansion} class="flex">
                {#if !expanded}
                    <FolderSolid class="w-3 mr-1"></FolderSolid>
                {:else}
                    <FolderOpenSolid class="w-3 mr-1"></FolderOpenSolid>
                {/if}
                {tree.label}
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
                {tree.label}
            </button>
        {:else}
            <span class="flex">
                <FolderSolid class="w-3 mr-1"></FolderSolid>
                {tree.label}
            </span>
        {/if}
    </li>
</ul>

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
