<script lang="ts">
    import { SpeedDial, SpeedDialButton } from "flowbite-svelte";
    import {
        ShareNodesSolid,
        PrinterOutline,
        DownloadSolid,
        FileCopySolid,
    } from "flowbite-svelte-icons";

    import { writable } from "svelte/store";
    import { addLogEntry, logEntries } from "../tools/logger";
</script>

<div>
    <div id="dial" class="fixed">
        <SpeedDial
            defaultClass="absolute end-0"
            class="bg-gray-800 rounded-full !bg-opacity-15"
        >
            <SpeedDialButton name="Share " class="h-12 w-14 right-10">
                <ShareNodesSolid class="w-6 h-6" />
            </SpeedDialButton>
            <SpeedDialButton name="Errors " class="h-12 w-14 text-lg">
                <PrinterOutline class="w-6 h-6" />
            </SpeedDialButton>
            <SpeedDialButton name="Warnings " class="h-12 w-14">
                <DownloadSolid class="w-6 h-6" />
            </SpeedDialButton>
            <SpeedDialButton name="Export " class="h-12 w-14">
                <FileCopySolid class="w-6 h-6" />
            </SpeedDialButton>
        </SpeedDial>
    </div>
    <div class="log-entries-container">
        {#each $logEntries as entry}
            <div class="log-entry text-sm">{entry}</div>
        {/each}
    </div>
</div>

<style>
    #dial {
        right: -12px !important;
        bottom: 12vh !important;
        /* Ensure the scale transform is applied as you want it */
        transform: scale(0.55) !important;
        z-index: 5;
        /* Add any additional styles needed to ensure it's positioned as desired */
    }
    .log-entries-container {
        position: absolute;
        bottom: 0;
        width: 100%; /* Adjust if necessary */
        max-height: 200px; /* Example maximum height; adjust as needed */
        overflow-x: hidden; /* Enables horizontal scrolling */
        overflow-y: auto; /* Enables vertical scrolling */
        white-space: nowrap; /* Keeps log entries in a single line for horizontal scrolling */
        padding-left: 1rem; /* Matches your existing styling */
        text-align: justify;
        box-sizing: border-box;
        scrollbar-width: none;
        /* For Firefox */
        -ms-overflow-style: none;
    }
    .log-entry {
        white-space: normal; /* Ensures text within each entry wraps properly */
        min-width: max-content; /* Ensures content dictates container width, allowing for horizontal scroll */
    }
</style>
