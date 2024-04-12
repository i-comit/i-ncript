<!-- Settings.svelte -->
<script>
    import {
        Button,
        Input,
        ButtonGroup,
        Dropdown,
        DropdownItem,
        Range,
    } from "flowbite-svelte";
    import { get } from "svelte/store";
    import { switchPages } from "../../tools/utils";
    import {
        darkBGColor,
        lightBGColor,
        lightTextColor,
        darkTextColor,
    } from "../../stores/constantVariables";
    import {
        ChevronDownOutline,
        SunSolid,
        MoonSolid,
    } from "flowbite-svelte-icons";

    import { AppPage, currentPage } from "../../enums/AppPage";
    import { darkLightMode } from "../../stores/dynamicVariables";
    import { LogInfo } from "../../../wailsjs/runtime/runtime";

    function toggleLightDarkMode() {
        darkLightMode.update((v) => !v);
        LogInfo("calling darklight ");
    }
    let _currentPage;
    currentPage.subscribe((value) => {
        _currentPage = value;
    });
    let stepValue = 2.5;
</script>

<div
    id="modal-panel"
    class="bg-gray-200 rounded-md ml-0.5 mr-1 !mb-0.5 hover:outline"
    style="max-height: {_currentPage === AppPage.Login
        ? '67vh'
        : '96%'};  margin-top: 0.16rem"
>
    <div
        class="mb-0.5 w-1/2 left-1/4 rounded-bl-lg rounded-br-lg"
        style={`position: sticky; top: 0px; height: 1.2rem;
        background-color: ${$darkLightMode ? darkBGColor : lightBGColor}; 
        color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
    >
        SETTINGS
    </div>
    <div class="flex justify-between mb-2 bg-gray-500 rounded-lg">
        <button class="!p-1" on:click={() => toggleLightDarkMode()}>
            {#if $darkLightMode}
                <MoonSolid class="w-5 h-5 m-0 p-0 " color="white" />
            {:else}
                <SunSolid class="w-5 h-5 m-0 p-0 " color="white" />
            {/if}
        </button>
        <div id="languages" class="h-full">
            <Button
                >Languages<ChevronDownOutline
                    class="w-5 h-5 ms-2 text-white dark:text-white"
                /></Button
            >
            <Dropdown class="">
                <DropdownItem
                    defaultClass="font-medium py-1 px-1 text-sm hover:bg-gray-100 dark:hover:bg-gray-600"
                    >English</DropdownItem
                >
                <DropdownItem
                    defaultClass="font-medium py-1 px-1 text-sm hover:bg-gray-100 dark:hover:bg-gray-600"
                    >French</DropdownItem
                >
                <DropdownItem
                    defaultClass="font-medium py-1 px-1 text-sm hover:bg-gray-100 dark:hover:bg-gray-600"
                    >German</DropdownItem
                >
                <DropdownItem
                    defaultClass="font-medium py-1 px-1 text-sm hover:bg-gray-100 dark:hover:bg-gray-600"
                    >Urdu</DropdownItem
                >
            </Dropdown>
        </div>
    </div>
    
    <div class="h-1" />
    <div id="darkLightMode"></div>
    <div id="accentColor flex" class="p-0 m-0">
        <p class="p-0 m-0">accent color</p>
        <ButtonGroup class="space-x-px">
            <Button pill color="blue"></Button>
            <Button pill color="green"></Button>
            <Button pill color="red"></Button>
            <Button pill color="yellow"></Button>
        </ButtonGroup>
    </div>
    <div id="accentColor flex" class="p-0 m-0">
        <p class="p-0 m-0">accent color</p>
        <ButtonGroup class="space-x-px">
            <Button pill color="blue"></Button>
            <Button pill color="green"></Button>
            <Button pill color="red"></Button>
            <Button pill color="yellow"></Button>
        </ButtonGroup>
    </div>
    <div id="accentColor flex" class="p-0 m-0">
        <p class="p-0 m-0">accent color</p>
        <ButtonGroup class="space-x-px">
            <Button pill color="blue"></Button>
            <Button pill color="green"></Button>
            <Button pill color="red"></Button>
            <Button pill color="yellow"></Button>
        </ButtonGroup>
    </div>
    <div id="transparency">
        <p class="p-0 m-0">transparency</p>
        <Range id="range1" min="0" max="100" bind:value={stepValue} size="md" />
    </div>
</div>

<style>
    .row {
        display: flex;
        justify-content: space-between; /* Spread the buttons evenly */
        margin-bottom: 10px; /* Spacing between rows of buttons */
    }
</style>
