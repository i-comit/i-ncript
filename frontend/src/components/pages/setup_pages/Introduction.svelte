<script lang="ts">
    import { GradientButton } from "flowbite-svelte";
    import { createEventDispatcher } from "svelte";
    // import { setupPagePClass } from "../../../stores/constantVariables";
    const dispatch = createEventDispatcher();

    function handleClick() {
        dispatch("click");
    }
    const setupPagePClass =
        "leading-1 text-primary-200 dark:text-primary-100 my-2";
</script>

<p class={setupPagePClass}>
    It looks like this is your first time running i&#8209;ncript. If you are
    already familiar with the app, click the button below to skip to account
    creation.
</p>
<GradientButton
    shadow
    color="cyan"
    on:click={handleClick}
    class="relative h-5 outline-dashed outline-1 border-0 mb-2.5 w-1/2 left-1/4"
    >create account</GradientButton
>
<p class={setupPagePClass}>
    i&#8209;ncript is a portable data encryption app, designed for use with
    removable storage devices. It is portable as it does not require an
    installer to run, can execute on any drive, and can be quickly transferred
    to another device without an install/uninstall process.
</p>
<p class={setupPagePClass}>
    The focus on portability plays an important role in data security. The fact
    is, data is most vulnerable is when it is connected to the internet, and
    while many measures have been taken to ensure the safety of online data, an
    offline, portable data store is a far more simple yet far more effective
    method of data security.
</p>
<p class={setupPagePClass}>
    In cybersecurity, an "attack vector" refers to a method that an attacker
    might use to break into a network and steal data. But what if the data
    wasn't on the network at all? i&#8209;ncript builds on this idea by not only
    keeping data offline but also encrypting it to enhance security.
</p>
<p class={setupPagePClass}>
    It is built using the
    <a
        href="https://github.com/wailsapp/wails"
        target="_blank"
        class="text-primary-500 hover:underline">Wails</a
    >
    GUI framework which leverages the Golang language for backend encryption tasks,
    and web frameworks such as TailwindCSS and Svelte to create a modern user interface
    for its frontend, incorporating the "Neumorphic" design style.
</p>
<p class={setupPagePClass}>
    i&#8209;ncript not only encrypts your personal files, but it also allows you
    to package and send files with a one-time password, enabling another
    i&#8209;ncript account to access them.
</p>
<p class={setupPagePClass}>
    i&#8209;ncript is open source and uses the permissive copyleft GNU GPLv3
    license.
</p>

<style>
    p {
        color: #eeeeee;
    }
</style>
