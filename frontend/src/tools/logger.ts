import { writable } from 'svelte/store';
import { LogInfo } from '../../wailsjs/runtime/runtime';

export const logEntries = writable<{ entry: string; timestamp: string }[]>([]);
export const logRetentionTimeStep = writable<number>(3);

export function clearLogEntries(entry: string) {
    logEntries.set([]);
}
export function addLogEntry(entry: string) {
    logEntries.update(entries => {
        // Generate a timestamp for the new entry
        const timestamp = new Date().toISOString();
        // Construct the new entry object
        const newEntry = { entry, timestamp };
        // Check if the array has reached its limit
        if (entries.length >= 10000) {
            // Remove the oldest entry and add the new one
            return [...entries.slice(1), newEntry];
        } else {
            return [...entries, newEntry];
        }
    });
}

export function getEntryKeyword(entry: string): string {
    // Split the string into words
    const words = entry.split(" ");
    // Get the first word
    const firstWord = words[0];
    // Print the first word
    let replacedChar = '';
    switch (firstWord) {
        case "encrypted":
            replacedChar = '🔒'
            break;
        case "decrypted":
            replacedChar = '🔓'
            break;
        case "opened":
            replacedChar = '📂'
            break;
        case "packed":
            replacedChar = '✉️'
            break;
        case "moved":
            replacedChar = '💼'
            break;
        case "warning":
            replacedChar = '⚠️'
            break;
    }
    const modifiedEntry = entry.replace(firstWord, replacedChar);
    return modifiedEntry;
}

export function formatTime(isoString: string): string {
    const date = new Date(isoString);
    const hours = date.getHours();
    const minutes = date.getMinutes();
    const ampm = hours >= 12 ? 'PM' : 'AM';
    const formattedHours = hours % 12 || 12; // Convert 24h to 12h format and treat 0 as 12
    const formattedMinutes = minutes < 10 ? `0${minutes}` : minutes;
    const month = date.getMonth() + 1; // getMonth() is zero-based
    const day = date.getDate();
    const year = date.getFullYear().toString().slice(-2); // Get last two digits of year

    return `${month}/${day}/${year} | ${formattedHours}:${formattedMinutes} ${ampm}`;
}


export let alertInterval = 25;
let inputString = 'Hello';
let displayString = '';
let index = 0;
let adding = true; // State to track whether we are adding or removing chars
let displayInterval: ReturnType<typeof setInterval> | null = null;

export function startDisplay(_inputString: string) {
    inputString = _inputString;
    index = 0;
    adding = true;
    displayString = '';

    if (displayInterval !== null) {
        clearInterval(displayInterval);
    }

    displayInterval = setInterval(() => {
        setAlertTextDisplay();
    }, alertInterval);
}

function setAlertTextDisplay() {
    // When adding characters
    if (adding) {
        if (index < inputString.length) {
            displayString += inputString[index];
            index++;
        } else {
            setTimeout(() => {
                adding = false; // Start removing characters after the delay
            }, 1000); // Delay for a second here
        }
    } else { // When removing characters
        if (displayString.length > 0) {
            displayString = displayString.substring(0, displayString.length - 1);
            index--;
        } else {
            adding = true; // Reset for next cycle
            index = 0; // Reset index to start adding chars again
            stopDisplay();
        }
    }
}

export function stopDisplay() {
    if (displayInterval !== null) {
        clearInterval(displayInterval);
        displayInterval = null;
    }
    index = 0;
    adding = true
    displayString = ""
}

export function getDisplayString() {
    return displayString;
}