<!-- NBox.svelte -->
<script>
    import { onMount } from "svelte";
    import { Label, Input, GradientButton } from "flowbite-svelte";
    import { ThumbsUpSolid, CaretUpSolid } from "flowbite-svelte-icons";

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

    function buttonAction(actionName) {
        console.log(`Action for ${actionName}`);
        // Define additional logic for button actions here
    }

    onMount(() => {
        loadDirectoryTree(1);
    });
    const buttonClasses = "max-w-48 min-h-2 max-h-5 pt-3 px-3";
</script>

<div class="flex h-screen rounded-2xl">
    <Frame />
    <div class="side-menu max-w-45">
        <div>
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
    <div class="main-panel bg-white mt-6">
        <TreeView tree={$fileTree} />
    </div>
</div>

<style>
    .side-menu {
        background-color: #f0f0f0;
        padding: 1rem;
    }
    .main-panel {
        flex-grow: 1;
        padding: 1rem;
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
