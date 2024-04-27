export enum FileTasks {
    Encrypt = "ENCRYPTING",
    Decrypt = "DECRYPTING",
    Move = "MOVING",
    Pack = "PACKING",
    Open = "OPENING",
    None = "none",
}

import { writable } from 'svelte/store';

export const currentFileTask = writable<FileTasks>(FileTasks.None);