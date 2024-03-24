<!-- OBox.svelte -->
<script>
    import { user } from "../stores/userStore";
    let loggedInUser;
    // Subscribe to the user store
    user.subscribe(($user) => {
        loggedInUser = $user;
    });

    function logout() {
        user.set(null); // Clear the user store on logout
    }
    function buttonAction(actionName) {
        console.log(`Action for ${actionName}`);
        // Define additional logic for button actions here
    }
</script>

<div class="app-container">
    <div class="side-menu">
        <button class="btn" on:click={() => buttonAction("Button 1")}
            >Button 1</button
        >
        <button class="btn" on:click={() => buttonAction("Button 2")}
            >Button 2</button
        >
        <button class="btn" on:click={() => buttonAction("Button 3")}
            >Button 3</button
        >
        <button class="btn" on:click={() => buttonAction("Button 4")}
            >Button 4</button
        >
    </div>
    <div class="main-panel">
        <h3>Welcome! Select an option.</h3>
    </div>
</div>

<style>
    .app-container {
        display: flex;
        height: 100vh;
    }
    .side-menu {
        min-width: 200px;
        background-color: #f0f0f0;
        padding: 1rem;
    }
    .main-panel {
        flex-grow: 1;
        padding: 1rem;
    }
    .btn {
        display: block;
        width: 100%;
        margin-bottom: 0.5rem;
        padding: 0.5rem;
        background-color: #007bff;
        color: white;
        border: none;
        cursor: pointer;
    }
    .btn:hover {
        background-color: #0056b3;
    }
</style>
