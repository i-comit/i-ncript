<!-- Login.svelte -->
<script>
    import { createEventDispatcher } from "svelte";

    import { usernameStore } from "../stores/usernameStore";
    import { passwordStore } from "../stores/passwordStore";
    import { currentModal } from "../stores/currentModal";

    import {
        Login,
        ResizeWindow,
        EncryptString,
    } from "../../wailsjs/go/main/App";
    import { Button, Input, GradientButton, Tooltip } from "flowbite-svelte";
    import { switchModals } from "../utils";
    import {
        InfoCircleOutline,
        AdjustmentsVerticalOutline,
    } from "flowbite-svelte-icons";
    import { Modals } from "../enums/Modals";

    import Frame from "./Frame.svelte";
    import Info from "./Info.svelte";
    import Settings from "./Settings.svelte";
    const dispatch = createEventDispatcher();
    let username = "";
    let password = "";

    async function submit(event) {
        event.preventDefault();
        try {
            const result = await Login(username, password);
            const encryptedUsername = await EncryptString(username);
            usernameStore.set({ encryptedUsername });
            const encryptedPassowrd = await EncryptString(password);
            passwordStore.set({ encryptedPassowrd });
            dispatch("loginSuccess"); // Emit an event for successful login
        } catch (error) {
            console.error("Error calling Login method:", error);
        }
    }
    let _modal;
    currentModal.subscribe((value) => {
        _modal = value;
    });
</script>

<form
    on:submit={submit}
    autocomplete="off"
    class="login-form flex-col rounded-lg"
>
    <!-- <p class="bg-gray-100">alert text</p> -->
    <Frame />
    <!-- <div class=" pt-5"></div> -->
    <div class="loginField">
        {#if _modal === Modals.None}
            <div class="flex items-center mx-auto">
                <!-- This will align the two <p> tags horizontally -->
                <p class="shrink-0 text-left mr-auto">i-ncript</p>
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
    .login-form,
    .modified-login-form {
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
