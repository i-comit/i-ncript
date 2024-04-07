<script lang="ts">
    import { onDestroy, onMount } from "svelte";
    import { get } from "svelte/store";

    import {
        FolderArrowRightOutline,
        FolderOpenSolid,
        PlaySolid,
    } from "flowbite-svelte-icons";
    import { darkLightMode } from "../../stores/dynamicVariables";
    import { darkLightTextInContainer } from "../../tools/utils";

    const unsub_darkLightMode = darkLightMode.subscribe((value) => {
        darkLightTextInContainer(!value, "file-tools", "button");
    });

    onMount(() => {
        var _value = get(darkLightMode);
        darkLightTextInContainer(!_value, "file-tools", "button");
    });
    onDestroy(() => {
        unsub_darkLightMode();
    });
</script>

<div id="file-tools" class="flex justify-end space-x-1">
    <button>
        <FolderArrowRightOutline />
    </button>
    <button>
        <PlaySolid />
    </button>
    <button>
        <FolderOpenSolid />
    </button>
</div>

<style>
    button {
        --text-color: #757575;
        color: var(--text-color);
    }
</style>
