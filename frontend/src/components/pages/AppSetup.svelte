<script lang="ts">
    import { onDestroy, onMount } from "svelte";
    import {
        Button,
        DarkMode,
        Dropdown,
        DropdownItem,
        GradientButton,
    } from "flowbite-svelte";
    import { LogInfo } from "../../../wailsjs/runtime/runtime";

    import { ArrowLeftOutline, ArrowRightOutline } from "flowbite-svelte-icons";
    import { darkBGColor, height, width } from "../../stores/constantVariables";
    import { accentColor, darkLightMode } from "../../stores/dynamicVariables";
    import Frame from "../widgets/Frame.svelte";
    import License from "../modals/License.svelte";
    import { ChevronDownOutline } from "flowbite-svelte-icons";
    import { darkLightBGOnHTML } from "../../tools/themes";
    import Introduction from "./setup_pages/Introduction.svelte";
    import Info_Vault from "./setup_pages/Info_Vault.svelte";
    import Info_MBox from "./setup_pages/Info_MBox.svelte";
    import CreateAccount from "./setup_pages/CreateAccount.svelte";
    import { AppPage, currentPage } from "../../enums/AppPage";
    import { ResizeWindow } from "../../../wailsjs/go/main/App";

    enum SetupPage {
        Intro = "introduction",
        Vault = "VAULT page",
        M_Box = "M-BOX page",
        CreateAccount = "create account",
    }
    let currentSetupPage: SetupPage = SetupPage.Intro;
    let setupPageContainer: HTMLDivElement;

    function toggleLightDarkMode() {
        darkLightMode.update((v) => !v);
    }

    function goToLoginPage() {
        currentPage.set(AppPage.Login);
        ResizeWindow(width, height + 5);
        LogInfo("going to login page");
    }

    function goToNextPage() {
        switch (currentSetupPage) {
            case SetupPage.Intro:
                currentSetupPage = SetupPage.Vault;
                break;
            case SetupPage.Vault:
                currentSetupPage = SetupPage.M_Box;
                break;
            case SetupPage.M_Box:
                currentSetupPage = SetupPage.CreateAccount;
                break;
        }
        if (setupPageContainer) {
            setupPageContainer.scrollTop = 0;
        }
    }

    function goToPreviousPage() {
        switch (currentSetupPage) {
            case SetupPage.CreateAccount:
                currentSetupPage = SetupPage.M_Box;
                break;
            case SetupPage.M_Box:
                currentSetupPage = SetupPage.Vault;
                break;
            case SetupPage.Vault:
                currentSetupPage = SetupPage.Intro;
                break;
        }
        if (setupPageContainer) {
            setupPageContainer.scrollTop = 0;
        }
    }
</script>

<div
    class="mb-2 w-1/2 left-1/4 rounded-bl-lg rounded-br-lg font-semibold select-none z-10
            bg-primary-300 dark:bg-primary-400 outline outline-1 outline-primary-400 dark:outline-primary-300"
    style={`position: sticky; top: 0px; color: ${$accentColor}; font-family: "Orbitron"; font-weight: 600;`}
>
    first time setup
</div>
<Frame />

<div
    class="flex justify-between rounded-xl mx-6"
    style={`background-color: ${$accentColor}`}
>
    <button class="!p-0" on:click={() => toggleLightDarkMode()}>
        <DarkMode btnClass="w-6 h-6 rounded-lg text-md p-2" />
    </button>
    <License />
    <div id="languages">
        <Button
            data-placement="left-start"
            class="outline outline-1 h-5 mt-2 mr-2"
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
<div
    class="setupPage h-[65vh] outline outline-1 outline-primary-200 dark:outline-primary-100 px-4 pt-1 pb-4 flex flex-col"
    style="color: #eeeeee;"
    bind:this={setupPageContainer}
>
    {#if currentSetupPage === SetupPage.Intro}
        <Introduction on:click={goToLoginPage} />
    {:else if currentSetupPage === SetupPage.Vault}
        <Info_Vault />
    {:else if currentSetupPage === SetupPage.M_Box}
        <Info_MBox />
    {:else if currentSetupPage === SetupPage.CreateAccount}
        <CreateAccount on:click={goToLoginPage} />
    {/if}
</div>
<div
    class="mt-2 w-1/3 left-1/3 rounded-tl-lg rounded-tr-lg font-semibold select-none z-10
            bg-primary-300 dark:bg-primary-400 outline outline-1 outline-primary-400 dark:outline-primary-300"
    style={`position: fixed; bottom: 0px;
color: ${$accentColor};`}
>
    {currentSetupPage}
</div>

<div class="flex justify-between absolute bottom-[0.1rem] w-full px-0 z-20">
    {#if currentSetupPage !== SetupPage.Intro}
        <button on:click={goToPreviousPage}>
            <ArrowLeftOutline
                class="outline outline-1 bg-primary-400 dark:bg-primary-300 rounded-tr-md text-primary-100 dark:text-primary-200"
                size="lg"
            /></button
        >
    {:else}
        <button disabled>
            <ArrowLeftOutline
                class="outline outline-1 bg-primary-400 dark:bg-primary-300 
                        rounded-tr-md text-primary-100 dark:text-primary-200 opacity-0"
                size="lg"
            /></button
        >
    {/if}
    {#if currentSetupPage !== SetupPage.CreateAccount}
        <button on:click={goToNextPage}>
            <ArrowRightOutline
                class="outline outline-1 bg-primary-400 dark:bg-primary-300 rounded-tl-md text-primary-100 dark:text-primary-200"
                size="lg"
            /></button
        >
    {:else}
        <button disabled>
            <ArrowRightOutline
                class="outline outline-1 bg-primary-400 dark:bg-primary-300
                        rounded-tl-md text-primary-100 dark:text-primary-200 opacity-0"
                size="lg"
            /></button
        >
    {/if}
</div>

<style>
    .setupPage {
        z-index: 8;
        position: absolute;
        bottom: 8px;
        width: 100%; /* Adjust if necessary */
        max-height: 100%; /* Example maximum height; adjust as needed */
        overflow-x: hidden; /* Enables horizontal scrolling */
        /* overflow-y: auto; */
        text-align: justify;
        scrollbar-width: none;
        /* For Firefox */
        -ms-overflow-style: none;
        scroll-behavior: smooth; /* Enables smooth scrolling */
    }
</style>
