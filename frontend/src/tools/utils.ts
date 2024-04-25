
import { get, writable } from 'svelte/store';

import { AppPage, currentPage } from '../enums/AppPage';
import { Modals, currentModal } from '../enums/Modals';

import {
    DirectoryWatcher, ResizeWindow
} from "../../wailsjs/go/main/App";
import {
    FindEncryptedDuplicates,
    GetDirectoryPath,
    GetFileProperties
} from "../../wailsjs/go/main/Getters";
import { LogDebug, LogError, LogInfo, LogTrace, LogWarning } from "../../wailsjs/runtime/runtime";
import { MoveFilesToPath } from '../../wailsjs/go/main/FileUtils';

import { FileTypes, getFileType } from '../enums/FileTypes.ts';
import { width } from "../stores/constantVariables.ts"
import { duplicateFiles, mBoxDir, height, vaultDir } from '../stores/dynamicVariables.ts';
import {
    clearHeldBtns,
    setIsInFileTask,
    currentFilePath
} from './fileTree';

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
        case AppPage.Login:
            return 2;
    };
};

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
    } else currentModal.set(modal);
    const _currentPage = get(currentPage);
    let _height = get(height)
    switch (get(currentModal)) {
        case Modals.Info:
        case Modals.Settings:
        case Modals.Logger:
            try {
                if (_currentPage === AppPage.Login)
                    ResizeWindow(width, _height + 50)
                else ResizeWindow(width * 2, _height)
            } catch (error) {
                console.error("Error calling ResizeWindow", error);
            }
            break;
        case Modals.None:
        default:
            try {
                if (_currentPage === AppPage.Login)
                    ResizeWindow(width, _height + 5)
                else ResizeWindow(width * 2, _height)
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
    // Remove a trailing separator, if it exists
    const normalizedPath = path.replace(/[\\/]+$/, '');
    // Split the path by directory separators and return the last part
    return normalizedPath.split(directoryPathRegex).pop() || normalizedPath;
}

export function getRootDir(): string {
    switch (pageIndex()) {
        case 0: return get(vaultDir);
        case 1: return get(mBoxDir);
    }
    return ""
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
            if (fileTypes !== FileTypes.Encrypted)
                filteredFileCt++
        } else {
            if (fileTypes === FileTypes.Encrypted)
                filteredFileCt++
        }
    });
    LogInfo("Final fileCount: " + filteredFileCt);
    return filteredFileCt
}

export function moveFilesToRelPath(targetRelPath: string) {
    let dirPath = getRootDir();
    let fullPath = dirPath + targetRelPath;
    var _currentRelPath = get(currentFilePath);
    if (_currentRelPath === targetRelPath) { return; }

    if (dirPath === targetRelPath) fullPath = dirPath
    getFileProperties(fullPath).then((fileProps) => {
        if (fileProps.fileType) {
            let pathToMoveTo: string;
            if (fileProps.fileType === "dir") {
                pathToMoveTo = fullPath;
            } else {
                LogDebug("Node being used for drop is file ");
                pathToMoveTo = removeFileName(fullPath);
            }

            let _heldDownBtns = get(heldDownBtns);
            if (Object.keys(_heldDownBtns).length > 0) {
                setIsInFileTask(true).then(() => {
                    prependAbsPathToRelPaths(pageIndex()).then((preprendedRelPaths) => {
                        MoveFilesToPath(preprendedRelPaths, pathToMoveTo);
                    })
                });
            }
        }
    });
}

export async function prependAbsPathToRelPaths(dirIndex: number): Promise<string[]> {
    try {
        var _heldDownBtns = get(heldDownBtns);
        const dirPath = getRootDir();
        const preprendedRelPaths = Object.keys(_heldDownBtns).map((key) =>
            key ? dirPath + key : key,
        );
        return preprendedRelPaths;
    } catch (error) { return []; }
}

export async function retrieveDuplicateFiles() {
    duplicateFiles.set([]);
    FindEncryptedDuplicates(0)
        .then((_duplicateFiles) => {
            duplicateFiles.update((current) => [
                ...current,
                ..._duplicateFiles,
            ]);
            return FindEncryptedDuplicates(1);
        })
        .then((_duplicateFiles) => {
            duplicateFiles.update((current) => [
                ...current,
                ..._duplicateFiles,
            ]);
        });
}

export function formatNumber(num: number | bigint): string {
    // Define the upper bounds for JavaScript's safe integer calculations.
    const MAX_SAFE_INTEGER = 9007199254740991;
    if (typeof num === 'bigint' || num > MAX_SAFE_INTEGER) {
        return "MAX";
    }
    // Conversion thresholds
    const sizes = [
        { limit: 1e9, suffix: 'B' },
        { limit: 1e6, suffix: 'M' },
        { limit: 1e3, suffix: 'K' }
    ];
    // Check and format based on thresholds
    for (const size of sizes) {
        if (num >= size.limit) {
            return (num / size.limit).toFixed(1).replace(/\.0$/, '') + size.suffix;
        }
    }
    // If number is less than 1000, return it as a string with no changes
    return num.toString();
}
