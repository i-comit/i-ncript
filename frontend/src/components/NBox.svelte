<!-- NBox.svelte -->
<script lang="ts">
    import { onDestroy, onMount } from "svelte";
    import { Input, Tooltip } from "flowbite-svelte";
    import {
        AdjustmentsVerticalOutline,
        ArrowUpDownOutline,
        CloseOutline,
        EnvelopeOpenSolid,
        EnvelopeSolid,
        MailBoxSolid,
    } from "flowbite-svelte-icons";

    import { AppPage, currentPage } from "../enums/AppPage.ts";
    import { Modals, currentModal } from "../enums/Modals.ts";

    import {
        accentColor,
        darkColor,
        lightBGColor,
        tooltipTailwindClass,
    } from "../stores/globalVariables.ts";
    import {
        buildFileTree,
        currentFilePath,
        fileTree,
    } from "../tools/fileTree.ts";

    import { switchPages, switchModals, heldDownBtns } from "../tools/utils.ts";
    import NeuButton from "../elements/NeuButton.svelte";

    import Frame from "../elements/Frame.svelte";
    import Info from "./Info.svelte";
    import Settings from "./Settings.svelte";
    import TreeView from "./FileTree.svelte";
    import Logger from "./Logger.svelte";
    import NeuSearch from "../elements/NeuSearch.svelte";
    import PanelDivider from "../elements/PanelDivider.svelte";
    import DirectorySizeBar from "../elements/DirectorySizeBar.svelte";
    import { LogInfo } from "../../wailsjs/runtime/runtime";
    import NeuButtonFake from "../elements/NeuButtonFake.svelte";
    import { FileTasks, currentFileTask } from "../enums/FileTasks.ts";
    import WaveProgress from "../elements/WaveProgress.svelte";
    import TaskDisplay from "../elements/TaskDisplay.svelte";
    import FileTools from "../elements/FileTools.svelte";

    let _modal: Modals;
    currentModal.subscribe((value) => {
        _modal = value;
    });

    let _currentFileTask: FileTasks;
    currentFileTask.subscribe((value) => {
        _currentFileTask = value;
    });
    let _fileTaskPercent: number;
    let password: string;

    let _heldDownBtns: { [key: string]: HTMLButtonElement };
    heldDownBtns.subscribe((value) => {
        _heldDownBtns = value;
        handleOnFileClick();
        // checklastSelectedFileExtension();
    });

    // $: _currentFilePath = $currentFilePath;

    function checklastSelectedFileExtension() {
        const entries = Object.entries(_heldDownBtns);
        var lastEntry = entries[entries.length - 1];
        const [key, value] = lastEntry;

        if (lastEntry !== null) {
            LogInfo("Last entry rel path " + key);
        } else LogInfo("Last entry is null");
    }

    onMount(() => {
        buildFileTree();
        password = "";
    });

    function enterPassword(event: KeyboardEvent) {
        if (event.code === "Enter") {
            const inputElement = event.target as HTMLInputElement;
            password = inputElement.value;
        }
    }

    function clearPassword() {
        password = "";
    }

    function handleOnFileClick() {
        LogInfo("on heldDownBtns changed ");
        Object.keys(_heldDownBtns).forEach((key) => {
            LogInfo("Held down node moveFiles on MBOX: " + key);
        });
    }

    let passwordCheck1 = false; // Length check
    let passwordCheck2 = false; // Uppercase and lowercase check
    let passwordCheck3 = false; // Symbol check
    let passwordCheck = false; // Overall password strength check

    function queryPasswordStrength() {
        // Check 1: Length is more than 5 characters
        const regexLength = /^.{5,}$/;
        passwordCheck1 = regexLength.test(password);
        // Check 2: At least one uppercase and one lowercase letter
        const regexLetters = /^(?=.*[A-Z])(?=.*[a-z])/;
        passwordCheck2 = regexLetters.test(password);
        // Check 3: At least one symbol from the set !@#$
        const regexSymbol = /[~!@#$%^&*_=+<>:;]/;
        passwordCheck3 = regexSymbol.test(password);

        passwordCheck = passwordCheck1 && passwordCheck2 && passwordCheck3;
    }
</script>

<div class="flex h-screen !rounded-lg">
    <Frame />
    <div
        id="left-panel"
        class="max-w-45"
        style="background-color:{lightBGColor}"
    >
        <DirectorySizeBar />

        {#if _currentFileTask === FileTasks.None}
            <div class="row">
                <Input
                    class="max-h-1"
                    id="small-input"
                    placeholder="enter username.."
                />
            </div>
            <div class="flex w-full h-1 px-0.5 relative bottom-1">
                <div
                    class="flex-1 text-center rounded-lg"
                    style={`background-color: ${passwordCheck1 ? accentColor : darkColor};`}
                ></div>
                <Tooltip
                    placement="left"
                    class={tooltipTailwindClass}
                    arrow={false}>more than 4 characters</Tooltip
                >
            </div>
            {#if password === ""}
                <div style="height: 1.1rem">
                    <FileTools />
                </div>

                <div class="row">
                    <Input
                        class="max-h-1 m-0"
                        id="small-input"
                        placeholder="enter password.."
                        type="password"
                        on:keyup={(event) => enterPassword(event)}
                    />
                </div>
                <div class="flex w-full h-1 px-0.5 relative bottom-1">
                    <div
                        class="flex-1 text-center rounded-l-lg"
                        style={`background-color: ${passwordCheck1 ? accentColor : darkColor};`}
                    ></div>
                    <Tooltip
                        placement="left"
                        class={tooltipTailwindClass}
                        arrow={false}>more than 4 characters</Tooltip
                    >
                    <div
                        class="flex-1 text-center"
                        style={`background-color: ${passwordCheck2 ? accentColor : darkColor};`}
                    ></div>
                    <Tooltip
                        placement="bottom"
                        offset={0}
                        class={tooltipTailwindClass}
                        arrow={false}>a symbol ( #$&amp;! )</Tooltip
                    >
                    <div
                        class="flex-1 text-center rounded-r-lg"
                        style={`background-color: ${passwordCheck3 ? accentColor : darkColor};`}
                    ></div>
                    <Tooltip
                        placement="right"
                        class={tooltipTailwindClass}
                        arrow={false}>upper &amp; lower case</Tooltip
                    >
                </div>
            {:else}
                <div class="row">
                    <Input
                        class="max-h-1 m-0"
                        id="small-input"
                        placeholder="confirm password.."
                        type="password"
                        on:keyup={(event) => enterPassword(event)}
                    />
                    <button on:click={() => clearPassword()}>
                        <CloseOutline class=" text-blue-700 " />
                    </button>
                </div>
                <div class="flex w-full h-1.5 px-0.5 relative bottom-2">
                    <div
                        class="flex-1 text-center rounded-lg"
                        style={`background-color: ${passwordCheck1 ? accentColor : darkColor};`}
                    ></div>
                    <Tooltip
                        placement="left"
                        class={tooltipTailwindClass}
                        arrow={false}>must match password</Tooltip
                    >
                </div>
            {/if}
        {:else}
            <TaskDisplay />
            <div class="h-0.5" />
            <WaveProgress dataProgress={_fileTaskPercent}></WaveProgress>
        {/if}
        <div class="row space-x-5 space-evenly">
            <NeuButton on:click={() => switchPages(AppPage.Vault)}
                >VAULT</NeuButton
            >
            <NeuButtonFake></NeuButtonFake>
        </div>
    </div>
    <PanelDivider />

    <div
        id="right-panel"
        class="mt-0 px-0"
        style="background-color:{lightBGColor}"
    >
        <NeuSearch />
        {#if _modal === Modals.None}
            <TreeView _fileTree={$fileTree} />
        {:else if _modal === Modals.Settings}
            <Settings />
        {:else if _modal === Modals.Info}
            <Info />
        {:else if _modal === Modals.Logger}
            <Logger />
        {/if}
    </div>
</div>

<style>
    .row {
        display: flex;
        justify-content: space-between; /* Spread the buttons evenly */
        margin-bottom: 4px; /* Spacing between rows of buttons */
        margin-top: 4px;
    }
</style>
