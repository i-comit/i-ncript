<script>
    import { MinimizeApp, CloseApp } from "../../wailsjs/go/main/App";
    import { MinusOutline, CloseOutline } from "flowbite-svelte-icons";
    import { Button, Input, GradientButton, Tooltip } from "flowbite-svelte";
    import { LogDebug, LogError } from "../../wailsjs/runtime/runtime";
    import appLogo from "../assets/images/i-comiti.png";

    import { lightBGColor, darkColor } from "../stores/globalVariables";

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
    class="window-controls h-6 w-screen flex justify-between items-center py-0.5 px-0 rounded-lg z-30"
    style="--wails-draggable:drag"
>
    <div class="flex justify-start h-full ml-1 select-none">
        <img src={appLogo} alt="Description" />
        <Tooltip class={tooltipTailwindClass} offset={-1} arrow={true}
            >portable data encryption app</Tooltip
        >
    </div>
    <div class="flex justify-end">
        <Button
            class="p-0 my-1"
            style={`background-color: ${darkColor}`}
            on:click={minimizeApp}
        >
            <MinusOutline class="w-5 h-5 m-0 p-0" color="white" />
        </Button>
        <Button
            class="p-0 my-1 !mr-1 !ml-px"
            style={`background-color: ${darkColor}`}
            on:click={CloseApp}
        >
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
