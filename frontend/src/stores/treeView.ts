import { writable } from "svelte/store";
import { get } from "svelte/store";
import { AppPage, currentPage } from "../enums/AppPage";
import { basePath, heldDownBtns, removeFileName } from "../tools/utils";
import { BuildDirectoryFileTree, } from "../../wailsjs/go/main/App";
import {
    EventsOff,
    EventsOn,
    LogError,
    LogInfo,
    LogPrint,
} from "../../wailsjs/runtime/runtime";

import { getFileProperties, pageName, pageIndex } from "../tools/utils";
import {
    GetDirectoryPath,
} from "../../wailsjs/go/main/Getters";
import {
    SetIsInFileTask, FilesDragNDrop,
} from "../../wailsjs/go/main/FileUtils";

export const vaultExpansionState = writable<{ [key: string]: boolean }>({});

export const allFileBtns = writable<HTMLButtonElement[]>([])
export const allTreeViewBtns = writable<{ [key: string]: HTMLButtonElement }>({});

export const nBoxExpansionState = writable<{ [key: string]: boolean }>({});
export const oBoxExpansionState = writable<{ [key: string]: boolean }>({});

export const isInFileTask = writable<boolean>(false);

export const fileTree = writable<FileNode>({ relPath: "", children: [] });
export const currentRelPath = writable<string>("");


interface FileNode {
    relPath: string;
    children?: FileNode[];
}

interface EnhancedFileNode extends FileNode {
    parent?: EnhancedFileNode;
}

export function addButtonRefToStore(path: string, buttonRef: HTMLButtonElement) {
    allTreeViewBtns.update(currentBtns => {
        return { ...currentBtns, [path]: buttonRef };
    });
}

export function setHeldBtnsStyle() {
    const _heldDownBtns = get(heldDownBtns);
    Object.entries(_heldDownBtns).forEach(([path, btn]) => {
        LogInfo("Held down node moveFiles: " + path); // Using console.error for demonstration
        btn.style.backgroundColor = "red";
    });
}
export function clearHeldBtns() {
    const _heldDownBtns = get(heldDownBtns);
    Object.entries(_heldDownBtns).forEach(([path, btn]) => {
        btn.style.backgroundColor = "transparent";
    });
    LogInfo("Held buttons cleared");
    heldDownBtns.set({});
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

export function handleDragOver(relPath: string, event: DragEvent) {
    event.preventDefault(); // Necessary to allow for a drop
    // if (!draggedOver) {
    //     FileDialogueForDragNDrop().then(() => {
    //         draggedOver = true;
    //         LogError("Dragged over lele " + relPath);
    //     })
    // }
}

export async function setIsInFileTask(b: boolean): Promise<boolean> {
    const _isInFileTask = await SetIsInFileTask(b);
    isInFileTask.set(_isInFileTask);
    LogPrint("SetIsInFileTask " + _isInFileTask);
    // Optionally perform further actions
    if (_isInFileTask) {
        subscribeToRebuildFileTree()
    }
    return b; // Return the boolean value
}
