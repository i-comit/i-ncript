export enum AppPage {
    Vault = 'VAULT',
    Mbox = 'M-BOX',
    Login = 'Login',
    AppSetup = 'AppSetup',
}

import { writable } from 'svelte/store';

export const currentPage = writable<AppPage>(AppPage.Login);