<script lang="ts">
    import { onDestroy, onMount } from "svelte";
    import { get } from "svelte/store";
    import { MinimizeApp, CloseApp } from "../../../wailsjs/go/main/App";
    import { MinusOutline, CloseOutline } from "flowbite-svelte-icons";
    import { Tooltip } from "flowbite-svelte";

    import { tooltipTailwindClass } from "../../stores/constantVariables";

    import {
        LogDebug,
        LogError,
        LogInfo,
    } from "../../../wailsjs/runtime/runtime";
    import appLogoLight from "../../assets/images/appLogo_light.png";
    import appLogoDark from "../../assets/images/appLogo_dark.png";

    import { darkLightMode, filterInputs } from "../../stores/dynamicVariables";

    import { AppPage, currentPage } from "../../enums/AppPage";
    import { SaveFileFilters } from "../../../wailsjs/go/main/FileUtils";

    let minimizeBtn: HTMLButtonElement;
    let closeBtn: HTMLButtonElement;
    const appVersion: string = __APP_VERSION__;

    function minimizeApp(event: MouseEvent) {
        if (event.detail === 0) {
            // 0 for non-mouse clicks, 1 for mouse clicks
            return; // Ignore non-mouse clicks
        }
        MinimizeApp()
            .then(() => {
                LogDebug("Application minimized");
            })
            .catch((error) => {
                LogError("Failed to minimize application: " + error);
            });
    }

    function closeApp() {
        if (get(filterInputs) !== undefined)
            SaveFileFilters(get(filterInputs)).finally(() => CloseApp());
        else CloseApp();
    }
</script>

<div
    class="window-controls h-6 w-screen flex justify-between items-center py-0.5 px-0 rounded-lg z-40"
    style="--wails-draggable:drag;"
>
    <div class="flex justify-start h-full ml-1 select-none">
        {#if $darkLightMode}
            <img src={appLogoLight} alt="Description" class="p-0.5" />
        {:else}
            <img src={appLogoDark} alt="Description" class="p-0.5" />
        {/if}
        {#if $currentPage !== AppPage.Vault && $currentPage !== AppPage.Mbox}
            <Tooltip
                placement="right"
                class={tooltipTailwindClass}
                offset={1}
                arrow={false}>{appVersion}</Tooltip
            >
        {/if}
    </div>
    <div class="flex justify-end">
        <button
            bind:this={minimizeBtn}
            class="p-0 my-1 rounded-md bg-primary-300 dark:bg-primary-400"
            on:click|preventDefault={(event) => minimizeApp(event)}
        >
            <div class="text-primary-200 dark:text-primary-100">
                <MinusOutline id="icon" class="w-5 h-5 m-0 p-0" />
            </div>
        </button>
        <button
            bind:this={closeBtn}
            class="p-0 my-1 mr-1 ml-px rounded-md bg-primary-300 dark:bg-primary-400"
            on:click={closeApp}
        >
            <div class="text-primary-200 dark:text-primary-100">
                <CloseOutline class="w-5 h-5 m-0 p-0" />
            </div>
        </button>
    </div>
</div>

<style>
    .window-controls {
        position: absolute;
        top: 0px;
        right: 0px;
    }
</style>
