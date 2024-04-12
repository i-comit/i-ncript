<!-- Info.svelte -->
<script>
    import { Button } from "flowbite-svelte";
    import { onMount } from "svelte";
    import { AppPage, currentPage } from "../../enums/AppPage";

    import {
        darkBGColor,
        lightBGColor,
        lightTextColor,
        darkTextColor,
    } from "../../stores/constantVariables";
    import { darkLightMode } from "../../stores/dynamicVariables";
    import { DownloadSolid, GithubSolid } from "flowbite-svelte-icons";

    let cwd = "";
    import { GetAppPath } from "../../../wailsjs/go/main/Getters";
    onMount(async () => {
        cwd = await GetAppPath();
    });
    let _currentPage;
    currentPage.subscribe((value) => {
        _currentPage = value;
    });
</script>

<div
    id="modal-panel"
    class="fixed rounded-md ml-0.5 mr-1 !mb-0.5 hover:outline bg-primary-300 dark:bg-primary-400"
    style="max-height: {_currentPage === AppPage.Login
        ? '67vh'
        : '96%'};  margin-top: 0.16rem"
>
    <div
        class="mb-0.5 w-1/2 left-1/4 rounded-bl-lg rounded-br-lg h-5 z-10"
        style={`position: sticky; top: 0px;
                background-color: ${$darkLightMode ? lightBGColor : darkBGColor}; 
                color: ${$darkLightMode ? darkTextColor : lightTextColor};`}
    >
        INFO
    </div>
    <div
        id="cwd"
        class=" px-0 m-0 bg-primary-500 text-primary-200 dark:text-primary-100 rounded-lg !mx-4"
    >
        current working directory
    </div>
    <div class="relative w-full overflow-hidden" style="max-width: 100%;">
        <!-- Ensure the parent doesn't expand -->
        <!-- Child div that can scroll horizontally -->
        <div
            class="px-0 m-0 whitespace-nowrap overflow-x-auto text-primary-200 dark:text-primary-100 z-5"
        >
            {cwd}
        </div>
    </div>
    <div id="viewLog" class="px-4"></div>
    <div class="h-1" />
    <div id="reportBug" class="flex justify-between px-4">
        <p class="p-0 m-0">open source repo: </p>
        <Button pill={true} outline={true} class="!p-1" color="dark"
            ><GithubSolid class="w-4 h-4 m-0" color="dark" /></Button
        >
    </div>
    <div class="h-1" />
    <div id="checkUpdate" class="flex justify-between px-4">
        <p class="p-0 m-0">check for updates:</p>
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
