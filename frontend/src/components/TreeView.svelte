<script context="module">
    // retain module scoped expansion state for each tree node
    const _expansionState = {
        /* treeNodeId: expanded <boolean> */
    };
</script>

<script>
    // import { slide } from 'svelte/transition'
    import { logFrontendMessage, getFilePath } from "../utils.ts";
    export let tree;
    const { label, children } = tree;

    let expanded = false;
    const toggleExpansion = () => {
        expanded = !expanded;
    };

    function logFilePath(treeLabel) {
        getFilePath(treeLabel).then((filePath) => {
            logFrontendMessage(filePath.toString() + treeLabel);
        });
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
                <!-- Apply the class:arrowDown directive here -->
                <span class="arrow" class:arrowDown={expanded}>&#x25b6;</span>
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
            <button
                class="bg-gray-800"
                on:click={() => logFilePath(tree.label)}
            >
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
