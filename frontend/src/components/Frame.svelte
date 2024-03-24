<script>
    import { MinimizeApp, CloseApp } from "../../wailsjs/go/main/App";
    import { MinusOutline, CloseOutline } from "flowbite-svelte-icons";
    import { Button, Input, GradientButton } from "flowbite-svelte";

    function minimizeApp() {
        MinimizeApp()
            .then(() => {
                console.log("Application minimized");
            })
            .catch((error) => {
                console.error("Failed to minimize application:", error);
            });
    }
    function closeApp() {
        CloseApp()
            .then(() => {
                console.log("Application minimized");
            })
            .catch((error) => {
                console.error("Failed to minimize application:", error);
            });
    }
</script>

<div class="window-controls" style="--wails-draggable:drag">
    <Button
        pill={true}
        outline={true}
        class="!p-1 control-button"
        color="dark"
        on:click={minimizeApp}
        ><MinusOutline class="w-5 h-5 m-0" color="white" /></Button
    >
    <Button
        pill={true}
        outline={true}
        class="!p-1 control-button"
        color="dark"
        on:click={closeApp}
        ><CloseOutline class="w-5 h-5 m-0" color="white" /></Button
    >
</div>

<style>
    .window-controls {
        position: absolute;
        top: 0;
        right: 0;
        padding: 0.5rem;
    }

    .control-button {
        cursor: pointer;
        margin-left: 0.5rem;
    }
</style>
