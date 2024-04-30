<!-- MBox.svelte -->
<script lang="ts">
    import { onMount, onDestroy } from "svelte";
    import { Input, Tooltip } from "flowbite-svelte";
    import { CloseOutline } from "flowbite-svelte-icons";

    import { AppPage, currentPage } from "../../enums/AppPage.ts";
    import { Modals, currentModal } from "../../enums/Modals.ts";
    import { FileTasks, currentFileTask } from "../../enums/FileTasks.ts";
    import { FileTypes, getFileType } from "../../enums/FileTypes.ts";

    import {
        darkInputColor,
        darkTextColor,
        lightInputColor,
        lightTextColor,
        tooltipTailwindClass,
    } from "../../stores/constantVariables.ts";
    import {
        darkLightMode,
        fileTaskPercent,
        largeFileName,
        largeFilePercent,
        accentColor,
        pageLoading,
        duplicateFiles,
    } from "../../stores/dynamicVariables.ts";

    import {
        buildFileTree,
        clearHeldBtns,
        clearHeldBtnsFromContainer,
        fileTree,
    } from "../../tools/fileTree.ts";
    import { EventsOff, LogInfo } from "../../../wailsjs/runtime/runtime";

    import {
        switchPages,
        heldDownBtns,
        prependAbsPathToRelPaths,
        getRootDir,
        retrieveDuplicateFiles,
    } from "../../tools/utils.ts";
    import { startDisplay } from "../../tools/logger.ts";

    import { EncryptENCPFile } from "../../../wailsjs/go/main/App";
    import {
        AuthenticateENCPFile,
        PackFilesForENCP,
    } from "../../../wailsjs/go/main/FileUtils";

    import TitleBar from "../widgets/TitleBar.svelte";
    import PanelDivider from "../widgets/PanelDivider.svelte";
    import FileTools from "../widgets/FileTools.svelte";
    import DirSize from "../widgets/DirectorySize.svelte";

    import NeuButton from "../widgets/NeuButton.svelte";
    import NeuButtonFake from "../widgets/NeuButtonFake.svelte";
    import WaveProgress from "../widgets/WaveProgress.svelte";
    import TaskDisplay from "../widgets/TaskDisplay.svelte";

    import Info from "../modals/Info.svelte";
    import Settings from "../modals/Settings.svelte";
    import TreeView from "../modals/FileTree.svelte";
    import Logger from "../modals/Logger.svelte";
    import PasswordScan from "../widgets/PasswordScan.svelte";

    import RadialProgress from "../widgets/RadialProgress.svelte";
    import DuplicateFiles from "../modals/DuplicateFiles.svelte";
    import GridSpinner from "../widgets/GridSpinner.svelte";
    import ModalButtons from "../widgets/ModalButtons.svelte";

    enum MboxState {
        Pack = "PACK",
        Open = "OPEN",
        None = "",
    }

    let currentMBoxState: MboxState = MboxState.None;

    let _modal: Modals;
    currentModal.subscribe((value) => {
        _modal = value;
    });

    let _currentFileTask: FileTasks;
    currentFileTask.subscribe((value) => {
        _currentFileTask = value;
    });

    let password: string;
    let enteredPassword: string = "";

    let username: string;
    let usernameCheck: boolean = false;

    let checks = {
        passwordCheck1: false,
        passwordCheck2: false,
        passwordCheck3: false,
        passwordCheck: false,
    };

    let passwordMatch: boolean;

    let _heldDownBtns: { [key: string]: HTMLButtonElement };

    const unsub_heldDownBtns = heldDownBtns.subscribe((value) => {
        _heldDownBtns = value;
        handleOnFileClick();
    });

    onMount(() => {
        pageLoading.set(true);
        currentMBoxState = MboxState.None;
        buildFileTree();
        if (_currentFileTask === FileTasks.None) retrieveDuplicateFiles();

        clearUsername();
        clearPassword();
    });

    onDestroy(() => {
        unsub_heldDownBtns();
        EventsOff("fileProcessed");
    });
    function handleOnFileClick() {
        LogInfo("on heldDownBtns changed ");

        if (Object.keys(_heldDownBtns).length === 0) {
            LogInfo("heldDownBtns is empty!");
            currentMBoxState = MboxState.None;
            return;
        }
        Object.keys(_heldDownBtns).forEach((key) => {
            LogInfo("Held down node moveFiles on MBOX: " + key);
        });
        const entries = Object.entries(_heldDownBtns);
        var lastEntry = entries[entries.length - 1];

        const [key, _] = lastEntry;
        if (lastEntry) {
            LogInfo("Last entry rel path " + key);
            var lastFileType = getFileType(key);
            if (
                lastFileType === FileTypes.EncryptedP ||
                lastFileType === FileTypes.Encrypted
            ) {
                currentMBoxState = MboxState.Open;
            } else currentMBoxState = MboxState.Pack;
        }
    }

    function enterPassword(event: KeyboardEvent) {
        if (event.code === "Enter")
            if (checks.passwordCheck && !enteredPassword) {
                if (currentMBoxState === MboxState.Pack) {
                    const inputElement = event.target as HTMLInputElement;
                    enteredPassword = inputElement.value;
                }
                if (currentMBoxState === MboxState.Open) openENCPFiles();
                password = "";
                Object.keys(checks).forEach((key) => {
                    checks[key as keyof typeof checks] = false;
                });
            } else if (passwordMatch && enteredPassword) {
                packFilesToENCP();
            }
    }

    function enterPasswordBtn() {
        enteredPassword = password;
        password = "";
        Object.keys(checks).forEach((key) => {
            checks[key as keyof typeof checks] = false;
        });
    }

    function clearUsername() {
        username = "";
        usernameCheck = false;
    }
    function clearPassword() {
        password = "";
        Object.keys(checks).forEach((key) => {
            checks[key as keyof typeof checks] = false;
        });
        enteredPassword = "";
    }

    function checkMatchedPassword() {
        passwordMatch = password === enteredPassword;
    }

    function queryUsernameStrength() {
        const regex = /^.{5,}$/;
        usernameCheck = regex.test(username);
        if (username === "") enteredPassword = "";
    }

    function handlePasswordStrengthUpdated(event: CustomEvent) {
        const {
            passwordCheck1,
            passwordCheck2,
            passwordCheck3,
            passwordCheck,
        } = event.detail;
        checks = {
            passwordCheck1,
            passwordCheck2,
            passwordCheck3,
            passwordCheck,
        };
    }

    function packFilesToENCP() {
        prependAbsPathToRelPaths(1).then((absFilePaths) => {
            currentFileTask.set(FileTasks.Pack);
            PackFilesForENCP(username, password, absFilePaths).then(
                (zipFilePath) => {
                    if (zipFilePath) {
                        currentFileTask.set(FileTasks.Encrypt);
                        EncryptENCPFile(username, password, zipFilePath)
                            .then((encpFinished) => {
                                if (encpFinished) {
                                    startDisplay(
                                        `packed ${absFilePaths.length} files`,
                                    );
                                    clearHeldBtns();
                                    buildFileTree();
                                }
                            })
                            .finally(() => {
                                currentFileTask.set(FileTasks.None);
                                clearUsername();
                                clearPassword();
                            });
                    }
                },
            );
        });
    }

    function openENCPFiles() {
        const entries = Object.entries(_heldDownBtns);
        var lastEntry = entries[entries.length - 1];
        var lastFileType = getFileType(lastEntry[0]);
        if (lastFileType === FileTypes.EncryptedP) {
            currentFileTask.set(FileTasks.Open);
            AuthenticateENCPFile(password, getRootDir() + lastEntry[0])
                .then((authenticated) => {
                    if (authenticated) {
                        startDisplay("successfully opened");
                        clearHeldBtns();
                        buildFileTree();
                    } else startDisplay("failed to open");
                })
                .catch(() => {
                    startDisplay("failed to open");
                })
                .finally(() => {
                    currentFileTask.set(FileTasks.None);
                    clearUsername();
                    clearPassword();
                });
        }
    }
