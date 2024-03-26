import { writable } from 'svelte/store';

export const fileProgress = writable<number>(0);