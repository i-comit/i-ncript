import {
    currentPage
} from './stores/currentPage'; // Assuming currentPage is a Svelte store
import {
    currentModal
} from './stores/currentModal';
import { fileTree } from './stores/fileTree';

import {
    AppPage
} from './enums/AppPage';
import {
    Modals
} from './enums/Modals';

import {
    ResizeWindow, BuildDirectoryFileTree
} from "../wailsjs/go/main/App";
import {
    GetAppPath,
    GetDirectoryPath,
    GetFileProperties
} from "../wailsjs/go/main/Getters";
import { LogMessage } from "../wailsjs/go/main/Logger";
import { get } from 'svelte/store';

export function switchFormButton(page: AppPage) {
    console.log(`Switching to ${page}`);
    currentPage.set(page); // Assuming currentPage is a writable store
}

export function switchModals(modal: Modals) {
    const _modal = get(currentModal);
    if (_modal === modal) {
        currentModal.set(Modals.None);
    } else {
        currentModal.set(modal);
    }

    const newModal = get(currentModal);
    switch (newModal) {
        case Modals.Info:
            try {
                ResizeWindow(220, 275, false)
            } catch (error) {
                console.error("Error calling ResizeWindow", error);
            }
            break;
        case Modals.Settings:
            try {
                ResizeWindow(220, 274, false)
            } catch (error) {
                console.error("Error calling ResizeWindow", error);
            }
            break;
        case Modals.None:
        default:
            try {
                ResizeWindow(220, 155, false)
            } catch (error) {
                console.error("Error calling ResizeWindow", error);
            }
            break;
    }
}

export function loadDirectoryTree(index: number) {
    interface Node {
        relPath: string;
        label: string;
        children?: Node[]; // Make children optional to match the Go structure
    }
    BuildDirectoryFileTree(index)
        .then((result: Node) => {
            fileTree.set(result);
            LogMessage(JSON.stringify(fileTree, null, 2)); // Should show the updated structure
        })
        .catch((error) => {
            console.error("Failed to get directory structure", error);
            // LogMessage(error);
        });
}

interface FileProperties {
    modifiedDate: string;
    fileSize: number;
    fileType?: string; // Optional
}

export async function getFileProperties(filePath: string): Promise<FileProperties> {
    // logFrontendMessage("File PATH " + filePath);
    try {
        const properties: FileProperties = await GetFileProperties(filePath);
        logFrontendMessage(`File properties for ${filePath}: ${JSON.stringify(properties)}`);
        return properties;
    } catch (error) {
        logFrontendMessage("Failed to get file properties" + error);
        throw error; // Rethrow or handle as needed
    }
}

export async function getFilePath(label: string): Promise<string> {
    var index = 0;

    const _currentPage = get(currentPage);
    switch (_currentPage) {
        case AppPage.Vault:
            index = 0; break;
        case AppPage.NBox:
            index = 1; break;
        case AppPage.OBox:
            index = 2; break;
    }
    return await GetDirectoryPath(index); // Await the promise to resolve
}

export function logFrontendMessage(str: string) {
    LogMessage(str);
}