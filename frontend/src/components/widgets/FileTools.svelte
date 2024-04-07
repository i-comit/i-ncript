<script lang="ts">
    import { onDestroy, onMount } from "svelte";
    import { get } from "svelte/store";

    import {
        FolderArrowRightOutline,
        FolderOpenSolid,
        PlaySolid,
    } from "flowbite-svelte-icons";
    import { darkLightMode } from "../../stores/dynamicVariables";
    import { darkLightTextInContainer } from "../../tools/utils";

    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightTextInContainer(!value, "file-tools", "button");
    });

    onMount(() => {
        var _value = get(darkLightMode);
        darkLightTextInContainer(!_value, "file-tools", "button");
    });
    onDestroy(() => {
        unsub_darkLightMode();
    });

    let lastEntry: [string, HTMLButtonElement] | undefined;

    // Reactive statement to update lastEntry whenever heldDownBtns changes
    $: {
        const entries = Object.entries($heldDownBtns);
        lastEntry = entries[entries.length - 1];
        printoutLastEntry();
    }

    function printoutLastEntry() {
        if (lastEntry) {
            var lastEntryFileType = getFileType(lastEntry[0]);
            LogInfo("key " + lastEntry[0] + " file " + lastEntryFileType);
        }
    }

    import { heldDownBtns } from "../../tools/utils";
    import { LogInfo } from "../../../wailsjs/runtime/runtime";
    import { getFileType } from "../../enums/FileTypes";
</script>

{#if Object.keys($heldDownBtns).length > 0}
    <div id="file-tools" class="flex justify-end space-x-1">
        <button>
            <FolderArrowRightOutline />
        </button>
        <button>
            <PlaySolid />
        </button>
        <button>
            <FolderOpenSolid />
        </button>
    </div>
{/if}

<style>
    button {
        --text-color: #757575;
        color: var(--text-color);
    }
</style>
