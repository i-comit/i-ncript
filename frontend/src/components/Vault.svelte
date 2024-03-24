<!-- Vault.svelte -->
<script>
    import { GradientButton } from "flowbite-svelte";

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

    function switchFormButton(actionName) {
        console.log(`Action for ${actionName}`);
        // Define additional logic for button actions here
    }
</script>

<div class="app-container">
    <div class="side-menu">
        <div class="vault-info">
            <p>VAULT</p>
            <p>3.6GB</p>
        </div>
        <div class="buttons">
            <div class="row">
                <GradientButton
                    color="purpleToBlue"
                    on:click={() => buttonAction("Button 1")}
                    >Button 1</GradientButton
                >
                <GradientButton
                    color="cyanToBlue"
                    on:click={() => buttonAction("Button 2")}
                    >Button2</GradientButton
                >
            </div>
            <div class="row center">
                <button
                    class="btn"
                    on:click={() => buttonAction("Another Button")}
                    >Another Button</button
                >
            </div>
            <div class="row">
                <button class="btn" on:click={() => switchFormButton("O-BOX")}
                    >O-BOX</button
                >
                <button class="btn" on:click={() => switchFormButton("N-BOX")}
                    >N-BOX</button
                >
            </div>
        </div>
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

    .vault-info {
        display: flex;
        justify-content: space-between; /* Adjust as needed for your design */
        align-items: center;
        margin-bottom: 20px; /* Provides spacing between this section and the buttons */
    }

    .buttons .row {
        display: flex;
        justify-content: space-between; /* Spread the buttons evenly */
        margin-bottom: 10px; /* Spacing between rows of buttons */
    }

    .buttons .row.center {
        justify-content: center; /* Center the button in this row */
    }
</style>
