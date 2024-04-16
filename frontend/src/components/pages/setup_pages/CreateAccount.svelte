<script>
    import { createEventDispatcher } from "svelte";
    import { GradientButton } from "flowbite-svelte";
    // import { setupPagePClass } from "../../../stores/constantVariables";
    const setupPagePClass =
        "leading-1 text-primary-200 dark:text-primary-100 my-2";
    const dispatch = createEventDispatcher();

    function handleClick() {
        dispatch("click");
    }
</script>

<p class={`${setupPagePClass} !mt-1`}>
    While i-ncript does have mechanisms in place to check for duplicate files,
    it is up to the user to take precaution when encrypting their data. Always
    ensure that you are encrypting the data that you want to encrypt.
</p>
<p class={`${setupPagePClass} text-center text-lg font-semibold !mb-0`}>
    TERMS
</p>
<p class={setupPagePClass}>
    i-ncript was created to uphold the principle of privacy and is intended for
    use by ethically responsible individuals who aim to protect their own data
    and that of those they trust.
</p>
<p class={setupPagePClass}>
    The author does not condone using i-ncript for illicit or malicious acts,
    such as encrypting and sending illegal content, or encrypting someone else's
    data without their consent.
</p>
<p class={setupPagePClass}>
    Please note that using this software may lead to damage or data loss, and a
    key, once lost, cannot be recovered. The author is therefore not responsible
    for any resulting damages or loss of data under any circumstances.
</p>
<p class={setupPagePClass}>
    By creating an account, you consent to abide by the terms above.
</p>
<GradientButton
    shadow
    color="cyan"
    on:click={handleClick}
    class="relative h-6 outline-dashed outline-1 border-0 mt-2 mb-1.5 w-1/2 left-1/4"
    >create account</GradientButton
>
<p class={`${setupPagePClass} leading-none text-left text-xs !mt-4 !mb-2`}>
    Copyright © 2022-present Khiem Luong (@khiemgluong).
</p>
