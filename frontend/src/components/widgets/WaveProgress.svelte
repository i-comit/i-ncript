<script lang="ts">
    import { PauseOutline, PauseSolid } from "flowbite-svelte-icons";
    export let dataProgress: number;
</script>

<div class="flex items-center mb-1.5">
    <div class="icon space-y-1 flex flex-grow justify-center items-center">
        <div class="icon__pause">
            <PauseSolid class="pl-px !w-8 !h-8" />
        </div>
    </div>
    <!-- <div class="flex flex-grow justify-center items-center"> -->
    <!-- <div class="flex space-x-1 text-black text-center text-sm m-0 p-0 leading-none">ENCRYPTING</div> -->
</div>
{#if dataProgress > 0}
    <div class="progress progress-striped active mb-1 rounded-md h-4 p-0 m-0">
        <div
            style="width: {` ${dataProgress}`}%;"
            class="progress-bar rounded-md h-4"
        >
            <p class="text-center text-sm"><slot /></p>
        </div>
    </div>
{/if}

<style lang="scss">
    @import "../../neumorphic.scss";

    $brand-default: #b0bec5;
    $brand-primary: #2196f3;

    $brand-danger: #ef1c1c;
    $bg-light-gray: #f5f5f5;

    .progress {
        background-color: $bg-light-gray;
        box-shadow: none;
    }

    .progress-bar {
        background-color: $brand-primary;
        box-shadow: none;
        &.text-left {
            text-align: left;
            span {
                margin-left: 10px;
            }
        }
        &.text-right {
            text-align: right;
            span {
                margin-right: 10px;
            }
        }
    }
    @mixin gradient-striped($color: rgba(255, 255, 255, 0.15), $angle: 45deg) {
        background-image: -webkit-linear-gradient(
            $angle,
            $color 25%,
            transparent 25%,
            transparent 50%,
            $color 50%,
            $color 75%,
            transparent 75%,
            transparent
        );
        background-image: -o-linear-gradient(
            $angle,
            $color 25%,
            transparent 25%,
            transparent 50%,
            $color 50%,
            $color 75%,
            transparent 75%,
            transparent
        );
        background-image: linear-gradient(
            $angle,
            $color 25%,
            transparent 25%,
            transparent 50%,
            $color 50%,
            $color 75%,
            transparent 75%,
            transparent
        );
    }

    @-webkit-keyframes progress-bar-stripes {
        from {
            background-position: 40px 0;
        }
        to {
            background-position: 0 0;
        }
    }

    // Spec and IE10+
    @keyframes progress-bar-stripes {
        from {
            background-position: 40px 0;
        }
        to {
            background-position: 0 0;
        }
    }

    @mixin animation($animation) {
        -webkit-animation: $animation;
        -o-animation: $animation;
        animation: $animation;
    }
    .progress.active .progress-bar,
    .progress-bar.active {
        @include animation(progress-bar-stripes 2s linear infinite);
    }
    .progress-striped .progress-bar,
    .progress-bar-striped {
        @include gradient-striped;
        background-size: 40px 40px;
    }

    $icon-size: 2rem;

    body {
        --primary-light: #8abdff;
        --primary: #8daacb;
        --primary-dark: #5b0eeb;
    }

    $dropShadow:
        2px 2px 4px -2px rgba($darkShadow, 0.4),
        -2px -4px 4px -1px rgba($lightShadow, 1);

    $innerShadow:
        inset -2px -2px 2px -1px rgba($lightShadow, 1),
        inset 1px 1px 2px -1px rgba($darkShadow, 0.5);
    /*  ICONS  */
    .icon {
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        &__pause {
            width: $icon-size;
            height: $icon-size;
            border-radius: 50%;
            box-shadow: $dropShadow;
            display: flex;
            justify-content: center;
            align-items: center;
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
