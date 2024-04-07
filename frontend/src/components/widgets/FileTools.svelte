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

    // let lastEntry: [string, HTMLButtonElement] | undefined;
    var lastFile: FileTypes;

    // Reactive statement to update lastEntry whenever heldDownBtns changes
    $: {
        const entries = Object.entries($heldDownBtns);
        let lastEntry = entries[entries.length - 1];
        lastFile = printoutLastEntry(lastEntry);
    }

    function printoutLastEntry(
        lastEntry: [string, HTMLButtonElement],
    ): FileTypes {
        if (lastEntry) {
            let lastEntryFileType = getFileType(lastEntry[0]);
            LogInfo("key " + lastEntry[0] + " file " + lastEntryFileType);
            return lastEntryFileType;
        }
        return FileTypes.Unknown;
    }

    import { heldDownBtns } from "../../tools/utils";
    import { LogInfo } from "../../../wailsjs/runtime/runtime";
    import { FileTypes, getFileType } from "../../enums/FileTypes";
</script>

{#if Object.keys($heldDownBtns).length > 0}
    <div id="file-tools" class="flex justify-end space-x-1">
        <button>
            <FolderArrowRightOutline />
        </button>
        {#if lastFile !== FileTypes.Encrypted && lastFile !== FileTypes.Unknown}
            <button>
                <PlaySolid />
            </button>
        {/if}
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
