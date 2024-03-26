<!-- OBox.svelte -->
<script>
    import { onMount } from "svelte";
    import { GradientButton } from "flowbite-svelte";

    import { AppPage } from "../enums/AppPage";

    import { usernameStore } from "../stores/usernameStore";
    import { fileTree } from "../stores/fileTree.ts";

    import { switchFormButton, loadDirectoryTree } from "../utils";

    import Frame from "./Frame.svelte";
    import TreeView from "./TreeView.svelte";

    let loggedInUser;
    usernameStore.subscribe(($user) => {
        loggedInUser = $user;
    });

    onMount(() => {
        loadDirectoryTree(2);
    });
    function buttonAction(actionName) {
        console.log(`Action for ${actionName}`);
    }
</script>

<div class="app-container">
    <div class="side-menu w-50 max-w-50">
        <Frame />
        <div>
            <p>O-BOX</p>
            <p>3.6GB</p>
        </div>
        <div class="buttons">
            <div class="row">
                <GradientButton
                    color="cyanToBlue"
                    pill
                    on:click={() => buttonAction("ENCRYPT")}
                    >Button 1</GradientButton
                >
                <GradientButton
                    color="cyanToBlue"
                    pill
                    on:click={() => buttonAction("DECRYPT")}
                    >Button2</GradientButton
                >
            </div>
            <div class="row center"></div>
            <div class="row">
                <GradientButton
                    color="cyanToBlue"
                    pill
                    on:click={() => switchFormButton(AppPage.NBox)}
                    >N-BOX</GradientButton
                >
                <GradientButton
                    color="cyanToBlue"
                    pill
                    on:click={() => switchFormButton(AppPage.Vault)}
                    >VAULT</GradientButton
                >
            </div>
        </div>
    </div>
    <div class="main-panel bg-white mt-6">
        <TreeView tree={$fileTree} />
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

    .buttons .row {
        display: flex;
        justify-content: space-between; /* Spread the buttons evenly */
        margin-bottom: 10px; /* Spacing between rows of buttons */
    }

    .buttons .row.center {
        justify-content: center; /* Center the button in this row */
    }
</style>
