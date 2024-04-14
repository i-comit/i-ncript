<!-- Login.svelte -->
<script lang="ts">
    import { createEventDispatcher, onMount, onDestroy } from "svelte";

    import { Modals, currentModal } from "../../enums/Modals";

    import { Login, ResizeWindow } from "../../../wailsjs/go/main/App";
    import { Button, Input, Tooltip, Progressbar } from "flowbite-svelte";
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

    import Frame from "../widgets/Frame.svelte";
    import Info from "../modals/Info.svelte";
    import Settings from "../modals/Settings.svelte";

    import NeuButton from "../widgets/NeuButton.svelte";
    import {
        tooltipTailwindClass,
        darkTextColor,
        lightTextColor,
        darkInputColor,
        lightInputColor,
        width,
        height,
    } from "../../stores/constantVariables.ts";

    import {
        darkLightMode,
        accentColor,
        newAccount,
    } from "../../stores/dynamicVariables.ts";
    import {
        darkLightBGOnElement,
        darkLightTextOnElement,
    } from "../../tools/themes";
    import { LogInfo } from "../../../wailsjs/runtime/runtime";

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
                return GetFormattedAppDirSize();
            })
            .then((_formattedAppDirSize) => {
                formattedAppDirSize = _formattedAppDirSize;
                return GetFormattedDriveSize();
            })
            .then((_formattedDriveSize) => {
                formattedDriveSize = _formattedDriveSize;
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

    async function submit(event: SubmitEvent) {
        event.preventDefault();
        try {
            await Login(username, password).then((loginResult) => {
                switch (loginResult) {
                    case 0: //File does not exist
                    case 2: //hash matched with key
                        dispatch("loginSuccess");
                        break;
                    case 1: //File is empty
                        startDisplay("key file is empty...");
                        break;
                    case 3:
                        startDisplay("wrong credentials...");
                        break;
                }
            });
        } catch (error) {
            console.error("Error calling Login method:", error);
        }
    }
    let _modal: Modals;
    currentModal.subscribe((value) => {
        _modal = value;
    });

    let usernameCheck = false;
    function queryUsernameStrength() {
        const regex = /^.{5,}$/;
        usernameCheck = regex.test(username);
        if (username === "") enteredPassword = "";
    }

    let checks = {
        passwordCheck1: false,
        passwordCheck2: false,
        passwordCheck3: false,
        passwordCheck: false,
    };

    function enterPassword(event: KeyboardEvent) {
        LogInfo("emteriong password ");
        if (event.code === "Enter" && checks.passwordCheck) {
            password = "";
            const inputElement = event.target as HTMLInputElement;
            enteredPassword = inputElement.value;
            Object.keys(checks).forEach((key) => {
                checks[key as keyof typeof checks] = false;
            });
        }
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
        LogInfo("passwordCheck 1 " + passwordCheck1);
    }
</script>

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
    <Frame />
    {#if typewriter === ""}
        <p
            class="shrink-0 text-left absolute top-0 left-1/3 w-1/2"
            style={`--text-color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
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
                    class="top-0 z-10 w-full my-1 mb-1.5 mx-4 rounded-full h-2.5 bg-primary-400 dark:bg-primary-300"
                >
                    <div
                        class="h-2.5 rounded-full transition-all duration-700 ease-in-out"
                        style={`width: ${driveToAppDirPercent}%; background-color: ${$accentColor};`}
                    ></div>
                </div>

                <Tooltip
                    placement="bottom"
                    type="custom"
                    class={tooltipTailwindClass}
                    arrow={false}
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
                        class="flex-1 text-center rounded-lg bg-primary-400 dark:bg-primary-300"
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
                            <CloseOutline class=" text-primary-500 mb-2" />
                        </button>
                    </div>
                    <div
                        class="flex w-full h-1 px-0.5 relative bottom-1"
                        tabindex="-1"
                    >
                        {#if !passwordMatch}
                            <div
                                class="flex-1 text-center rounded-lg bg-primary-400 dark:bg-primary-300"
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
                    class="max-h-4 w-full mb-2"
                    style={`background-color: ${$darkLightMode ? darkInputColor : lightInputColor};
                        color: ${$darkLightMode ? lightTextColor : darkTextColor};`}
                    id="small-input"
                    placeholder="enter password.."
                    type="password"
                    bind:value={password}
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
                class="p-0 px-0.5"
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
                class="p-0.5"
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
                            on:click={() => enterPasswordBtn()}>ENTER</NeuButton
                        >
                    {:else}
                        <NeuButtonFake _class="!w-20 opacity-20"
                            >ENTER</NeuButtonFake
                        >
                    {/if}
                {:else if usernameCheck && passwordMatch}
                    <NeuButton _class="!w-20" type="submit">LOGIN</NeuButton>
                {:else}
                    <NeuButtonFake _class="!w-20 opacity-20"
                        >LOGIN</NeuButtonFake
                    >
                {/if}
            {:else if usernameCheck && checks.passwordCheck}
                <NeuButton _class="!w-20" type="submit">LOGIN</NeuButton>
            {:else}
                <NeuButtonFake _class="!w-20 opacity-20">LOGIN</NeuButtonFake>
            {/if}
        </div>
    </div>
    <div class={`${$currentModal === Modals.None ? `h-0.5` : `h-0`}`} />
</form>

<style>
    :root {
        --bg-color: #757575;
    }

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
