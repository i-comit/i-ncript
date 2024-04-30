<script lang="ts">
    import { onMount, onDestroy } from "svelte";

    import { Progressbar, Tooltip } from "flowbite-svelte";
    import {
        darkTextColor,
        lightTextColor,
        tooltipTailwindClass,
    } from "../../stores/constantVariables";
    import { darkLightMode, accentColor } from "../../stores/dynamicVariables";
    import { darkLightTextOnElement } from "../../tools/themes";
    import { alertInterval, getDisplayString } from "../../tools/logger";
    import {
        EventsOff,
        EventsOn,
        LogDebug,
        LogError,
    } from "../../../wailsjs/runtime/runtime";
    import {
        GetDiskSpacePercent,
        GetFormattedDiskSpace,
    } from "../../../wailsjs/go/main/Getters";
    import { pageIndex } from "../../tools/utils";

    let typewriter: HTMLElement;
    let displayString = "";

    let formattedTotalDriveSize: string;
    let formattedFreeDriveSize: string;
    let diskSpacePercent: number;

    onMount(() => {
        const interval = setInterval(() => {
            displayString = getDisplayString();
        }, alertInterval);
        refreshDirSize();
        EventsOn("refreshDirSize", () => {
            refreshDirSize();
        });
        darkLightTextOnElement(!$darkLightMode, typewriter);
    });
    onDestroy(() => {
        EventsOff("refreshDirSize");
    });

    function refreshDirSize() {
        LogDebug("refreshing dir size");
        let _pageIndex = pageIndex();
        GetFormattedDiskSpace(true)
            .then((totalDriveSize) => {
                formattedTotalDriveSize = totalDriveSize;
                return GetFormattedDiskSpace(false);
            })
            .then((freeDriveSize) => {
                formattedFreeDriveSize = freeDriveSize;
                return GetDiskSpacePercent();
            })
            .then((_diskSpacePercent) => {
                diskSpacePercent = _diskSpacePercent;
            })
            .catch((error) => {
                LogError("An error occurred:" + error);
            });
    }
</script>

<div class="h-6 select-none" style="--wails-draggable: drag;">
    {#if displayString !== ""}
        <div
            class="mb-1 h-6 leading-none absolute top-0 left-4 z-10 w-1/3 ml-3 mt-0"
        >
            {#if $darkLightMode}
                <p
                    class="relative top-1 text-left leading-none"
                    style={`color:${lightTextColor}`}
                >
                    {displayString}
                </p>
            {:else}
                <p
                    class="relative top-1.5 text-left leading-none"
                    style={`color:${darkTextColor}`}
                >
                    {displayString}
                </p>
            {/if}
        </div>
    {:else if diskSpacePercent}
        <div
            class="absolute top-px left-4 w-1/3 ml-3 mt-1.5 rounded-full h-2.5
                    bg-primary-300 dark:bg-primary-400 outline outline-1 hover:outline-2"
            style={`outline-color:${$accentColor}; z-index:42`}
        >
            <div
                class="h-2.5 rounded-full"
                style={`width: ${diskSpacePercent}%; background-color: ${$accentColor}`}
            ></div>
        </div>
        <Tooltip
            placement="bottom"
            type="custom"
            class={tooltipTailwindClass}
            arrow={false}
            offset={1}
            >FREE:{formattedFreeDriveSize}-{formattedTotalDriveSize} | {diskSpacePercent}%</Tooltip
        >
    {/if}
</div>

<style>
    p {
        --text-color: #757575;
        color: var(--text-color);
    }
</style>
