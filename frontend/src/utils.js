import {
    currentPage
} from './stores/currentPage'; // Assuming currentPage is a Svelte store

export function switchFormButton(page) {
    console.log(`Switching to ${page}`);
    currentPage.set(page); // Assuming currentPage is a writable store
}