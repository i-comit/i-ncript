<script lang="ts">
    import { FileTasks, currentFileTask } from "../../enums/FileTasks";
    import NeuButtonFake from "../widgets/NeuButtonFake.svelte";

    let _currentFileTask: FileTasks;
    currentFileTask.subscribe((value) => {
        _currentFileTask = value;
    });
</script>

<div class="row space-x-0 h-6 mb-1">
    <div class="flex justify-center items-center mt-0.5">
        <NeuButtonFake _style="font-size: 15px;" _class="!h-5 text-sm"
            >{_currentFileTask}...</NeuButtonFake
        >
    </div>
</div>
