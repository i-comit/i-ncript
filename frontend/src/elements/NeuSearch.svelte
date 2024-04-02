<!-- https://codepen.io/caesura/pen/QWNjvOX -->
<script>
    import { lightBGColor } from "../stores/globalVariables";
    import { onMount, onDestroy } from "svelte";

    import { CloseButton } from "flowbite-svelte";
    import { LogInfo } from "../../wailsjs/runtime/runtime";
    export let _class = "";

    export let searchInput = "";

    onMount(() => {
        clearSearchInput();
    });
    function registerSearchInput() {
        LogInfo("Search Input " + searchInput);
    }
    function clearSearchInput() {
        searchInput = "";
        LogInfo("Search Input cleared");
    }
</script>

<div
    class="absolute search z-10 {` ${_class}`}"
    style="--wails-draggable:drag;"
>
    <!-- <svg
        class="search__icon icon"
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 40 40"
    >
        <path
            d="M18 3C26.3 3 33.1 9.7 33.1 18 33.1 21.5 31.9 24.7 29.9 27.3L37 34.4 34.4 37 27.3 29.9C24.7 31.9 21.5 33.1 18 33.1 9.7 33.1 3 26.3 3 18 3 9.7 9.7 3 18 3ZM18.1 6.9C11.9 6.9 6.9 11.9 6.9 18.1 6.9 24.2 11.9 29.2 18.1 29.2 24.2 29.2 29.2 24.2 29.2 18.1 29.2 11.9 24.2 6.9 18.1 6.9Z"
        ></path>
    </svg> -->
    <input
        type="text"
        class="search__input oval-sm"
        placeholder="search..."
        bind:value={searchInput}
        on:keyup={registerSearchInput}
    />
    <CloseButton
        class="absolute top-0 p-0 rounded-md"
        style="left: 4.6rem; margin-top: 0.1rem"
        color="dark"
        role="button"
        on:click={() => {
            clearSearchInput();
            this.blur();
        }}
    ></CloseButton>
</div>

<style lang="scss">
    @import url("https://fonts.googleapis.com/css2?family=Montserrat:wght@500&display=swap");
    @import "../neumorphic.scss";

    //  Variables
    $fontFamily: "Montserrat", sans-serif;
    $fontSize: 13px;
    $height: 20px;
    // Mixins
    @mixin size($width, $height: $width) {
        width: $width;
        height: $height;
    }

    #closeButton {
        position: absolute;
        left: 12rem !important;
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

    %shared-styles {
        @include applyFontStyle;
        padding: 0 8px;
        position: relative;
        border: 1px solid rgba($lightShadow, 0);
        outline: none;
        text-align: center;
        transition: all 100ms ease-in-out;

        &:hover {
            box-shadow: none;
            border-color: rgba($lightShadow, 0.5);
        }
    }

    // Layout
    *,
    *:before,
    *:after {
        box-sizing: border-box;
    }

    input {
        @extend %shared-styles;
        box-shadow: $innerShadow;
        position: absolute;
        &:focus {
            box-shadow: none;
            border-color: rgba($lightShadow, 0.5);
        }

        &::placeholder {
            @include applyFontStyle;
            opacity: 1;
        }
    }

    // Oval Buttons

    %oval-btn {
        height: $height;
        padding: 0 2px;
    }

    .oval-sm {
        @extend %oval-btn;
        width: 110px;
        border-radius: 0.4rem;
        margin-top: 1.5px;
    }
    // Search

    .search {
        @include gridPlacement(column, 1, 3);
        position: relative;
        &__icon {
            position: absolute;
            z-index: 2;
            top: 50%;
            left: 22px;
            transform: translateY(-50%);
            stroke-width: 0.25px;
        }

        &__input {
            padding: 0 1.2rem;
            text-align: left;
            left: 74px;
        }
    }
</style>
