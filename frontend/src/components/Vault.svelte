<!-- Vault.svelte -->
<script lang="ts">
    import { onMount } from "svelte";
    import { Button, GradientButton, Popover } from "flowbite-svelte";
    import {
        CaretUpSolid,
        AdjustmentsVerticalOutline,
    } from "flowbite-svelte-icons";

    import { AppPage } from "../enums/AppPage.ts";
    import { Modals } from "../enums/Modals.ts";

    import { usernameStore } from "../stores/usernameStore";
    import { fileTree } from "../stores/fileTree.ts";
    import { defaultBtn } from "../stores/defaultBtn.js";
    import { GetDirectoryStructure } from "../../wailsjs/go/main/App";
    import { LogMessage } from "../../wailsjs/go/main/Logger";

    import {
        switchFormButton,
        switchModals,
        loadDirectoryTree,
    } from "../utils.ts";

    import Frame from "./Frame.svelte";
    import LogPanel from "./LogPanel.svelte";
    import Options from "./Settings.svelte";
    import TreeView from "./TreeView.svelte";

    onMount(() => {
        loadDirectoryTree(0);
    });
    let loggedInUser;
    usernameStore.subscribe(($user) => {
        loggedInUser = $user;
    });

    function buttonAction(actionName: string) {
        console.log(`Action for ${actionName}`);
    }
</script>

<div class="app-container h-screen rounded-lg">
    <Frame />
    <div class="side-menu w-45 max-w-45">
        <div class="vault-info static">
            <p>VAULT</p>
            <p>3.6GB</p>
        </div>
        <div class="buttons">
            <div class="row space-x-5">
                <GradientButton
                    color="cyanToBlue"
                    class={defaultBtn}
                    on:click={() => buttonAction("ENCRYPT")}
                    >ENCRYPT</GradientButton
                >
                <GradientButton
                    color="cyanToBlue"
                    class={defaultBtn}
                    on:click={() => buttonAction("DECRYPT")}
                    >DECRYPT</GradientButton
                >
            </div>
            <div class="h-2"></div>
            <div class="row center">
                <GradientButton
                    color="cyanToBlue"
                    class="min-w-24 max-h-3 m-0 px-0 pt-3"
                    pill
                    on:click={() => {}}>HOT FILER</GradientButton
                >
            </div>
            <!-- <div class="h-8"></div> -->
            <div class="row space-x-3">
                <GradientButton
                    color="cyanToBlue"
                    class={defaultBtn}
                    on:click={() => switchFormButton(AppPage.OBox)}
                    >O-BOX</GradientButton
                >
                <Button
                    pill={true}
                    outline={true}
                    class="!p-1"
                    color="dark"
                    on:click={() => switchModals(Modals.Settings)}
                    ><AdjustmentsVerticalOutline
                        class="w-5 h-5 m-0"
                        color="dark"
                    /></Button
                >
                <Popover
                    class="w-64 text-sm font-light "
                    placement="bottom"
                    title="Popover left"
                    >And here's some amazing content. It's very engaging. Right?</Popover
                >

                <GradientButton
                    color="cyanToBlue"
                    class={defaultBtn}
                    on:click={() => switchFormButton(AppPage.NBox)}
                    >N-BOX</GradientButton
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
    }
    .side-menu {
        background-color: aliceblue;
        padding: 1rem;
        padding-top: 0.2rem;
    }
    .main-panel {
        /* padding: 0.1rem; */
        /* padding-top: 1rem; */
        max-height: 80vh; /* 80% of the viewport height */
        max-width: 100%; /* Optional: For horizontal scrolling, you might set a max-width */

        /* Enable scrolling within the div */
        overflow-y: auto; /* Enables vertical scrolling */
        overflow-x: auto; /* Enables horizontal scrolling */

        /* Other styles for layout and appearance */
        position: relative; /* or 'fixed' depending on your layout needs */
        box-sizing: border-box;
        scrollbar-width: none; /* For Firefox */
        -ms-overflow-style: none;
    }

    .main-panel::-webkit-scrollbar {
        width: 0px; /* For vertical scrollbar */
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
