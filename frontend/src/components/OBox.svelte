<!-- OBox.svelte -->
<script>
    import { onMount } from "svelte";
    import { GradientButton, Label, Input } from "flowbite-svelte";

    import { AppPage } from "../enums/AppPage";

    import { usernameStore } from "../stores/usernameStore";
    import { fileTree } from "../stores/fileTree.ts";
    import { pageChangeBtn } from "../stores/pageChangeBtn.js";

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

<div class="flex h-screen rounded-lg">
    <Frame />
    <div id="left-panel" class="max-w-45">
        <div id="page-info" class="static bg-gray-100">
            <p>O-BOX</p>
            <p>3.5GB</p>
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
                    class={pageChangeBtn}
                    on:click={() => switchFormButton(AppPage.NBox)}
                    >N-BOX</GradientButton
                >
                <GradientButton
                    color="cyanToBlue"
                    class={pageChangeBtn}
                    on:click={() => switchFormButton(AppPage.Vault)}
                    >VAULT</GradientButton
                >
            </div>
        </div>
    </div>
    <div id="right-panel" class="bg-white mt-6">
        <TreeView tree={$fileTree} />
    </div>
</div>

<style>
    p {
        color: black;
    }
    #left-panel {
        background-color: #f5f5f5;
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
