<script lang="ts">
    import { Button, ButtonGroup, Modal } from "flowbite-svelte";
    import { GetLatestRelease } from "../../../wailsjs/go/main/Getters";
    import { onMount } from "svelte";
    import { LogError, LogInfo } from "../../../wailsjs/runtime/runtime";
    import { ArrowRightOutline } from "flowbite-svelte-icons";
    import { accentColor } from "../../stores/dynamicVariables";

    const appVersion = __APP_VERSION__;

    let release = getLatestRelease();
    let modalBool = false;

    interface Release {
        tag_name: string;
        html_url: string;
    }
    async function getLatestRelease(): Promise<Release> {
        try {
            const _release = await GetLatestRelease();
            modalBool = compareVersions(appVersion, _release.tag_name);
            return _release;
        } catch (error) {
            LogError("Error getting release " + error);
            return release;
        }
    }

    function compareVersions(appVersion: string, newVersion: string): boolean {
        const appParts = appVersion.split(".").map(Number);
        const newParts = newVersion.split(".").map(Number);
        // Compare each part from major to patch
        for (let i = 0; i < appParts.length; i++) {
            if (newParts[i] > appParts[i]) {
                return true; // New version is greater, update needed
            } else if (newParts[i] < appParts[i]) {
                return false; // App version is greater, no update needed
            }
            // If they are equal, move to the next part
        }
        return false;
    }
</script>

<Modal
    title="UPDATE?"
    bind:open={modalBool}
    autoclose
    outsideclose
    dialogClass="fixed top-0 start-0 end-0 z-50 w-full p-3.5 mt-4 !h-100"
    headerClass="flex justify-center items-center p-4 md:p-5 rounded-t-lg h-4 !bg-primary-600 
                    !dark:bg-primary-700 !text-primary-200 !dark:text-primary-100"
    backdropClass="fixed inset-0 z-40 bg-gray-800 !bg-opacity-70"
    bodyClass="bg-primary-400 dark:bg-primary-300 text-primary-100 dark:text-primary-200 rounded-b-lg  !h-50"
>
    <div class="update-modal overflow-y-auto overflow-x-clip p-1">
        <div class="flex flex-wrap">
            <p class="text-lg">
                {appVersion}
            </p>
            <ArrowRightOutline size="lg" />
            <p class="text-lg">
                {#await release then _release}
                    {_release.tag_name}
                {/await}
            </p>
        </div>
        <div class="h-1.5" />
        {#await release then _release}
            <ButtonGroup class="flex justify-between h-8">
                <Button
                    class="rounded-lg font-semibold text-md"
                    style={`background-color: ${$accentColor};`}
                    ><a href={_release.html_url} target="_blank">YES</a></Button
                >
                <Button
                    class="rounded-lg font-semibold text-md"
                    on:click={() => (modalBool = false)}>NO</Button
                >
            </ButtonGroup>
        {/await}
    </div>
</Modal>

<style>
    .update-modal {
        height: 50vh;
        scroll-behavior: smooth;
    }
</style>
