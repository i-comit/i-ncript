import { writable } from "svelte/store";
import { get } from "svelte/store";
import { AppPage, currentPage } from "../enums/AppPage";
import { addFileToHeldFilesArr, basePath, heldDownBtns, removeFileName } from "../tools/utils";
import {
    DirectoryWatcher,
    BuildDirectoryFileTree,
} from "../../wailsjs/go/main/App";
import {
    LogDebug,
    LogError,
    LogInfo,
    LogPrint,
    LogWarning,
} from "../../wailsjs/runtime/runtime";

import { getFileProperties, pageName, pageIndex } from "../tools/utils";
import {
    GetDirectoryPath,
    GetFileProperties
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

interface FileNode {
    relPath: string;
    children?: FileNode[];
}

interface EnhancedFileNode extends FileNode {
    parent?: EnhancedFileNode;
}

import { heldDownFiles } from "../tools/utils";

export function setFileStyle(relPath: string, buttonElement: HTMLButtonElement) {
    addFileToHeldFilesArr(relPath)
    const filesState = get(heldDownFiles);
    LogError("set File style " + relPath);
    if (filesState.includes(relPath)) {
        buttonElement.style.backgroundColor = "blue"; // Example of setting the style
        // return "background-color: blue;"; // Selected state
    } else {
        buttonElement.style.backgroundColor = "red"; // Example of setting the style
        // return "background-color: transparent;"; // Not selected state
    }
}

export function addButtonRefToStore(path: string, buttonRef: HTMLButtonElement) {
    allTreeViewBtns.update(currentBtns => {
        return { ...currentBtns, [path]: buttonRef };
    });
}

export function setHeldBtnsStyle() {
    const _heldDownBtns = get(heldDownBtns);
    Object.entries(_heldDownBtns).forEach(([path, btn]) => {
        LogError("Held down node moveFiles: " + path); // Using console.error for demonstration
        btn.style.backgroundColor = "red";
    });

    // // Check if the current relPath exists in the heldDownBtns and specifically style its button
    // if (relPath in _heldDownBtns) {
    //     LogError("Set File style " + relPath);
    //     // If relPath exists, set its button's background color to blue
    //     _heldDownBtns[relPath].style.backgroundColor = "blue";
    // }
}
export function clearHeldBtns() {
    const _heldDownBtns = get(heldDownBtns);
    Object.entries(_heldDownBtns).forEach(([path, btn]) => {
        console.error("Clearing Btns: " + path); // Assuming console.error for demonstration
        btn.style.backgroundColor = "transparent";
    });
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

    // Retrieves a node based on its relPath
    public getNodeByPath(relPath: string): EnhancedFileNode | undefined {
        return this.pathMap.get(relPath);
    }

    // Retrieves the parent of a node by its relPath
    public getParentByPath(relPath: string): EnhancedFileNode | undefined {
        const node = this.pathMap.get(relPath);
        return node?.parent;
    }
}

export function loadFileTree(index: number) {
    BuildDirectoryFileTree(index)
        .then((result: FileNode) => {
            LogPrint("Rebuilt File Tree " + index);
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
        // LogInfo("Expanded " + dirPath + " file tree.");
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

export function setIsInFileTask(b: boolean) {
    SetIsInFileTask(b).then((_isInFileTask) => {
        isInFileTask.set(_isInFileTask);
        LogPrint("SetIsInFileTask " + _isInFileTask);
        if (!_isInFileTask) {
            DirectoryWatcher(pageIndex())
        }
    })
};

let droppedFilesCt: number

export function handleDrop(relPath: string, event: DragEvent) {
    event.preventDefault();
    draggedOver = false;
    if (event.dataTransfer && event.dataTransfer.files) {
        const droppedFiles = event.dataTransfer.files;
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
                    setIsInFileTask(true)
                    droppedFilesCt = 1;
                    for (let i = 0; i < droppedFiles.length; i++) {
                        const file = droppedFiles[i];
                        const reader = new FileReader();
                        reader.onprogress = function (event) {
                            if (event.lengthComputable) {
                                const loadedBytes = event.loaded;
                                const totalBytes = event.total;
                                const progressMsg = `${loadedBytes} of ${totalBytes} bytes (${Math.round((loadedBytes / totalBytes) * 100)}%)`;
                                LogDebug(progressMsg);
                            }
                        };
                        reader.onload = function (e) {
                            const arrayBuffer = e.target?.result as ArrayBuffer;
                            if (arrayBuffer) { // Check if the result is not null or undefined
                                const byteArray = new Uint8Array(arrayBuffer);
                                FilesDragNDrop(Array.from(byteArray), file.name, file.lastModified, pathToMoveTo);
                                LogDebug("frontend file on load" + file.name + " " + file.lastModified);
                                droppedFilesCt++
                                if (droppedFilesCt === droppedFiles.length + 1) {
                                    setIsInFileTask(false)
                                    LogDebug("droppedFilesCt " + droppedFilesCt++);
                                    loadFileTree(index)
                                }
                            }
                        };
                        reader.readAsArrayBuffer(file);
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
