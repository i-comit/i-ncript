<!-- Vault.svelte -->
<script lang="ts">
    import { onMount, onDestroy } from "svelte";
    import { get } from "svelte/store";
    import { Progressbar, Button } from "flowbite-svelte";
    import Toggle from "../elements/Toggle.svelte";

    import { sineOut } from "svelte/easing";

    import {
        InfoCircleOutline,
        AdjustmentsVerticalOutline,
        CirclePauseSolid,
        BarsFromLeftOutline,
        PauseSolid,
    } from "flowbite-svelte-icons";

    import { AppPage, currentPage } from "../enums/AppPage.ts";
    import { Modals, currentModal } from "../enums/Modals.ts";

    import { height, width, lightBGColor } from "../stores/globalVariables.ts";
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
        InterruptEncryption,
    } from "../../wailsjs/go/main/App";

    import { switchPages, switchModals } from "../tools/utils.ts";

    import NeuButton from "../elements/NeuButton.svelte";

    import Frame from "../elements/Frame.svelte";
    import Info from "./Info.svelte";
    import Settings from "./Settings.svelte";
    import TreeView from "./FileTree.svelte";
    import Logger from "./Logger.svelte";

    import {
        EventsOff,
        EventsOn,
        LogError,
        LogInfo,
    } from "../../wailsjs/runtime/runtime";
    import {
        GetDirectoryPath,
        GetFilesByType,
    } from "../../wailsjs/go/main/Getters";
    import DirSize from "../elements/DirectorySizeBar.svelte";
    import CircleProgressBar from "../elements/RadialProgress.svelte";

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
        setIsInFileTask(true).then(() => {
            GetFilesByType(0, false).then((filePaths) => {
                LogInfo("Began encrypting");
                if (filePaths.length > 0) {
                    _fileCt = filePaths.length;
                    EncryptFilesInDir(0);
                    // ResizeWindow(width * 2, height + 15);
                }
            });
        });
    }

    function decrypt() {
        setIsInFileTask(true).then(() => {
            LogInfo("Began decrypting1");
            GetFilesByType(0, true).then((filePaths) => {
                if (filePaths.length > 0) {
                    _fileCt = filePaths.length;
                    DecryptFilesInDir(0);
                }
            });
        });
    }

    function handleDropOnModal(event: DragEvent) {
        event.preventDefault();
        LogError("MODAL " + _modal);
        if (_modal === Modals.None) {
        }
    }

    function stinky() {
        LogInfo("Interrupt");
        InterruptEncryption();
    }
</script>

<div class="flex h-screen !rounded-lg">
    <Frame />
    <DirSize />
    <CircleProgressBar className="left-5 ">
        <div class="absolute inset-5 flex flex-col align-center top-7">
            <p class="h-4 text-center leading-none text-sm select-none">ENCRYPTING</p>
            <PauseSolid class="w-12 h-12 self-center " color="dark" />
            <p class="bg-gray-400 h-4 text-center leading-none select-none">12/123</p>
        </div></CircleProgressBar
    >
    <div
        id="left-panel"
        style="background-color:{lightBGColor}"
        role="none"
        on:mousedown={clearHeldBtns}
    >
        <div id="page-info" class="static">
            <p>VAULT</p>
            <Button
                pill={true}
                outline={true}
                class="z-20 h-0"
                color="dark"
                style="--wails-draggable:drag"
                on:click={() => switchModals(Modals.Info)}
                ><InfoCircleOutline class="w-5 h-5" color="white" /></Button
            >
            <p>32GB</p>
        </div>
        <div class=" !flex !justify-start row center bg-white mb-1">
            <p>encrypted 999999 files</p>
        </div>
        <div class="buttons">
            <div class="row space-x-2">
                <NeuButton on:click={() => encrypt()}>ENCRYPT</NeuButton>
                <NeuButton on:click={() => decrypt()}>DECRYPT</NeuButton>
            </div>
            <div class="h-1"></div>
            <div class="flex justify-between items-center">
                <div class="flex space-x-1">
                    <Button
                        pill={true}
                        outline={true}
                        color="dark"
                        class="!p-1 !px-0 !mb-2 h-1"
                        on:click={stinky}
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

            <div class="h-1"></div>
            <div class="row space-x-2">
                <NeuButton on:click={() => switchPages(AppPage.OBox)}
                    >O-BOX</NeuButton
                >
                <Button
                    pill={true}
                    outline={true}
                    class="!p-1 h-7"
                    color="dark"
                    on:click={() => switchModals(Modals.Settings)}
                    ><AdjustmentsVerticalOutline
                        class="w-5 h-5"
                        color="white"
                    /></Button
                >
                <NeuButton on:click={() => switchPages(AppPage.NBox)}
                    >N-BOX</NeuButton
                >
            </div>
        </div>
    </div>
    <div
        id="right-panel"
        role="none"
        class="bg-gray-500"
        style="background-color:{lightBGColor}"
        on:mouseleave={onmouseleave}
        on:click={clearHeldBtns}
    >
        <CircleProgressBar className="right-10" />
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
