<script lang="ts">
    import { onMount, onDestroy } from "svelte";

    import { Progressbar, Tooltip } from "flowbite-svelte";
    import { tooltipTailwindClass } from "../stores/globalVariables";
    import { LogInfo } from "../../wailsjs/runtime/runtime";
    onMount(() => {
        LogInfo("Shidded");
    });
</script>

<div class="h-6">
    <!-- <div
        class="mb-1 h-6 leading-none absolute top-0 left-4 z-10 w-1/3 ml-3 mt-0"
    >
        <p class="relative top-1.5 text-left leading-none">...</p>
    </div> -->
    <Progressbar
        class="absolute top-0 left-4 z-10 w-1/3 ml-3 mt-1.5"
        progress="50"
        size="h-3"
        color="red"
        labelInside
        labelInsideClass="h-3 rounded-full text-xs font-medium text-center leading-none"
    /><!--Folder size to Drive size -->
    <Tooltip class={tooltipTailwindClass} offset={1}>big chungus</Tooltip>
</div>
