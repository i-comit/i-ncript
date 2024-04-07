<script lang="ts">
    import { FileTasks, currentFileTask } from "../../enums/FileTasks";
    import NeuButtonFake from "../widgets/NeuButtonFake.svelte";

    let _currentFileTask: FileTasks;
    currentFileTask.subscribe((value) => {
        _currentFileTask = value;
    });
</script>

<div class="row space-x-0 h-6">
    <div class="flex flex-grow justify-center items-center">
        <NeuButtonFake _style="font-size: 15px;"
            >{_currentFileTask}...</NeuButtonFake
        >
    </div>
</div>
