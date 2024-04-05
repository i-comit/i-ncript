<script>
    import { onMount, onDestroy } from "svelte";
    import { spring } from "svelte/motion";
    import { FileOutline } from "flowbite-svelte-icons";
    import { width } from "../stores/globalVariables";

    onMount(() => {});

    let coords = spring(
        { x: 0, y: 0 },
        {
            stiffness: 0.5,
            damping: 0.5,
        },
    );

    let size = spring(10);

    function handleMouseMove(event) {
        coords = {
            x: event.clientX,
            y: event.clientY,
        };
    }
</script>

<!-- svelte-ignore a11y-no-static-element-interactions -->
<!-- <svelte:window on:mousemove={handleMouseMove} /> -->

<div class="fixed top-0 right-0 h-full w-1/2 -z-5 bg-blue-300" role="none" on:mousemove={handleMouseMove}>
    <div
        class="absolute pointer-events-none select-none"
        style={`transform: translate(${coords.x - width - 10}px, ${coords.y - 10}px); z-index: 9999;`}
    >
        <FileOutline class="text-black" />
        <!-- <p>{_heldDownBtnsCt}</p> -->
    </div>
</div>

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
