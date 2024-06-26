<!-- Login.svelte -->
<script lang="ts">
    import { createEventDispatcher, onMount, onDestroy } from "svelte";

    import { Modals, currentModal } from "../../enums/Modals";

    import { Login } from "../../../wailsjs/go/main/App";
    import { Button, Input, Tooltip, Spinner } from "flowbite-svelte";
    import { formatNumber, switchModals } from "../../tools/utils";
    import {
        InfoCircleOutline,
        CogSolid,
        CloseOutline,
    } from "flowbite-svelte-icons";

    import {
        startDisplay,
        stopDisplay,
        getDisplayString,
        alertInterval,
    } from "../../tools/logger";

    import TitleBar from "../widgets/TitleBar.svelte";
    import Info from "../modals/Info.svelte";
    import Settings from "../modals/Settings.svelte";

    import NeuButton from "../widgets/NeuButton.svelte";
    import {
        tooltipTailwindClass,
        darkTextColor,
        lightTextColor,
        darkInputColor,
        lightInputColor,
    } from "../../stores/constantVariables.ts";

    import {
        darkLightMode,
        accentColor,
        newAccount,
        pageLoading,
        loadedFileCt,
    } from "../../stores/dynamicVariables.ts";

    import { LogError } from "../../../wailsjs/runtime/runtime";

    import PasswordScan from "../widgets/PasswordScan.svelte";
    import NeuButtonFake from "../widgets/NeuButtonFake.svelte";
    import {
        GetDiskSpacePercent,
        GetFormattedDiskSpace,
    } from "../../../wailsjs/go/main/Getters";

    let typewriter = "";
    const dispatch = createEventDispatcher();

    const appName: string = __APP_NAME__;
    let username = "";
    let password = "";
    let enteredPassword: string = "";
    let passwordMatch = false;

    let formattedTotalDriveSize: string;
    let formattedFreeDriveSize: string;
    let diskSpacePercent: number;

    let loginForm: HTMLFormElement;
    let loginBtn: HTMLButtonElement;

    let _modal: Modals;
    currentModal.subscribe((value) => {
        _modal = value;
    });

    let usernameCheck = false;

    let checks = {
        passwordCheck1: false,
        passwordCheck2: false,
        passwordCheck3: false,
        passwordCheck: false,
    };

    onMount(() => {
        const interval = setInterval(() => {
            typewriter = getDisplayString();
        }, alertInterval);

        GetFormattedDiskSpace(true)
            .then((totalDriveSize) => {
                formattedTotalDriveSize = totalDriveSize;
                // Return the next promise in the chain
                return GetFormattedDiskSpace(false);
            })
            .then((freeDriveSize) => {
                formattedFreeDriveSize = freeDriveSize;
                return GetDiskSpacePercent();
            })
            .then((_diskSpacePercent) => {
                diskSpacePercent = _diskSpacePercent;
            })
            .catch((error) => {
                LogError("An error occurred:" + error);
            });

        return () => {
            clearInterval(interval);
        };
    });

    onDestroy(() => {
        stopDisplay();
    });

    async function verifyLogin(): Promise<boolean> {
        pageLoading.set(true);
        try {
            const loginResult = await Login(username, password); // Await the promise here directly
            switch (loginResult) {
                case 0: // File does not exist
                case 2: // Hash matched with key
                    dispatch("loginSuccess");
                    return true;
                case 1: // File is empty
                    startDisplay("key file is empty...");
                    pageLoading.set(false);
                    return false;
                case 3: // Wrong credentials
                    startDisplay("wrong credentials...");
                    pageLoading.set(false);
                    return false;
                default:
                    LogError("Unexpected login result: " + loginResult);
                    pageLoading.set(false);
                    return false;
            }
        } catch (error) {
            LogError("Error calling Login method: " + error);
            return false;
        }
    }

    function submit(event: SubmitEvent) {
        event.preventDefault();
        verifyLogin();
    }

    function enterPassword(event: KeyboardEvent) {
        if (event.code === "Enter" && checks.passwordCheck) {
            if ($newAccount && !enteredPassword) {
                password = "";
                const inputElement = event.target as HTMLInputElement;
                enteredPassword = inputElement.value;
                Object.keys(checks).forEach((key) => {
                    checks[key as keyof typeof checks] = false;
                });
            } else if (!$newAccount)
                verifyLogin().then((loginBool) => {
                    if (!loginBool) {
                        username = "";
                        clearPassword();
                        pageLoading.set(false);
                    }
                });
        }
    }

    function queryUsernameStrength() {
        const regex = /^.{5,}$/;
        usernameCheck = regex.test(username);
        if (username === "") enteredPassword = "";
    }

    function enterPasswordBtn() {
        enteredPassword = password;
        password = "";
        Object.keys(checks).forEach((key) => {
            checks[key as keyof typeof checks] = false;
        });
    }

    function checkMatchedPassword() {
        passwordMatch = password === enteredPassword;
        if (passwordMatch) loginBtn.focus();
    }
    function clearPassword() {
        password = "";
        Object.keys(checks).forEach((key) => {
            checks[key as keyof typeof checks] = false;
        });
        enteredPassword = "";
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
</script>

<TitleBar />
{#if typewriter === ""}
    <p
        class="absolute top-0 left-1/3 w-1/2 drop-shadow-[0_1.2px_1.2px_rgba(0,0,0,0.8)]
                !text-primary-200 !dark:text-primary-100 text-left shrink-0"
        style="font-family: 'Orbitron'; font-weight: 600;"
    >
        {appName}
    </p>
{/if}
{#if $pageLoading}
    <div class="flex flex-col items-center justify-center h-screen pt-5">
        <div class="relative">
            <Spinner
                class="z-0"
                style="fill: {$accentColor} !important"
                bg="!text-primary-400 !dark:text-primary-300"
                size={32}
            />
            {#if $loadedFileCt > 0}
                <span
                    class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 z-10
                !text-primary-200 !dark:text-primary-100 text-xl font"
                >
                    {formatNumber($loadedFileCt)}
                </span>
            {/if}
        </div>
        <span
            class="!text-primary-200 !dark:text-primary-100 text-sm relative top-3
                w-full overflow-hidden whitespace-nowrap px-1.5 text-ellipsis-left"
        >
            initializing file tree..
        </span>
    </div>
{:else}
    <form
        on:submit|preventDefault={submit}
        bind:this={loginForm}
        autocomplete="off"
        class="login-form flex-col rounded-lg bg-primary-400 dark:bg-primary-300"
    >
        <p
            class="absolute top-0 left-0 text-justify w-screen pl-6 pt-1 text-sm"
            style={`--text-color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
        >
            {typewriter}
        </p>
        <div class="loginField">
            {#if _modal === Modals.None}
                <div class="flex items-center mx-auto">
                    {#if diskSpacePercent}
                        <div
                            class="top-0 z-40 w-full my-2 mb-0 mx-5 rounded-full h-2.5 bg-primary-300 dark:bg-primary-400"
                        >
                            <div
                                class="h-2.5 rounded-full"
                                style={`width: ${diskSpacePercent}%; background-color: ${$accentColor};`}
                            ></div>
                        </div>
                        <Tooltip
                            placement="bottom"
                            class={tooltipTailwindClass}
                            arrow={false}
                            offset={1}
                            >FREE:{formattedFreeDriveSize} - {formattedTotalDriveSize}
                            | {diskSpacePercent}%</Tooltip
                        >
                    {/if}
                </div>
                <div class="h-1.5" />
                <div class="field">
                    <Input
                        class="max-h-5 w-full py-0 leading-none"
                        style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                            color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                        placeholder={`${$newAccount ? "create username.." : "enter username.."} `}
                        type="text"
                        bind:value={username}
                        on:input={queryUsernameStrength}
                        required
                    />
                </div>
                <div class="flex w-full h-1.5 px-0.5 relative bottom-2">
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
                        placement="bottom"
                        offset={0}
                        class={tooltipTailwindClass}
                        arrow={false}>more than 4 characters</Tooltip
                    >
                </div>
                <div class="h-1" />

                {#if $newAccount}
                    {#if !enteredPassword}
                        <Input
                            class="max-h-5 w-full mb-0.5"
                            style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                        color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                            placeholder="create password.."
                            type="password"
                            bind:value={password}
                            on:keyup={(event) => enterPassword(event)}
                            required
                        />
                        <PasswordScan
                            {password}
                            _class="flex w-full px-0.5 relative bottom-px"
                            on:passwordStrengthUpdated={handlePasswordStrengthUpdated}
                        />
                    {:else}
                        <div class="flex justify-between">
                            <Input
                                class="max-h-5 w-full mb-0.5"
                                style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                                    color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                                placeholder="confirm password.."
                                type="password"
                                bind:value={password}
                                on:input={checkMatchedPassword}
                                required
                            />
                            <button
                                on:pointerdown|stopPropagation={clearPassword}
                                tabindex={1}
                            >
                                <CloseOutline
                                    class="mb-0.5"
                                    style="color: {$accentColor};"
                                />
                            </button>
                        </div>
                        <div
                            class="flex w-full h-1.5 px-0.5 relative bottom-px"
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
                    <Input
                        class="max-h-5 w-full mb-0.5"
                        style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                        color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                        placeholder="enter password.."
                        type="password"
                        bind:value={password}
                        on:keyup={(event) => enterPassword(event)}
                        required
                    />
                    <PasswordScan
                        {password}
                        _class="flex w-full px-0.5 relative bottom-px"
                        on:passwordStrengthUpdated={handlePasswordStrengthUpdated}
                    />
                {/if}
            {:else if _modal === Modals.Info}
                <Info />
            {:else if _modal === Modals.Settings}
                <Settings />
            {/if}
        </div>

        <div
            class={`flex justify-between items-center relative ${_modal === Modals.None ? `top-11` : `top-0`}`}
        >
            <div class={`flex space-x-1`}>
                <Button
                    pill={true}
                    class="p-0 px-0.5 hover:drop-shadow-[0_1.2px_1.2px_rgba(0,0,0,0.8)] hover:outline outline-1"
                    shadow
                    on:click={() => switchModals(Modals.Info)}
                >
                    <InfoCircleOutline
                        class="w-6 h-6"
                        color={$darkLightMode ? lightTextColor : darkTextColor}
                    />
                </Button>
                <Button
                    pill={true}
                    shadow
                    class="p-0.5 hover:drop-shadow-[0_1.2px_1.2px_rgba(0,0,0,0.8)] hover:outline outline-1"
                    on:click={() => switchModals(Modals.Settings)}
                >
                    <CogSolid
                        class="w-6 h-6"
                        color={$darkLightMode ? lightTextColor : darkTextColor}
                    />
                </Button>
            </div>
            <div class={``}>
                {#if $newAccount}
                    {#if !enteredPassword}
                        {#if usernameCheck && checks.passwordCheck}
                            <NeuButton
                                _class="!w-20"
                                on:click={() => enterPasswordBtn()}
                                >ENTER</NeuButton
                            >
                        {:else}
                            <NeuButtonFake _class="!w-20 opacity-20"
                                >ENTER</NeuButtonFake
                            >
                        {/if}
                    {:else if usernameCheck && passwordMatch}
                        <button class="focus:outline-none" bind:this={loginBtn}>
                            <NeuButton _class="!w-20" type="submit"
                                >LOGIN</NeuButton
                            >
                        </button>
                    {:else}
                        <NeuButtonFake _class="!w-20 opacity-20"
                            >LOGIN</NeuButtonFake
                        >
                    {/if}
                {:else if usernameCheck && checks.passwordCheck}
                    <button class="focus:outline-none" bind:this={loginBtn}>
                        <NeuButton _class="!w-20" type="submit">LOGIN</NeuButton
                        >
                    </button>
                {:else}
                    <NeuButtonFake _class="!w-20 opacity-20"
                        >LOGIN</NeuButtonFake
                    >
                {/if}
            </div>
        </div>
    </form>
{/if}

<style>
    p {
        --text-color: #757575;
        color: var(--text-color);
    }

    .login-form {
        margin: auto;
        padding: 0.5rem;
        padding-top: 1.5rem;
        padding-bottom: 0.25rem !important;
    }

    .field {
        margin-bottom: 0.5rem;
    }
</style>
