<script>
    import {
        SpeedDial,
        SpeedDialButton,
        GradientButton,
    } from "flowbite-svelte";
    import {
        ShareNodesSolid,
        PrinterOutline,
        DownloadSolid,
        FileCopySolid,
    } from "flowbite-svelte-icons";
</script>

<div class="bg-white h-100">
    <h3 class="bg-black h-100">Welcome! Select an option.</h3>
    <GradientButton color="cyanToBlue">O-BOX</GradientButton>
    <SpeedDial defaultClass="absolute end-1 bottom-1" class="bg-gray-800 rounded-full">
        <SpeedDialButton name="Share">
            <ShareNodesSolid class="w-5 h-5" />
        </SpeedDialButton>
        <SpeedDialButton name="Print">
            <PrinterOutline class="w-5 h-5" />
        </SpeedDialButton>
        <SpeedDialButton name="Download">
            <DownloadSolid class="w-5 h-5" />
        </SpeedDialButton>
        <SpeedDialButton name="Copy">
            <FileCopySolid class="w-5 h-5" />
        </SpeedDialButton>
    </SpeedDial>
</div>
