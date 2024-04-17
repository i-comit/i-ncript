import { writable } from "svelte/store";
import { get } from "svelte/store";
import { AppPage, currentPage } from "../enums/AppPage";
import {
    addToHeldFileBtnsArr, basePath,
    checkIfRelPathIsInHeldDownBtns,
    getRootDir,
    heldDownBtns, leftCtrlDown, removeFileName
} from "./utils";
import { BuildDirectoryFileTree, SetIsInFileTask } from "../../wailsjs/go/main/App";
import {
    EventsOff,
    EventsOn,
    LogError,
    LogInfo,
    LogPrint,
} from "../../wailsjs/runtime/runtime";

import { pageName, pageIndex } from "./utils";
import {
    GetDirectoryPath,
    GetFileTreePath,
} from "../../wailsjs/go/main/Getters";
import { lastHighlight_light, prevHighlight_light } from "../stores/constantVariables";
import { FileTypes, getFileType } from "../enums/FileTypes";
import { pageLoading } from "../stores/dynamicVariables";

export const vaultExpansionState = writable<{ [key: string]: boolean }>({});
export const mBoxExpansionState = writable<{ [key: string]: boolean }>({});

export const isInFileTask = writable<boolean>(false);

export const fileTree = writable<FileNode>({ relPath: "", children: [] });
export const currentFilePath = writable<string>("");
export const currentDirPath = writable<string>("");

export interface FileNode {
    relPath: string;
    children?: FileNode[];
}

let expanded = false;
let _appPage: AppPage;

export function handleFileClick(relPath: string, _buttonRef: HTMLButtonElement) {
    if (!get(leftCtrlDown) && checkIfRelPathIsInHeldDownBtns(relPath) ||
        getFileType(relPath) === FileTypes.EncryptedP) {
        clearHeldBtns();
        LogInfo("Cleared held buttons from file click")
    }
    addToHeldFileBtnsArr(relPath, _buttonRef);
    setHeldBtnsStyle();
}


export function buildFileTree() {
    BuildDirectoryFileTree(pageIndex())
        .then(sortFileTree)
        .then((sortedTree) => {
            fileTree.set(sortedTree);
            return sortedTree; // Pass the sorted tree for further processing
        })
        .then(loadExpansionState)
        .then(updateExpansionStateStore)
        .then(() => {
            LogPrint("Fully initialized and loaded " + pageName());
            pageLoading.set(false);
        })
        .catch((error) => {
            LogError("Failed to get directory structure: " + error);
        });

    clearHeldBtns();
    EventsOff("rebuildFileTree");
    subscribeToRebuildFileTree();
}

function subscribeToRebuildFileTree() {
    EventsOn("rebuildFileTree", buildFileTree);
    LogError("subscribed to rebuildFileTree");
}

function loadExpansionState() {
    const currentPageStore = getCurrentPageStore();
    const currentState = get(currentPageStore);
    const basePathKey = getRootDir();
    if (currentState[basePathKey] !== undefined) {
        expanded = currentState[basePathKey];
    }
    expandRoot();
}

function updateExpansionStateStore() {
    let _fileTree = get(fileTree);
    getCurrentPageStore().update((currentState) => {
        const basePathKey = _fileTree.relPath;
        currentState[basePathKey] = expanded;
        return currentState;
    });
}

export function setHeldBtnsStyle() {
    const _heldDownBtns = get(heldDownBtns);
    const entries = Object.entries(_heldDownBtns);
    const lastIndex = entries.length - 1; // Get the index of the last element

    entries.forEach(([path, btn], index) => {
        btn.style.backgroundColor = index === lastIndex ? prevHighlight_light : lastHighlight_light;
        btn.style.textDecoration = index === lastIndex ? "underline" : "none";
    });
}
export function clearHeldBtns() {
    if (get(currentDirPath) === "") {
        const _heldDownBtns = get(heldDownBtns);
        Object.entries(_heldDownBtns).forEach(([path, btn]) => {
            btn.style.backgroundColor = "transparent";
            btn.style.textDecoration = "none";
        });
        LogInfo("Held buttons cleared");
        heldDownBtns.set({});
    }
}
export function clearHeldBtnsFromContainer() {
    if (get(currentFilePath) === "" &&
        get(currentDirPath) === "") {
        clearHeldBtns();
    }
}
export async function checkFileDragDirectory(relPath: string): Promise<boolean> {
    const currentDirectory = getRootDir();
    return GetFileTreePath(pageIndex(), relPath).then((filePath) => {
        var _currentDirPath = get(currentDirPath);
        if (_currentDirPath !== "") {
            if (_currentDirPath === removeFileName(relPath)) return false;
        }
        if (removeFileName(filePath) === currentDirectory ||
            removeFileName(filePath) === removeFileName(currentDirectory + relPath) ||
            checkIfRelPathIsInHeldDownBtns(relPath)) return false;
        return true;
    }).catch((error) => {
        LogError(error);
        return false;
    });
}

export const expandRoot: () => void = () => {
    _appPage = get(currentPage)
    getCurrentPageStore().update((currentState) => {
        if (get(fileTree).relPath === getRootDir()) {
            expanded = true;
            LogInfo("expanding rootDir");
        }
        return currentState; //returns the currentState to 
    });
};


export function getCurrentPageStore() {
    _appPage = get(currentPage)
    switch (
    _appPage
    ) {
        case AppPage.Vault:
            return vaultExpansionState;
        case AppPage.Mbox:
            return mBoxExpansionState;
        default:
            throw new Error("Unrecognized page");
    }
}

export async function setIsInFileTask(b: boolean): Promise<boolean> {
    const _isInFileTask = await SetIsInFileTask(b);
    isInFileTask.set(_isInFileTask);
    LogPrint("SetIsInFileTask " + _isInFileTask);
    return b; // Return the boolean value
}

function sortFileTree(node: FileNode): FileNode {
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
