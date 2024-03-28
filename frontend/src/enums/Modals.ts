export enum Modals {
    None = "None",
    Info = "Info",
    Settings = "Settings",
    Filter = "Filter",
    Logger = "Logger",
}

import { writable } from 'svelte/store';

export const currentModal = writable<Modals>(Modals.None);