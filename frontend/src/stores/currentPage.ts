import { writable } from 'svelte/store';
import { AppPage } from "../enums/AppPage";

export const currentPage = writable<AppPage>(AppPage.Login);