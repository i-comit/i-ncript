<!-- Login.svelte -->
<script>
    import { createEventDispatcher } from "svelte";
    import { userStore } from "../stores/userStore";
    import { Login } from "../../wailsjs/go/main/App";
    import { Button, Input, GradientButton, Tooltip } from "flowbite-svelte";
    import { settingsOpened } from "../stores/settingsOpened";
    import { switchFormButton, toggleSettings } from "../utils";
    import {
        InfoCircleOutline,
        AdjustmentsVerticalOutline,
    } from "flowbite-svelte-icons";
    import Frame from "./Frame.svelte";
    import Settings from "./Settings.svelte";
    const dispatch = createEventDispatcher();
    let username = "";
    let password = "";

    async function submit(event) {
        event.preventDefault();
        try {
            const result = await Login(username, password);
            userStore.set({ username }); // Update the user store with the logged-in user's info
            dispatch("loginSuccess"); // Emit an event for successful login
        } catch (error) {
            console.error("Error calling Login method:", error);
        }
    }

    let _settingsOpened;
    settingsOpened.subscribe((value) => {
        _settingsOpened = value;
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
        {#if !_settingsOpened}
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
        {:else}
            <Settings />
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
                on:click={toggleSettings}
                ><InfoCircleOutline class="w-5 h-5" color="white" /></Button
            >
            <Button
                pill={true}
                outline={true}
                class="!p-1"
                color="dark"
                on:click={toggleSettings}
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
        padding-top: 1.5rem;
        padding-bottom: .3rem;
        background-color: gray;
    }
    .field {
        margin-bottom: 0.6rem;
    }
</style>
