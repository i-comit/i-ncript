import { writable } from "svelte/store";
import { get } from "svelte/store";
import { AppPage, currentPage } from "../enums/AppPage";
import { basePath, checkIfRelPathIsInHeldDownBtns, heldDownBtns, removeFileName } from "./utils";
import { BuildDirectoryFileTree, SetIsInFileTask } from "../../wailsjs/go/main/App";
import {
    EventsOff,
    EventsOn,
    LogError,
    LogInfo,
    LogPrint,
} from "../../wailsjs/runtime/runtime";

import { getFileProperties, pageName, pageIndex } from "./utils";
import {
    GetDirectoryPath,
    GetFileTreePath,
} from "../../wailsjs/go/main/Getters";
import {
    FilesDragNDrop, OpenDirectory, OpenFile,
} from "../../wailsjs/go/main/FileUtils";
import { lastHighlight_light, prevHighlight_light } from "../stores/globalVariables";

export const vaultExpansionState = writable<{ [key: string]: boolean }>({});

export const nBoxExpansionState = writable<{ [key: string]: boolean }>({});
export const oBoxExpansionState = writable<{ [key: string]: boolean }>({});

export const isInFileTask = writable<boolean>(false);

export const fileTree = writable<FileNode>({ relPath: "", children: [] });
export const currentFilePath = writable<string>("");
export const currentDirPath = writable<string>("");

interface FileNode {
    relPath: string;
    children?: FileNode[];
}

export function setHeldBtnsStyle() {
    const _heldDownBtns = get(heldDownBtns);
    const entries = Object.entries(_heldDownBtns);
    const lastIndex = entries.length - 1; // Get the index of the last element

    entries.forEach(([path, btn], index) => {
        console.log("Held down node moveFiles: " + path); // Assuming LogInfo is analogous to console.log for demonstration
        btn.style.backgroundColor = index === lastIndex ? lastHighlight_light : prevHighlight_light;
        btn.style.textDecoration = index === lastIndex ? "underline" : "none";
        btn.style.opacity = "50%"
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
    if (get(currentFilePath) === "" && get(currentDirPath) === "") {
        clearHeldBtns();
    }
}
export function openDirectory(relPath: string) {
    GetDirectoryPath(pageIndex()).then((filePath) => {
        OpenDirectory(filePath + relPath);
    });
}

export function openFile(relPath: string) {
    GetDirectoryPath(pageIndex()).then((filePath) => {
        OpenFile(filePath + relPath);
    });
}

export function buildFileTree() {
    loadFileTree(pageIndex());
    clearHeldBtns();
    EventsOff("rebuildFileTree");
    subscribeToRebuildFileTree();
}

function subscribeToRebuildFileTree() {
    EventsOn("rebuildFileTree", buildFileTree);
    LogError("subscribed to rebuildFileTree");
}

export async function checkFileDragDirectory(relPath: string): Promise<boolean> {
    const currentDirectory = await GetDirectoryPath(pageIndex());
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

function loadFileTree(index: number) {
    BuildDirectoryFileTree(index)
        .then((result: FileNode) => {
            LogPrint("Rebuilt File Tree " + pageName());
            const sortedTree = sortFileTree(result); // Sort the tree before setting it
            fileTree.set(sortedTree);
            updateExpansionStateStore()
            loadExpansionState(index)
        })
        .catch((error) => {
            LogError("Failed to get directory structure: " + error);
        });
}

function updateExpansionStateStore() {
    var _fileTree = get(fileTree);
    const currentPageStore = getCurrentPageStore();
    currentPageStore.update((currentState) => {
        const basePathKey = basePath(_fileTree.relPath);
        currentState[basePathKey] = expanded;
        return currentState;
    });
}

function loadExpansionState(index: number) {
    const currentPageStore = getCurrentPageStore();
    GetDirectoryPath(index).then((dirPath) => {
        const currentState = get(currentPageStore); // Synchronously get the current state
        const basePathKey = dirPath;
        if (currentState[basePathKey] !== undefined) {
            expanded = currentState[basePathKey];
        }
        expandRoot();
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
        case AppPage.Mbox:
            return nBoxExpansionState;
        case AppPage.OBox:
            return oBoxExpansionState;
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