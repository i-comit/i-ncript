<!-- Login.svelte -->
<script>
    import { onMount } from "svelte";
    import { Button, GradientButton, Popover } from "flowbite-svelte";

    import { GetRootFolder, CreateRootFolder } from "../../wailsjs/go/main/App";
    import { defaultBtn } from "../stores/defaultBtn";
    import Frame from "./Frame.svelte";
    let rootFolder = "";
    onMount(async () => {
        rootFolder = await GetRootFolder();
    });
    function createRootFolder() {
        CreateRootFolder();
    }
</script>

<div>
    <Frame />
    <div class="h-6" />
    <p>app must run inside a folder named {rootFolder}</p>
    <GradientButton
        color="cyanToBlue"
        class={defaultBtn}
        on:click={createRootFolder}>Create Folder</GradientButton
    >
</div>
