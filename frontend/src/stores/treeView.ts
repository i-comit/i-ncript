import { writable } from "svelte/store";
import { get } from "svelte/store";
import { AppPage, currentPage } from "../enums/AppPage";
import { basePath, removeFileName } from "../tools/utils";
import {
    BuildDirectoryFileTree
} from "../../wailsjs/go/main/App";
import {
    LogDebug,
    LogError,
    LogInfo,
    LogWarning,
} from "../../wailsjs/runtime/runtime";

import { getFileProperties } from "../tools/utils";
import {
    GetDirectoryPath,
    GetFileProperties
} from "../../wailsjs/go/main/Getters";

export const vaultExpansionState = writable<{ [key: string]: boolean }>({});
export const vaultRootExpanded = writable<boolean>(false);
export const nBoxExpansionState = writable<{ [key: string]: boolean }>({});
export const nboxRootExpanded = writable<boolean>(false);
export const oBoxExpansionState = writable<{ [key: string]: boolean }>({});

interface FileNode {
    relPath: string;
    children?: FileNode[];
}

export const fileTree = writable<FileNode>({ relPath: "", children: [] });

let expanded = false;
let _appPage: AppPage;

export const pageName: () => string = () => {
    const _appPage: AppPage = get(currentPage);
    return _appPage;
};

export const pageIndex: () => number = () => {
    _appPage = get(currentPage)
    switch (
    _appPage // Assuming _appPage holds the current page enum value
    ) {
        case AppPage.Vault:
        default:
            return 0;
        case AppPage.NBox:
            return 1;
        case AppPage.OBox:
            return 2;
        case AppPage.Login:
            return 3;
    };
};

export function getCurrentPageStore() {
    _appPage = get(currentPage)
    switch (
    _appPage // Assuming _appPage holds the current page enum value
    ) {
        case AppPage.Vault:
            return vaultExpansionState;
        case AppPage.NBox:
            return nBoxExpansionState;
        case AppPage.OBox:
            return oBoxExpansionState;
        default:
            throw new Error("Unrecognized page");
    }
}

export function loadFileTree(index: number) {
    BuildDirectoryFileTree(index)
        .then((result: FileNode) => {
            fileTree.set(result);
            loadExpansionState(index)
        })
        .catch((error) => {
            LogError("Failed to get directory structure: " + error);
        });
}

function loadExpansionState(index: number) {
    const currentPageStore = getCurrentPageStore();
    GetDirectoryPath(index).then((dirPath) => {
        LogInfo("Expanded " + dirPath + " file tree.");
        const unsubscribe = currentPageStore.subscribe((state) => {
            const basePathKey = dirPath;
            if (state[basePathKey] !== undefined) {
                expanded = state[basePathKey];
            }
        });
        expandRoot();
        return unsubscribe; // Unsubscribe when the component unmounts
    });
}

export const expandRoot: () => void = () => {
    const currentPageStore = getCurrentPageStore();
    _appPage = get(currentPage)

    currentPageStore.update((currentState) => {
        currentState[pageName()] = true;
        return currentState; //returns the currentState to currentPageStore
    });
};

let draggedOver = false;

export async function getFullFilePath(relPath: string): Promise<string> {
    GetDirectoryPath(pageIndex()).then((dirPath) => {
        LogWarning(dirPath + relPath);
        return dirPath + relPath;
    });
    LogError("Error getting full filePath " + relPath);
    return relPath;
}

export function handleDragOver(relPath: string, event: DragEvent) {
    event.preventDefault(); // Necessary to allow for a drop
    draggedOver = true;
    LogError("Dragged over lele " + relPath);
}

export function handleDrop(relPath: string, event: DragEvent) {
    event.preventDefault();
    draggedOver = false;
    if (event.dataTransfer && event.dataTransfer.files) {
        const files = event.dataTransfer.files;
        var index = pageIndex();
        GetDirectoryPath(index).then((dirPath) => {
            var fullPath = dirPath + relPath;
            getFileProperties(fullPath).then((fileProps) => {
                LogError(files.length + " file(s) dropped. " + fullPath);
                if (fileProps.fileType) {
                    let pathToMoveTo: string;
                    if (fileProps.fileType === "dir") {
                        LogError("Node being used for drop is dir ");
                        pathToMoveTo = fullPath;
                    } else {
                        LogError("Node being used for drop  is file ");
                        pathToMoveTo = removeFileName(fullPath);
                    }
                    for (let i = 0; i < files.length; i++) {
                        const file = files[i];
                        const reader = new FileReader();
                        reader.onload = (function (theFile) {
                            return function (e) {
                                const arrayBuffer = e.target?.result as ArrayBuffer;
                                const byteArray = new Uint8Array(arrayBuffer);
                                // e.target.result contains the file's data.
                                // Send this data to the backend to be saved to the new location.
                            };
                        })(file);
                        // reader.readAsDataURL(file);
                        LogError("READER " + file.stream());
                    }
                }
            });
        });
    }
}

export function handleDragLeave(event: DragEvent) {
    event.preventDefault();
    draggedOver = false;
}
