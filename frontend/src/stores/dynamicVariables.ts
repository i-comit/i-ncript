import { writable } from 'svelte/store';

export const fileProgress = writable<number>(0);

export const vaultDir = writable<string>("");
export const mBoxDir = writable<string>("");

export const newAccount = writable<boolean>(true);
export const darkLightMode = writable<boolean>(false);