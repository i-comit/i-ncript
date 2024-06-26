import { writable } from 'svelte/store';
import { FileNode } from '../tools/fileTree';

export const duplicateFiles = writable<string[]>([]);
export const pageLoading = writable<boolean>(false);

export const loadedFileCt = writable<number>(0);

export const hotFiler = writable<boolean>(false);

export const maxFileSize = writable<number>(10);

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

export const filterInputs = writable<string>();

//File Tree dynamic store consts
export const vaultExpansionState = writable<{ [key: string]: boolean }>({});
export const mBoxExpansionState = writable<{ [key: string]: boolean }>({});

export const isInFileTask = writable<boolean>(false);

export const fileTree = writable<FileNode>({ relPath: "", children: [] });
export const currentFilePath = writable<string>("");
export const currentDirPath = writable<string>("");