<script lang="ts">
    import { onMount, onDestroy } from "svelte";

    import { Progressbar, Tooltip } from "flowbite-svelte";
    import { tooltipTailwindClass } from "../stores/globalVariables";
    import { LogInfo } from "../../wailsjs/runtime/runtime";
    onMount(() => {
        LogInfo("Shidded");
    });
</script>

<div>
    <Progressbar
        class="absolute top-0 left-1 z-10 w-1/5 ml-3 mt-1.5"
        progress="50"
        size="h-3"
        color="red"
        labelInside
        labelInsideClass="h-3 rounded-full text-xs font-medium text-center leading-none"
    /><!--Folder size to Drive size -->
    <Tooltip class={tooltipTailwindClass} offset={1}>big chungus</Tooltip>
</div>
