<script lang="ts">
    import { onDestroy, onMount } from "svelte";
    import {
        Button,
        DarkMode,
        Dropdown,
        DropdownItem,
        Modal,
    } from "flowbite-svelte";

    import { darkBGColor, height } from "../../stores/constantVariables";
    import { accentColor, darkLightMode } from "../../stores/dynamicVariables";
    import Frame from "../widgets/Frame.svelte";
    import License from "../modals/License.svelte";
    import { ChevronDownOutline } from "flowbite-svelte-icons";
    import { darkLightBGOnHTML } from "../../tools/themes";

    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightBGOnHTML(value);
    });
    onMount(async () => {
        darkLightBGOnHTML($darkLightMode);
    });

    onDestroy(() => {
        unsub_darkLightMode();
    });
    function toggleLightDarkMode() {
        darkLightMode.update((v) => !v);
    }
</script>

<div
    class="mb-2 w-1/3 left-1/3 rounded-bl-lg rounded-br-lg font-semibold z-10 select-none bg-primary-400 dark:bg-primary-300"
    style={`position: sticky; top: 0px;
    color: ${$accentColor};`}
>
    first time setup
</div>
<Frame />

<div
    class="flex justify-between rounded-xl mx-1"
    style={`background-color: ${$accentColor}`}
>
    <button class="!p-0" on:click={() => toggleLightDarkMode()}>
        <DarkMode btnClass="w-6 h-6 rounded-lg text-md p-2" />
    </button>
    <License />
    <div id="languages">
        <Button
            data-placement="left-start"
            class="outline h-5 mt-2 mr-2"
            size="xs"
            style={`background-color: ${$accentColor}`}
            >Languages<ChevronDownOutline
                class="w-5 h-5 text-white dark:text-white"
            /></Button
        >
        <Dropdown class="">
            <DropdownItem
                defaultClass="font-medium py-0.5  px-1 text-sm hover:bg-gray-100 dark:hover:bg-gray-400"
                >English</DropdownItem
            >
            <DropdownItem
                defaultClass="font-medium py-0.5 px-1 text-sm hover:bg-gray-100 dark:hover:bg-gray-400"
                >French</DropdownItem
            >
            <DropdownItem
                defaultClass="font-medium py-0.5 px-1 text-sm hover:bg-gray-100 dark:hover:bg-gray-400"
                >German</DropdownItem
            >
            <DropdownItem
                defaultClass="font-medium py-0.5  px-1 text-sm hover:bg-gray-100 dark:hover:bg-gray-400"
                >Urdu</DropdownItem
            >
        </Dropdown>
    </div>
</div>
