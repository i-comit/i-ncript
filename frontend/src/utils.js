import {
    currentPage
} from './stores/currentPage'; // Assuming currentPage is a Svelte store
import {
    settingsOpened
} from './stores/settingsOpened';

export function switchFormButton(page) {
    console.log(`Switching to ${page}`);
    currentPage.set(page); // Assuming currentPage is a writable store
}

export function toggleSettings() {
    settingsOpened.update((value) => !value);
}