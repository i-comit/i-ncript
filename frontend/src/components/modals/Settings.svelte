<!-- Settings.svelte -->
<script>
    import {
        Button,
        Input,
        ButtonGroup,
        Dropdown,
        DropdownItem,
        Range,
        DarkMode,
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
    import { darkLightMode, accentColor } from "../../stores/dynamicVariables";
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

    function setAccentColor(hexColor) {
        accentColor.set(hexColor);
        LogInfo("accent color set to " + hexColor);
    }
</script>

<div
    id="modal-panel"
    class="rounded-md ml-0.5 mr-1 !mb-0.5 hover:outline bg-primary-300 dark:bg-primary-400"
    style="max-height: {_currentPage === AppPage.Login
        ? '67vh'
        : '96%'};  margin-top: 0.16rem"
>
    <div
        class="mb-0.5 w-1/2 left-1/4 rounded-bl-lg rounded-br-lg font-semibold h-5"
        style={`position: sticky; top: 0px;
        background-color: ${$darkLightMode ? lightBGColor : darkBGColor}; 
        color: ${accentColor};`}
    >
        SETTINGS
    </div>
    <div class="flex justify-between my-1 bg-primary-500 rounded-lg mx-1">
        <button class="!p-0" on:click={() => toggleLightDarkMode()}>
            <DarkMode btnClass="w-6 h-6 rounded-lg text-md p-2" />
        </button>
        <div id="languages" class="h-full w-1/2">
            <Button data-placement="left-start"
                >Languages<ChevronDownOutline
                    class="w-5 h-5 text-white dark:text-white"
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
        <p class="p-0 m-0 text-primary-200 dark:text-primary-100">
            accent color
        </p>
        <ButtonGroup class="space-x-px ">
            <Button
                pill
                class="bg-primary-500 dark:bg-primary-500 rounded-r-lg"
                on:click={() => setAccentColor("#95c7db")}
            />
            <Button
                pill
                class="bg-primary-600 dark:bg-primary-600"
                on:click={() => setAccentColor("#CF33ED")}
            />
            <Button
                pill
                class="bg-primary-700 dark:bg-primary-700"
                on:click={() => setAccentColor("#E9C456")}
            ></Button>
            <Button pill class="bg-primary-800 dark:bg-primary-800"></Button>
            <Button pill class="bg-primary-900 dark:bg-primary-900"></Button>
            <Button
                pill
                class="bg-primary-1000 dark:bg-primary-1000 rounded-r-lg"
            ></Button>
        </ButtonGroup>
    </div>

    <div class="px-5">
        <p class="p-0 text-sm text-primary-200 dark:text-primary-100">
            delete log entries older than:
        </p>
        <Range id="range1" min="0" max="5" bind:value={stepValue} size="md" />
    </div>
</div>
