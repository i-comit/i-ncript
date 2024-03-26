import { writable } from 'svelte/store';

interface FileNode {
    relPath: string;
    label: string;
    children?: FileNode[];
}

export const fileTree = writable<FileNode>({ relPath: "", label: "empty..", children: [] });