<script lang="ts">
  import { onDestroy, onMount } from "svelte";
  import { get } from "svelte/store";

  import Login from "./components/pages/Login.svelte";
  import Vault from "./components/pages/Vault.svelte";
  import M_Box from "./components/pages/MBox.svelte";
  import WrongDir from "./components/pages/WrongDir.svelte";

  import { AppPage, currentPage } from "./enums/AppPage.ts";
  import {
    CheckKeyFileInCWD,
    GetDirName,
    GetDirectoryPath,
    GetHeight,
  } from "../wailsjs/go/main/Getters";
  import {
    EventsOn,
    LogError,
    LogInfo,
    LogWarning,
  } from "../wailsjs/runtime/runtime";
  import { DirectoryWatcher, ResizeWindow } from "../wailsjs/go/main/App";
  import {
    vaultDir,
    mBoxDir,
    largeFilePercent,
    largeFileName,
    newAccount,
    height,
    filterInputs,
  } from "./stores/dynamicVariables.ts";
  import { buildFileTree, fileTree } from "./tools/fileTree.ts";
  import { addLogEntry } from "./tools/logger.ts";
  import AppSetup from "./components/pages/AppSetup.svelte";
  import { width } from "./stores/constantVariables.ts";
  import {
    AddInputToFilterTemplate,
    LoadFileFilters,
  } from "../wailsjs/go/main/FileUtils";
  import UpdateApp from "./components/modals/UpdateApp.svelte";

  let _page: AppPage;
  let isRightDir = false;

  currentPage.subscribe((value) => {
    _page = value;
  });

  onMount(async () => {
    isRightDir = await GetDirName();
    let _height = await GetHeight();
    height.set(_height);

    if (!isRightDir) ResizeWindow(320, _height + 20);
    else {
      CheckKeyFileInCWD().then((_keyFilePath) => {
        if (_keyFilePath === "") {
          currentPage.set(AppPage.AppSetup);
          newAccount.set(true);
          ResizeWindow(350, _height + 100);
        }
      });
    }
    EventsOn("addLogFile", (logEntry) => {
      addLogEntry(logEntry);
    });
    EventsOn("largeFilePercent", (_largeFilePercent: number) => {
      largeFilePercent.set(_largeFilePercent);
      LogInfo("largeFile " + $largeFilePercent);
    });
    EventsOn("largeFileName", (_largeFileName: string) => {
      largeFileName.set(_largeFileName);
    });
  });

  async function loggedIn() {
    await GetDirectoryPath(0).then((vaultPath) => {
      vaultDir.set(vaultPath);
      LogWarning("vaultPath " + vaultPath);
    });
    await GetDirectoryPath(1).then((mBoxPath) => {
      mBoxDir.set(mBoxPath);
      LogWarning("mboxPath " + mBoxPath);
    });
    try {
      const _filterInputs = await LoadFileFilters();
      if (_filterInputs && _filterInputs.length !== 0) {
        const singleString = _filterInputs.join("\n");
        filterInputs.set(singleString);
        let lines = get(filterInputs)
          .split("\n")
          .filter((line) => line.trim() !== "");

        lines.forEach((_filterInput) => {
          AddInputToFilterTemplate(_filterInput);
        });
      }
    } catch (error) {
      LogError("Error loading file filters:" + error);
    }

    currentPage.set(AppPage.Vault);
    ResizeWindow(width * 2, $height);

    let unsubscribe = () => {};
    unsubscribe = fileTree.subscribe((value) => {
      if (value && value.relPath !== "") {
        buildFileTree();
        DirectoryWatcher(0);
        unsubscribe(); // Now safe to unsubscribe
      }
    });
  }
</script>

<main class="rounded-lg">
  <UpdateApp />
  {#if !isRightDir}
    <WrongDir />
  {:else if _page === AppPage.AppSetup}
    <AppSetup />
  {:else if _page === AppPage.Login}
    <Login on:loginSuccess={loggedIn} />
  {:else if _page === AppPage.Vault}
    <Vault />
  {:else if _page === AppPage.Mbox}
    <M_Box />
  {/if}
</main>
