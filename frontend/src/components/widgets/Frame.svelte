<script lang="ts">
    import { onDestroy, onMount } from "svelte";
    import { get } from "svelte/store";
    import { MinimizeApp, CloseApp } from "../../../wailsjs/go/main/App";
    import { MinusOutline, CloseOutline } from "flowbite-svelte-icons";
    import { Button, Input, GradientButton, Tooltip } from "flowbite-svelte";

    import {
        lightBGColor,
        darkBGColor,
        tooltipTailwindClass,
    } from "../../stores/constantVariables";

    import {
        LogDebug,
        LogError,
        LogInfo,
    } from "../../../wailsjs/runtime/runtime";
    import appLogoLight from "../../assets/images/appLogo_light.png";
    import appLogoDark from "../../assets/images/appLogo_dark.png";

    import { darkLightMode } from "../../stores/dynamicVariables";

    import {
        darkLightBGOnElement,
        darkLightTextOnId,
    } from "../../tools/themes";
    import { AppPage, currentPage } from "../../enums/AppPage";

    let minimizeBtn: HTMLButtonElement;
    let closeBtn: HTMLButtonElement;
    const appVersion: string = __APP_VERSION__;

    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightBGOnElement(!value, minimizeBtn);
        darkLightBGOnElement(!value, closeBtn);
        darkLightTextOnId(value, "minimize-icon");
        darkLightTextOnId(value, "close-icon");
    });
    onMount(() => {
        var _value = get(darkLightMode);
        darkLightBGOnElement(!_value, minimizeBtn);
        darkLightBGOnElement(!_value, closeBtn);
        darkLightTextOnId(_value, "minimize-icon");
        darkLightTextOnId(_value, "close-icon");
    });
    onDestroy(() => {
        unsub_darkLightMode();
    });

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
            class="p-0 my-1 rounded-md"
            on:click|preventDefault={(event) => minimizeApp(event)}
        >
            <div id="minimize-icon">
                <MinusOutline id="icon" class="w-5 h-5 m-0 p-0" />
            </div>
        </button>
        <button
            bind:this={closeBtn}
            class="p-0 my-1 mr-1 ml-px rounded-md"
            on:click={CloseApp}
        >
            <div id="close-icon">
                <CloseOutline class="w-5 h-5 m-0 p-0" />
            </div>
        </button>
    </div>
</div>

<style>
    #minimize-icon,
    #close-icon {
        --text-color: #dddddd;
        color: var(--text-color);
    }

    button {
        --bg-color: #757575;
        background-color: var(--bg-color);
    }

    .window-controls {
        position: absolute;
        top: 0px;
        right: 0px;
    }
</style>
