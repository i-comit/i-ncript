<script lang="ts">
    import { onDestroy, onMount } from "svelte";
    import { get } from "svelte/store";

    import {
        FolderArrowRightOutline,
        FolderOpenSolid,
        PlaySolid,
    } from "flowbite-svelte-icons";
    import { darkLightMode } from "../../stores/dynamicVariables";
    import { darkLightTextInContainer, getRootDir } from "../../tools/utils";

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

    var lastFile: FileTypes;
    var lastFilePath: string;
    // Reactive statement to update lastEntry whenever heldDownBtns changes
    // $: {
    //     const entries = Object.entries($heldDownBtns);
    //     lastFilePath = entries[entries.length - 1][0];
    //     printoutLastEntry2(lastFilePath);
    // }

    function getLastEntryPath() {
        const entries = Object.entries($heldDownBtns);
        lastFilePath = entries[entries.length - 1][0];
        printoutLastEntry(lastFilePath);
    }

    function printoutLastEntry(lastEntryString: string) {
        if (lastEntryString) {
            lastFilePath = getRootDir() + lastEntryString;
            GetAppendedFileBytes(lastFilePath).then(() => {
                LogInfo("Last file path Play FileTools" + lastFilePath);
            });
        }
    }

    import { heldDownBtns } from "../../tools/utils";
    import { LogInfo } from "../../../wailsjs/runtime/runtime";
    import { FileTypes, getFileType } from "../../enums/FileTypes";
    import { GetAppendedFileBytes } from "../../../wailsjs/go/main/FileUtils";
</script>

{#if Object.keys($heldDownBtns).length > 0}
    <div id="file-tools" class="flex justify-end space-x-1">
        <button>
            <FolderArrowRightOutline />
        </button>
        {#if lastFile !== FileTypes.Encrypted && lastFile !== FileTypes.Unknown}
            <button on:click|stopPropagation={getLastEntryPath}>
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
