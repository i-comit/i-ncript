<!-- https://codepen.io/caesura/pen/QWNjvOX -->
<script>
    import { createEventDispatcher } from "svelte";
    const dispatch = createEventDispatcher();
    function handleClick() {
        dispatch("click");
    }
    import { lightBGColor } from "../stores/constantVariables";

    export let _class = "";
    export let _style = "";
</script>

<button
    class="oval-lg select-none pointer-events-none {` ${_class}`}"
    style="background-color:{lightBGColor}; {` ${_style}`}"
    disabled><slot /></button
>

<style lang="scss">
    @import url("https://fonts.googleapis.com/css2?family=Montserrat:wght@500&display=swap");
    @import "../neumorphic.scss";
    $fontSize: 15px;
    $height: 1.5rem;

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
</style>
