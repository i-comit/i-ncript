<script lang="ts">
    import { onMount, onDestroy } from "svelte";
    import { get } from "svelte/store";
    import {
        CogSolid,
        BarsFromLeftOutline,
        InfoCircleSolid,
        InfoCircleOutline,
    } from "flowbite-svelte-icons";
    import { Modals } from "../../enums/Modals";


    import { darkLightMode, accentColor } from "../../stores/dynamicVariables";
    import { switchModals } from "../../tools/utils";

    import {
        darkLightBGOnId,
        darkLightTextOnClasses,
        darkLightShadowOnIcons,
    } from "../../tools/themes";

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
</script>

<div
    class="absolute -z-10 w-20 h-full select-none left-1/3"
    style="background-color:{$accentColor}"
></div>
<div id="panel">
    <div
        class="absolute w-full h-1/5 rounded-full select-none"
        style={`top:90%; background-color: ${$accentColor}`}
    ></div>

    <div class="icon space-y-1">
        <button
            class="icon__neumorphic"
            on:click={() => switchModals(Modals.Info)}
        >
            <InfoCircleOutline class="p-px pt-0.5" />
        </button>
        <button
            class="icon__neumorphic"
            on:click={() => switchModals(Modals.Logger)}
        >
            <BarsFromLeftOutline class="p-px pt-0.5" role="button" />
        </button>
        <button
            class="icon__neumorphic"
            on:click={() => switchModals(Modals.Settings)}
        >
            <CogSolid class="p-px pt-0.5" />
        </button>
    </div>
    <div
        class="absolute w-full h-1/5 rounded-full select-none"
        style={`bottom:90%; background-color: ${$accentColor}`}
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
    @import "../../neu_icons.scss";
</style>
