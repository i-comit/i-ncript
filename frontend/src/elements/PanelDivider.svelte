<script lang="ts">
    import { onMount } from "svelte";
    import { get } from "svelte/store";

    import {
        accentColor,
        activeAccentColor,
        darkBGColor,
        lightBGColor,
    } from "../stores/constantVariables";

    import {
        CogSolid,
        BarsFromLeftOutline,
        InfoCircleSolid,
        InfoCircleOutline,
    } from "flowbite-svelte-icons";
    import { switchModals, toggleDarkLightMode } from "../tools/utils";
    import { Modals } from "../enums/Modals";
    import { LogInfo } from "../../wailsjs/runtime/runtime";
    import { darklightMode } from "../stores/dynamicVariables";

    darklightMode.subscribe((value) => {
        toggleDarkLightMode(value);
    });

    onMount(() => {
        document.documentElement.style.setProperty("--icon-color", _color);
        toggleDarkLightMode(get(darklightMode));
    });

    let _color: string = darkBGColor;
</script>

<div
    class="absolute -z-10 w-20 h-full select-none left-1/3"
    style="background-color:{accentColor}"
></div>
<div class="panel">
    <div
        class="absolute w-full h-1/5 rounded-full select-none"
        style={`top:90%; background-color: ${accentColor}`}
    ></div>

    <div class="icon space-y-1">
        <div class="icon__home">
            <InfoCircleOutline
                class="p-px"
                on:click={() => switchModals(Modals.Info)}
            />
        </div>
        <button
            class="icon__account"
            on:click={() => switchModals(Modals.Logger)}
        >
            <BarsFromLeftOutline class="p-px" role="button" />
        </button>
        <button
            class="icon__settings"
            on:click={() => switchModals(Modals.Settings)}
        >
            <CogSolid class="p-px" />
        </button>
    </div>
    <div
        class="absolute w-full h-1/5 rounded-full select-none"
        style={`bottom:90%; background-color: ${accentColor}`}
    ></div>
</div>

<style lang="scss">
    $panel-width: 1.65rem;
    @import "../neumorphic.scss";
    :root {
        --bg-color: #757575;
        --icon: #757575;
        --icon-hover: #8abdff;
        --icon-active: #a0b0ff;
    }

    .panel {
        background-color: var(--bg-color);
        width: $panel-width;
        display: flex;
        justify-content: center; /* Centers horizontally */
        align-items: center; /* Centers vertically */
        height: 65%;
        top: 20%;
        position: relative; // Ensure .inverted-border-radius positions relative to .panel
    }

    $icon-size: 1.2rem;

    $dropShadow:
        2px 2px 2px -2px rgba($darkShadow, 0.4),
        -2px -3px 3px -1px rgba($lightShadow, 1);

    $innerShadow:
        inset -2px -2px 2px -1px rgba($lightShadow, 1),
        inset 1px 1px 2px -1px rgba($darkShadow, 0.5);

    /*  ICONS  */
    .icon {
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        &__account,
        &__home,
        &__settings {
            width: $icon-size;
            height: $icon-size;
            border-radius: 50%;
            box-shadow: $dropShadow;
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 2rem;
            cursor: pointer;
            color: var(--icon);
            transition: all 0.2s ease;
            &:active {
                box-shadow: $innerShadow;
                color: var(--icon-active);
            }
            &:hover {
                color: var(--icon-hover);
            }
        }
    }
</style>
