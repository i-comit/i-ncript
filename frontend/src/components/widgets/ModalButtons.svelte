<script lang="ts">
    import { onMount, onDestroy } from "svelte";
    import { get } from "svelte/store";
    import {
        CogSolid,
        BarsFromLeftOutline,
        InfoCircleOutline,
    } from "flowbite-svelte-icons";
    import { Modals } from "../../enums/Modals";

    import { darkLightMode } from "../../stores/dynamicVariables";
    import { switchModals } from "../../tools/utils";

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

<div class="icon space-y-1 !flex-row !justify-center items-baseline space-x-2">
    <button
        class="icon__neumorphic"
        on:pointerdown|stopPropagation={() => switchModals(Modals.Info)}
    >
        <InfoCircleOutline class="p-px pt-0.5" size="lg" />
    </button>
    <button
        class="icon__neumorphic"
        on:pointerdown|stopPropagation={() => switchModals(Modals.Logger)}
    >
        <BarsFromLeftOutline class="p-px pt-0.5" role="button" size="lg" />
    </button>
    <button
        class="icon__neumorphic"
        on:pointerdown|stopPropagation={() => switchModals(Modals.Settings)}
    >
        <CogSolid class="p-px pt-0.5" size="lg" />
    </button>
</div>

<style lang="scss">
    $icon-size: 1.6rem;
    @import "../../neu_icons.scss";
</style>
