<script>
    import { MinimizeApp, CloseApp } from "../../wailsjs/go/main/App";
    import { MinusOutline, CloseOutline } from "flowbite-svelte-icons";
    import { Button, Input, GradientButton, Tooltip } from "flowbite-svelte";
    import { LogDebug, LogError } from "../../wailsjs/runtime/runtime";
    import appLogo from "../assets/images/i-comiti.png";

    import { tooltipTailwindClass } from "../stores/globalVariables";

    function minimizeApp() {
        MinimizeApp()
            .then(() => {
                LogDebug("Application minimized");
            })
            .catch((error) => {
                LogError("Failed to minimize application:", error);
            });
    }
</script>

<div
    class="window-controls h-6 w-screen flex justify-between items-center py-0.5 px-0 rounded-lg"
    style="--wails-draggable:drag"
>
    <div class="flex justify-start h-full ml-1 select-none">
        <img src={appLogo} alt="Description" />
        <Tooltip
        class={tooltipTailwindClass}
        offset={-1}
        arrow={true}>portable data encryption app</Tooltip
    >
    </div>
    <div class="flex justify-end">
        <Button pill={true} class="!p-1 !pr-0 !z-10" on:click={minimizeApp}>
            <MinusOutline class="w-5 h-5 m-0 p-0" color="white" />
        </Button>
        <Button pill={true} class="!p-1 !pl-0 !z-10" on:click={CloseApp}>
            <CloseOutline class="w-5 h-5 m-0 p-0" color="white" />
        </Button>
    </div>
</div>

<style>
    .window-controls {
        position: absolute;
        top: 0px;
        right: 0px;
    }
</style>
