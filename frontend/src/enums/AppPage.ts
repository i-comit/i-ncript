export enum AppPage {
    Login = 'Login',
    Vault = 'VAULT',
    NBox = 'N-BOX',
    OBox = 'O-BOX',
    WrongDir = 'WrongDir',
}

import { writable } from 'svelte/store';

export const currentPage = writable<AppPage>(AppPage.Login);