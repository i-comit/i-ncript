import {
    currentPage
} from './stores/currentPage';
import {
    currentModal
} from './stores/currentModal';
import { fileTree } from './stores/treeView';

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
    GetDirectoryPath,
    GetFileProperties
} from "../wailsjs/go/main/Getters";
import { LogMessage } from "../wailsjs/go/main/Logger";
import { get } from 'svelte/store';

export const width = 220
export const height = 180

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

    const _currentModal = get(currentModal);
    const _currentPage = get(currentPage);

    switch (_currentModal) {
        case Modals.Info:
        case Modals.Settings:
        case Modals.Logger:
            try {
                if (_currentPage === AppPage.Login)
                    ResizeWindow(width, height, false)
                else
                    ResizeWindow(width * 2, height, false)

            } catch (error) {
                console.error("Error calling ResizeWindow", error);
            }
            break;
        case Modals.None:
        default:
            try {
                if (_currentPage === AppPage.Login)
                    ResizeWindow(width, 155, false)
                else
                    ResizeWindow(width * 2, height, false)
            } catch (error) {
                console.error("Error calling ResizeWindow", error);
            }
            break;
    }
}

export function loadDirectoryTree(index: number) {
    interface FileNode {
        relPath: string;
        children?: FileNode[]; // Make children optional to match the Go structure
    }
    BuildDirectoryFileTree(index)
        .then((result: FileNode) => {
            fileTree.set(result);
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
        printFrontendMsg(`File properties for ${filePath}: ${JSON.stringify(properties)}`);
        return properties;
    } catch (error) {
        printFrontendMsg("Failed to get file properties" + error);
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

export function printFrontendMsg(str: string) {
    LogMessage(str);
}

export function basePath(path: string): string {
    const separator = path.includes("\\") ? "\\" : "/";
    return path.split(separator).pop() || path;
}