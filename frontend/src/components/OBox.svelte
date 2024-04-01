<!-- OBox.svelte -->
<script lang="ts">
    import { onMount } from "svelte";
    import { GradientButton, Label, Input } from "flowbite-svelte";

    import { AppPage } from "../enums/AppPage.ts";
    import { Modals, currentModal } from "../enums/Modals.ts";

    import { pageChangeBtn } from "../stores/globalVariables.ts";
    import { buildFileTree, fileTree } from "../tools/fileTree.ts";

    import { switchPages } from "../tools/utils.ts";

    import Frame from "./Frame.svelte";
    import TreeView from "./FileTree.svelte";
    import Info from "./Info.svelte";
    import Settings from "./Settings.svelte";
    import Logger from "./Logger.svelte";

    let _modal: Modals;
    currentModal.subscribe((value) => {
        _modal = value;
    });

    // let loggedInUser;
    // hashedCredentials.subscribe(($user) => {
    //     loggedInUser = $user;
    // });

    onMount(() => {
        buildFileTree();
    });
</script>

<div class="flex h-screen !rounded-lg">
    <Frame />
    <div id="left-panel" class="max-w-45">
        <div id="page-info" class="static">
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
                <GradientButton color="cyanToBlue" class="max-h-1"
                    >ENTER</GradientButton
                >
            </div>
            <div class="h-2"></div>

            <div class="row space-x-5">
                <GradientButton
                    color="cyanToBlue"
                    class={pageChangeBtn}
                    on:click={() => switchPages(AppPage.NBox)}
                    >N-BOX</GradientButton
                >
                <GradientButton
                    color="cyanToBlue"
                    class={pageChangeBtn}
                    on:click={() => switchPages(AppPage.Vault)}
                    >VAULT</GradientButton
                >
            </div>
        </div>
    </div>
    <div id="right-panel" class="bg-gray-500 mt-6 px-0">
        {#if _modal === Modals.None}
            <TreeView fileTree={$fileTree} />
        {:else if _modal === Modals.Settings}
            <Settings />
        {:else if _modal === Modals.Info}
            <Info />
        {:else if _modal === Modals.Logger}
            <Logger />
        {/if}
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
