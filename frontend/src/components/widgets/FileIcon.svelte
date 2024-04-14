<script lang="ts">
    import {
        LockSolid,
        FileImageSolid,
        FileVideoSolid,
        FileMusicSolid,
        FileOutline,
        EnvelopeSolid,
    } from "flowbite-svelte-icons";
    import { getFileType, FileTypes } from "../../enums/FileTypes";
    import { FileNode } from "../../tools/fileTree";

    export let _fileTree: FileNode;
</script>

{#if getFileType(_fileTree.relPath) === FileTypes.Encrypted}
    <LockSolid class="w-3 mr-0.5"></LockSolid>
{:else if getFileType(_fileTree.relPath) === FileTypes.Image}
    <FileImageSolid class="w-3 mr-0.5"></FileImageSolid>
{:else if getFileType(_fileTree.relPath) === FileTypes.Video}
    <FileVideoSolid class="w-3 mr-0.5"></FileVideoSolid>
{:else if getFileType(_fileTree.relPath) === FileTypes.Audio}
    <FileMusicSolid class="w-3 mr-0.5"></FileMusicSolid>
{:else if getFileType(_fileTree.relPath) === FileTypes.EncryptedP}
    <EnvelopeSolid class="w-3 mr-0.5"></EnvelopeSolid>
{:else}
    <FileOutline class="w-3 mr-1"></FileOutline>
{/if}
