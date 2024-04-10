<script lang="ts">
    import { onDestroy, onMount } from "svelte";
    import { get } from "svelte/store";

    import {
        FolderArrowRightOutline,
        FolderOpenSolid,
        PlaySolid,
    } from "flowbite-svelte-icons";
    import { darkLightMode } from "../../stores/dynamicVariables";
    import { getRootDir } from "../../tools/utils";

    import {
        darkLightTextOnClasses,
    } from "../../tools/themes";

    import { heldDownBtns } from "../../tools/utils";
    import { LogInfo } from "../../../wailsjs/runtime/runtime";
    import { FileTypes, getFileType } from "../../enums/FileTypes";
    import { GetAppendedFileBytes } from "../../../wailsjs/go/main/FileUtils";

    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightTextOnClasses(!value, "tool");
    });

    onMount(() => {
        var _value = get(darkLightMode);
        darkLightTextOnClasses(!_value, "tool");
    });
    onDestroy(() => {
        unsub_darkLightMode();
    });

    var lastFile: FileTypes;
    var lastFilePath: string;

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
</script>

{#if Object.keys($heldDownBtns).length > 0}
    <div id="file-tools" class="flex justify-end space-x-1">
        <button class="tool">
            <FolderArrowRightOutline />
        </button>
        {#if lastFile !== FileTypes.Encrypted && lastFile !== FileTypes.Unknown}
            <button class="tool" on:click|stopPropagation={getLastEntryPath}>
                <PlaySolid />
            </button>
        {/if}
        <button class="tool">
            <FolderOpenSolid />
        </button>
    </div>
{/if}

<style>
    .tool {
        --text-color: #757575;
        color: var(--text-color);
    }
</style>
