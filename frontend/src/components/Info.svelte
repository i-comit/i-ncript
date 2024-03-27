<!-- Info.svelte -->
<script>
    import {
        Button,
        Input,
        GradientButton,
        Tooltip,
        ButtonGroup,
        Dropdown,
        DropdownItem,
        Range,
    } from "flowbite-svelte";
    import { onMount } from "svelte";

    import { switchFormButton } from "../utils";
    import { DownloadSolid, GithubSolid } from "flowbite-svelte-icons";

    let cwd = "";
    import { GetAppPath } from "../../wailsjs/go/main/Getters";
    onMount(async () => {
        cwd = "CWD: " + (await GetAppPath());
    });
    const buttonClasses = "max-w-48 min-h-3 max-h-5 pt-3 px-3";
</script>

<div id="modal-panel" class="bg-gray-200 rounded-lg mt-1 mb-2">
    <div id="cwd" class=" px-0 m-0 bg-gray-100">
        {cwd}
    </div>
    <div id="viewLog" class="px-4">
        <GradientButton
            color="cyanToBlue"
            class={buttonClasses}
            on:click={() => {}}>VIEW LOG</GradientButton
        >
    </div>
    <div id="driveFormat" class=" px-0 m-0 bg-gray-100">format: NTFS</div>
    <div class="h-1" />
    <div id="reportBug" class="flex justify-between px-4">
        <p class="p-0 m-0">report a bug</p>
        <Button pill={true} outline={true} class="!p-1" color="dark"
            ><GithubSolid class="w-4 h-4 m-0" color="dark" /></Button
        >
    </div>
    <div class="h-1" />
    <div id="checkUpdate" class="flex justify-between px-4">
        <p class="p-0 m-0">check for updates</p>
        <Button pill={true} outline={true} class="!p-1" color="dark"
            ><DownloadSolid class="w-4 h-4 m-0" color="dark" /></Button
        >
    </div>
    <div class="h-1" />

    <div id="credits">
        <ul class="flex justify-start">
            <li>GUI: Wails</li>
            <li>Frontend: Svelte</li>
            <li>CSS: Flowbite</li>
        </ul>
    </div>
    <div class="h-1" />
</div>

<style>
    .row {
        display: flex;
        justify-content: space-between; /* Spread the buttons evenly */
        margin-bottom: 10px; /* Spacing between rows of buttons */
    }
</style>
