<!-- https://codepen.io/caesura/pen/QWNjvOX -->
<script lang="ts">
    import { onDestroy, onMount } from "svelte";
    import { get } from "svelte/store";

    import { darkLightMode } from "../../stores/dynamicVariables";
    import {
        darkLightBGOnElement,
        darkLightShadowOnNeuButton,
        darkLightTextOnElement,
    } from "../../tools/themes";
    export let _class = "";
    export let _style = "";

    let neuButton: HTMLButtonElement;
    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightBGOnElement(value, neuButton);
        darkLightTextOnElement(!value, neuButton);
        darkLightShadowOnNeuButton(value, neuButton);
    });

    onMount(() => {
        var _value = get(darkLightMode);
        darkLightBGOnElement(_value, neuButton);
        darkLightTextOnElement(!_value, neuButton);
        darkLightShadowOnNeuButton(_value, neuButton);
    });

    onDestroy(() => {
        unsub_darkLightMode();
    });
</script>

<button
    bind:this={neuButton}
    class="oval-lg select-none pointer-events-none {` ${_class}`}"
    style={` ${_style}`}
    disabled><slot /></button
>

<style lang="scss">
    @import url("https://fonts.googleapis.com/css2?family=Montserrat:wght@500&display=swap");
    @import "../../neumorphic.scss";
    $fontSize: 15px;
    $height: 1.5rem;

    button {
        --bg-color: #757575;
        --text-color: #dddddd;

        --light-shadow: #fafafa;
        --dark-shadow-rgb: 95, 95, 95; /* #5f5f5f */

        --drop-shadow: 2px 6px 6px -2px rgba(var(--dark-shadow-rgb), 0.6),
            -3px -6px 4px -2px var(--light-shadow);

        --inner-shadow: inset -4px -4px 4px -1px var(--light-shadow),
            inset 2px 2px 6px -1px rgba(var(--dark-shadow-rgb), 0.5);

        background-color: var(--bg-color);
        color: var(--text-color);

        @extend %shared-styles;
        box-shadow: var(--drop-shadow);
        cursor: pointer;

        &:active {
            box-shadow: var(--inner-shadow);
            border-color: rgba(var(--light-shadow), 0);
        }
    }
    // Layout
    *,
    *:before,
    *:after {
        box-sizing: border-box;
    }
    %shared-styles {
        @include applyFontStyle;
        padding: 0 8px;
        position: relative;
        border: $borderWidth solid rgba($lightShadow, 0);
        outline: none;
        text-align: center;

        box-shadow: $innerShadow;
        border-color: rgba($lightShadow, 0);
    }

    // Oval Buttons
    %oval-btn {
        height: $height;
        padding: 0 3px;
        border-radius: 0.5rem;
    }

    .oval-lg {
        @extend %oval-btn;
        width: 210px;
        font-size: $fontSize;
        &--with-icon {
            @extend .oval-lg;
            text-align: left;
        }
    }

    button {
        --bg-color: #757575;
        --text-color: #dddddd;

        --light-shadow: #fafafa;
        --dark-shadow-rgb: 95, 95, 95; /* #5f5f5f */

        --drop-shadow: 2px 6px 6px -2px rgba(var(--dark-shadow-rgb), 0.6),
            -3px -6px 4px -2px var(--light-shadow);

        --inner-shadow: inset -4px -4px 4px -1px var(--light-shadow),
            inset 2px 2px 6px -1px rgba(var(--dark-shadow-rgb), 0.5);

        background-color: var(--bg-color);
        color: var(--text-color);

        @extend %shared-styles;
        box-shadow: var(--drop-shadow);
        cursor: pointer;

        &:active {
            box-shadow: var(--inner-shadow);
            border-color: rgba(var(--light-shadow), 0);
        }
    }
</style>
