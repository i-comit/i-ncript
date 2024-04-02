<div class="toggle">
  <input id="toggle-1" type="checkbox" class="toggle__input" />
  <label for="toggle-1" class="toggle__switch"></label>
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

  // Toggle

  .toggle {
    @include size(80px, $height);
    position: relative;

    &__input {
      display: none;
      &:checked + .toggle__switch {
        background-color: $accentBlue;
        box-shadow:
          10px 10px 24px rgba($darkShadow, 0.4),
          -8px -8px 20px rgba($lightShadow, 1);

        &:before {
          left: 50%;
        }
      }
    }

    &__switch {
      @include size($height, 32px);
      @include center;
      display: block;
      border-radius: 16px;
      box-shadow:
        -10px 10px 24px rgba($darkShadow, 0.4),
        8px -8px 20px rgba($lightShadow, 1);
      background-color: $bgColor;
      cursor: pointer;
      transition: all 302ms ease-in-out;

      &:before {
        content: "";
        @include size(38px);
        position: absolute;
        top: 50%;
        left: -20%;
        border-radius: 100%;
        background-color: $lightShadow;
        transform: translateY(-50%);
        box-shadow: 0px 1px 2px rgba($darkShadow, 0.5);
        transition: left 300ms ease-in-out;
      }
    }
  }
</style>
