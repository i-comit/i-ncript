<!-- Vault.svelte -->
<script>
    import {  GradientButton } from "flowbite-svelte";
    import { AppPage } from "../enums/AppPage";
    import { user } from "../stores/userStore";
    import { switchFormButton } from '../utils';

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
        <div class="vault-info">
            <p>VAULT</p>
            <p>3.6GB</p>
        </div>
        <div class="buttons">
            <div class="row">
                <GradientButton
                    color="cyanToBlue"
                    pill
                    on:click={() => buttonAction("ENCRYPT")}
                    >ENCRYPT</GradientButton
                >
                <GradientButton
                    color="cyanToBlue"
                    pill
                    on:click={() => buttonAction("DECRYPT")}
                    >DECRYPT</GradientButton
                >
            </div>
            <div class="row center">
                <button class="btn" on:click={() => buttonAction("HOT FILER")}
                    >HOT FILER</button
                >
            </div>
            <div class="row">
                <GradientButton
                    color="cyanToBlue"
                    pill
                    on:click={() => switchFormButton(AppPage.OBox)}
                    >O-BOX</GradientButton
                >
                <GradientButton
                    color="cyanToBlue"
                    pill
                    on:click={() => switchFormButton(AppPage.NBox)}
                    >N-BOX</GradientButton
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
