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
    } from "../../stores/constantVariables.ts";

    import { darkLightMode } from "../../stores/dynamicVariables.ts";
    import { darkLightBGOnElement } from "../../tools/utils";
    import { LogInfo } from "../../../wailsjs/runtime/runtime";

    import PasswordScan from "../widgets/PasswordScan.svelte";

    let displayString = "";
    const appName = __APP_NAME__;
    const appVersion = __APP_VERSION__;
    let username = "";
    let password = "";
    const dispatch = createEventDispatcher();

    let loginForm;
    const unsubscribe = darkLightMode.subscribe((value) => {
        darkLightBGOnElement(value, loginForm);
    });

    onMount(() => {
        const interval = setInterval(() => {
            displayString = getDisplayString();
        }, alertInterval);
        darkLightBGOnElement(false, loginForm);
        return () => {
            clearInterval(interval);
        };
    });

    onDestroy(() => {
        stopDisplay();
        unsubscribe();
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

    let passwordMatch;
    // let usernameCheck: string;

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
    <p class="absolute top-0 left-0 text-justify w-screen pl-6 pt-1 text-sm">
        {displayString}
    </p>
    <Frame />
    <!-- <div class=" pt-5"></div> -->
    <div class="loginField">
        {#if _modal === Modals.None}
            <div class="flex items-center mx-auto">
                <p class="shrink-0 text-left mr-auto">{appName}</p>
                <Tooltip
                    placement="right"
                    type="custom"
                    defaultClass=""
                    class="px-0.5 text-s font-medium bg-gray-600 text-gray-100"
                    arrow={false}>{appVersion}</Tooltip
                >
                <p class="shrink-0 text-right ml-auto">3.6GB</p>
                <Tooltip
                    placement="left"
                    type="custom"
                    defaultClass=""
                    class="px-0.5 text-s font-medium bg-gray-600 text-gray-100"
                    arrow={false}>99%</Tooltip
                >
            </div>
            <div class="h-1.5"></div>
            <div class="field">
                <Input
                    class="max-h-5 w-full bg py-0"
                    id="small-input"
                    placeholder="enter username.."
                    type="text"
                    bind:value={username}
                    on:keyup={queryUsernameStrength}
                    required
                />
            </div>
            <div
                class="flex w-full h-1.5 px-0.5 relative bottom-2"
                tabindex="-1"
            >
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
                    class="max-h-5 w-full"
                    id="small-input"
                    placeholder="enter password.."
                    type="password"
                    bind:value={password}
                    required
                />
            </div>
            <PasswordScan
                {password}
                _class="flex w-full h-1.5 px-0.5 relative bottom-2"
                on:passwordStrengthUpdated={handlePasswordStrengthUpdated}
            />
        {:else if _modal === Modals.Settings}
            <Settings />
        {:else if _modal === Modals.Info}
            <Info />
        {/if}
    </div>

    <div class="flex justify-between items-center">
        <div class="flex space-x-1">
            <!-- First two buttons -->
            <Button
                pill={true}
                outline={true}
                class="!p-1"
                color="dark"
                on:click={() => switchModals(Modals.Info)}
                ><InfoCircleOutline class="w-5 h-5" color="white" /></Button
            >
            <Button
                pill={true}
                outline={true}
                class="!p-1"
                color="dark"
                on:click={() => switchModals(Modals.Settings)}
                ><AdjustmentsVerticalOutline
                    class="w-5 h-5"
                    color="white"
                /></Button
            >
        </div>
        <div>
            {#if usernameCheck && checks.passwordCheck}
                <NeuButton _class="!w-20 " type="submit">LOGIN</NeuButton>
            {:else}
                <NeuButton
                    disabled={true}
                    _class="!w-20 opacity-20"
                    type="submit">LOGIN</NeuButton
                >
            {/if}
        </div>
    </div>
</form>

<style>
    :root {
        --bg-color: #757575;
    }

    .login-form {
        margin: auto;
        padding: 0.5rem;

        background-color: var(--bg-color);
    }
    .login-form {
        padding-top: 1.5rem;
    }

    .field {
        margin-bottom: 0.6rem;
    }
</style>
