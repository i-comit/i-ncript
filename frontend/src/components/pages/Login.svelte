<!-- Login.svelte -->
<script lang="ts">
    import { createEventDispatcher, onMount, onDestroy } from "svelte";

    import { Modals, currentModal } from "../../enums/Modals";

    import { Login } from "../../../wailsjs/go/main/App";
    import { Button, Input, Tooltip, Spinner } from "flowbite-svelte";
    import { switchModals } from "../../tools/utils";
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
    } from "../../stores/dynamicVariables.ts";
    import {
        darkLightBGOnElement,
        darkLightTextOnElement,
    } from "../../tools/themes";
    import { LogError, LogInfo } from "../../../wailsjs/runtime/runtime";

    import PasswordScan from "../widgets/PasswordScan.svelte";
    import NeuButtonFake from "../widgets/NeuButtonFake.svelte";
    import { get } from "svelte/store";
    import {
        GetFormattedAppDirSize,
        GetFormattedDriveSize,
        GetPercentOfDriveToAppDirSize,
    } from "../../../wailsjs/go/main/Getters";

    let typewriter = "";
    const dispatch = createEventDispatcher();

    const appName: string = __APP_NAME__;
    const appVersion: string = __APP_VERSION__;
    let username = "";
    let password = "";
    let enteredPassword: string = "";
    let passwordMatch = false;

    let driveToAppDirPercent: number;
    let formattedDriveSize: string;
    let formattedAppDirSize: string;

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

    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightBGOnElement(value, loginForm);
    });

    onMount(() => {
        const interval = setInterval(() => {
            typewriter = getDisplayString();
        }, alertInterval);
        const _value = get(darkLightMode);
        GetPercentOfDriveToAppDirSize()
            .then((_driveToAppDirPercent) => {
                driveToAppDirPercent = _driveToAppDirPercent;
                // Return the next promise in the chain
                return GetFormattedDriveSize();
            })
            .then((_formattedDriveSize) => {
                formattedDriveSize = _formattedDriveSize;
                return GetFormattedAppDirSize();
            })
            .then((_formattedAppDirSize) => {
                formattedAppDirSize = _formattedAppDirSize;
            })
            .catch((error) => {
                console.error("An error occurred:", error);
            });

        darkLightBGOnElement(_value, loginForm);
        return () => {
            clearInterval(interval);
        };
    });

    onDestroy(() => {
        stopDisplay();
        unsub_darkLightMode();
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
{#if $pageLoading}
    <div class="flex items-center justify-center h-screen">
        <Spinner
            style="fill: {$accentColor} !important"
            bg="!text-primary-400 !dark:text-primary-300"
            size={24}
        />
    </div>
{:else}
    <form
        on:submit|preventDefault={submit}
        bind:this={loginForm}
        autocomplete="off"
        class="login-form flex-col rounded-lg"
    >
        <p
            class="absolute top-0 left-0 text-justify w-screen pl-6 pt-1 text-sm"
            style={`--text-color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
        >
            {typewriter}
        </p>
        {#if typewriter === ""}
            <p
                class="shrink-0 text-left absolute top-0 left-1/3 w-1/2 drop-shadow-[0_1.2px_1.2px_rgba(0,0,0,0.8)]"
                style={`--text-color: ${$darkLightMode ? lightTextColor : darkTextColor};
            font-family: "Orbitron"; font-weight: 600;`}
            >
                {appName}
            </p>
        {/if}

        <div class="loginField">
            {#if _modal === Modals.None}
                <div class="flex items-center mx-auto">
                    <Tooltip
                        placement="right"
                        type="custom"
                        class={tooltipTailwindClass}
                        arrow={false}>{appVersion}</Tooltip
                    >
                    <div
                        class="top-0 z-10 w-full my-1.5 mb-1 mx-5 rounded-full h-2.5 bg-primary-300 dark:bg-primary-400"
                    >
                        <div
                            class="h-2.5 rounded-full"
                            style={`width: ${driveToAppDirPercent}%; background-color: ${$accentColor};`}
                        ></div>
                    </div>

                    <Tooltip
                        placement="bottom"
                        type="custom"
                        class={tooltipTailwindClass}
                        arrow={false}
                        offset={1}
                        >{formattedAppDirSize} / {formattedDriveSize} | {driveToAppDirPercent}%</Tooltip
                    >
                </div>
                <div class="h-1" />
                <div class="field">
                    <Input
                        class="max-h-5 w-full bg py-0 leading-none"
                        style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                            color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                        id="small-input"
                        placeholder={`${$newAccount ? "create username.." : "enter username.."} `}
                        type="text"
                        bind:value={username}
                        on:keyup={queryUsernameStrength}
                        required
                    />
                </div>
                <div
                    class="flex w-full h-1 px-0.5 relative bottom-1.5"
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
                    <div tabindex="-1">
                        <Tooltip
                            placement="bottom"
                            offset={0}
                            class={tooltipTailwindClass}
                            arrow={false}>more than 4 characters</Tooltip
                        >
                    </div>
                </div>

                {#if $newAccount}
                    {#if !enteredPassword}
                        <Input
                            class="max-h-4 w-full mb-2"
                            style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                        color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                            id="small-input"
                            placeholder="create password.."
                            type="password"
                            bind:value={password}
                            on:keyup={(event) => enterPassword(event)}
                            required
                        />
                        <PasswordScan
                            {password}
                            _class="flex w-full h-1 px-0.5 relative bottom-1.5"
                            on:passwordStrengthUpdated={handlePasswordStrengthUpdated}
                        />
                    {:else}
                        <div class="flex justify-between">
                            <Input
                                class="max-h-4 w-full mb-2"
                                style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                                    color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                                id="small-input"
                                placeholder="confirm password.."
                                type="password"
                                bind:value={password}
                                on:keyup={checkMatchedPassword}
                                required
                            />
                            <button on:click|stopPropagation={clearPassword}>
                                <CloseOutline
                                    class="mb-2"
                                    style="color: {$accentColor};"
                                />
                            </button>
                        </div>
                        <div
                            class="flex w-full h-1 px-0.5 relative bottom-1"
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
                    <Input
                        class="max-h-4 w-full mb-2 focus:outline-1"
                        style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                        color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                        id="small-input"
                        placeholder="enter password.."
                        type="password"
                        bind:value={password}
                        on:keyup={(event) => enterPassword(event)}
                        required
                    />
                    <PasswordScan
                        {password}
                        _class="flex w-full h-1 px-0.5 relative bottom-1.5"
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
            class={`flex justify-between items-center ${$currentModal === Modals.None ? `mt-0` : `mt-1`}`}
        >
            <div class="flex space-x-1">
                <Button
                    pill={true}
                    class="p-0 px-0.5 hover:drop-shadow-[0_1.2px_1.2px_rgba(0,0,0,0.8)] "
                    shadow
                    on:click={() => switchModals(Modals.Info)}
                >
                    <InfoCircleOutline
                        class="w-6 h-6 "
                        color={$darkLightMode ? lightTextColor : darkTextColor}
                    />
                </Button>
                <Button
                    pill={true}
                    shadow
                    class="p-0.5 hover:drop-shadow-[0_1.2px_1.2px_rgba(0,0,0,0.8)]"
                    on:click={() => switchModals(Modals.Settings)}
                >
                    <CogSolid
                        class="w-6 h-6"
                        color={$darkLightMode ? lightTextColor : darkTextColor}
                    />
                </Button>
            </div>
            <div>
                {#if $newAccount}
                    {#if !enteredPassword}
                        {#if usernameCheck && checks.passwordCheck}
                            <NeuButton
                                _class="!w-20 "
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
        <div class={`${$currentModal === Modals.None ? `h-0.5` : `h-0`}`} />
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
        background-color: var(--bg-color);
    }

    .field {
        margin-bottom: 0.5rem;
    }
</style>
