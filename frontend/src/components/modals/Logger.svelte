<script lang="ts">
    import { onMount, afterUpdate, onDestroy } from "svelte";
    import { SpeedDial, SpeedDialButton, Tooltip } from "flowbite-svelte";
    import {
        PrinterOutline,
        DownloadSolid,
        FileCopySolid,
    } from "flowbite-svelte-icons";

    import {
        addLogEntry,
        formatTime,
        getEntryKeyword,
        logEntries,
    } from "../../tools/logger";
    import { get, writable } from "svelte/store";
    import {
        lightBGColor,
        darkBGColor,
        darkTextColor,
        lightTextColor,
        tooltipTailwindClass,
    } from "../../stores/constantVariables.ts";
    import { darkLightMode } from "../../stores/dynamicVariables.ts";
    import { SaveLogEntries } from "../../../wailsjs/go/main/FileUtils";

    let logContainer: { scrollTop: any; scrollHeight: any }; // Reference to the log entries container element

    afterUpdate(() => {
        if (logContainer) {
            logContainer.scrollTop = logContainer.scrollHeight; // Scroll to the bottom
        }
    });

    function clearLogEntry() {
        logEntries.set([]);
    }

    function saveLogEntries() {
        const logs = get(logEntries); // Directly use the store's value if this function is called reactively.
        const entries = logs.map((log) => log.entry);
        const timestamps = logs.map((log) => log.timestamp);
        SaveLogEntries(entries, timestamps);
    }
</script>

<div
    class="mb-0.5 w-1/3 left-1/3 rounded-bl-lg rounded-br-lg z-10"
    style={`position: sticky; top: 0px; height: 1.4rem;
        background-color: ${$darkLightMode ? lightBGColor : darkBGColor}; 
        color: ${$darkLightMode ? darkTextColor : lightTextColor};`}
>
    LOGGER
</div>
<div>
    <div id="dial" class="fixed">
        <SpeedDial
            class="flex items-center justify-center bg-gray-600 !rounded-br-xl !rounded-tl-3xl h-8 w-14"
            popperDefaultClass="flex items-center !mb-0 gap-0.5"
        >
            <SpeedDialButton name="Warnings " class="h-10 w-14">
                <DownloadSolid class="w-6 h-6" />
            </SpeedDialButton>
            <SpeedDialButton
                name="Save"
                class="h-10 w-14"
                on:click={saveLogEntries}
            >
                <FileCopySolid class="w-6 h-6" />
            </SpeedDialButton>
            <SpeedDialButton
                name="Clear"
                class="h-10 w-14"
                on:click={clearLogEntry}
            >
                <FileCopySolid class="w-6 h-6" />
            </SpeedDialButton>
        </SpeedDial>
    </div>
    <div bind:this={logContainer} class="log-entries-container">
        {#each $logEntries as { entry, timestamp }}
            <div
                class="log-entry text-xs whitespace-nowrap overflow-hidden overflow-ellipsis text-gray-500"
                style={`color: ${get(darkLightMode) ? lightBGColor : darkBGColor}`}
            >
                {getEntryKeyword(entry)}
            </div>
            <Tooltip class={tooltipTailwindClass} offset={-1} arrow={true}
                >{formatTime(timestamp)}</Tooltip
            >
        {/each}
    </div>
</div>

<style>
    #dial {
        right: -0.7rem !important;
        bottom: -8vh !important;
        transform: scale(0.55) !important;
        z-index: 35;
        /* Add any additional styles needed to ensure it's positioned as desired */
    }
    .log-entries-container {
        position: absolute;
        bottom: 0;
        width: 100%; /* Adjust if necessary */
        max-height: 100%; /* Example maximum height; adjust as needed */
        overflow-x: hidden; /* Enables horizontal scrolling */
        overflow-y: auto; /* Enables vertical scrolling */
        white-space: nowrap; /* Keeps log entries in a single line for horizontal scrolling */
        padding-left: 1rem; /* Matches your existing styling */
        text-align: justify;
        box-sizing: border-box;
        scrollbar-width: none;
        /* background-color: antiquewhite !important; */
        /* For Firefox */
        -ms-overflow-style: none;
        scroll-behavior: smooth; /* Enables smooth scrolling */
    }

    .log-entry {
        white-space: normal; /* Ensures text within each entry wraps properly */
        min-width: max-content; /* Ensures content dictates container width, allowing for horizontal scroll */
    }
</style>
