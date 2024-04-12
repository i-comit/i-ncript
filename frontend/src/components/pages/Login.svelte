<!-- Login.svelte -->
<script>
    import { createEventDispatcher, onMount, onDestroy } from "svelte";

    import { Modals, currentModal } from "../../enums/Modals";

    import { Login } from "../../../wailsjs/go/main/App";
    import { Button, Input, Tooltip } from "flowbite-svelte";
    import { switchModals } from "../../tools/utils";
    import {
        InfoCircleOutline,
        AdjustmentsVerticalOutline,
        CogSolid,
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
        accentColor,
        darkBGColor,
        darkTextColor,
        lightTextColor,
        lightBGColor,
    } from "../../stores/constantVariables.ts";

    import { darkLightMode } from "../../stores/dynamicVariables.ts";
    import {
        darkLightBGOnElement,
        darkLightTextOnElement,
    } from "../../tools/themes";
    import { LogInfo } from "../../../wailsjs/runtime/runtime";

    import PasswordScan from "../widgets/PasswordScan.svelte";
    import NeuButtonFake from "../widgets/NeuButtonFake.svelte";

    let typewriter = "";

    const appName = __APP_NAME__;
    const appVersion = __APP_VERSION__;
    let username = "";
    let password = "";
    const dispatch = createEventDispatcher();

    let loginForm;
    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightBGOnElement(value, loginForm);
        darkLightTextOnElement(value, typewriter);
    });

    onMount(async () => {
        const interval = setInterval(() => {
            typewriter = getDisplayString();
        }, alertInterval);
        const _value = await get(darkLightMode);

        darkLightBGOnElement(_value, loginForm);
        darkLightTextOnElement(_value, typewriter);
        return () => {
            clearInterval(interval);
        };
    });

    onDestroy(() => {
        stopDisplay();
        unsub_darkLightMode();
    });

    async function submit(event) {
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
    let _modal;
    currentModal.subscribe((value) => {
        _modal = value;
    });

    let usernameCheck = false;
    function queryUsernameStrength() {
        const regex = /^.{5,}$/;
        usernameCheck = regex.test(username);
    }

    let checks = {
        passwordCheck1: false,
        passwordCheck2: false,
        passwordCheck3: false,
        passwordCheck: false,
    };

    // Function to handle the custom event
    function handlePasswordStrengthUpdated(event) {
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
        // Now 'checks' object contains the updated check values
    }
</script>

<form
    on:submit|preventDefault={submit}
    bind:this={loginForm}
    autocomplete="off"
    class="login-form flex-col rounded-lg"
>
    {#if $darkLightMode}
        <p
            class="absolute top-0 left-0 text-justify w-screen pl-6 pt-1 text-sm"
            style={`--text-color: ${lightTextColor};`}
        >
            {typewriter}
        </p>
    {:else}
        <p
            class="absolute top-0 left-0 text-justify w-screen pl-6 pt-1 text-sm"
            style={`--text-color: ${darkTextColor};`}
        >
            {typewriter}
        </p>
    {/if}

    <Frame />
    <div class="loginField">
        {#if _modal === Modals.None}
            <div class="flex items-center mx-auto">
                {#if $darkLightMode}
                    <p
                        class="shrink-0 text-left mr-auto"
                        style={`--text-color: ${lightTextColor};`}
                    >
                        {appName}
                    </p>
                {:else}
                    <p
                        class="shrink-0 text-left mr-auto"
                        style={`--text-color: ${darkTextColor};`}
                    >
                        {appName}
                    </p>
                {/if}
                <Tooltip
                    placement="right"
                    type="custom"
                    class={tooltipTailwindClass}
                    arrow={false}>{appVersion}</Tooltip
                >
                {#if $darkLightMode}
                    <p
                        class="shrink-0 text-right ml-auto"
                        style={`--text-color: ${lightTextColor};`}
                    >
                        3.6GB
                    </p>
                {:else}
                    <p
                        class="shrink-0 text-right ml-auto"
                        style={`--text-color: ${darkTextColor};`}
                    >
                        3.6GB
                    </p>
                {/if}

                <Tooltip
                    placement="left"
                    type="custom"
                    class={tooltipTailwindClass}
                    arrow={false}>99%</Tooltip
                >
            </div>
            <div class="h-0.5"></div>
            <div class="field">
                <Input
                    class="max-h-5 w-full bg py-0 focus:none"
                    id="small-input"
                    placeholder="enter username.."
                    type="text"
                    bind:value={username}
                    on:keyup={queryUsernameStrength}
                    required
                />
            </div>
            <div class="flex w-full h-1 px-0.5 relative bottom-2" tabindex="-1">
                <div
                    class="flex-1 text-center rounded-lg"
                    style={`background-color: ${usernameCheck ? accentColor : darkBGColor};`}
                ></div>
                <div tabindex="-1">
                    <Tooltip
                        placement="bottom"
                        offset={0}
                        class={tooltipTailwindClass}
                        arrow={false}>more than 4 characters</Tooltip
                    >
                </div>
            </div>
            <div class="field">
                <Input
                    class="max-h-4 w-full"
                    id="small-input"
                    placeholder="enter password.."
                    type="password"
                    bind:value={password}
                    required
                />
            </div>
            <PasswordScan
                {password}
                _class="flex w-full h-1 px-0.5 relative bottom-2"
                on:passwordStrengthUpdated={handlePasswordStrengthUpdated}
            />
        {:else if _modal === Modals.Settings}
            <Settings />
        {:else if _modal === Modals.Info}
            <Info />
        {/if}
    </div>

    <div
        class={`flex justify-between items-center ${$currentModal === Modals.None ? `mt-0` : `mt-1`}`}
    >
        <div class="flex space-x-1">
            <Button
                pill={true}
                class="!p-0"
                on:click={() => switchModals(Modals.Info)}
            >
                {#if $darkLightMode}
                    <InfoCircleOutline class="w-6 h-6" color={lightTextColor} />
                {:else}
                    <InfoCircleOutline class="w-7 h-7" color={darkTextColor} />
                {/if}
            </Button>
            <Button
                pill={true}
                class="!p-0.5"
                on:click={() => switchModals(Modals.Settings)}
            >
                {#if $darkLightMode}
                    <CogSolid class="w-6 h-6" color={lightTextColor} />
                {:else}
                    <CogSolid class="w-6 h-6" color={darkTextColor} />
                {/if}
            </Button>
        </div>
        <div>
            {#if usernameCheck && checks.passwordCheck}
                <NeuButton _class="!w-20 " type="submit">LOGIN</NeuButton>
            {:else}
                <NeuButtonFake _class="!w-20 opacity-20">LOGIN</NeuButtonFake>
            {/if}
        </div>
    </div>
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
