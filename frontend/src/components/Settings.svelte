<!-- Settings.svelte -->
<script>
    import {
        Button,
        Input,
        GradientButton,
        Tooltip,
        Dropdown,
        DropdownItem,
        Range,
    } from "flowbite-svelte";
    import { switchFormButton, toggleSettings } from "../utils";
    import {
        InfoCircleOutline,
        AdjustmentsVerticalOutline,
        ChevronDownOutline,
        SunSolid,
        MoonSolid,
    } from "flowbite-svelte-icons";
    function closeSettingsPanel() {}
    function toggleLightDarkMode() {}

    let stepValue = 2.5;
</script>

<div class="settings-panel bg-gray-200">
    <div id="languages h-1 bg-gray-400">
        <Button
            >Languages<ChevronDownOutline
                class="w-3 h-3 ms-2 text-white dark:text-white "
            /></Button
        >
        <Dropdown>
            <DropdownItem>English</DropdownItem>
            <DropdownItem>French</DropdownItem>
            <DropdownItem>German</DropdownItem>
            <DropdownItem>Urdu</DropdownItem>
        </Dropdown>
    </div>
    <div id="changeUsername">
        <Input
            class="max-h-6 w-full"
            id="small-input"
            placeholder="enter username.."
        />
    </div>
    <div id="changePassword">
        <Input
            class="max-h-6 w-full"
            id="small-input"
            placeholder="enter username.."
        />
    </div>
    <div id="darkLightMode">
        <Button
            pill={true}
            outline={true}
            class="!p-1"
            color="dark"
            on:click={() => toggleLightDarkMode()}
            ><SunSolid class="w-5 h-5 m-0" color="dark" /></Button
        >
    </div>
    <div id="accentColor flex">
        <p>Accent Color</p>
        
    </div>
    <div id="transparency">
        <p>transparency</p>
        <Range id="range1" min="0" max="100" bind:value={stepValue} size="md" />
    </div>
</div>
