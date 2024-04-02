<!-- https://codepen.io/caesura/pen/QWNjvOX -->
<script>
    import { createEventDispatcher } from "svelte";
    const dispatch = createEventDispatcher();
    function handleClick() {
        dispatch("click");
    }
    import { lightBGColor } from "../stores/globalVariables";

    export let className = "";
    export let disabled = false;
</script>

<button
    class="oval-lg {` ${className}`}"
    style="background-color:{lightBGColor}"
    {disabled}
    on:click={handleClick}><slot /></button
>

<style lang="scss">
    @import url("https://fonts.googleapis.com/css2?family=Montserrat:wght@500&display=swap");

    $fontFamily: "Montserrat", sans-serif;
    $fontSize: 15px;
    $textColor: #5d5d5d;

    $height: 26px;

    // $bgColor: #e9e9e9;
    $iconColor: #8992a5;

    $accentBlue: #1657f0;
    $brightBlue: #36d7e0;

    $darkShadow: #484f60;
    $lightShadow: #ffffff;

    $dropShadow:
        8px 8px 12px -2px rgba($darkShadow, 0.4),
        -6px -6px 12px -1px rgba($lightShadow, 1);

    $innerShadow:
        inset -4px -4px 6px -1px rgba($lightShadow, 1),
        inset 2px 2px 8px -1px rgba($darkShadow, 0.5);

    // Mixins

    @mixin size($width, $height: $width) {
        width: $width;
        height: $height;
    }

    @mixin applyFontStyle() {
        font-family: $fontFamily;
        font-size: $fontSize;
        line-height: $fontSize;
        color: $textColor;
    }

    @mixin gridPlacement($direction, $start, $end) {
        grid-#{$direction}-start: $start;
        grid-#{$direction}-end: $end;
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

    // Global
    %shared-styles {
        @include applyFontStyle;
        padding: 0 2px;
        // padding-top: 15px;
        padding-bottom: 4px;
        position: relative;
        border: 2px solid rgba($lightShadow, 0);
        outline: none;
        text-align: center;
        transition: all 100ms ease-in-out;

        &:hover {
            box-shadow: none;
            border-color: rgba($lightShadow, 0.5);
        }
    }

    button {
        @extend %shared-styles;
        box-shadow: $dropShadow;
        cursor: pointer;

        &:active {
            box-shadow: $innerShadow;
            border-color: rgba($lightShadow, 0);
        }

        & > * {
            vertical-align: middle;
        }
        & > span:last-child {
            padding-left: 5px;
        }
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
        &--with-icon {
            @extend .oval-lg;
            text-align: left;
        }
    }
</style>
