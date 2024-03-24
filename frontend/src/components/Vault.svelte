<!-- Vault.svelte -->
<script>
    import { Button, GradientButton } from "flowbite-svelte";
    import { AppPage } from "../enums/AppPage";
    import { userStore } from "../stores/userStore";
    import { switchFormButton, toggleSettings } from "../utils";
    import { CaretUpSolid } from "flowbite-svelte-icons";
    import { settingsOpened } from "../stores/settingsOpened";
    import LogPanel from "./LogPanel.svelte";
    import Settings from "./Settings.svelte";

    let loggedInUser;
    // Subscribe to the user store
    userStore.subscribe(($user) => {
        loggedInUser = $user;
    });

    function logout() {
        userStore.set(null); // Clear the user store on logout
    }
    function buttonAction(actionName) {
        console.log(`Action for ${actionName}`);
        // Define additional logic for button actions here
    }
    const buttonClasses = "max-w-48 min-h-3 max-h-5 pt-3 px-3";

    let _settingsOpened;
    settingsOpened.subscribe((value) => {
        _settingsOpened = value;
    });
</script>

<div class="app-container">
    <div class="side-menu w-45 max-w-45">
        <div class="vault-info">
            <p>VAULT</p>
            <p>3.6GB</p>
        </div>
        <div class="buttons">
            <div class="row space-x-5">
                <GradientButton
                    color="cyanToBlue"
                    class={buttonClasses}
                    on:click={() => buttonAction("ENCRYPT")}
                    >ENCRYPT</GradientButton
                >
                <GradientButton
                    color="cyanToBlue"
                    class={buttonClasses}
                    on:click={() => buttonAction("DECRYPT")}
                    >DECRYPT</GradientButton
                >
            </div>
            <div class="h-4"></div>
            <div class="row center">
                <GradientButton
                    color="cyanToBlue"
                    class="min-w-24 max-h-3 m-0 px-0 pt-3"
                    pill
                    on:click={() => buttonAction("HOT FILER")}
                    >HOT FILER</GradientButton
                >
            </div>
            <div class="h-8"></div>
            <div class="row space-x-3">
                <GradientButton
                    color="cyanToBlue"
                    class={buttonClasses}
                    on:click={() => switchFormButton(AppPage.OBox)}
                    >O-BOX</GradientButton
                >
                <Button
                    pill={true}
                    outline={true}
                    class="!p-1"
                    color="dark"
                    on:click={() => toggleSettings()}
                    ><CaretUpSolid class="w-5 h-5 m-0" color="dark" /></Button
                >
                <GradientButton
                    color="cyanToBlue"
                    class={buttonClasses}
                    on:click={() => switchFormButton(AppPage.NBox)}
                    >N-BOX</GradientButton
                >
            </div>
        </div>
    </div>
    <div class="main-panel">
        {#if !_settingsOpened}
            <LogPanel />
        {:else}
            <Settings />
        {/if}
    </div>
</div>

<style>
    .app-container {
        display: flex;
        height: 100vh;
    }
    .side-menu {
        /* min-width: 200px; */
        background-color: #f0f0f0;
        padding: 1rem;
    }
    .main-panel {
        flex-grow: 1;
        padding: 1rem;
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
