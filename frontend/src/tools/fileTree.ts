import { writable } from "svelte/store";
import { get } from "svelte/store";
import { AppPage, currentPage } from "../enums/AppPage";
import { basePath, heldDownBtns, removeFileName } from "./utils";
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
} from "../../wailsjs/go/main/Getters";
import {
    FilesDragNDrop, OpenDirectory, OpenFile,
} from "../../wailsjs/go/main/FileUtils";

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

interface EnhancedFileNode extends FileNode {
    parent?: EnhancedFileNode;
}

export function setHeldBtnsStyle() {
    const _heldDownBtns = get(heldDownBtns);
    const entries = Object.entries(_heldDownBtns);
    const lastIndex = entries.length - 1; // Get the index of the last element

    entries.forEach(([path, btn], index) => {
        console.log("Held down node moveFiles: " + path); // Assuming LogInfo is analogous to console.log for demonstration
        // Apply blue to all but the last, red to the last
        btn.style.backgroundColor = index === lastIndex ? "red" : "blue";
    });
}
export function clearHeldBtns() {
    if (get(currentFilePath) === "" && get(currentDirPath) === "") {
        const _heldDownBtns = get(heldDownBtns);
        Object.entries(_heldDownBtns).forEach(([path, btn]) => {
            btn.style.backgroundColor = "transparent";
        });
        LogInfo("Held buttons cleared");
        heldDownBtns.set({});
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


export class FileTreeMap {
    root: EnhancedFileNode;
    private pathMap: Map<string, EnhancedFileNode> = new Map();

    constructor(root: FileNode) {
        this.root = this.enhanceTree(root, undefined);
    }
    // Enhances the tree with parent references and builds the path map
    private enhanceTree(node: FileNode, parent?: EnhancedFileNode): EnhancedFileNode {
        const enhancedNode: EnhancedFileNode = { ...node, parent };
        this.pathMap.set(enhancedNode.relPath, enhancedNode);

        if (node.children) {
            enhancedNode.children = node.children.map(child => this.enhanceTree(child, enhancedNode));
        }
        return enhancedNode;
    }

    public getNodeByPath(relPath: string): EnhancedFileNode | undefined {
        return this.pathMap.get(relPath);
    }
    // Retrieves the parent of a node by its relPath
    public getParentByPath(relPath: string): EnhancedFileNode | undefined {
        const node = this.pathMap.get(relPath);
        return node?.parent;
    }
}

export function buildFileTree() {
    loadFileTree(pageIndex());
    clearHeldBtns();
    EventsOff("rebuildFileTree");
}

function subscribeToRebuildFileTree() {
    EventsOn("rebuildFileTree", buildFileTree);
    LogError("Subscribed to rebuildFileTree");
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
        case AppPage.NBox:
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