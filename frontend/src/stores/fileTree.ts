import { writable } from 'svelte/store';

interface FileNode {
    relPath: string;
    children?: FileNode[];
}

export const fileTree = writable<FileNode>({ relPath: "", children: [] });