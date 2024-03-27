import { writable } from 'svelte/store';

export const logEntries = writable<string[]>([]);
export function addLogEntry(entry: string) {
    logEntries.update(entries => [...entries, entry]);
}

export function clearLogEntries(entry: string) {
    logEntries.set([]);
}