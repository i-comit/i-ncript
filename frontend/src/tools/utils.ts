
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
import { LogDebug, LogError, LogInfo, LogTrace, LogWarning } from "../../wailsjs/runtime/runtime";
import { MoveFilesToPath } from '../../wailsjs/go/main/FileUtils';
import { clearHeldBtns, setIsInFileTask } from './fileTree';

import { width, height } from "../stores/globalVariables.ts"

export const pageName: () => string = () => {
    const _appPage: AppPage = get(currentPage);
    return _appPage;
};
export const heldDownBtns = writable<{ [relPath: string]: HTMLButtonElement }>({});

export const leftCtrlDown = writable<boolean>(false);
export const pointerDown = writable<boolean>(false);

export const pageIndex: () => number = () => {
    const _appPage = get(currentPage)
    switch (
    _appPage
    ) {
        case AppPage.Vault:
        default:
            return 0;
        case AppPage.Mbox:
            return 1;
        case AppPage.OBox:
            return 2;
        case AppPage.Login:
            return 3;
    };
};

export const totalFileCt = writable<number>(0);

export function switchPages(page: AppPage) {
    currentPage.set(page);
    clearHeldBtns();
    DirectoryWatcher(pageIndex());
    switchModals(get(currentModal));
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
                    ResizeWindow(width, height + 15)
                else
                    ResizeWindow(width * 2, height)

            } catch (error) {
                console.error("Error calling ResizeWindow", error);
            }
            break;
        case Modals.None:
        default:
            try {
                if (_currentPage === AppPage.Login)
                    ResizeWindow(width, height + 15)
                else
                    ResizeWindow(width * 2, height)
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

export function checkIfRelPathIsInHeldDownBtns(relPath: string): boolean {
    const currentHeldDownBtns = get(heldDownBtns);
    return currentHeldDownBtns.hasOwnProperty(relPath);
}

export function addToHeldFileBtnsArr(relPath: string, button: HTMLButtonElement) {
    heldDownBtns.update(currentHeldDownBtns => {
        if (!(relPath in currentHeldDownBtns)) {
            LogDebug("Added to heldDownBtns: " + relPath);
            return { ...currentHeldDownBtns, [relPath]: button };
        }
        return currentHeldDownBtns;
    });
}

export function checkFileTypesinHeldDownBtns(encryptOrDecrypt: boolean): number {
    var filteredFileCt: number = 0;
    Object.keys(get(heldDownBtns)).forEach((relPath) => {
        var fileTypes = getFileType(relPath)
        if (fileTypes === FileTypes.Unknown) { filteredFileCt++; return }

        if (encryptOrDecrypt) {
            if (fileTypes !== FileTypes.Encrypted) {
                filteredFileCt++
            }
        } else {
            if (fileTypes === FileTypes.Encrypted) {
                filteredFileCt++
            }
        }
    });
    LogInfo("Final fileCount: " + filteredFileCt);
    return filteredFileCt
}

import { currentFilePath } from './fileTree';
import { FileTypes, getFileType } from '../enums/FileTypes.ts';
export function moveFilesToRelPath(targetRelPath: string) {
    GetDirectoryPath(pageIndex()).then((dirPath) => {
        var fullPath = dirPath + targetRelPath;
        var _currentRelPath = get(currentFilePath);
        if (_currentRelPath === targetRelPath) { return; }

        if (dirPath.slice(0, -1) === targetRelPath)
            fullPath = dirPath
        getFileProperties(fullPath).then((fileProps) => {
            if (fileProps.fileType) {
                let pathToMoveTo: string;
                if (fileProps.fileType === "dir") {
                    LogDebug("Node being used for drop is dir ");
                    pathToMoveTo = fullPath;
                } else {
                    LogDebug("Node being used for drop is file ");
                    pathToMoveTo = removeFileName(fullPath);
                }

                var _heldDownBtns = get(heldDownBtns);
                if (Object.keys(_heldDownBtns).length > 0) {
                    setIsInFileTask(true).then(() => {
                        prependAbsPathToRelPaths(pageIndex()).then((preprendedRelPaths) => {
                            MoveFilesToPath(preprendedRelPaths, pathToMoveTo);
                        })
                    });
                }
            }
        });
    });
}

export async function prependAbsPathToRelPaths(dirIndex: number): Promise<string[]> {
    try {
        var _heldDownBtns = get(heldDownBtns);
        const dirPath = await GetDirectoryPath(dirIndex);
        const preprendedRelPaths = Object.keys(_heldDownBtns).map((key) =>
            key ? dirPath + key : key,
        );
        return preprendedRelPaths;
    } catch (error) { return []; }
}
