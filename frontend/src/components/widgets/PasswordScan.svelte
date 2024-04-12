<script lang="ts">
    import { Input, Tooltip } from "flowbite-svelte";
    import { createEventDispatcher } from "svelte";
    import { tooltipTailwindClass } from "../../stores/constantVariables";
    import { accentColor } from "../../stores/dynamicVariables";

    export let password: string = "";
    export let _class: string = "flex w-full h-1 px-0.5 relative bottom-1";
    const dispatch = createEventDispatcher();

    // Reactive statement that runs whenever 'password' prop changes
    $: {
        console.log("Password:", password);
        queryPasswordStrength();
    }

    let passwordCheck1 = false; // Length check
    let passwordCheck2 = false; // Uppercase and lowercase check
    let passwordCheck3 = false; // Symbol check
    let passwordCheck = false; // Overall password strength check

    function queryPasswordStrength() {
        // Check 1: Length is more than 5 characters
        const regexLength = /^.{5,}$/;
        passwordCheck1 = regexLength.test(password);
        // Check 2: At least one uppercase and one lowercase letter
        const regexLetters = /^(?=.*[A-Z])(?=.*[a-z])/;
        passwordCheck2 = regexLetters.test(password);
        // Check 3: At least one symbol from the set !@#$
        const regexSymbol = /[~!@#$%^&*_=+<>:;]/;
        passwordCheck3 = regexSymbol.test(password);

        passwordCheck = passwordCheck1 && passwordCheck2 && passwordCheck3;

        dispatch("passwordStrengthUpdated", {
            passwordCheck1,
            passwordCheck2,
            passwordCheck3,
            passwordCheck,
        });
    }
</script>

<div class={`flex w-full h-1 px-0.5 relative bottom-1 ${_class}`} tabindex="-1">
    {#if passwordCheck1 === false}
        <div
            class="flex-1 text-center rounded-l-lg bg-primary-400 dark:bg-primary-300"
        />
    {:else}
        <div
            class="flex-1 text-center rounded-l-lg"
            style={`background-color: ${$accentColor}`}
        />
    {/if}
    <Tooltip placement="left" class={tooltipTailwindClass} arrow={false}
        >&gt; 4 characters</Tooltip
    >
    {#if passwordCheck2 === false}
        <div class="flex-1 text-center bg-primary-400 dark:bg-primary-300" />
    {:else}
        <div
            class="flex-1 text-center"
            style={`background-color: ${$accentColor}`}
        />
    {/if}
    <Tooltip
        placement="top"
        offset={0}
        class={tooltipTailwindClass}
        arrow={false}>a symbol ( #$&amp;! )</Tooltip
    >
    {#if passwordCheck3 === false}
        <div
            class="flex-1 text-center rounded-r-lg bg-primary-400 dark:bg-primary-300"
        />
    {:else}
        <div
            class="flex-1 text-center rounded-r-lg"
            style={`background-color: ${$accentColor}`}
        />
    {/if}
    <Tooltip placement="right" class={tooltipTailwindClass} arrow={false}
        >upper &amp; lower case</Tooltip
    >
</div>
