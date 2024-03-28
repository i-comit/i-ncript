export enum AppPage {
    Vault = 'VAULT',
    NBox = 'N-BOX',
    OBox = 'O-BOX',
    Login = 'Login',
    WrongDir = 'WrongDir',
}

import { writable } from 'svelte/store';

export const currentPage = writable<AppPage>(AppPage.Login);