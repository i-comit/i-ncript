
import { get, writable } from 'svelte/store';

import { AppPage, currentPage } from '../enums/AppPage';
import { Modals, currentModal } from '../enums/Modals';

import {
    DirectoryWatcher, ResizeWindow
} from "../../wailsjs/go/main/App";
import {
    GetDirectoryPath,
    GetFileProperties
} from "../../wailsjs/go/main/Getters";
import { LogDebug, LogError, LogTrace, LogWarning } from "../../wailsjs/runtime/runtime";
import { MoveFilesToPath } from '../../wailsjs/go/main/FileUtils';
import { setIsInFileTask } from '../stores/treeView';

export const width = 220
export const height = 180

export const pageName: () => string = () => {
    const _appPage: AppPage = get(currentPage);
    return _appPage;
};
export const heldDownFiles = writable<string[]>([]);
export const heldDownBtns = writable<{ [key: string]: HTMLButtonElement }>({});

export const pageIndex: () => number = () => {
    const _appPage = get(currentPage)
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

export function switchPages(page: AppPage) {
    currentPage.set(page); // Assuming currentPage is a writable store
    DirectoryWatcher(pageIndex());
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
        LogError("Failed to get file properties; " + error);
        throw error; // Rethrow or handle as needed
    }
}

const directoryPathRegex = /^(.*[\\/])/;

export function basePath(path: string): string {
    return path.split(directoryPathRegex).pop() || path;
}
export async function getFullFilePath(relPath: string): Promise<string> {
    GetDirectoryPath(pageIndex()).then((dirPath) => {
        LogWarning(dirPath + relPath);
        return dirPath + relPath;
    });
    LogError("Error getting full filePath " + relPath);
    return relPath;
}

export function removeFileName(filePath: string): string {
    const match = filePath.match(directoryPathRegex);
    return match ? match[1] : filePath;
}

export function formatFileSize(fileSize: number): string {
    if (fileSize < 1) return 'no size';
    const units = ['B', 'KB', 'MB', 'GB', 'TB', 'EB', 'ZB', 'YB'];
    const digits = Math.floor((Math.log(fileSize) / Math.log(1024)));
    const unitIndex = Math.min(digits, units.length - 1); // Ensure we don't exceed the units array
    const sizeInUnit = fileSize / Math.pow(1024, unitIndex);
    // Format the number to have up to 3 digits (including after the decimal point if applicable)
    const formattedSize = sizeInUnit.toFixed(sizeInUnit >= 100 ? 0 : sizeInUnit >= 10 ? 1 : 2);
    return `${formattedSize} ${units[unitIndex]}`;
}

//LOCAL DRAG AND DROP
export function addFileToHeldFilesArr(relPath: string) {
    // GetDirectoryPath(pageIndex()).then((dirPath) => {
    heldDownFiles.update((currentHeldDownFiles) => {
        // Check if the newString is not already in the array
        if (!currentHeldDownFiles.includes(relPath)) {
            LogDebug("Added currentHeldDownFiles: " + currentHeldDownFiles);
            return [...currentHeldDownFiles, relPath];
        }
        // If it's already there, just return the current array without adding
        return currentHeldDownFiles;
    });
    // });
}

export function addToHeldBtnsArr(relPath: string, button: HTMLButtonElement) {
    heldDownBtns.update(currentHeldDownBtns => {
        if (!(relPath in currentHeldDownBtns)) {
            LogDebug("Added to heldDownBtns: " + relPath); // Using console.debug for demonstration
            return { ...currentHeldDownBtns, [relPath]: button };
        }
        return currentHeldDownBtns;
    });
}

export function moveFilesToRelPath(targetRelPath: string) {
    GetDirectoryPath(pageIndex()).then((dirPath) => {
        var fullPath = dirPath + targetRelPath;
        getFileProperties(fullPath).then((fileProps) => {
            if (fileProps.fileType) {
                let pathToMoveTo: string;
                if (fileProps.fileType === "dir") {
                    LogDebug("Node being used for drop is dir ");
                    pathToMoveTo = fullPath;
                } else {
                    LogDebug("Node being used for drop  is file ");
                    pathToMoveTo = removeFileName(fullPath);
                }
                var _heldDownFiles = get(heldDownFiles);
                if (_heldDownFiles.length > 0) {
                    setIsInFileTask(true)
                    const modifiedFiles = _heldDownFiles.map(node => {
                        return node ? dirPath + node : node;
                    });
                    MoveFilesToPath(modifiedFiles, pathToMoveTo);
                    _heldDownFiles.forEach((node) => {
                        LogError("held down node moveFiles: " + node);
                    });
                }
            }
        });
    });
}

export function clearHeldDownFiles() {
    heldDownFiles.set([]);
    LogError("Held down Files Cleared");
}