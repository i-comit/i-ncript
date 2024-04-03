<div class="toggle">
  <input id="toggle-1" type="checkbox" class="toggle__input" />
  <label for="toggle-1" class="toggle__switch"></label>
</div>

<style lang="scss">
  @import url("https://fonts.googleapis.com/css2?family=Montserrat:wght@500&display=swap");
  @import "../neumorphic.scss";

  //  Variables
  $fontSize: 1px;
  $height: 26px;

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
</style>
