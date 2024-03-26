import { writable } from 'svelte/store';

export const encryptProgress = writable<number>(0);
// export const fileProgress = writable<number>(0);