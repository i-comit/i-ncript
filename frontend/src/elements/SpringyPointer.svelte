<script>
    import { onMount, onDestroy } from "svelte";
    import { spring } from "svelte/motion";
    import { FileOutline } from "flowbite-svelte-icons";

    let coords = spring(
        { x: 0, y: 0 },
        {
            stiffness: 0.5,
            damping: 0.5,
        },
    );

    let size = spring(10);
</script>

<!-- svelte-ignore a11y-no-static-element-interactions -->
<svelte:window
    on:mousemove={(e) => coords.set({ x: e.clientX, y: e.clientY })}
/>
<div cx={$coords.x} cy={$coords.y} class=" absolute w-10 h-10" />

<svg class="absolute pointer-events-none select-none" style="z-index: 9999">
    <!-- <circle cx={$coords.x} cy={$coords.y} r={$size} /> -->
    <path
        transform="translate({$coords.x - 10}, {$coords.y - 10})"
        stroke="currentColor"
        stroke-linejoin="round"
        stroke-width="2"
        d="M10 3v4a1 1 0 0 1-1 1H5m14-4v16a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V7.914a1 1 0 0 1 .293-.707l3.914-3.914A1 1 0 0 1 9.914 3H18a1 1 0 0 1 1 1Z"
    />
</svg>

<style>
    svg {
        width: 100%;
        height: 100%;
        margin: -8px;
    }
    circle {
        fill: #ff3e00;
    }
</style>