</script>

<TitleBar />
<GridSpinner />
<DuplicateFiles />
<div
    class="flex h-screen !rounded-lg {$pageLoading || $duplicateFiles.length > 0
        ? 'pointer-events-none opacity-40'
        : ''}"
>
    <div
        id="left-panel"
        role="none"
        class="bg-primary-400 dark:bg-primary-300"
        on:click={() => {
            clearHeldBtnsFromContainer();
            clearUsername();
            clearPassword();
        }}
        on:pointerdown={() => {
            currentModal.set(Modals.None);
        }}
    >
        <DirSize />
        <div class="h-1/3">
            {#if currentMBoxState === MboxState.None}
                {#if _currentFileTask === FileTasks.None}
                    <div class="text-primary-100 dark:text-primary-200">
                        <p
                            class="text-center text-sm"
                            style="font-family: 'Orbitron'; font-weight: regular;"
                        >
                            no files selected..
                        </p>
                    </div>
                {:else}
                    <TaskDisplay />
                    <WaveProgress dataProgress={$fileTaskPercent} />
                    <div class="h-0.5" />
                {/if}
            {:else if currentMBoxState === MboxState.Open}
                {#if _currentFileTask === FileTasks.None}
                    <div style="height: 3.2rem">
                        <div style="height: 1.95rem" />
                        <FileTools />
                    </div>
                    <div class="h-1.5" />
                    <div class="row" role="none" on:click|stopPropagation>
                        <Input
                            class="max-h-5 m-0"
                            style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                                color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                            placeholder="enter password.."
                            type="password"
                            bind:value={password}
                            on:keyup={(event) => enterPassword(event)}
                        />
                    </div>
                    <PasswordScan
                        _class="!bottom-1"
                        {password}
                        on:passwordStrengthUpdated={handlePasswordStrengthUpdated}
                    />
                {:else}
                    <TaskDisplay />
                    <WaveProgress dataProgress={$fileTaskPercent} />
                    <div class="h-0.5" />
                {/if}
            {:else if currentMBoxState === MboxState.Pack}
                {#if _currentFileTask === FileTasks.None}
                    <div class="row" role="none" on:click|stopPropagation>
                        <Input
                            class="max-h-5"
                            style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                                color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                            placeholder="enter username.."
                            type="text"
                            bind:value={username}
                            on:input={queryUsernameStrength}
                        />
                    </div>
                    <div
                        class="flex w-full h-1.5 px-0.5 relative bottom-1"
                        tabindex="-1"
                    >
                        {#if usernameCheck === false}
                            <div
                                class="flex-1 text-center rounded-lg bg-primary-300 dark:bg-primary-400"
                            />
                        {:else}
                            <div
                                class="flex-1 text-center rounded-lg"
                                style={`background-color: ${$accentColor};`}
                            />
                        {/if}
                        <Tooltip
                            placement="left"
                            class={tooltipTailwindClass}
                            arrow={false}>more than 4 characters</Tooltip
                        >
                    </div>
                    <div class="h-5">
                        <FileTools />
                    </div>
                    <div class="h-1.5" />
                    {#if !enteredPassword}
                        <div class="row" role="none" on:click|stopPropagation>
                            <Input
                                class="max-h-5 m-0"
                                style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                                    color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                                placeholder="enter password.."
                                type="password"
                                bind:value={password}
                                on:keyup={(event) => enterPassword(event)}
                            />
                        </div>
                        <PasswordScan
                            _class="!bottom-1"
                            {password}
                            on:passwordStrengthUpdated={handlePasswordStrengthUpdated}
                        />
                    {:else}
                        <div class="row" role="none" on:click|stopPropagation>
                            <Input
                                class="max-h-5 m-0"
                                style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                            color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                                placeholder="confirm password.."
                                type="password"
                                bind:value={password}
                                on:input={checkMatchedPassword}
                                on:keyup={(event) => enterPassword(event)}
                            />
                            <button
                                on:pointerdown|stopPropagation={clearPassword}
                                tabindex={-1}
                            >
                                <CloseOutline style="color: {$accentColor};" />
                            </button>
                        </div>
                        <div
                            class="flex w-full h-1.5 px-0.5 relative bottom-1"
                            tabindex="-1"
                        >
                            {#if !passwordMatch}
                                <div
                                    class="flex-1 text-center rounded-lg bg-primary-300 dark:bg-primary-400"
                                />
                            {:else}
                                <div
                                    class="flex-1 text-center rounded-lg"
                                    style={`background-color: ${$accentColor};`}
                                />
                            {/if}
                            <Tooltip
                                placement="left"
                                class={tooltipTailwindClass}
                                arrow={false}>must match password</Tooltip
                            >
                        </div>
                    {/if}
                {:else}
                    <TaskDisplay />
                    <WaveProgress dataProgress={$fileTaskPercent} />
                    <div class="h-0.5" />
                {/if}
            {/if}
        </div>
        <div class="h-1/2">
            <div class="relative top-[2.25rem]">
                <ModalButtons />
            </div>
            <div
                class="flex justify-between space-x-3.5 space-evenly relative top-[2.85rem]"
            >
                <NeuButton on:click={() => switchPages(AppPage.Vault)}
                    >VAULT</NeuButton
                >
                {#if currentMBoxState === MboxState.Pack}
                    {#if !enteredPassword}
                        {#if checks.passwordCheck}
                            <NeuButton on:click={() => enterPasswordBtn()}
                                >ENTER</NeuButton
                            >
                        {:else}
                            <NeuButtonFake></NeuButtonFake>
                        {/if}
                    {:else if passwordMatch}
                        <NeuButton on:click={packFilesToENCP}>PACK</NeuButton>
                    {:else}
                        <NeuButtonFake></NeuButtonFake>
                    {/if}
                {:else if currentMBoxState === MboxState.Open}
                    {#if Object.keys(_heldDownBtns).length > 0 && checks.passwordCheck}
                        <NeuButton on:click={openENCPFiles}>OPEN</NeuButton>
                    {:else}
                        <NeuButtonFake></NeuButtonFake>
                    {/if}
                {:else}
                    <NeuButtonFake></NeuButtonFake>
                {/if}
            </div>
        </div>
    </div>
    <PanelDivider />

    <div
        id="right-panel"
        role="none"
        class="bg-primary-400 dark:bg-primary-300"
        on:mouseleave={onmouseleave}
        on:click={() => {
            clearHeldBtnsFromContainer();
        }}
    >
        <RadialProgress
            _style="right: 3.4rem"
            dataProgress={$largeFilePercent}
            overlayText={$largeFileName}
        />
        {#if _modal === Modals.None}
            {#if !$pageLoading}
                <TreeView _fileTree={$fileTree} />
            {/if}
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
    }
</style>
