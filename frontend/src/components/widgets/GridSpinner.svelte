<!-- https://codepen.io/ruigewaard/pen/dyKZYQ -->
<script>
    import {
        accentColor,
        loadedFileCt,
        pageLoading,
    } from "../../stores/dynamicVariables";
    import { fly } from "svelte/transition";

    import { formatNumber } from "../../tools/utils";

    var blockClass =
        "block bg-primary-600 dark:bg-primary-700 outline outline-1 outline-primary-100 dark:outline-primary-200";
    var blockStyle = `--half-color: ${$accentColor};`;
</script>

{#if $pageLoading}
    <div
        in:fly={{ x: -15, duration: 400 }}
        class="fixed items-center justify-center w-1/2 bottom-[3.5rem] -right-10 z-60 select-none"
        style="--wails-draggable: drag; background-color:transparent"
    >
        <div class="container">
            <div class={blockClass} style={blockStyle} />
            <div class={blockClass} style={blockStyle} />
            <div class={blockClass} style={blockStyle} />
            <div class={blockClass} style={blockStyle} />
            <div class={blockClass} style={blockStyle} />
            <div class={blockClass} style={blockStyle} />
            <div class={blockClass} style={blockStyle} />
            <div class={blockClass} style={blockStyle} />
            <div class={blockClass} style={blockStyle} />
            <div class={blockClass} style={blockStyle} />
            <div class={blockClass} style={blockStyle} />
            <div class={blockClass} style={blockStyle} />
            <div class={blockClass} style={blockStyle} />
            <div class={blockClass} style={blockStyle} />
            <div class={blockClass} style={blockStyle} />
        </div>
        {#if $loadedFileCt > 0}
            <span class="absolute text-md text-white right-[120px] w-[100px]">
                {formatNumber($loadedFileCt)}</span
            >
        {/if}
    </div>
{/if}

<style>
    .container {
        width: 100px;
        height: 85px;
        margin: 5px 0;
    }

    .block {
        position: relative;
        box-sizing: border-box;
        float: left;
        margin: 0 10px 10px 0;
        width: 12px;
        height: 12px;
        border-radius: 4px;
    }

    .block:nth-child(5n + 1) {
        animation: wave 2s ease 0s infinite;
    }
    .block:nth-child(5n + 2) {
        animation: wave 2s ease 0.15s infinite;
    }
    .block:nth-child(5n + 3) {
        animation: wave 2s ease 0.3s infinite;
    }
    .block:nth-child(5n + 4) {
        animation: wave 2s ease 0.45s infinite;
    }
    .block:nth-child(5n + 5) {
        animation: wave 2s ease 0.6s infinite;
        margin-right: 0;
    }
    @keyframes wave {
        0% {
            /* background-color: var(--start-color); */
            background-color: var(--half-color);

            top: 0;
            opacity: 1;
        }
        50% {
            top: 25px;
            opacity: 0.2;
        }
        100% {
            /* background-color: var(--end-color); */
            top: 0;
            opacity: 1;
        }
    }
</style>
