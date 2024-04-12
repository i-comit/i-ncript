<script lang="ts">
    import { PauseOutline, PauseSolid } from "flowbite-svelte-icons";
    import { onMount, onDestroy } from "svelte";
    import { get } from "svelte/store";
    import { EventsOn } from "../../../wailsjs/runtime/runtime";
    import {
        darkLightMode,
        fileCount,
        fileProgress,
        fileTaskPercent,
        largeFilePercent,
    } from "../../stores/dynamicVariables";
    import { getCurrentPageStore, buildFileTree } from "../../tools/fileTree";

    import {
        darkLightTextOnElement,
        darkLightShadowOnIcons,
    } from "../../tools/themes";

    import { InterruptFileTask } from "../../../wailsjs/go/main/App";
    import { FileTasks, currentFileTask } from "../../enums/FileTasks";
    export let dataProgress: number;

    let pauseBtn: HTMLDivElement;

    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightTextOnElement(!value, pauseBtn);
        darkLightShadowOnIcons(value);
    });

    onMount(() => {
        var _value = get(darkLightMode);
        darkLightTextOnElement(!_value, pauseBtn);
        darkLightShadowOnIcons(_value);
    });

    onDestroy(() => {
        unsub_darkLightMode();
    });

    function interruptFileTask() {
        InterruptFileTask();
        currentFileTask.set(FileTasks.None);
        fileCount.set(0);
        fileProgress.set(0);
        fileTaskPercent.set(0);
        largeFilePercent.set(0);
    }
</script>

<div class="flex items-center mb-1 mt-0">
    <div class="icon space-y-1 flex flex-grow justify-center items-center">
        <div class="icon__pause" bind:this={pauseBtn}>
            <button on:click={interruptFileTask}>
                <PauseSolid class="pl-px !w-8 !h-8" />
            </button>
        </div>
    </div>
</div>
<!-- {#if dataProgress > 0} -->
<div class="progress progress-striped active rounded-md h-3.5 p-0 m-0">
    <div
        style="width: {` ${dataProgress}`}%;"
        class="progress-bar rounded-lg h-3.5"
    >
        <p class="text-center text-sm"><slot /></p>
    </div>
</div>

<!-- {/if} -->

<style lang="scss">
    @import "../../neumorphic.scss";

    $brand-default: #b0bec5;
    $brand-primary: #2196f3;

    $brand-danger: #ef1c1c;
    $bg-light-gray: #cdcdcd;

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
    @mixin gradient-striped($color: rgba(150, 150, 150, 0.75), $angle: 45deg) {
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

    $icon-size: 2.1rem;

    /*  ICONS  */
    @import "../../neu_icons.scss";
</style>
