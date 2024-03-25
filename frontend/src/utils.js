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

export function switchFormButton(page) {
    console.log(`Switching to ${page}`);
    currentPage.set(page); // Assuming currentPage is a writable store
}

export function switchModals(modal) {
    console.log(`Switching to ${modal}`);
    currentModal.set(modal); // Assuming currentPage is a writable store
}

// export function logout() {
//     // let _loggedIn;
//     // loggedIn.subscribe((value) => {
//     //     _loggedIn = value;
//     // });
//     loggedIn.set(false);
//     userStore.set(null);
// }