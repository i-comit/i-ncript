import { writable } from "svelte/store";
import { get } from "svelte/store";
import { AppPage, currentPage } from "../enums/AppPage";
import { basePath } from "../tools/utils";
import {
    BuildDirectoryFileTree
} from "../../wailsjs/go/main/App";
import {
    LogDebug,
    LogError,
    LogInfo,
} from "../../wailsjs/runtime/runtime";

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
    var index = 0;
    switch (
    _appPage // Assuming _appPage holds the current page enum value
    ) {
        case AppPage.Vault:
        default:
            index = 0;
        case AppPage.NBox:
            index = 1;
        case AppPage.OBox:
            index = 2;
    };
    GetDirectoryPath(index).then((dirPath) => {
        currentPageStore.update((currentState) => {
            currentState[pageName()] = true;
            return currentState; //returns the currentState to currentPageStore
        });
    });

};
