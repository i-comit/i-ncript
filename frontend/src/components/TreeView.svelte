<script context="module">
    // retain module scoped expansion state for each tree node
    const _expansionState = {
        /* treeNodeId: expanded <boolean> */
    };
</script>

<script>
    //	import { slide } from 'svelte/transition'
    import { logFrontendMessage } from "../utils.ts";
    export let tree;
    const { label, children } = tree;

    let expanded = _expansionState[label] || false;
    const toggleExpansion = () => {
        expanded = _expansionState[label] = !expanded;
    };
    $: arrowDown = expanded;
    function getFullPath(node) {
        // Assuming each node has a 'path' or you have a method to construct it
        return `Full path for ${node.label}`;
    }

    function logFilePath(node) {
        const fullPath = getFullPath(node);
        logFrontendMessage(fullPath.toString());
    }

    function isFile(node) {
        return (
            !node.children || (node.children.length === 0 && node.size === 0)
        );
    }
</script>

<ul>
    <li>
        {#if tree.children && tree.children.length > 0}
            <span on:click={toggleExpansion}>
                <span class="arrow" class:expanded>&#x25b6;</span>
                {tree.label}
            </span>
            {#if expanded}
                <ul>
                    {#each tree.children as child}
                        <svelte:self tree={child} />
                    {/each}
                </ul>
            {/if}
        {:else if isFile(tree)}
            <!-- Render as a button for files (including empty folders as per your criteria) -->
            <button class="bg-gray-800" on:click={() => logFilePath(tree)}>
                {tree.label}
            </button>
        {:else}
            <!-- This case handles empty folders that are not considered files by your criteria -->
            <span>
                <span class="no-arrow"></span>
                {tree.label}
            </span>
        {/if}
    </li>
</ul>

<style>
    ul {
        margin: 0;
        list-style: none;
        padding-left: 0.5rem;
        user-select: none;
        background-color: gray;
        text-align: justify;
        font-size: small;
    }
    .no-arrow {
        padding-left: 1rem;
    }
    .arrow {
        cursor: pointer;
        display: inline-block;
        /* transition: transform 200ms; */
    }
    .arrowDown {
        transform: rotate(90deg);
    }
</style>
