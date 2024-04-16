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
    } from "../../../wailsjs/runtime/runtime";
    import {
        GetFormattedDirIndexSize,
        GetFormattedDriveSize,
        GetPercentOfDriveToDirIndexSize,
    } from "../../../wailsjs/go/main/Getters";
    import { pageIndex } from "../../tools/utils";

    let typewriter: HTMLElement;
    let displayString = "";

    let driveToDirIndexPercent: number;
    let formattedDriveSize: string;
    let formattedDirIndexSize: string;

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
        GetPercentOfDriveToDirIndexSize(_pageIndex)
            .then((_driveToDirIndexPercent) => {
                driveToDirIndexPercent = _driveToDirIndexPercent;
                return GetFormattedDriveSize();
            })
            .then((_formattedDriveSize) => {
                formattedDriveSize = _formattedDriveSize;
                return GetFormattedDirIndexSize(_pageIndex);
            })
            .then((_formattedDirIndexSize) => {
                formattedDirIndexSize = _formattedDirIndexSize;
            })
            .catch((error) => {
                console.error("An error occurred:", error);
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
                    class="relative top-1.5 text-left leading-none"
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
    {:else}
        <div
            class="absolute top-0 left-4 w-1/3 ml-3 mt-1.5 rounded-full h-2.5
                    bg-primary-400 dark:bg-primary-300 outline outline-1 hover:outline-2"
            style={`outline-color:${$accentColor}; z-index:42`}
        >
            <div
                class="h-2.5 rounded-full"
                style={`width: ${driveToDirIndexPercent}%; background-color: ${$accentColor}`}
            ></div>
        </div>
        <Tooltip
            placement="bottom"
            type="custom"
            class={tooltipTailwindClass}
            arrow={false}
            offset={1}
            >{formattedDirIndexSize} / {formattedDriveSize} | {driveToDirIndexPercent}%</Tooltip
        >
    {/if}
</div>

<style>
    p {
        --text-color: #757575;
        color: var(--text-color);
    }
</style>
