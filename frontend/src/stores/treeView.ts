import { writable } from "svelte/store";
import { get } from "svelte/store";
import { AppPage, currentPage } from "../enums/AppPage";
import { basePath, removeFileName } from "../tools/utils";
import {
    BuildDirectoryFileTree, FilesDragNDrop
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


export const fileTree = writable<FileNode>({ relPath: "", children: [] });

export const pageName: () => string = () => {
    const _appPage: AppPage = get(currentPage);
    return _appPage;
};

export const pageIndex: () => number = () => {
    _appPage = get(currentPage)
    switch (
    _appPage
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

interface FileNode {
    relPath: string;
    children?: FileNode[];
}

export function loadFileTree(index: number) {
    BuildDirectoryFileTree(index)
        .then((result: FileNode) => {
            const sortedTree = sortFileTree(result); // Sort the tree before setting it
            fileTree.set(sortedTree);
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

function sortFileTree(node: FileNode): FileNode {
    // Check if the node has children
    if (node.children) {
        // First, sort the children recursively
        node.children = node.children.map(sortFileTree);
        // Then, sort the current node's children putting directories (nodes with children) first
        node.children.sort((a, b) => {
            // If 'a' is a directory and 'b' is not, 'a' should come first
            if (a.children && !b.children) { return -1; }
            // If 'b' is a directory and 'a' is not, 'b' should come first
            if (b.children && !a.children) { return 1; }
            // If both are directories or both are files, they remain in their original order
            return 0;
        });
    }
    return node;
}

export const expandRoot: () => void = () => {
    const currentPageStore = getCurrentPageStore();
    _appPage = get(currentPage)

    currentPageStore.update((currentState) => {
        currentState[pageName()] = true;
        return currentState; //returns the currentState to currentPageStore
    });
};
let expanded = false;
let _appPage: AppPage;


export function getCurrentPageStore() {
    _appPage = get(currentPage)
    switch (
    _appPage
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
    // if (!draggedOver) {
    //     FileDialogueForDragNDrop().then(() => {
    //         draggedOver = true;
    //         LogError("Dragged over lele " + relPath);
    //     })
    // }
    draggedOver = true;
}

export function handleDrop(relPath: string, event: DragEvent) {
    event.preventDefault();
    draggedOver = false;
    if (event.dataTransfer && event.dataTransfer.files) {
        const droppedFiles = event.dataTransfer.files;
        // LogError("Dropped file relativePath " + relPath)
        var index = pageIndex();
        GetDirectoryPath(index).then((dirPath) => {
            var fullPath = dirPath + relPath;
            getFileProperties(fullPath).then((fileProps) => {
                LogInfo(droppedFiles.length + " file(s) dropped. " + fullPath);
                if (fileProps.fileType) {
                    let pathToMoveTo: string;
                    if (fileProps.fileType === "dir") {
                        LogDebug("Node being used for drop is dir ");
                        pathToMoveTo = fullPath;
                    } else {
                        LogDebug("Node being used for drop  is file ");
                        pathToMoveTo = removeFileName(fullPath);
                    }

                    const file = droppedFiles[0];
                    const reader = new FileReader();

                    reader.onload = function (e) {
                        const arrayBuffer = e.target?.result as ArrayBuffer;
                        if (arrayBuffer) { // Check if the result is not null or undefined
                            const byteArray = new Uint8Array(arrayBuffer);
                            FilesDragNDrop(Array.from(byteArray), file.name, pathToMoveTo);
                            LogDebug("frontend file on load" + file.name);
                        }
                    };
                    reader.readAsArrayBuffer(file);
                    // LogError("front end file " +       reader.readAsArrayBuffer(file));

                    // for (let i = 0; i < droppedFiles.length; i++) {
                    //     const file = droppedFiles[i];
                    //     const reader = new FileReader();
                    //     reader.onload = (function (theFile) {
                    //         return function (e) {
                    //             const arrayBuffer = e.target?.result as ArrayBuffer;
                    //             const byteArray = new Uint8Array(arrayBuffer);
                    //             LogError("front end file " + theFile.name);
                    //             // e.target.result contains the file's data.
                    //             // Send this data to the backend to be saved to the new location.
                    //         };
                    //     })(file);
                    //     LogError("READER " + file.stream());
                    // }
                }
            });
        });
    }
}

export function handleDragLeave(event: DragEvent) {
    event.preventDefault();
    draggedOver = false;
}
