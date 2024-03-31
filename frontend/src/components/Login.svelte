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

    import Frame from "./Frame.svelte";
    import Info from "./Info.svelte";
    import Settings from "./Settings.svelte";
    import { LogDebug } from "../../wailsjs/runtime/runtime";

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
            <div class=" pt-1"></div>
            <div class="field">
                <Input
                    class="max-h-6 w-full"
                    id="small-input"
                    placeholder="enter username.."
                    type="text"
                    bind:value={username}
                    required
                />
            </div>
            <div class="field">
                <Input
                    class="max-h-6 w-full"
                    id="small-input"
                    placeholder="enter password.."
                    type="password"
                    bind:value={password}
                    required
                />
            </div>
            <!-- <Popover class="text-sm h-2 w-20" placement="bottom">
                <h3 class="font-semibold text-gray-900 dark:text-white">
                    Must have at least 6 characters
                </h3>
                <div class="grid grid-cols-4 gap-2">
                    <div class="h-1 bg-orange-300 dark:bg-orange-400" />
                    <div class="h-1 bg-orange-300 dark:bg-orange-400" />
                    <div class="h-1 bg-gray-200 dark:bg-gray-600" />
                    <div class="h-1 bg-gray-200 dark:bg-gray-600" />
                </div>
                <p class="py-2">It’s better to have:</p>
                <ul>
                    <li class="flex items-center mb-1">
                        <CheckOutline
                            class="me-2 w-4 h-4 text-green-400 dark:text-green-500"
                        />
                        Upper &amp; lower case letters
                    </li>
                    <li class="flex items-center mb-1">
                        <CheckOutline
                            class="me-2 w-4 h-4 text-green-400 dark:text-green-500"
                        />
                        A symbol (#$&amp;)
                    </li>
                    <li class="flex items-center">
                        <CloseOutline
                            class="me-2 w-4 h-4 text-gray-300 dark:text-gray-400"
                        />A longer password (min. 12 chars.)
                    </li>
                </ul>
            </Popover> -->
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
            <GradientButton
                color="cyanToBlue"
                class="!w-100 min-h-5 max-h-7 pt-3 "
                type="submit">LOGIN</GradientButton
            >
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
        padding-bottom: 0.3rem;
    }

    .field {
        margin-bottom: 0.6rem;
    }
</style>
