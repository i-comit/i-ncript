<script lang="ts">
  import Login from "./components/pages/Login.svelte";
  import Vault from "./components/pages/Vault.svelte";
  import M_Box from "./components/pages/MBox.svelte";
  import WrongDir from "./components/pages/WrongDir.svelte";

  import { AppPage, currentPage } from "./enums/AppPage.ts";
  import { onMount } from "svelte";
  import { GetDirName } from "../wailsjs/go/main/Getters";
  import { LogDebug, LogWarning } from "../wailsjs/runtime/runtime";
  import { DirectoryWatcher } from "../wailsjs/go/main/App";

  let loggedIn = false;
  let _page: AppPage;
  currentPage.subscribe((value) => {
    _page = value;
  });

  function handleLoginSuccess() {
    loggedIn = true;
    currentPage.set(AppPage.Vault);
    DirectoryWatcher(0);
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
    <Login on:loginSuccess={handleLoginSuccess} />
  {:else if _page === AppPage.Vault}
    <Vault />
  {:else if _page === AppPage.Mbox}
    <M_Box />
  {/if}
</main>
