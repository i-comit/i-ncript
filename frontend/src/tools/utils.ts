
import { get } from 'svelte/store';

import { AppPage, currentPage } from '../enums/AppPage';
import { Modals, currentModal } from '../enums/Modals';

import {
    ResizeWindow
} from "../../wailsjs/go/main/App";
import {
    GetDirectoryPath,
    GetFileProperties
} from "../../wailsjs/go/main/Getters";
import { LogDebug, LogTrace } from "../../wailsjs/runtime/runtime";

export const width = 220
export const height = 180

export function switchFormButton(page: AppPage) {
    currentPage.set(page); // Assuming currentPage is a writable store
}

export function switchModals(modal: Modals) {
    const _currentModal = get(currentModal);
    if (_currentModal === modal) {
        currentModal.set(Modals.None);
    } else {
        currentModal.set(modal);
    }
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
                LogTrace("Error calling ResizeWindow: " + error);
            }
            break;
    }
}

interface FileProperties {
    modifiedDate: string;
    fileSize: number;
    fileType?: string; // Optional
}
export async function getFileProperties(filePath: string): Promise<FileProperties> {
    try {
        const properties: FileProperties = await GetFileProperties(filePath);
        return properties;
    } catch (error) {
        LogTrace("Failed to get file properties; " + error);
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

export function basePath(path: string): string {
    const separator = path.includes("\\") ? "\\" : "/";
    return path.split(separator).pop() || path;
}