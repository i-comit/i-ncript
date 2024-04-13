<!-- Login.svelte -->
<script>
    import { onMount } from "svelte";
    import { Accordion, AccordionItem } from "flowbite-svelte";
    import {
        FolderArrowRightOutline,
        FolderOpenSolid,
        PlaySolid,
    } from "flowbite-svelte-icons";

    import { LogInfo } from "../../../wailsjs/runtime/runtime";
    import { darkLightMode, accentColor } from "../../stores/dynamicVariables";

    import { OpenDirectory } from "../../../wailsjs/go/main/FileUtils";

    import {
        lightBGColor,
        darkBGColor,
        width,
        height,
    } from "../../stores/constantVariables";
    import {
        InitializeRootFolder,
        CloseApp,
        ResizeWindow,
    } from "../../../wailsjs/go/main/App";
    import {
        GetRootFolder,
        CheckRootFolderInCWD,
    } from "../../../wailsjs/go/main/Getters";

    import Frame from "../widgets/Frame.svelte";
    import NeuButton from "../widgets/NeuButton.svelte";

    let rootFolder = "";
    var rootFolderPath = "";
    onMount(async () => {
        rootFolder = await GetRootFolder();
        ResizeWindow(350, height + 10);
        checkForRootFolderInCWD();
    });
    function initializeRootFolder() {
        InitializeRootFolder().then(checkForRootFolderInCWD);
    }

    function checkForRootFolderInCWD() {
        CheckRootFolderInCWD().then((_rootFolderPath) => {
            if (_rootFolderPath) {
                rootFolderPath = _rootFolderPath;
                LogInfo();
            }
        });
    }
    let rootFolderClass = "text-primary-500 font-semibold select-text";
</script>

<div
    class="mb-2 w- left-1/2 rounded-bl-lg rounded-br-lg font-semibold z-10 select-none"
    style={`position: sticky; top: 0px;
    background-color: ${darkBGColor}; 
    color: ${$accentColor};`}
>
    incorrect directory
</div>
<Frame />

<div class="rounded-lg wrongdir px-1 text-center">
    <div class="h-8" />
    <p class="mx-2 select-none">
        i-ncript must run inside a directory named <span class={rootFolderClass}
            >{rootFolder}</span
        >
    </p>
    <div class="h-2" />
    <Accordion
        class="mx-1 outline bg-primary-200 outline"
        activeClass="focus:none"
    >
        <AccordionItem>
            <span slot="header">why is this step necessary?</span>
            <p class="mb-2 text-gray-500 dark:text-gray-400">
                For organizational and security purposes, i-ncript can only run
                within a specified directory, in this case: <span
                    class={rootFolderClass}>{rootFolder}</span
                >.
            </p>
            <p class="text-gray-500 dark:text-gray-400">
                This will prevent unintentional encryption of unrelated files
                from other subdirectories, keep encrypted files organized within
                the apps directory, and allow the app and its contents to be
                easily transferred to another device.
            </p>
        </AccordionItem>
        <AccordionItem>
            <span slot="header">how do I create a directory?</span>
            <p class="mb-2 text-gray-500 dark:text-gray-400">
                As i-ncript is designed to be a portable application that
                operates within a removable storage device, it must be entirely
                self contained inside a single directory.
            </p>
            <p class="mb-2 text-gray-500 dark:text-gray-400">
                You can create a directory named <span class={rootFolderClass}
                    >{rootFolder}</span
                >, preferably inside a removable drive such as a USB, then
                close the app and move the executable inside there.
            </p>
            {#if rootFolderPath === ""}
                <p class="text-gray-500 dark:text-gray-400">
                    Otherwise, click the [Create Folder] button below which will
                    generate the <span class={rootFolderClass}
                        >{rootFolder}</span
                    > directory for you to place the executable in.
                </p>
            {/if}
        </AccordionItem>
    </Accordion>
    <div class="h-3" />

    {#if rootFolderPath === ""}
        <NeuButton on:click={initializeRootFolder}>Create Folder</NeuButton>
    {:else}
        <div class="flex justify-center">
            <p
                class="text-primary-400 dark:text-primary-300 text-sm pl-2 leading-none !text-justify"
            >
                the <button
                    class={`${rootFolderClass} hover:outline`}
                    on:click={() => OpenDirectory(rootFolderPath)}
                    >{rootFolder}</button
                > directory has been found, close the app and place the executable
                inside there.
            </p>
            <button on:click={() => OpenDirectory(rootFolderPath)}>
                <FolderArrowRightOutline
                    class="mx-1.5 text-primary-200 w-10 h-10 outline-dotted border rounded-lg hover:text-primary-500"
                />
            </button>
        </div>
    {/if}
    <div class="h-2.5" />
</div>

<style>
    p {
        text-align: start;
    }
    .wrongdir {
        z-index: 8;
        position: absolute;
        bottom: 0;
        width: 100%;
        max-height: 100%;
        overflow-x: hidden;
        overflow-y: auto;
        box-sizing: border-box;
        scrollbar-width: none;
        /* For Firefox */
        -ms-overflow-style: none;
        scroll-behavior: smooth; /* Enables smooth scrolling */
    }
</style>
