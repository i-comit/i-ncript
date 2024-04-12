<!-- NBox.svelte -->
<script lang="ts">
    import { onMount, onDestroy } from "svelte";
    import { get } from "svelte/store";
    import { Input, Tooltip } from "flowbite-svelte";
    import { CloseOutline } from "flowbite-svelte-icons";

    import { AppPage, currentPage } from "../../enums/AppPage.ts";
    import { Modals, currentModal } from "../../enums/Modals.ts";
    import { FileTasks, currentFileTask } from "../../enums/FileTasks.ts";

    import {
        accentColor,
        darkBGColor,
        darkInputColor,
        darkTextColor,
        lightBGColor,
        lightInputColor,
        lightTextColor,
        tooltipTailwindClass,
    } from "../../stores/constantVariables.ts";
    import {
        darkLightMode,
        fileCount,
        fileTaskPercent,
        largeFileName,
        largeFilePercent,
        totalFileCt,
    } from "../../stores/dynamicVariables.ts";

    import {
        buildFileTree,
        clearHeldBtns,
        clearHeldBtnsFromContainer,
        fileTree,
    } from "../../tools/fileTree.ts";

    import {
        switchPages,
        heldDownBtns,
        prependAbsPathToRelPaths,
        getRootDir,
    } from "../../tools/utils.ts";

    import { darkLightBGOnId } from "../../tools/themes";

    import {
        EventsOff,
        EventsOn,
        LogDebug,
        LogInfo,
    } from "../../../wailsjs/runtime/runtime";

    import Frame from "../widgets/Frame.svelte";
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
    import { FileTypes, getFileType } from "../../enums/FileTypes.ts";
    import PasswordScan from "../widgets/PasswordScan.svelte";
    import {
        AuthenticateENCPFile,
        PackFilesForENCP,
    } from "../../../wailsjs/go/main/FileUtils";
    import { EncryptENCPFile } from "../../../wailsjs/go/main/App";
    import RadialProgress from "../widgets/RadialProgress.svelte";
    import { startDisplay } from "../../tools/logger.ts";

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

    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightBGOnId(value, "right-panel");
        darkLightBGOnId(value, "left-panel");
    });

    onMount(() => {
        buildFileTree();
        EventsOn("fileProcessed", (fileCtEvt: number) => {
            fileCount.set(fileCtEvt);
            fileTaskPercent.set(
                Math.round(($fileCount / get(totalFileCt)) * 100),
            );
            if (fileCtEvt === 0) currentFileTask.set(FileTasks.None);
        });
        darkLightBGOnId(get(darkLightMode), "right-panel");
        darkLightBGOnId(get(darkLightMode), "left-panel");
        clearUsername();
        clearPassword();
    });

    onDestroy(() => {
        unsub_heldDownBtns();
        unsub_darkLightMode();
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

        const [key, value] = lastEntry;
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
        if (event.code === "Enter") {
            password = "";
            const inputElement = event.target as HTMLInputElement;
            enteredPassword = inputElement.value;
        }
    }

    function enterPasswordBtn() {
        enteredPassword = password;
        password = "";
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
    }
    // Function to handle the custom event
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

    function packFilesForENCP() {
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

    function authenticateENCPFile() {
        const entries = Object.entries(_heldDownBtns);
        var lastEntry = entries[entries.length - 1];

        var lastFileType = getFileType(lastEntry[0]);
        if (lastFileType === FileTypes.EncryptedP) {
            AuthenticateENCPFile(password, getRootDir() + lastEntry[0])
                .then((authenticated) => {
                    if (authenticated) {
                        startDisplay("successfully opened");
                        clearHeldBtns();
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

<div class="flex h-screen !rounded-lg">
    <Frame />
    <div
        id="left-panel"
        role="none"
        on:click={() => {
            clearHeldBtnsFromContainer();
            clearUsername();
            clearPassword();
        }}
    >
        <DirSize />
        {#if currentMBoxState === MboxState.None}
            <div class="h-12">
                <p class="text-start">no selected files</p>
            </div>
            <div class="h-9">
                <p class="text-start">text</p>
            </div>
        {:else if currentMBoxState === MboxState.Open}
            {#if _currentFileTask === FileTasks.None}
                <div style="height: 3.175rem">
                    <div style="height: 2.075rem" />
                    <FileTools />
                </div>
                <div class="row" role="none" on:click|stopPropagation>
                    <Input
                        class="max-h-1 m-0"
                        style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                                color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                        id="small-input"
                        placeholder="enter password.."
                        type="password"
                        bind:value={password}
                        on:keyup={(event) => enterPassword(event)}
                    />
                </div>
                <PasswordScan
                    {password}
                    on:passwordStrengthUpdated={handlePasswordStrengthUpdated}
                />
            {:else}
                <TaskDisplay />
                <div class="h-0.5" />
                <WaveProgress dataProgress={$fileTaskPercent}></WaveProgress>
            {/if}
        {:else if currentMBoxState === MboxState.Pack}
            {#if _currentFileTask === FileTasks.None}
                <div class="row" role="none" on:click|stopPropagation>
                    <Input
                        class="max-h-1"
                        style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                                color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                        id="small-input"
                        placeholder="enter username.."
                        type="text"
                        bind:value={username}
                        on:keyup={queryUsernameStrength}
                    />
                </div>
                <div
                    class="flex w-full h-1 px-0.5 relative bottom-1"
                    tabindex="-1"
                >
                    <div
                        class="flex-1 text-center rounded-lg"
                        style={`background-color: ${usernameCheck ? accentColor : darkBGColor};`}
                    ></div>
                    <Tooltip
                        placement="left"
                        class={tooltipTailwindClass}
                        arrow={false}>more than 4 characters</Tooltip
                    >
                </div>
                {#if !enteredPassword}
                    <div style="height: 1.1rem">
                        <FileTools />
                    </div>

                    <div class="row" role="none" on:click|stopPropagation>
                        <Input
                            class="max-h-1 m-0"
                            style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                                    color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                            id="small-input"
                            placeholder="enter password.."
                            type="password"
                            bind:value={password}
                            on:keyup={(event) => enterPassword(event)}
                        />
                    </div>
                    <PasswordScan
                        {password}
                        on:passwordStrengthUpdated={handlePasswordStrengthUpdated}
                    />
                {:else}
                    <div style="height: 1.1rem">
                        <FileTools />
                    </div>
                    <div class="row" role="none" on:click|stopPropagation>
                        <Input
                            class="max-h-1 m-0"
                            style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                            color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                            id="small-input"
                            placeholder="confirm password.."
                            type="password"
                            bind:value={password}
                            on:keyup={checkMatchedPassword}
                        />
                        <button on:click|stopPropagation={clearPassword}>
                            <CloseOutline class=" text-blue-700 " />
                        </button>
                    </div>
                    <div
                        class="flex w-full h-1 px-0.5 relative bottom-1"
                        tabindex="-1"
                    >
                        <div
                            class="flex-1 text-center rounded-lg"
                            style={`background-color: ${passwordMatch ? accentColor : darkBGColor};`}
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
                <WaveProgress dataProgress={$fileTaskPercent}></WaveProgress>
            {/if}
        {/if}

        <div class="row space-x-5 space-evenly">
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
                        <NeuButtonFake>ENTER</NeuButtonFake>
                    {/if}
                {:else if passwordMatch}
                    <NeuButton on:click={packFilesForENCP}>PACK</NeuButton>
                {:else}
                    <NeuButtonFake>PACK</NeuButtonFake>
                {/if}
            {:else if currentMBoxState === MboxState.Open}
                {#if Object.keys(_heldDownBtns).length > 0}
                    <NeuButton on:click={() => authenticateENCPFile()}
                        >OPEN</NeuButton
                    >
                {:else}
                    <NeuButtonFake>OPEN</NeuButtonFake>
                {/if}
            {:else}
                <NeuButtonFake></NeuButtonFake>
            {/if}
        </div>
    </div>
    <PanelDivider />

    <div
        id="right-panel"
        role="none"
        on:mouseleave={onmouseleave}
        on:click={() => {
            clearHeldBtnsFromContainer();
        }}
    >
        <!-- <NeuSearch /> -->
        <RadialProgress
            _style="right: 3.6rem"
            dataProgress={$largeFilePercent}
            overlayText={$largeFileName}
        />
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
