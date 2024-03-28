<!-- Settings.svelte -->
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
    import { switchFormButton } from "../tools/utils";
    import {
        InfoCircleOutline,
        AdjustmentsVerticalOutline,
        ChevronDownOutline,
        SunSolid,
        MoonSolid,
    } from "flowbite-svelte-icons";

    import { AppPage, currentPage } from "../enums/AppPage";

    function toggleLightDarkMode() {}
    let _currentPage;
    currentPage.subscribe((value) => {
        _currentPage = value;
    });
    let stepValue = 2.5;
</script>

<div
    id="modal-panel"
    class="bg-gray-200 rounded-lg mt-1 mb-2 ml-1 mr-1.5"
    style="max-height: {_currentPage === AppPage.Login ? '66vh' : '81vh'};"
>
    <div class="row bg-gray-500 rounded-lg">
        <Button
            pill={true}
            outline={false}
            class="!p-1"
            on:click={() => toggleLightDarkMode()}
            ><SunSolid class="w-5 h-5 m-0 p-0 " color="white" /></Button
        >
        <div id="languages h-1 bg-gray-400">
            <Button
                >Languages<ChevronDownOutline
                    class="w-3 h-3 ms-2 text-white dark:text-white "
                /></Button
            >
            <Dropdown class="py-1 my-1">
                <DropdownItem>English</DropdownItem>
                <DropdownItem>French</DropdownItem>
                <DropdownItem>German</DropdownItem>
                <DropdownItem>Urdu</DropdownItem>
            </Dropdown>
        </div>
    </div>
    <div id="changeUsername" class=" px-4">
        <Input
            class="max-h-6 w-full"
            id="small-input"
            placeholder="change username.."
            type="text"
        />
    </div>
    <div id="changePassword" class=" px-4">
        <Input
            class="max-h-6 w-full"
            id="small-input"
            placeholder="change password.."
            type="password"
        />
    </div>
    <div class="h-1" />
    <div id="darkLightMode"></div>
    <div id="accentColor flex" class="p-0 m-0">
        <p class="p-0 m-0">accent color</p>
        <ButtonGroup class="space-x-px">
            <Button pill color="blue"></Button>
            <Button pill color="green"></Button>
            <Button pill color="red"></Button>
            <!-- <Button pill color="primary"></Button> -->
            <Button pill color="yellow"></Button>
        </ButtonGroup>
    </div>
    <div id="accentColor flex" class="p-0 m-0">
        <p class="p-0 m-0">accent color</p>
        <ButtonGroup class="space-x-px">
            <Button pill color="blue"></Button>
            <Button pill color="green"></Button>
            <Button pill color="red"></Button>
            <!-- <Button pill color="primary"></Button> -->
            <Button pill color="yellow"></Button>
        </ButtonGroup>
    </div>
    <div id="accentColor flex" class="p-0 m-0">
        <p class="p-0 m-0">accent color</p>
        <ButtonGroup class="space-x-px">
            <Button pill color="blue"></Button>
            <Button pill color="green"></Button>
            <Button pill color="red"></Button>
            <!-- <Button pill color="primary"></Button> -->
            <Button pill color="yellow"></Button>
        </ButtonGroup>
    </div>
    <div id="transparency">
        <p class="p-0 m-0">transparency</p>
        <Range id="range1" min="0" max="100" bind:value={stepValue} size="md" />
    </div>
    <div class="h-1" />
</div>

<style>
    .row {
        display: flex;
        justify-content: space-between; /* Spread the buttons evenly */
        margin-bottom: 10px; /* Spacing between rows of buttons */
    }
    #modal-panel.expanded {
        height: 200px; /* Height when expanded */
    }
</style>
