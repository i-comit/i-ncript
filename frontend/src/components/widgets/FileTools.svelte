<script lang="ts">
    import {
        FolderOpenSolid,
        PlaySolid,
        BookOpenSolid,
    } from "flowbite-svelte-icons";
    import { darkLightMode } from "../../stores/dynamicVariables";
    import { getRootDir } from "../../tools/utils";


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

    let fileToolIconClass =
        "drop-shadow-[0_1.2px_1.2px_rgba(0,0,0,0.8)] hover:drop-shadow-[0_1.2px_1.2px_rgba(220,220,220,1)]";
</script>

{#if $currentModal !== Modals.Logger}
    {#if Object.keys($heldDownBtns).length > 0}
        <div id="file-tools" class="flex justify-end space-x-0.5">
            {#if lastFile !== FileTypes.Encrypted && lastFile !== FileTypes.Unknown && lastFile !== FileTypes.EncryptedP}
                <button
                    style={`--text-color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                    tabindex={-1}
                    on:click|stopPropagation={() => OpenFile(lastFilePath)}
                >
                    <PlaySolid size="lg" class={fileToolIconClass} />
                </button>
            {/if}
            <button
                style={`--text-color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                tabindex={-1}
                on:click|stopPropagation={() => OpenDirectory(lastFilePath)}
            >
                <FolderOpenSolid size="lg" class={fileToolIconClass} />
            </button>
        </div>
    {/if}
{:else}
    <div id="file-tools" class="flex justify-end space-x-1">
        <button
            style={`--text-color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
            tabindex={-1}
            on:click|stopPropagation={OpenLogEntriesFile}
        >
            <BookOpenSolid size="lg" class={fileToolIconClass} />
        </button>
    </div>
{/if}

<style>
    button {
        color: var(--text-color);
    }
</style>
