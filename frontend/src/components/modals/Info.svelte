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
        cwd = "CWD: " + (await GetAppPath());
    });
    let _currentPage;
    currentPage.subscribe((value) => {
        _currentPage = value;
    });
</script>

<div
    id="modal-panel"
    class="bg-gray-200 rounded-md ml-0.5 mr-1 !mb-0.5 hover:outline"
    style="max-height: {_currentPage === AppPage.Login
        ? '67vh'
        : '96%'};  margin-top: 0.16rem"
>
    <div
        class="mb-0.5 w-1/3 left-1/3 rounded-bl-lg rounded-br-lg"
        style={`position: sticky; top: 0px; height: 1.2rem;
background-color: ${$darkLightMode ? darkBGColor : lightBGColor}; 
color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
    >
        INFO
    </div>
    <div id="cwd" class=" px-0 m-0 bg-gray-100">
        {cwd}
    </div>
    <div id="viewLog" class="px-4"></div>
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
