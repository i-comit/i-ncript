import { writable } from "svelte/store";
import { get } from "svelte/store";
import { currentPage } from "./currentPage";
import { AppPage } from "../enums/AppPage";
import { basePath, printFrontendMsg } from "../utils";
import { fileTree } from "./fileTree";

export const vaultExpansionState = writable<{ [key: string]: boolean }>({});
export const vaultRootExpanded = writable<boolean>(false);
export const nBoxExpansionState = writable<{ [key: string]: boolean }>({});
export const nboxRootExpanded = writable<boolean>(false);

export const oBoxExpansionState = writable<{ [key: string]: boolean }>({});

let expanded = false;

let _appPage: AppPage;
let _fileTree = get(fileTree)

export const pageName: () => string = () => {
    switch (
    _appPage // Assuming _appPage holds the current page enum value
    ) {
        case AppPage.Vault:
            return "VAULT";
        case AppPage.NBox:
            return "N-BOX";
        case AppPage.OBox:
            return "O-BOX";
        default:
            throw new Error("Unrecognized page");
    }
};

function getCurrentPageStore() {
    _appPage = get(currentPage)
    switch (
    _appPage // Assuming _appPage holds the current page enum value
    ) {
        case AppPage.Vault:
            return vaultExpansionState;
        case AppPage.NBox:
            return nBoxExpansionState;
        case AppPage.OBox:
            return oBoxExpansionState;
        default:
            throw new Error("Unrecognized page");
    }
}

export const toggleExpansion = () => {
    expanded = !expanded;
    const currentPageStore = getCurrentPageStore();
    currentPageStore.update((currentState) => {
        currentState[basePath(_fileTree.relPath)] = expanded;
        return currentState;
    });
};

const expandRoot: () => void = () => {
    const currentPageStore = getCurrentPageStore();
    currentPageStore.update((currentState) => {
        currentState["VAULT"] = true;
        printFrontendMsg(" REL PATH " + _fileTree.relPath);
        return currentState; //returns the currentState to currentPageStore
    });
};

export function loadExpansionState() {
    expandRoot();
    const currentPageStore = getCurrentPageStore();
    printFrontendMsg("AMOGUS " + pageName());

    const unsubscribe = currentPageStore.subscribe((state) => {
        const basePathKey = basePath(_fileTree.relPath);
        if (state[basePathKey] !== undefined) {
            expanded = state[basePathKey];
        }
    });
    return unsubscribe; // Unsubscribe when the component unmounts
}