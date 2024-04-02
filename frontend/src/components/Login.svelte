<!-- Login.svelte -->
<script>
    import { createEventDispatcher, onMount, onDestroy } from "svelte";

    import { Modals, currentModal } from "../enums/Modals";

    import { Login, ResizeWindow } from "../../wailsjs/go/main/App";
    import {
        Button,
        Input,
        GradientButton,
        Tooltip,
        ButtonGroup,
        Popover,
    } from "flowbite-svelte";
    // import { Popover, Label, Input, Checkbox, Button } from 'flowbite-svelte';
    import { CheckOutline, CloseOutline } from "flowbite-svelte-icons";
    import { switchModals } from "../tools/utils";
    import {
        InfoCircleOutline,
        AdjustmentsVerticalOutline,
    } from "flowbite-svelte-icons";

    import {
        startDisplay,
        stopDisplay,
        getDisplayString,
        alertInterval,
    } from "../tools/logger";
    let displayString = "";

    import Frame from "../elements/Frame.svelte";
    import Info from "./Info.svelte";
    import Settings from "./Settings.svelte";
    import {
        height,
        width,
        tooltipTailwindClass,
        accentColor,
        darkColor,
    } from "../stores/globalVariables.ts";
    import { LogDebug, LogTrace } from "../../wailsjs/runtime/runtime.js";

    const appName = __APP_NAME__;
    const appVersion = __APP_VERSION__;
    const dispatch = createEventDispatcher();
    let username = "";
    let password = "";

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

    onMount(() => {
        const interval = setInterval(() => {
            displayString = getDisplayString();
        }, alertInterval);

        return () => {
            clearInterval(interval);
        };
    });
    onDestroy(() => {
        stopDisplay();
    });

    let usernameCheck = false;
    function queryUsernameStrength() {
        const regex = /^.{5,}$/;
        usernameCheck = regex.test(username);
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

    import NeuButton from "../elements/NeuButton.svelte";
</script>

<form
    on:submit={submit}
    autocomplete="off"
    class="login-form flex-col rounded-lg"
>
    <p class="absolute top-0 left-0 text-justify w-screen pl-2 pt-1 text-sm">
        {displayString}
    </p>
    <Frame />
    <!-- <CircleProgressBar /> -->

    <!-- <div class=" pt-5"></div> -->
    <div class="loginField">
        {#if _modal === Modals.None}
            <div class="flex items-center mx-auto">
                <!-- This will align the two <p> tags horizontally -->
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
            <div class="flex w-full h-1.5 px-0.5 relative bottom-2">
                <div
                    class="flex-1 text-center rounded-lg"
                    style={`background-color: ${usernameCheck ? accentColor : darkColor};`}
                ></div>
                <Tooltip
                    placement="bottom"
                    offset={0}
                    class={tooltipTailwindClass}
                    arrow={false}>more than 4 characters</Tooltip
                >
            </div>
            <div class="field">
                <Input
                    class="max-h-5 w-full"
                    id="small-input"
                    placeholder="enter password.."
                    type="password"
                    bind:value={password}
                    on:keyup={queryPasswordStrength}
                    required
                />
            </div>
            <div class="flex w-full h-1.5 px-0.5 relative bottom-2">
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
            {#if usernameCheck && passwordCheck}
                <NeuButton className="!w-20 " type="submit"
                    >LOGIN</NeuButton
                >
            {:else}
                <NeuButton
                    disabled={true}
                    className="!w-20 opacity-20"
                    type="submit">LOGIN</NeuButton
                >
            {/if}
        </div>
    </div>
</form>

<style>
    .login-form {
        margin: auto;
        padding: 0.5rem;

        background-color: gray;
    }
    .login-form {
        padding-top: 1.5rem;
    }

    .field {
        margin-bottom: 0.6rem;
    }
</style>
