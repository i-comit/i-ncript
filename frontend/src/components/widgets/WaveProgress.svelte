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
        loadedFileCt,
    } from "../../stores/dynamicVariables";

    import {
        darkLightTextOnElement,
        darkLightShadowOnIcons,
    } from "../../tools/themes";

    import { InterruptFileTask } from "../../../wailsjs/go/main/App";
    import { FileTasks, currentFileTask } from "../../enums/FileTasks";
    import {
        formatNumber,
        heldDownBtns,
        retrieveDuplicateFiles,
    } from "../../tools/utils";
    import { EventsOff, EventsOn } from "../../../wailsjs/runtime/runtime";
    import { Tooltip } from "flowbite-svelte";
    export let dataProgress: number;

    let pauseBtn: HTMLDivElement;
    let progressPClass = "text-primary-100 dark:text-primary-200 font-semibold";

    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightShadowOnIcons(value);
    });

    let _currentFileTask: FileTasks;
    currentFileTask.subscribe((value) => {
        _currentFileTask = value;
    });

    onMount(() => {
        EventsOn("fileProcessed", (fileCtEvt: number) => {
            fileCount.set(fileCtEvt);
            fileTaskPercent.set(
                Math.round(($fileCount / get(totalFileCt)) * 100),
            );
            if (fileCtEvt === 0) endFileTask();
        });
        EventsOn("fileTaskSize", (fileTaskSize: string) => {
            cipheredFilesSize.set(fileTaskSize);
        });
        var _value = get(darkLightMode);
        darkLightShadowOnIcons(_value);
    });

    onDestroy(() => {
        unsub_darkLightMode();
        EventsOff("fileProcessed");
        EventsOff("fileTaskSize");
    });

    function endFileTask() {
        InterruptFileTask().finally(() => {
            setTimeout(() => {
                retrieveDuplicateFiles();
            }, 2000);
        });
        currentFileTask.set(FileTasks.None);
        cipheredFilesSize.set("");
        fileCount.set(0);
        totalFileCt.set(0);
        fileTaskPercent.set(0);
        largeFilePercent.set(0);
        heldDownBtns.set({});
    }
</script>

<div class="flex items-center mb-1 mt-2" class:hidden={$totalFileCt === 0}>
    <div class="icon space-y-1 flex flex-grow justify-center items-center">
        <div
            class="icon__neumorphic hover:outline outline-1
                        text-primary-300 dark:text-primary-400"
            bind:this={pauseBtn}
        >
            <button on:click={endFileTask}>
                <PauseSolid class="pl-px !w-10 !h-10" />
            </button>
        </div>
    </div>
</div>
<!-- https://codepen.io/uimax/pen/KgdgGa -->
{#if $fileCount > 0}
    <div
        class="progress progress-striped active rounded-md h-3.5 p-0 m-0 !mt-1.5 outline outline-1 hover:outline-2"
        style={`outline-color: ${$accentColor}`}
    >
        <div
            style={`width: ${dataProgress}%; background-color: ${$accentColor}`}
            class="progress-bar rounded-md h-3.5 relative bottom-0"
        />

        <div
            class="relative bottom-3 flex justify-between w-full px-1 select-none"
        >
            <p class={`${progressPClass} -mt-0.5 text-xs`}>
                {formatNumber($fileCount)}/{formatNumber($totalFileCt)}
            </p>
            <p class={`${progressPClass} -mt-6 text-md`}>
                {$cipheredFilesSize}
            </p>
        </div>
        <Tooltip
            placement="bottom"
            class="py-0 m-0 text-xs font-semibold px-1 !bg-opacity-0 z-20"
            arrow={false}
            offset={-17}>{dataProgress}%</Tooltip
        >
    </div>
{:else}
    <p class="leading-none p-2 text-primary-100 dark:text-primary-200">
        {formatNumber($loadedFileCt)}
    </p>
{/if}

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
    @mixin gradient-striped($color: rgba(200, 200, 200, 0.5), $angle: -55deg) {
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
            background-position: -40px 0;
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

    $icon-size: 2.5rem;

    /*  ICONS  */
    @import "../../neu_icons.scss";
</style>
