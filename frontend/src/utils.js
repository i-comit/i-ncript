import {
    currentPage
} from './stores/currentPage'; // Assuming currentPage is a Svelte store
import {
    currentModal
} from './stores/currentModal';

import {
    AppPage
} from './enums/AppPage';
import {
    Modals
} from './enums/Modals';

import {
    ResizeWindow
} from "../wailsjs/go/main/App";
import {
    get
} from 'svelte/store';

export function switchFormButton(page) {
    console.log(`Switching to ${page}`);
    currentPage.set(page); // Assuming currentPage is a writable store
}

export function switchModals(modal) {
    const _modal = get(currentModal);
    if (_modal === modal) {
        currentModal.set(Modals.None);
    } else {
        currentModal.set(modal);
    }

    const newModal = get(currentModal);
    if (newModal === Modals.Info) {
        try {
            ResizeWindow(220, 275)
        } catch (error) {
            console.error("Error calling ResizeWindow", error);
        }
    } else if (newModal === Modals.Settings) {
        try {
            ResizeWindow(220, 274)
        } catch (error) {
            console.error("Error calling ResizeWindow", error);
        }
    } else if (newModal === Modals.None) {
        try {
            ResizeWindow(220, 155)
        } catch (error) {
            console.error("Error calling ResizeWindow", error);
        }
    }
}

// export function logout() {
//     // let _loggedIn;
//     // loggedIn.subscribe((value) => {
//     //     _loggedIn = value;
//     // });
//     loggedIn.set(false);
//     userStore.set(null);
// }