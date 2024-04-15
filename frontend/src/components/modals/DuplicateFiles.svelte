<!-- Modal toggle -->
<script lang="ts">
    import { Alert, Tooltip } from "flowbite-svelte";
    import { duplicateFiles } from "../../stores/dynamicVariables";
    import { InfoCircleSolid } from "flowbite-svelte-icons";
    import { tooltipTailwindClass } from "../../stores/constantVariables";
    import { OpenDirectory } from "../../../wailsjs/go/main/FileUtils";
    import { getRootDir, removeFileName, retrieveDuplicateFiles } from "../../tools/utils";
    import {
        FindEncryptedDuplicates,
        GetDirectoryPath,
    } from "../../../wailsjs/go/main/Getters";
    import { onMount } from "svelte";
    import { LogInfo } from "../../../wailsjs/runtime/runtime";

    // function removeRootPathFromFilePath(filePath: string): string {
    //     var rootPath = removeFileName(getRootDir());
    //     if (filePath.startsWith(rootPath)) {
    //         // Adjust for potential trailing slash after the rootPath in the filePath
    //         return filePath.substring(rootPath.length).replace(/^\/+/, "");
    //     }
    //     return filePath;
    // }


</script>

<!-- Main modal -->
<div
    id="static-modal"
    data-modal-backdrop="static"
    tabindex="-1"
    aria-hidden={$duplicateFiles ? "false" : "true"}
    class:hidden={$duplicateFiles.length === 0}
    class="hidden overflow-y-auto overflow-x-hidden fixed top-[1.6rem] right-0 left-0 z-40
            justify-center items-center w-full md:inset-0 h-[80%] max-h-full rounded-2xl"
>
    <div class="relative pt-0 px-1 w-full max-w-2xl max-h-full">
        <!-- Modal content -->
        <div class="relative bg-white rounded-lg shadow dark:bg-gray-700">
            <!-- Modal header -->
            <div
                class="flex items-center justify-between p-1 px-3 py-0.5 md:p-5 border-b
                rounded-t dark:border-primary-100 z-[100] sticky top-0 bg-red-300"
            >
                <Alert class="m-0 p-0 text-white text-xs " defaultClass="gap-1">
                    <InfoCircleSolid slot="icon" class="w-4 h-4 text-white" />
                    <span class="font-semibold text-white m-0 p-0"
                        >duplicate(s) found!
                    </span>
                    handle these files to use the app.
                </Alert>
                <button
                    class="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-10 h-8 ms-auto
                            inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white"
                    on:click={retrieveDuplicateFiles}
                >
                    Refresh
                </button>
            </div>
            <!-- Modal body -->
            <div class="p-2 md:p-3 space-y-1.5">
                {#each $duplicateFiles as duplicateFile}
                    <button
                        class="text:center hover:text-center flex"
                        on:click={() => OpenDirectory(duplicateFile)}
                    >
                        <div
                            class="text-xs truncate text-primary-100 dark:text-primary-200 leading-none hover:underline"
                        >
                            {duplicateFile}
                        </div>
                    </button>
                {/each}
            </div>
        </div>
    </div>
</div>

<style>
    #static-modal {
        overflow-x: hidden;
        overflow-y: auto;
        box-sizing: border-box;
        scrollbar-width: none;
    }
</style>
