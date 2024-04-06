<!-- https://codepen.io/caesura/pen/QWNjvOX -->
<script lang="ts">
    import { createEventDispatcher, onDestroy, onMount } from "svelte";
    import { get } from "svelte/store";

    import {
        darkLightBGOnElement,
        darkLightTextOnElement,
    } from "../../tools/utils";
    import { darklightMode } from "../../stores/dynamicVariables";

    export let _class = "";
    export let _style = "";
    const dispatch = createEventDispatcher();

    let neuButton: HTMLButtonElement;
    const unsubscribe = darklightMode.subscribe((value) => {
        darkLightBGOnElement(value, neuButton);
        darkLightTextOnElement(!value, neuButton);
        darkLightShadowOnElement(value);
    });

    onMount(() => {
        var _value = get(darklightMode);
        darkLightBGOnElement(_value, neuButton);
        darkLightTextOnElement(!_value, neuButton);
        darkLightShadowOnElement(_value);
    });
    onDestroy(() => {
        unsubscribe();
    });

    function darkLightShadowOnElement(darkLightMode: boolean) {
        if (!neuButton) return;
        if (darkLightMode) {
            neuButton.style.setProperty("--light-shadow", "#808080");
            neuButton.style.setProperty("--dark-shadow-rgb", "30, 30, 30");
        } else {
            neuButton.style.setProperty("--light-shadow", "#fafafa");
            neuButton.style.setProperty("--dark-shadow-rgb", "95, 95, 95");
        }
    }

    function handleClick() {
        dispatch("click");
    }

    function handleMouseDown() {
        dispatch("mousedown");
    }
</script>

<button
    bind:this={neuButton}
    class="oval-lg select-none {` ${_class}`}"
    style={` ${_style}`}
    on:click={handleClick}
    on:mousedown={handleMouseDown}><slot /></button
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
            -3px -6px 8px -2px var(--light-shadow);

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

    // Mixins
    @mixin size($width, $height: $width) {
        width: $width;
        height: $height;
    }

    @mixin center {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
    }

    // Layout
    *,
    *:before,
    *:after {
        box-sizing: border-box;
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
</style>
