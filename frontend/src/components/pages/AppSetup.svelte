<script lang="ts">
    import { onMount } from "svelte";
    import { ResizeWindow } from "../../../wailsjs/go/main/App";
    import { GetRootFolder } from "../../../wailsjs/go/main/Getters";
    import { darkBGColor, height } from "../../stores/constantVariables";
    import { Frame } from "flowbite-svelte";
    import { accentColor } from "../../stores/dynamicVariables";

    onMount(async () => {
        ResizeWindow(350, height + 10);
    });
</script>

<div
    class="mb-2 w- left-1/2 rounded-bl-lg rounded-br-lg font-semibold z-10 select-none"
    style={`position: sticky; top: 0px;
    background-color: ${darkBGColor}; 
    color: ${$accentColor};`}
>
    first time setup
</div>
<Frame />

<div></div>
