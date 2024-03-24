<!-- Login.svelte -->
<script>
    import { createEventDispatcher } from "svelte";
    import { userStore } from "../stores/userStore";
    import { Login } from "../../wailsjs/go/main/App";
    import { Button, Input, GradientButton } from "flowbite-svelte";
    import { switchFormButton, toggleSettings } from "../utils";
    import { CaretUpSolid } from "flowbite-svelte-icons";
    import Frame from "./Frame.svelte";

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
</script>

<form on:submit={submit} autocomplete="off" class="login-form flex flex-col">
    <!-- Use flex-col for vertical stacking -->
    <Frame />
    <div class="flex space-x-5">
        <!-- This will align the two <p> tags horizontally -->
        <p class="flex-1">i-ncript</p>
        <p class="flex-1">3.6GB</p>
    </div>
    <div class="field">
        <Input
            class="max-h-8 w-full"
            id="small-input"
            placeholder="Enter password"
            bind:value={username}
            required
        />
    </div>
    <div class="field">
        <Input
            class="max-h-8 w-full"
            id="small-input"
            placeholder="Enter password"
            bind:value={password}
            required
        />
    </div>
    <div class="space-x-5 mt-0 pt-0">
        <!-- Removed flex-1 to not force flex grow -->
        <GradientButton
            color="cyanToBlue"
            class="max-w-48 min-h-5 max-h-7 pt-3 px-3"
            type="submit">Login</GradientButton
        >
        <Button
            pill={true}
            outline={true}
            class="!p-1"
            color="dark"
            on:click={toggleSettings}
            ><CaretUpSolid class="w-5 h-5 m-0" color="white" /></Button
        >
    </div>
</form>

<style>
    .login-form {
        max-width: 400px;
        margin: auto;
        padding: 1rem;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    }
    .field {
        margin-bottom: 1rem;
    }
    label {
        display: block;
        margin-bottom: 0.5rem;
    }
    input {
        width: 100%;
        padding: 0.5rem;
        box-sizing: border-box;
        color: black;
    }
    .btn {
        display: inline-block;
        padding: 0.5rem 1rem;
        background-color: #007bff;
        color: white;
        border: none;
        cursor: pointer;
    }
    .btn:hover {
        background-color: #0056b3;
    }
</style>
