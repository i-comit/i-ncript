<script lang="ts">
  import { onMount } from "svelte";
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
  } from "../wailsjs/go/main/Getters";
  import {
    EventsOff,
    EventsOn,
    LogDebug,
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
    filterInputs,
    loadedFileCt,
  } from "./stores/dynamicVariables.ts";
  import { addLogEntry } from "./tools/logger.ts";
  import AppSetup from "./components/pages/AppSetup.svelte";
  import { height, width } from "./stores/constantVariables.ts";
  import { fileTree } from "./stores/dynamicVariables.ts";

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
    if (!isRightDir) ResizeWindow(320, height + 10);
    else {
      CheckKeyFileInCWD().then((_keyFilePath) => {
        if (_keyFilePath === "") {
          currentPage.set(AppPage.AppSetup);
          newAccount.set(true);
          ResizeWindow(350, height + 50);
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
    EventsOn("loadedFileCt", (_loadedFileCt: number) => {
      // LogInfo("loadedFileCt " + _loadedFileCt);
      loadedFileCt.set(_loadedFileCt);
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
          LogWarning("Filter lines: " + _filterInput);
          AddInputToFilterTemplate(_filterInput);
        });
      }
    } catch (error) {
      LogError("Error loading file filters:" + error);
    }
  });

  async function loggedIn() {
    await GetDirectoryPath(0).then((vaultPath) => {
      vaultDir.set(vaultPath);
      LogDebug("vaultPath " + vaultPath);
    });
    await GetDirectoryPath(1).then((mBoxPath) => {
      mBoxDir.set(mBoxPath);
      LogDebug("mboxPath " + mBoxPath);
    });

    currentPage.set(AppPage.Vault);
    ResizeWindow(width * 2, height);

    let unsubscribe = () => {};
    unsubscribe = fileTree.subscribe((value) => {
      if (value && value.relPath !== "") {
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
