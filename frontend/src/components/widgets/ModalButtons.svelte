<script lang="ts">
    import { onMount, onDestroy } from "svelte";
    import { get } from "svelte/store";
    import {
        CogSolid,
        BarsFromLeftOutline,
        InfoCircleOutline,
    } from "flowbite-svelte-icons";
    import { currentModal, Modals } from "../../enums/Modals";

    import { darkLightMode, accentColor } from "../../stores/dynamicVariables";
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

    let iconClass = "p-px hover:outline outline-1 rounded-full";
</script>

<div class="icon space-y-1 !flex-row !justify-center items-baseline space-x-2">
    <button
        class="icon__neumorphic"
        on:pointerdown|stopPropagation={() => switchModals(Modals.Info)}
    >
        <InfoCircleOutline
            class={iconClass}
            size="lg"
            style={`color: ${$currentModal === Modals.Info ? $accentColor : "var(--text-color)"}`}
        />
    </button>
    <button
        class="icon__neumorphic"
        on:pointerdown|stopPropagation={() => switchModals(Modals.Logger)}
    >
        <BarsFromLeftOutline
            class={iconClass}
            role="button"
            size="lg"
            style={`color: ${$currentModal === Modals.Logger ? $accentColor : "var(--text-color)"}`}
        />
    </button>
    <button
        class="icon__neumorphic"
        on:pointerdown|stopPropagation={() => switchModals(Modals.Settings)}
    >
        <CogSolid
            class={iconClass}
            size="lg"
            style={`color: ${$currentModal === Modals.Settings ? $accentColor : "var(--text-color)"}`}
        />
    </button>
</div>

<style lang="scss">
    $icon-size: 1.6rem;
    @import "../../neu_icons.scss";
</style>
