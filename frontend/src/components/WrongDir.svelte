<!-- Login.svelte -->
<script>
    import { onMount } from "svelte";
    import { Button, GradientButton, Popover } from "flowbite-svelte";

    import { InitializeRootFolder, CloseApp } from "../../wailsjs/go/main/App";
    import { GetRootFolder } from "../../wailsjs/go/main/Getters";

    import { pageChangeBtn } from "../stores/globalVariables";
    import Frame from "./Frame.svelte";
    let rootFolder = "";
    onMount(async () => {
        rootFolder = await GetRootFolder();
    });
    function initializeRootFolder() {
        InitializeRootFolder();
        CloseApp();
    }
</script>

<div class="rounded-lg">
    <Frame />
    <div class="h-6" />
    <p>app must run inside a folder named {rootFolder}</p>
    <GradientButton
        color="cyanToBlue"
        class={pageChangeBtn}
        on:click={initializeRootFolder}>Create Folder</GradientButton
    >
</div>
