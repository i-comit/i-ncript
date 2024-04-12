<script lang="ts">
    import { onMount, onDestroy } from "svelte";

    import { Progressbar, Tooltip } from "flowbite-svelte";
    import {
        darkTextColor,
        lightTextColor,
        tooltipTailwindClass,
    } from "../../stores/constantVariables";
    import { alertInterval, getDisplayString } from "../../tools/logger";
    import { darkLightMode } from "../../stores/dynamicVariables";
    import { darkLightTextOnElement } from "../../tools/themes";

    let typewriter: HTMLElement;
    let displayString = "";

    onMount(() => {
        const interval = setInterval(() => {
            displayString = getDisplayString();
        }, alertInterval);
        darkLightTextOnElement(!$darkLightMode, typewriter);
    });
</script>

<div class="h-6 select-none pointer-events-none">
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
            class="absolute top-0 left-4 z-10 w-1/3 ml-3 mt-1.5 rounded-full h-2.5 bg-primary-400 dark:bg-primary-300"
        >
            <div
                class="h-2.5 rounded-full bg-primary-500"
                style={`width: ${65}%`}
            ></div>
        </div>
        <Tooltip class={tooltipTailwindClass} offset={1}></Tooltip>
    {/if}
</div>

<style>
    p {
        --text-color: #757575;
        color: var(--text-color);
    }
</style>
