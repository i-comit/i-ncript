<script lang="ts">
    import { lightBGColor } from "../stores/globalVariables";
    import {
        CogSolid,
        BarsFromLeftOutline,
        InfoCircleSolid,
        InfoCircleOutline,
    } from "flowbite-svelte-icons";
    import { switchModals } from "../tools/utils";
    import { Modals } from "../enums/Modals";
    import { LogInfo } from "../../wailsjs/runtime/runtime";
</script>

<div class="absolute -z-10 bg-slate-500 w-20 h-full select-none left-1/3"></div>
<div class="panel" style="background-color:{lightBGColor}">
    <div
        class="absolute w-full h-1/5 rounded-full bg-slate-500 select-none"
        style="top:90%"
    ></div>

    <div class="icon space-y-1">
        <div class="icon__home">
            <InfoCircleOutline
                class="p-px"
                on:click={() => switchModals(Modals.Info)}
            />
        </div>
        <div class="icon__account">
            <BarsFromLeftOutline
                class="p-px" role="button"
                on:click={() => switchModals(Modals.Logger)}
            />
        </div>
        <button
            class="icon__settings"
            on:click={() => switchModals(Modals.Settings)}
        >
            <CogSolid class="p-px" />
        </button>
    </div>
    <div
        class="absolute w-full h-1/5 rounded-full bg-slate-500 select-none"
        style="bottom:90%"
    ></div>

</div>

<style lang="scss">
    $panel-width: 1.65rem;
    @import "../neumorphic.scss";

    .panel {
        width: $panel-width;
        background-color: green;
        display: flex;
        justify-content: center; /* Centers horizontally */
        align-items: center; /* Centers vertically */
        height: 65%;
        top: 20%;
        position: relative; // Ensure .inverted-border-radius positions relative to .panel
    }

    $icon-size: 1.2rem;
    :root {
        --primary-light: #8abdff;
        --primary: #8daacb;
        --primary-dark: #5b0eeb;

        --white: #ffffff;
        --greyLight-1: #e4ebf5;
        --greyLight-2: #c8d0e7;
        --greyLight-3: #bec8e4;
        --greyDark: #8e8e8e;
    }

    $dropShadow:
        2px 2px 2px -2px rgba($darkShadow, 0.4),
        -2px -3px 3px -1px rgba($lightShadow, 1);

    $innerShadow:
        inset -2px -2px 2px -1px rgba($lightShadow, 1),
        inset 1px 1px 2px -1px rgba($darkShadow, 0.5);

    /*  ICONS  */
    .icon {
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        &__account,
        &__home,
        &__settings {
            width: $icon-size;
            height: $icon-size;
            border-radius: 50%;
            box-shadow: $dropShadow;
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 2rem;
            cursor: pointer;
            color: var(--greyDark);
            transition: all 0.2s ease;
            &:active {
                box-shadow: $innerShadow;
                color: var(--primary);
            }
            &:hover {
                color: var(--primary);
            }
        }
    }
</style>
