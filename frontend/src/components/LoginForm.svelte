<!-- LoginForm.svelte -->
<script>
    import { createEventDispatcher } from "svelte";
    import { user } from "../stores/userStore";
    import { Login } from "../../wailsjs/go/main/App";

    const dispatch = createEventDispatcher();
    let username = "";
    let password = "";

    async function submit(event) {
        event.preventDefault();
        try {
            const result = await Login(username, password);
            user.set({ username }); // Update the user store with the logged-in user's info
            dispatch('loginSuccess'); // Emit an event for successful login
        } catch (error) {
            console.error("Error calling Login method:", error);
        }
    }
</script>

<form on:submit={submit} autocomplete="off" class="login-form">
    <h2>Login</h2>
    <div class="field">
        <label for="username">Username</label>
        <input type="text" id="username" bind:value={username} required />
    </div>
    <div class="field">
        <label for="password">Password</label>
        <input type="password" id="password" bind:value={password} required />
    </div>
    <button class="btn" type="submit">Login</button>
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
