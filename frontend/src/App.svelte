<script lang="ts">
  import Login from "./components/pages/Login.svelte";
  import Vault from "./components/pages/Vault.svelte";
  import M_Box from "./components/pages/MBox.svelte";
  import WrongDir from "./components/pages/WrongDir.svelte";

  import { AppPage, currentPage } from "./enums/AppPage.ts";
  import { onMount } from "svelte";
  import { GetDirName, GetDirectoryPath } from "../wailsjs/go/main/Getters";
  import { LogDebug, LogInfo, LogWarning } from "../wailsjs/runtime/runtime";
  import { DirectoryWatcher } from "../wailsjs/go/main/App";
  import { vaultDir, mBoxDir } from "./stores/dynamicVariables.ts";
  import { buildFileTree, fileTree } from "./tools/fileTree.ts";

  let _page: AppPage;
  currentPage.subscribe((value) => {
    _page = value;
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

    currentPage.set(AppPage.Vault);
    let unsubscribe = () => {}; // Define a no-op function to avoid undefined errors
    unsubscribe = fileTree.subscribe((value) => {
      if (value && value.relPath !== "") {
        // Check if fileTree has been initialized
        buildFileTree();
        DirectoryWatcher(0);
        LogInfo("fileTree loaded on login");
        unsubscribe(); // Now safe to unsubscribe
      }
    });
  }

  let isRightDir = false;

  onMount(async () => {
    isRightDir = await GetDirName();
    LogDebug(isRightDir.toString());
  });
</script>

<main class="rounded-lg">
  {#if !isRightDir}
    <WrongDir />
  {:else if _page === AppPage.Login}
    <Login on:loginSuccess={loggedIn} />
  {:else if _page === AppPage.Vault}
    <Vault />
  {:else if _page === AppPage.Mbox}
    <M_Box />
  {/if}
</main>
