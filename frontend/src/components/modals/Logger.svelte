<script lang="ts">
    import { onMount, afterUpdate, onDestroy } from "svelte";
    import { get } from "svelte/store";

    import { SpeedDial, SpeedDialButton, Tooltip } from "flowbite-svelte";
    import { DownloadSolid, FileCopySolid } from "flowbite-svelte-icons";
    import { Modals, currentModal } from "../../enums/Modals.ts";

    import {
        EventsOff,
        EventsOn,
        LogDebug,
        LogInfo,
    } from "../../../wailsjs/runtime/runtime";

    import {
        lightBGColor,
        darkBGColor,
        tooltipTailwindClass,
    } from "../../stores/constantVariables.ts";
    import {
        darkLightMode,
        accentColor,
    } from "../../stores/dynamicVariables.ts";

    import {
        formatTime,
        getEntryKeyword,
        logEntries,
    } from "../../tools/logger";
    import { SaveLogEntries } from "../../../wailsjs/go/main/FileUtils";

    let logContainer: { scrollTop: any; scrollHeight: any };
    let isPointerIn: boolean = false;

    onMount(() => {
        logContainer.scrollTop = logContainer.scrollHeight; // Scroll to the bottom
        EventsOn("refreshDirSize", () => {
            LogInfo("Filect reset " + isPointerIn);
            if (!isPointerIn) {
                LogInfo("pointer change page modal");
                setTimeout(() => {
                    currentModal.set(Modals.None);
                }, 100);
            }
            EventsOff("refreshDirSize");
        });
    });
    onDestroy(() => {
        EventsOff("refreshDirSize");
    });
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
    class="fixed w-full h-full rounded-lg"
    id="grid-dot-bg"
    style=" z-index:5;"
    on:pointerover={() => {
        isPointerIn = true;
    }}
    on:pointerleave={() => {
        isPointerIn = false;
    }}
    on:pointerdown={() => {
        isPointerIn = true;
    }}
/>
<div
    class="mb-0.5 w-1/3 left-1/3 rounded-bl-lg rounded-br-lg font-semibold z-10"
    style={`position: sticky; top: 0px; height: 1.45rem;
        background-color: ${$darkLightMode ? lightBGColor : darkBGColor}; 
        color: ${$accentColor};`}
>
    LOGGER
</div>
<div id="dial" class="fixed">
    <SpeedDial
        class="flex items-center justify-center h-8 w-14"
        popperDefaultClass="flex items-center !mb-0 gap-0.5"
        style={`background-color: ${$accentColor}; border-radius: 50% 0% 50% 0%;`}
    >
        <SpeedDialButton
            name="Save"
            class="h-10 w-14"
            on:click={saveLogEntries}
        >
            <DownloadSolid class="w-6 h-6" />
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
<div
    bind:this={logContainer}
    class="log-entries-container"
    on:pointerover={() => {
        isPointerIn = true;
        LogDebug("entered log entries");
    }}
    on:pointerleave={() => {
        isPointerIn = false;
        LogDebug("left log entries");
    }}
    on:pointerdown={() => {
        isPointerIn = true;
    }}
>
    {#each $logEntries as { entry, timestamp }}
        <div
            style={`color: ${get(darkLightMode) ? lightBGColor : darkBGColor}`}
            class="log-entry text-xs truncate !hover:text-sky-400"
        >
            {getEntryKeyword(entry)}
        </div>
        <Tooltip class={tooltipTailwindClass} offset={-1} arrow={true}
            >{formatTime(timestamp)}</Tooltip
        >
        {#if $darkLightMode}
            <div
                class="divider div-transparent"
                style={`--bg-color: ${lightBGColor};`}
            ></div>
        {:else}
            <div
                class="divider div-transparent"
                style={`--bg-color: ${darkBGColor};`}
            ></div>
        {/if}
    {/each}
</div>

<style>
    #grid-dot-bg {
        /* https://codepen.io/mapsandapps/pen/pbzooY */
        background-image: radial-gradient(rgb(180, 180, 180) 5%, transparent 0);
        background-size: 15px 15px;
    }
    #dial {
        right: -0.7rem !important;
        bottom: -14vh !important;
        transform: scale(0.55) !important;
        z-index: 35;
    }
    .log-entries-container {
        z-index: 8;
        user-select: none;
        position: absolute;
        bottom: 0;
        width: 95%;
        max-height: 100%;
        overflow-x: hidden;
        overflow-y: auto;
        white-space: nowrap;
        padding-left: 0.2rem;
        text-align: justify;
        box-sizing: border-box;
        scrollbar-width: none;
        /* For Firefox */
        -ms-overflow-style: none;
        scroll-behavior: smooth;
    }

    .log-entry {
        white-space: normal;
        min-width: max-content;
    }

    .divider {
        position: relative;
        margin-bottom: 2px;
    }

    .div-transparent:before {
        content: "";
        position: absolute;
        top: 0;
        left: 5%;
        right: 5%;
        width: 95%;
        height: 1px;
        background-image: linear-gradient(
            to right,
            transparent,
            var(--bg-color, #000),
            transparent
        );
    }
</style>
