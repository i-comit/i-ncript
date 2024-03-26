import {
    writable
} from 'svelte/store';
import {
    Modals
} from '../enums/Modals';

export const currentModal = writable<Modals>(Modals.None);