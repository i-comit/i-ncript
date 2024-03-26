import { writable } from 'svelte/store';

interface Node {
    label: string;
    children?: Node[];
}

export const tree = writable<Node>({ label: "Loading...", children: [] });
// When updating
// tree.set(result); 