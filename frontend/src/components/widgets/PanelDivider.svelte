<script lang="ts">
    import { onMount, onDestroy } from "svelte";
    import { get } from "svelte/store";
    import {
        CogSolid,
        BarsFromLeftOutline,
        InfoCircleSolid,
        InfoCircleOutline,
    } from "flowbite-svelte-icons";

    import { LogInfo } from "../../../wailsjs/runtime/runtime";

    import { Modals } from "../../enums/Modals";

    import {
        accentColor,
        darkShadow_Dark,
        darkShadow_Light,
        lightShadow_Dark,
        lightShadow_Light,
    } from "../../stores/constantVariables";
    import { darkLightMode } from "../../stores/dynamicVariables";

    import {
        switchModals,
        darkLightBGOnId,
        darkLightTextOnClasses,
    } from "../../tools/utils";

    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightBGOnId(value, "panel");
        darkLightTextOnClasses(!value, "icon");
        darkLightShadowOnIcons(value);
    });

    onMount(() => {
        var _value = get(darkLightMode);
        darkLightBGOnId(_value, "panel");
        darkLightTextOnClasses(!_value, "icon");
        darkLightShadowOnIcons(_value);
    });

    onDestroy(() => {
        unsub_darkLightMode();
    });

    function darkLightShadowOnIcons(darkLightMode: boolean) {
        const elements = document.getElementsByClassName("icon");
        if (!elements) return; // Early return if container is not found
        for (const _element of elements) {
            const _icon = _element as HTMLElement;

            if (darkLightMode) {
                _icon.style.setProperty("--light-shadow", lightShadow_Dark);
                _icon.style.setProperty("--dark-shadow-rgb", darkShadow_Dark);
            } else {
                _icon.style.setProperty("--light-shadow", lightShadow_Light);
                _icon.style.setProperty("--dark-shadow-rgb", darkShadow_Light);
            }
        }
    }
</script>

<div
    class="absolute -z-10 w-20 h-full select-none left-1/3"
    style="background-color:{accentColor}"
></div>
<div id="panel">
    <div
        class="absolute w-full h-1/5 rounded-full select-none"
        style={`top:90%; background-color: ${accentColor}`}
    ></div>

    <div class="icon space-y-1">
        <button class="icon__info">
            <InfoCircleOutline
                class="p-px"
                on:click={() => switchModals(Modals.Info)}
            />
        </button>
        <button
            class="icon__logger"
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
    @import "../../neumorphic.scss";
    body {
        --bg-color: #757575;
        --text-color: #757575;
        --icon-hover: #8abdff;
        --icon-active: #a0d6ff;
    }

    #panel {
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

    /*  ICONS  */
    .icon {
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        --light-shadow: #fafafa;
        --dark-shadow-rgb: 95, 95, 95; /* #5f5f5f */

        --drop-shadow: 2px 3px 4px -2px rgba(var(--dark-shadow-rgb), 0.8),
            -2px -3px 3px -1px var(--light-shadow);

        --inner-shadow: inset -3px -3px 3px -1px var(--light-shadow),
            inset 2px 2px 2px -1px rgba(var(--dark-shadow-rgb), 0.6);

        &__info,
        &__logger,
        &__settings {
            width: $icon-size;
            height: $icon-size;
            border-radius: 50%;
            box-shadow: var(--drop-shadow);
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 2rem;
            cursor: pointer;
            color: var(--text-color);
            transition: all 0.2s ease;
            &:active {
                box-shadow: var(--inner-shadow);
                color: var(--icon-active);
            }
            &:hover {
                color: var(--icon-hover);
            }
        }
    }
</style>
