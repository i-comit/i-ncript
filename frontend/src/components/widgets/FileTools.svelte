<script lang="ts">
    import {
        FolderArrowRightOutline,
        FolderOpenSolid,
        PlaySolid,
        BookOpenOutline,
    } from "flowbite-svelte-icons";
    import { darkLightMode } from "../../stores/dynamicVariables";
    import { getRootDir } from "../../tools/utils";

    import { darkLightTextOnClasses } from "../../tools/themes";

    import { heldDownBtns } from "../../tools/utils";
    import { LogInfo } from "../../../wailsjs/runtime/runtime";
    import { FileTypes, getFileType } from "../../enums/FileTypes";
    import {
        OpenDirectory,
        OpenFile,
        OpenLogEntriesFile,
    } from "../../../wailsjs/go/main/FileUtils";
    import {
        lightTextColor,
        darkTextColor,
    } from "../../stores/constantVariables";
    import { Modals, currentModal } from "../../enums/Modals";

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
        }
        return FileTypes.Unknown;
    }
</script>

{#if $currentModal !== Modals.Logger}
    {#if Object.keys($heldDownBtns).length > 0}
        <div id="file-tools" class="flex justify-end space-x-1">
            {#if lastFile !== FileTypes.Encrypted && lastFile !== FileTypes.Unknown && lastFile !== FileTypes.EncryptedP}
                <button
                    style={`--text-color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                    on:click|stopPropagation={() => OpenFile(lastFilePath)}
                >
                    <PlaySolid />
                </button>
            {/if}
            <button
                style={`--text-color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                on:click|stopPropagation={() => OpenDirectory(lastFilePath)}
            >
                <FolderOpenSolid />
            </button>
        </div>
    {/if}
{:else}
    <div id="file-tools" class="flex justify-end space-x-1">
        <button
            style={`--text-color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
            on:pointerdown|stopPropagation={OpenLogEntriesFile}
        >
            <BookOpenOutline />
        </button>
    </div>
{/if}

<style>
    button {
        color: var(--text-color);
    }
</style>
