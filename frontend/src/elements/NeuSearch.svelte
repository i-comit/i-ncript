<!-- https://codepen.io/caesura/pen/QWNjvOX -->
<div class="search">
    <svg class="search__icon icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 40 40">
      <path d="M18 3C26.3 3 33.1 9.7 33.1 18 33.1 21.5 31.9 24.7 29.9 27.3L37 34.4 34.4 37 27.3 29.9C24.7 31.9 21.5 33.1 18 33.1 9.7 33.1 3 26.3 3 18 3 9.7 9.7 3 18 3ZM18.1 6.9C11.9 6.9 6.9 11.9 6.9 18.1 6.9 24.2 11.9 29.2 18.1 29.2 24.2 29.2 29.2 24.2 29.2 18.1 29.2 11.9 24.2 6.9 18.1 6.9Z"></path>
    </svg>
    <input type="text" class="search__input oval-xlg" placeholder="Search for...">
  </div>
<style lang="scss">
    @import url("https://fonts.googleapis.com/css2?family=Montserrat:wght@500&display=swap");

    //  Variables

    $fontFamily: "Montserrat", sans-serif;
    $fontSize: 18px;
    $textColor: #6c7587;

    $height: 54px;

    $bgColor: #ebecf0;
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
        padding: 0 8px;
        position: relative;
        border: 3px solid rgba($lightShadow, 0);
        outline: none;
        text-align: center;
        background-color: $bgColor;
        transition: all 100ms ease-in-out;

        &:hover {
            box-shadow: none;
            border-color: rgba($lightShadow, 0.5);
        }
    }

    input {
        @extend %shared-styles;
        box-shadow: $innerShadow;

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
        padding: 0 28px;
        border-radius: 1.5rem;
    }

    // Search

    .search {
        @include gridPlacement(column, 1, 3);
        position: relative;

        &__icon {
            position: absolute;
            z-index: 2;
            top: 50%;
            left: 28px;
            transform: translateY(-50%);
            stroke-width: 0.5px;
        }

        &__input {
            padding: 0 60px;
            text-align: left;
        }
    }
</style>
