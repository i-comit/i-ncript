<!-- https://codepen.io/singhimalaya/pen/EdVzNL -->
<script lang="ts">
    let isChecked: boolean;
    isChecked = false;
    import { LogMessage } from "../../wailsjs/go/main/Logger";

    // Function to log the checkbox's state
    function logCheckboxState() {
        LogMessage("Checkbox is pressed. Current state:" + isChecked);
    }
</script>

<div class="button r bg-gray-100" id="button-1">
    <input
        type="checkbox"
        class="checkbox"
        bind:checked={isChecked}
        on:click={logCheckboxState}
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
        width: 70px;
        height: 20px;
        margin: 1px auto 0 10px;
        overflow: hidden;
    }

    .button.r,
    .button.r .layer {
        border-radius: 100px;
    }

    .button.b2 {
        border-radius: 2px;
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
        background-color: #ebf7fc;
        transition: 0.2s ease all;
        z-index: 1;
    }

    #button-1 .knobs:before {
        content: "";
        position: absolute;
        top: 2px;
        left: 2px;
        width: 18px;
        height: 10px;
        color: #fff;
        font-size: 10px;
        font-weight: bold;
        text-align: center;
        line-height: 1;
        padding: 8px 3px;
        background-color: #03a9f4;
        border-radius: 50%;
        transition: 0.2s cubic-bezier(0.18, 0.89, 0.35, 1.15) all;
    }

    #button-1 .checkbox:checked + .knobs:before {
        content: "";
        left: 50px;
        background-color: #f44336;
    }

    #button-1 .checkbox:checked ~ .layer {
        background-color: #fcebeb;
    }

    #button-1 .knobs,
    #button-1 .knobs:before,
    #button-1 .layer {
        transition: 0.2s ease all;
    }
</style>
