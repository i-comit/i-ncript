import { writable } from 'svelte/store';

export const fileProgress = writable<number>(0);

export const vaultDir = writable<string>("");
export const mBoxDir = writable<string>("");

export const accentColor = writable<string>("");

export const newAccount = writable<boolean>(true);
export const darkLightMode = writable<boolean>(true);

export const fileCount = writable<number>(0);
export const totalFileCt = writable<number>(0);
export const fileTaskPercent = writable<number>(0);

export const largeFileName = writable<string>("");
export const largeFilePercent = writable<number>(0);