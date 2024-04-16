<script lang="ts">
    import { PauseSolid } from "flowbite-svelte-icons";
    import { onMount, onDestroy } from "svelte";
    import { get } from "svelte/store";
    import {
        darkLightMode,
        fileCount,
        fileTaskPercent,
        largeFilePercent,
        accentColor,
        totalFileCt,
        cipheredFilesSize,
    } from "../../stores/dynamicVariables";

    import {
        darkLightTextOnElement,
        darkLightShadowOnIcons,
    } from "../../tools/themes";

    import { InterruptFileTask } from "../../../wailsjs/go/main/App";
    import { FileTasks, currentFileTask } from "../../enums/FileTasks";
    import { formatNumber, retrieveDuplicateFiles } from "../../tools/utils";
    import { EventsOff, EventsOn } from "../../../wailsjs/runtime/runtime";
    import { Tooltip } from "flowbite-svelte";
    import { tooltipTailwindClass } from "../../stores/constantVariables";
    export let dataProgress: number;

    let pauseBtn: HTMLDivElement;

    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightTextOnElement(!value, pauseBtn);
        darkLightShadowOnIcons(value);
    });

    onMount(() => {
        EventsOn("fileProcessed", (fileCtEvt: number) => {
            fileCount.set(fileCtEvt);
            fileTaskPercent.set(
                Math.round(($fileCount / get(totalFileCt)) * 100),
            );
            if (fileCtEvt === 0) interruptFileTask();
        });
        EventsOn("fileTaskSize", (fileTaskSize: string) => {
            cipheredFilesSize.set(fileTaskSize);
        });
        var _value = get(darkLightMode);
        darkLightTextOnElement(!_value, pauseBtn);
        darkLightShadowOnIcons(_value);
    });

    onDestroy(() => {
        unsub_darkLightMode();
        EventsOff("fileProcessed");
        EventsOff("fileTaskSize");
    });

    function interruptFileTask() {
        InterruptFileTask();
        currentFileTask.set(FileTasks.None);
        cipheredFilesSize.set("");
        fileCount.set(0);
        totalFileCt.set(0);
        fileTaskPercent.set(0);
        largeFilePercent.set(0);
        setTimeout(() => {
            retrieveDuplicateFiles();
        }, 1000);
    }
</script>

<div class="flex items-center mb-1 mt-0">
    <div class="icon space-y-1 flex flex-grow justify-center items-center">
        <div class="icon__neumorphic" bind:this={pauseBtn}>
            <button on:click={interruptFileTask}>
                <PauseSolid class="pl-px !w-8 !h-8" />
            </button>
        </div>
    </div>
</div>
<div
    class="absolute flex justify-between w-full bottom-5 px-1 text-xs
            text-primary-100 dark:text-primary-200 font-semibold select-none"
>
    <p>{formatNumber($fileCount)}/{formatNumber($totalFileCt)}</p>
    <p>{$cipheredFilesSize}</p>
</div>
<!-- https://codepen.io/uimax/pen/KgdgGa -->
<div class="progress progress-striped active rounded-md h-3.5 p-0 m-0">
    <div
        style={`width: ${dataProgress}%; background-color: ${$accentColor}`}
        class="progress-bar rounded-md h-3.5"
    ></div>
    <Tooltip placement="bottom" class={tooltipTailwindClass} arrow={false}
        >{dataProgress}</Tooltip
    >
</div>

<style lang="scss">
    @import "../../neumorphic.scss";

    $bg-light-gray: #cdcdcd;

    .progress {
        background-color: $bg-light-gray;
        box-shadow: none;
    }

    .progress-bar {
        box-shadow: none;
    }
    @mixin gradient-striped($color: rgba(180, 180, 180, 0.5), $angle: 50deg) {
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
