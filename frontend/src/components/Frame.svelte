<script>
    import { MinimizeApp, CloseApp } from "../../wailsjs/go/main/App";
    import { MinusOutline, CloseOutline } from "flowbite-svelte-icons";
    import { Button, Input, GradientButton } from "flowbite-svelte";
    import { LogDebug, LogError } from "../../wailsjs/runtime/runtime";

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
    class=" window-controls h-6 w-screen flex justify-end py-0.5 px-0"
    style="--wails-draggable:drag"
>
    <Button pill={true} class="!p-1 control-button !z-10" on:click={minimizeApp}
        ><MinusOutline class="w-5 h-5 m-0 p-0" color="white" /></Button
    >
    <Button
        pill={true}
        class="!p-1 control-button !z-10"
        on:click={CloseApp}
        ><CloseOutline class="w-5 h-5 m-0 p-0" color="white" /></Button
    >
</div>

<style>
    .window-controls {
        position: absolute;
        top: 0px;
        right: 0px;
    }

    .control-button {
        cursor: pointer;
        padding: 0;
        margin: 0;
    }
</style>
