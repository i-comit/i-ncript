<!-- https://codepen.io/singhimalaya/pen/EdVzNL -->
<script lang="ts">
    import { onDestroy } from "svelte";
    import { LogPrint } from "../../../wailsjs/runtime/runtime";
    import {
        EncryptFilesInDir,
        SetIsHotFilerEnabled,
    } from "../../../wailsjs/go/main/App";
    import { GetFilesByType } from "../../../wailsjs/go/main/Getters";
    import { setIsInFileTask } from "../../tools/fileTree";
    import { FileTasks, currentFileTask } from "../../enums/FileTasks";
    import { totalFileCt } from "../../stores/dynamicVariables";

    let isChecked: boolean;
    isChecked = false;

    // Function to log the checkbox's state
    function toggleHotFiler() {
        LogPrint("toggle state: " + !isChecked);
        SetIsHotFilerEnabled(!isChecked);
        if (!isChecked)
            GetFilesByType(0, false).then((filePaths) => {
                setIsInFileTask(true).then(() => {
                    currentFileTask.set(FileTasks.Encrypt);
                    if (filePaths.length > 0) {
                        totalFileCt.set(filePaths.length);
                        EncryptFilesInDir(0);
                    } else setIsInFileTask(false);
                });
            });
    }

    onDestroy(() => {
        isChecked = true;
    });
</script>

<div class="button r" id="button-1">
    <input
        type="checkbox"
        class="checkbox"
        bind:checked={isChecked}
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
        top: 0px;
        margin: 1px auto 0 10px;
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
