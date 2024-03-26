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
    ResizeWindow, GetDirectoryStructure
} from "../wailsjs/go/main/App";
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
    if (newModal === Modals.Info) {
        try {
            ResizeWindow(220, 275, false)
        } catch (error) {
            console.error("Error calling ResizeWindow", error);
        }
    } else if (newModal === Modals.Settings) {
        try {
            ResizeWindow(220, 274, false)
        } catch (error) {
            console.error("Error calling ResizeWindow", error);
        }
    } else if (newModal === Modals.None) {
        try {
            ResizeWindow(220, 155, false)
        } catch (error) {
            console.error("Error calling ResizeWindow", error);
        }
    }
}

export function loadDirectoryTree(i: number) {
    interface Node {
        label: string;
        children?: Node[]; // Make children optional to match the Go structure
    }
    GetDirectoryStructure(i)
        .then((result: Node) => {
            fileTree.set(result);
            LogMessage(JSON.stringify(fileTree, null, 2)); // Should show the updated structure
        })
        .catch((error) => {
            console.error("Failed to get directory structure", error);
            // LogMessage(error);
        });
}

export function getFilePath(){
    
}

export function logFrontendMessage(str: string) {
    LogMessage(str);
}