<script lang="ts">
    import { onMount, onDestroy } from "svelte";
    import { get } from "svelte/store";

    import { darkLightMode, accentColor } from "../../stores/dynamicVariables";
    import {
        darkLightTextOnClasses,
        darkLightShadowOnIcons,
    } from "../../tools/themes";

    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightTextOnClasses(!value, "icon");
        darkLightShadowOnIcons(value);
    });

    onMount(() => {
        var _value = get(darkLightMode);
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
<div id="panel" class="bg-primary-400 dark:bg-primary-300">
    <div
        class="absolute w-full h-1/5 rounded-full select-none"
        style={`top:90%; background-color: ${$accentColor}`}
    ></div>

    <div
        class="absolute w-full h-1/5 rounded-full select-none"
        style={`bottom:90%; background-color: ${$accentColor}`}
    ></div>
</div>

<style lang="scss">
    $panel-width: 0.4rem;
    @import "../../neumorphic.scss";

    #panel {
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
