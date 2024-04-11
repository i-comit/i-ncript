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

    import { darkLightTextOnClasses } from "../../tools/themes";

    import { heldDownBtns } from "../../tools/utils";
    import { LogInfo } from "../../../wailsjs/runtime/runtime";
    import { FileTypes, getFileType } from "../../enums/FileTypes";
    import {
        GetAppendedFileBytes,
        OpenDirectory,
        OpenFile,
    } from "../../../wailsjs/go/main/FileUtils";

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

    $: if (Object.keys($heldDownBtns).length > 0) {
        const entries = Object.entries($heldDownBtns);
        lastFilePath = entries[entries.length - 1][0];
        lastFile = printoutLastEntry(lastFilePath); // Make sure this function is defined or imported
    }

    function printoutLastEntry(lastEntryString: string): FileTypes {
        if (lastEntryString) {
            lastFilePath = getRootDir() + lastEntryString;
            return getFileType(lastFilePath);
            // GetAppendedFileBytes(lastFilePath).then(() => {
            //     LogInfo("Last file path Play FileTools" + lastFilePath);
            // });
        }
        return FileTypes.Unknown;
    }

    function openDirectory() {
        OpenDirectory(lastFilePath);
    }
</script>

{#if Object.keys($heldDownBtns).length > 0}
    <div id="file-tools" class="flex justify-end space-x-1">
        <button class="tool">
            <FolderArrowRightOutline />
        </button>
        {#if lastFile !== FileTypes.Encrypted && lastFile !== FileTypes.Unknown && lastFile !== FileTypes.EncryptedP}
            <button
                class="tool"
                on:click|stopPropagation={() => OpenFile(lastFilePath)}
            >
                <PlaySolid />
            </button>
        {/if}
        <button
            class="tool"
            on:click|stopPropagation={() => OpenDirectory(lastFilePath)}
        >
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
