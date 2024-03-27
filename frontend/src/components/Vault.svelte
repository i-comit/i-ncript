<!-- Vault.svelte -->
<script lang="ts">
    import { onMount, onDestroy } from "svelte";
    import { GradientButton, Popover, Progressbar } from "flowbite-svelte";
    import Button from "../elements/Button.svelte";
    import Spinner from "../elements/Spinner.svelte";

    import { sineOut } from "svelte/easing";

    import {
        InfoCircleOutline,
        AdjustmentsVerticalOutline,
    } from "flowbite-svelte-icons";

    import { AppPage } from "../enums/AppPage.ts";
    import { Modals } from "../enums/Modals.ts";

    import { usernameStore } from "../stores/usernameStore";
    import { fileTree } from "../stores/fileTree";
    import { defaultBtn } from "../stores/defaultBtn";
    import { encryptProgress } from "../stores/encryptProgress";
    import { currentModal } from "../stores/currentModal";

    import {
        ResizeWindow,
        EncryptFilesInDir,
        DecryptFilesInDir,
    } from "../../wailsjs/go/main/App";
    import { GetDirectoryFileCt } from "../../wailsjs/go/main/Getters";
    import {
        GetEncryptedFiles,
        GetDecryptedFiles,
    } from "../../wailsjs/go/main/Encryptr";

    import { LogMessage } from "../../wailsjs/go/main/Logger";

    import {
        switchFormButton,
        switchModals,
        loadDirectoryTree,
    } from "../utils.ts";

    import Frame from "./Frame.svelte";
    import Info from "./Info.svelte";
    import Settings from "./Settings.svelte";
    import TreeView from "./TreeView.svelte";
    import { EventsOn } from "../../wailsjs/runtime/runtime";

    let _fileCt: number;
    _fileCt = 0;
    onMount(() => {
        loadDirectoryTree(0);
        EventsOn("fileProcessed", (currentCount) => {
            encryptProgress.set(currentCount);
            _encryptPercent = (_encryptProgress / _fileCt) * 100;
            // LogMessage(
            //     `Processed ${_encryptProgress} of ${_fileCt} files. ${_encryptPercent}`,
            // );
        });
        EventsOn("fileCount", (fileCount) => {
            _fileCt = fileCount;
            // LogMessage(`New fileCount ${_fileCt}`);
        });
    });
    let _username;
    usernameStore.subscribe(($user) => {
        _username = $user;
    });

    let _encryptProgress: number;
    _encryptProgress = 0;
    encryptProgress.subscribe(($encryptProgress) => {
        _encryptProgress = $encryptProgress;
    });

    let _modal: Modals;
    currentModal.subscribe((value) => {
        _modal = value;
    });

    let _encryptPercent: number;
    let encrypting: boolean;
    encrypting = false;
    function encrypt() {
        GetDecryptedFiles(0).then((filePaths) => {
            if (filePaths.length > 0) {
                _fileCt = filePaths.length;
                EncryptFilesInDir(0);
                ResizeWindow(500, 260, false);
            }
        });
    }

    function decrypt() {
        GetEncryptedFiles(0).then((filePaths) => {
            if (filePaths.length > 0) {
                _fileCt = filePaths.length;
                DecryptFilesInDir();
                ResizeWindow(440, 230, false);
            }
        });
    }
</script>

<div class="flex h-screen rounded-lg">
    <Frame />
    <div id="left-panel" class="w-45">
        <div id="page-info" class="static bg-gray-100">
            <p>VAULT</p>
            <Button
                pill={true}
                outline={true}
                class="z-20"
                color="dark"
                style="--wails-draggable:drag"
                on:click={() => switchModals(Modals.Info)}
                ><InfoCircleOutline class="w-5 h-5" color="white" /></Button
            >
            <p>3.6GB</p>
        </div>
        <div class=" !flex !justify-start row center bg-white mb-1">
            <p>encrypted n files</p>
        </div>
        <div class="buttons">
            <div class="row space-x-5">
                <Button on:click={() => encrypt()}>ENCRYPT</Button>
                <Button on:click={() => encrypt()}>DECRYPT</Button>
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
                        class="w-5 h-5"
                        color="white"
                    /></Button
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
    <div id="right-panel" class="bg-white mt-6 px-0">
        {#if _modal === Modals.None}
            <TreeView tree={$fileTree} />
        {:else if _modal === Modals.Settings}
            <Settings />
        {:else if _modal === Modals.Info}
            <Info />
        {/if}
    </div>
    {#if _encryptProgress !== 0}
        <Progressbar
            progress={_encryptPercent}
            animate
            precision={0}
            labelInside
            tweenDuration={1000}
            easing={sineOut}
            size="h-6"
            labelInsideClass="bg-blue-600 text-blue-100 text-base font-medium text-center p-1 leading-none rounded-lg"
            class="absolute bottom-0 w-screen"
        />
    {/if}
</div>

<style>
    p {
        color: black;
    }

    #left-panel {
        background-color: #e8e8e8;
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
