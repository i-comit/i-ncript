<!-- NBox.svelte -->
<script lang="ts">
    import { onMount } from "svelte";
    import { Label, Input, GradientButton, Button } from "flowbite-svelte";
    import {
        AdjustmentsVerticalOutline,
        ThumbsUpSolid,
        CaretUpSolid,
    } from "flowbite-svelte-icons";

    import { AppPage } from "../enums/AppPage";
    import { Modals } from "../enums/Modals.ts";

    import { usernameStore } from "../stores/usernameStore";
    import { fileTree } from "../stores/fileTree.ts";
    import { pageChangeBtn } from "../stores/pageChangeBtn.js";
    import { currentModal } from "../stores/currentModal";

    import {
        switchFormButton,
        switchModals,
        loadDirectoryTree,
    } from "../utils";

    import Frame from "./Frame.svelte";
    import Info from "./Info.svelte";
    import Settings from "./Settings.svelte";
    import TreeView from "./TreeView.svelte";
    import Logger from "./Logger.svelte";

    import {loadExpansionState} from "../stores/treeViewStates"

    let loggedInUser;
    usernameStore.subscribe(($user) => {
        loggedInUser = $user;
    });

    // function buttonAction(actionName) {
    //     console.log(`Action for ${actionName}`);
    //     // Define additional logic for button actions here
    // }
    let _modal: Modals;
    currentModal.subscribe((value) => {
        _modal = value;
    });
    onMount(() => {
        loadDirectoryTree(1);
        // loadExpansionState();
    });
</script>

<div class="flex h-screen rounded-lg">
    <Frame />
    <div id="left-panel" class="max-w-45">
        <div id="page-info" class="static">
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
                    on:click={() => {}}>ENTER</GradientButton
                >
            </div>
            <div class="h-2"></div>

            <div class="row">
                <GradientButton
                    color="cyanToBlue"
                    class={pageChangeBtn}
                    on:click={() => switchFormButton(AppPage.Vault)}
                    >VAULT</GradientButton
                >
                <Button
                    pill={true}
                    outline={true}
                    class="!p-1"
                    color="dark"
                    on:click={() => switchModals(Modals.Settings)}
                    ><AdjustmentsVerticalOutline
                        class="w-5 h-5"
                        color="white"
                    /></Button
                >
                <GradientButton
                    color="cyanToBlue"
                    class={pageChangeBtn}
                    on:click={() => switchFormButton(AppPage.OBox)}
                    >O-BOX</GradientButton
                >
            </div>
        </div>
    </div>
    <div id="right-panel" class="bg-gray-500 mt-6 px-0">
        {#if _modal === Modals.None}
            <TreeView tree={$fileTree} />
        {:else if _modal === Modals.Settings}
            <Settings />
        {:else if _modal === Modals.Logger}
            <Logger />
        {:else if _modal === Modals.Info}
            <Info />
        {/if}
    </div>
</div>

<style>
    p {
        color: black;
    }
    #left-panel {
        background-color: #fcfcfc;
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
