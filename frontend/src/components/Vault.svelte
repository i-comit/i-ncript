<!-- Vault.svelte -->
<script lang="ts">
    import { onMount, onDestroy } from "svelte";
    import { get } from "svelte/store";
    import { GradientButton, Popover, Progressbar } from "flowbite-svelte";
    import Button from "../elements/Button.svelte";
    import Toggle from "../elements/Toggle.svelte";

    import { sineOut } from "svelte/easing";

    import {
        InfoCircleOutline,
        AdjustmentsVerticalOutline,
        CirclePauseSolid,
        BarsFromLeftOutline,
    } from "flowbite-svelte-icons";

    import { AppPage, currentPage } from "../enums/AppPage.ts";
    import { Modals, currentModal } from "../enums/Modals.ts";

    import {
        pageChangeBtn,
    } from "../stores/globalVariables.js";
    import { encryptProgress } from "../stores/encryptProgress";

    import {
        buildFileTree,
        fileTree,
        setIsInFileTask,
        clearHeldBtns,
    } from "../tools/fileTree.ts";

    import {
        ResizeWindow,
        EncryptFilesInDir,
        DecryptFilesInDir,
    } from "../../wailsjs/go/main/App";
    // import {
    //     GetEncryptedFiles,
    //     GetDecryptedFiles,
    // } from "../../wailsjs/go/main/Encrypt";

    import {
        switchPages,
        switchModals,
        height,
        width,
    } from "../tools/utils.ts";

    import Frame from "./Frame.svelte";
    import Info from "./Info.svelte";
    import Settings from "./Settings.svelte";
    import TreeView from "./FileTree.svelte";
    import Logger from "./Logger.svelte";

    import {
        EventsOff,
        EventsOn,
        LogError,
    } from "../../wailsjs/runtime/runtime";
    import {
        GetDirectoryPath,
        GetFilesByType,
    } from "../../wailsjs/go/main/Getters";

    let _fileCt: number;
    _fileCt = 0;
    onMount(() => {
        buildFileTree();
        EventsOn("fileProcessed", (currentCount) => {
            encryptProgress.set(currentCount);
            _encryptPercent = (_encryptProgress / _fileCt) * 100;
        });
    });

    let _encryptProgress: number;
    encryptProgress.subscribe(() => {
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
        GetFilesByType(0, false).then((filePaths) => {
            setIsInFileTask(true);
            if (filePaths.length > 0) {
                _fileCt = filePaths.length;
                EventsOff("rebuildFileTree");
                EncryptFilesInDir(0);
                ResizeWindow(width * 2, height + 15, false);
            }
        });
    }

    function decrypt() {
        GetFilesByType(0, true).then((filePaths) => {
            setIsInFileTask(true);
            if (filePaths.length > 0) {
                _fileCt = filePaths.length;
                EventsOff("rebuildFileTree");
                DecryptFilesInDir(0);
                ResizeWindow(width * 2, height + 15, false);
            }
        });
    }

    function handleDropOnModal(event: DragEvent) {
        event.preventDefault();
        LogError("MODAL " + _modal);
        if (_modal === Modals.None) {
        }
    }
</script>

<div class="flex h-screen !rounded-lg">
    <Frame />
    <div id="left-panel" class="w-45" role="none" on:mousedown={clearHeldBtns}>
        <div id="page-info" class="static">
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
            <p>32GB</p>
        </div>
        <div class=" !flex !justify-start row center bg-white mb-1">
            <p>encrypted n files</p>
        </div>
        <div class="buttons">
            <div class="row space-x-5">
                <Button on:click={() => encrypt()}>NCRYPT</Button>
                <Button on:click={() => decrypt()}>DCRYPT</Button>
            </div>
            <div class="h-1"></div>
            <div class="flex justify-between items-center">
                <div class="flex space-x-1">
                    <Button
                        pill={true}
                        outline={true}
                        color="dark"
                        class="!p-1 !px-0 !mb-2"
                        ><CirclePauseSolid
                            class="w-5 h-5"
                            color="white"
                        /></Button
                    >
                    <button
                        class="!p-1 !px-0 !mb-2"
                        color="dark"
                        on:click={() => switchModals(Modals.Logger)}
                        ><BarsFromLeftOutline
                            class="w-5 h-5"
                            color="dark"
                        /></button
                    >
                </div>
                <div>
                    <Toggle />
                    <p class="text-sm">HOT FILER</p>
                </div>
            </div>

            <div class="h-2.5"></div>
            <div class="row">
                <GradientButton
                    color="cyanToBlue"
                    class={pageChangeBtn}
                    on:click={() => switchPages(AppPage.OBox)}
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
                    class={pageChangeBtn}
                    on:click={() => switchPages(AppPage.NBox)}
                    >N-BOX</GradientButton
                >
            </div>
        </div>
    </div>
    <div
        id="right-panel"
        role="none"
        class="bg-gray-500 mt-0 px-0"
        on:mouseleave={onmouseleave}
        on:click={clearHeldBtns}
    >
        {#if _modal === Modals.None}
            <TreeView tree={$fileTree} />
            <!-- <Settings /> -->
        {:else if _modal === Modals.Settings}
            <Settings />
        {:else if _modal === Modals.Logger}
            <Logger />
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
        justify-content: space-between;
        margin-bottom: 10px;
    }
</style>
