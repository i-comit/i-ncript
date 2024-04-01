<!-- NBox.svelte -->
<script lang="ts">
    import { onDestroy, onMount } from "svelte";
    import { Label, Input, GradientButton, Button } from "flowbite-svelte";
    import { AdjustmentsVerticalOutline } from "flowbite-svelte-icons";

    import { AppPage, currentPage } from "../enums/AppPage.ts";
    import { Modals, currentModal } from "../enums/Modals.ts";

    import { pageChangeBtn } from "../stores/globalVariables.ts";
    import { buildFileTree, fileTree } from "../tools/fileTree.ts";

    import { switchPages, switchModals } from "../tools/utils.ts";

    import Frame from "../elements/Frame.svelte";
    import Info from "./Info.svelte";
    import Settings from "./Settings.svelte";
    import TreeView from "./FileTree.svelte";
    import Logger from "./Logger.svelte";

    let _modal: Modals;
    currentModal.subscribe((value) => {
        _modal = value;
    });
    onMount(() => {
        buildFileTree();
    });
</script>

<div class="flex h-screen !rounded-lg">
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
                    on:click={() => switchPages(AppPage.Vault)}
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
                    on:click={() => switchPages(AppPage.OBox)}
                    >O-BOX</GradientButton
                >
            </div>
        </div>
    </div>
    <div id="right-panel" class="bg-gray-500 mt-6 px-0">
        {#if _modal === Modals.None}
            <TreeView _fileTree={$fileTree} />
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
