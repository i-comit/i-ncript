<!-- NBox.svelte -->
<script>
    import { userStore } from "../stores/userStore";
    import { AppPage } from "../enums/AppPage";
    import { Label, Input, GradientButton } from "flowbite-svelte";
    import { switchFormButton } from "../utils";
    import { ThumbsUpSolid, CaretUpSolid } from "flowbite-svelte-icons";

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
    const buttonClasses = "max-w-48 min-h-2 max-h-5 pt-3 px-3";
</script>

<div class="app-container">
    <div class="side-menu max-w-45">
        <div class="vault-info">
            <p>N-BOX</p>
            <p>3.6GB</p>
        </div>
        <div class="buttons">
            <div class="row">
                <Label for="small-input" class="block mb-2">Default</Label>
                <Input
                    class="max-h-1 max-w-32"
                    id="small-input"
                    placeholder="Default input"
                />
            </div>
            <div class="row">
                <Label for="small-input" class="block mb-2">Default</Label>
                <Input
                    class="max-h-1 max-w-32"
                    id="small-input"
                    placeholder="Default input"
                />
            </div>
            <div class="row center">
                <GradientButton
                    color="cyanToBlue"
                    class="max-h-1"
                    on:click={() => buttonAction("HOT FILER")}
                    >ENTER</GradientButton
                >
            </div>
            <div class="h-2"></div>

            <div class="row space-x-5">
                <GradientButton
                    color="cyanToBlue"
                    class={buttonClasses}
                    on:click={() => switchFormButton(AppPage.Vault)}
                    >VAULT</GradientButton
                >
                <GradientButton
                    color="cyanToBlue"
                    class={buttonClasses}
                    on:click={() => switchFormButton(AppPage.OBox)}
                    >O-BOX</GradientButton
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
