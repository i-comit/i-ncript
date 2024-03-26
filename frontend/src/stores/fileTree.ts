import { writable } from 'svelte/store';

interface Node {
    label: string;
    children?: Node[];
}

export const fileTree = writable<Node>({ label: "empty..", children: [] });