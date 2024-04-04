export enum AppPage {
    Vault = 'VAULT',
    Mbox = 'M-BOX',
    OBox = 'O-BOX',
    Login = 'Login',
    WrongDir = 'WrongDir',
}

import { writable } from 'svelte/store';

export const currentPage = writable<AppPage>(AppPage.Login);