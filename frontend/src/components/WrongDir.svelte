<!-- Login.svelte -->
<script>
    import { onMount } from "svelte";
    import { Button, GradientButton, Popover } from "flowbite-svelte";

    import { InitializeRootFolder, CloseApp } from "../../wailsjs/go/main/App";
    import { GetRootFolder } from "../../wailsjs/go/main/Getters";

    import { defaultBtn } from "../stores/pageChangeBtn";
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

<div>
    <Frame />
    <div class="h-6" />
    <p>app must run inside a folder named {rootFolder}</p>
    <GradientButton
        color="cyanToBlue"
        class={defaultBtn}
        on:click={initializeRootFolder}>Create Folder</GradientButton
    >
</div>
