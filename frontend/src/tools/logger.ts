import { writable } from 'svelte/store';

export const logEntries = writable<string[]>([]);
export function addLogEntry(entry: string) {
    logEntries.update(entries => [...entries, entry]);
}

export function clearLogEntries(entry: string) {
    logEntries.set([]);
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