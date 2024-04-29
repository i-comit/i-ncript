export enum FileTasks {
    Encrypt = "ENCRYPTING",
    Decrypt = "DECRYPTING",
    Move = "MOVING",
    Pack = "PACKING",
    Open = "OPENING",
    LoadFiles = "LOADING FILES",
    None = "none",
}

import { writable } from 'svelte/store';

export const currentFileTask = writable<FileTasks>(FileTasks.None);