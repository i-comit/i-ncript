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
    class="oval-lg select-none {` ${className}`}"
    style="background-color:{lightBGColor}"
    {disabled}
    on:click={handleClick}><slot /></button
>

<style lang="scss">
    @import url("https://fonts.googleapis.com/css2?family=Montserrat:wght@500&display=swap");
    @import '../neumorphic.scss';
    $fontSize: 1px;
    $height: 26px;

    // Mixins
    @mixin size($width, $height: $width) {
        width: $width;
        height: $height;
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
