import {
    currentPage
} from './stores/currentPage'; // Assuming currentPage is a Svelte store
import {
    settingsOpened
} from './stores/settingsOpened';
import {
    userStore
} from './stores/userStore';

import {
    AppPage
} from './enums/AppPage';

export function switchFormButton(page) {
    console.log(`Switching to ${page}`);
    currentPage.set(page); // Assuming currentPage is a writable store
}

export function toggleSettings() {
    settingsOpened.update((value) => !value);
}

// export function logout() {
//     // let _loggedIn;
//     // loggedIn.subscribe((value) => {
//     //     _loggedIn = value;
//     // });
//     loggedIn.set(false);
//     userStore.set(null);
// }