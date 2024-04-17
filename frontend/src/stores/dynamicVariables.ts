import { writable } from 'svelte/store';

export const duplicateFiles = writable<string[]>([]);
export const height = writable<number>(145);
export const pageLoading = writable<boolean>(false);

export const vaultDir = writable<string>("");
export const mBoxDir = writable<string>("");

export const accentColor = writable<string>("#95C7DB");

export const newAccount = writable<boolean>(false);
export const darkLightMode = writable<boolean>(true);

export const fileCount = writable<number>(0);
export const cipheredFilesSize = writable<string>("");

export const totalFileCt = writable<number>(0);
export const fileTaskPercent = writable<number>(0);

export const largeFileName = writable<string>("");
export const largeFilePercent = writable<number>(0);