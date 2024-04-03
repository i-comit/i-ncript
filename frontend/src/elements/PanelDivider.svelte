<script lang="ts">
    import { lightBGColor } from "../stores/globalVariables";
    import {
        CogSolid,
        BarsFromLeftOutline,
        InfoCircleSolid,
        InfoCircleOutline,
    } from "flowbite-svelte-icons";
</script>

<div class="absolute -z-10 bg-slate-500 w-20 h-full select-none left-1/3"></div>
<div class="panel" style="background-color:{lightBGColor}">
    <div class="absolute inverted-border-radius left"></div>
    <div class="icon space-y-1">
        <div class="icon__home">
            <InfoCircleOutline class="pt-px" />
        </div>
        <div class="icon__account">
            <BarsFromLeftOutline class="p-px" />
        </div>
        <div class="icon__settings">
            <CogSolid class="p-px" />
            <!-- <ion-icon name="settings"></ion-icon> -->
        </div>
    </div>

    <div class="inverted-border-radius right"></div>
</div>

<style lang="scss">
    $panel-width: 1.6rem;
    $icon-size: 1.2rem;

    @import "../neumorphic.scss";

    $dropShadow:
        2px 2px 4px -2px rgba($darkShadow, 0.4),
        -2px -4px 4px -1px rgba($lightShadow, 1);

    $innerShadow:
        inset -2px -2px 2px -1px rgba($lightShadow, 1),
        inset 1px 1px 2px -1px rgba($darkShadow, 0.5);

    $bg: #eeeeee;
    $element-height: 44px;
    $element-width: 1.6rem;
    $element-box-shadow: 0 25px 0 $bg;
    $element-box-shadow-inverse: 0 -25px 0 $bg;

    .picIcon {
        position: absolute;
        padding: 1rem;
    }

    .panel {
        width: $panel-width;
        background-color: green;
        display: flex;
        justify-content: center; /* Centers horizontally */
        align-items: center; /* Centers vertically */
        height: 50%;
        top: 25%;
        position: relative; // Ensure .inverted-border-radius positions relative to .panel
    }

    .inverted-border-radius {
        position: absolute;
        height: 100%;
        z-index: -10;
        user-select: none;
        width: $panel-width;
        border-radius: 25px 0 0 25px;

        &.left,
        &.right {
            // Combine common pseudo-element styles
            &::before,
            &::after {
                content: "";
                position: absolute;
                background-color: transparent;
                height: $element-height;
                width: $element-width;
            }

            &::before {
                top: 100%;
                box-shadow: $element-box-shadow-inverse;
            }

            &::after {
                bottom: 100%;
                box-shadow: $element-box-shadow;
            }
        }

        // Apply side-specific styles using nesting
        &.right {
            &::before {
                right: 0px;
                border-top-right-radius: 50%;
            }

            &::after {
                right: 0px;
                border-bottom-right-radius: 50%;
            }
        }

        &.left {
            &::before {
                left: 0px;
                border-top-left-radius: 50%;
            }

            &::after {
                left: 0px;
                border-bottom-left-radius: 50%;
            }
        }
    }

    :root {
        --primary-light: #8abdff;
        --primary: #8daacb;
        --primary-dark: #5b0eeb;

        --white: #ffffff;
        --greyLight-1: #e4ebf5;
        --greyLight-2: #c8d0e7;
        --greyLight-3: #bec8e4;
        --greyDark: #8e8e8e;
    }

    // $shadow:
    //     0.2rem 0.3rem 0.6rem var(--greyLight-2),
    //     -0.2rem -0.2rem 0.5rem var(--white);
    // $inner-shadow:
    //     inset 0.2rem 0.2rem 0.5rem var(--greyLight-2),
    //     inset -0.2rem -0.2rem 0.5rem var(--white);

    /*  ICONS  */
    .icon {
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        &__account,
        &__home,
        &__settings {
            width: $icon-size;
            height: $icon-size;
            border-radius: 50%;
            box-shadow: $dropShadow;
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 2rem;
            cursor: pointer;
            color: var(--greyDark);
            transition: all 0.2s ease;
            &:active {
                box-shadow: $innerShadow;
                color: var(--primary);
            }
            &:hover {
                color: var(--primary);
            }
        }
    }
</style>
