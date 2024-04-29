<!-- https://codepen.io/singhimalaya/pen/EdVzNL -->
<script lang="ts">
    import { onDestroy } from "svelte";
    import { EncryptFiles, SetHotFiler } from "../../../wailsjs/go/main/App";
    import { GetFilesByType } from "../../../wailsjs/go/main/Getters";
    import { setIsInFileTask } from "../../tools/fileTree";
    import { FileTasks, currentFileTask } from "../../enums/FileTasks";
    import {
        accentColor,
        hotFiler,
        totalFileCt,
    } from "../../stores/dynamicVariables";
    import { heldDownBtns } from "../../tools/utils";
    import { startDisplay } from "../../tools/logger";
    import { get } from "svelte/store";

    function toggleHotFiler() {
        hotFiler.update((v) => !v);
        let _hotFiler = get(hotFiler);
        SetHotFiler(_hotFiler);
        if (_hotFiler)
            GetFilesByType(0, false).then((filePaths) => {
                if (!filePaths) {
                    startDisplay("no files to encrypt..");
                    return;
                }
                setIsInFileTask(true).then(() => {
                    heldDownBtns.set({});
                    currentFileTask.set(FileTasks.Encrypt);
                    if (filePaths.length > 0) {
                        totalFileCt.set(filePaths.length);
                        EncryptFiles(filePaths);
                    } else setIsInFileTask(false);
                });
            });
    }

    onDestroy(() => {
        hotFiler.set(false);
    });
</script>

<div
    class="button r hover:outline focus:outline-none"
    style={`outline-color:${$accentColor}`}
    id="button-1"
>
    <input
        type="checkbox"
        class="checkbox"
        bind:checked={$hotFiler}
        on:click={toggleHotFiler}
    />
    <div class="knobs"></div>
    <div class="layer"></div>
</div>

<style>
    .knobs,
    .layer {
        position: absolute;
        top: 0;
        right: 0;
        bottom: 0;
        left: 0;
    }
    .button {
        position: relative;
        width: 50%;
        height: 22px;
        top: 0.5px;
        margin: 1px 0.5px 0 10px;
        overflow: hidden;
    }

    .button.r,
    .button.r .layer {
        border-radius: 0.5rem;
    }

    .checkbox {
        position: relative;
        width: 100%;
        height: 100%;
        padding: 0;
        margin: 0;
        opacity: 0;
        cursor: pointer;
        z-index: 3;
    }

    .knobs {
        z-index: 2;
    }

    .layer {
        width: 100%;
        background-color: #b6b6b6;
        transition: 0.2s ease all;
        z-index: 1;
    }

    #button-1 .knobs:before {
        content: "";
        position: absolute;
        top: 2px;
        left: 2px;
        width: 20px;
        height: 18px;
        color: #fff;
        font-size: 10px;
        font-weight: bold;
        text-align: center;
        line-height: 1;
        padding: 8px 3px;
        background-color: #969696;
        border-radius: 36%;
        transition: 0.2s cubic-bezier(0.18, 0.89, 0.35, 1.15) all;
    }

    #button-1 .checkbox:checked + .knobs:before {
        content: "";
        left: 72%;
        background-color: #a9d3ff;
    }

    #button-1 .checkbox:checked ~ .layer {
        background-color: #d1eef7;
    }

    #button-1 .knobs,
    #button-1 .knobs:before,
    #button-1 .layer {
        transition: 0.2s ease all;
    }
</style>
