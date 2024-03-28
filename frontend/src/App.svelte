<script lang="ts">
  import Login from "./components/Login.svelte";
  import Vault from "./components/Vault.svelte";
  import N_Box from "./components/NBox.svelte";
  import O_Box from "./components/OBox.svelte";
  import WrongDir from "./components/WrongDir.svelte";

  import { AppPage, currentPage } from "./enums/AppPage.ts";
  import { onMount } from "svelte";
  import { GetDirName } from "../wailsjs/go/main/Getters";
  import { LogDebug, LogWarning } from "../wailsjs/runtime/runtime";

  let loggedIn = false;
  let _page: AppPage;
  currentPage.subscribe((value) => {
    _page = value;
  });

  function handleLoginSuccess() {
    loggedIn = true;
    currentPage.set(AppPage.Vault);
    LogWarning("Logged in");
  }
  let isRightDir = false;

  onMount(async () => {
    isRightDir = await GetDirName();
    LogDebug(isRightDir.toString());
  });
</script>

<main class="rounded-2xl">
  {#if !isRightDir}
    <WrongDir />
  {:else if _page === AppPage.Login}
    <Login on:loginSuccess={handleLoginSuccess} />
  {:else if _page === AppPage.Vault}
    <Vault />
  {:else if _page === AppPage.NBox}
    <N_Box />
  {:else if _page === AppPage.OBox}
    <O_Box />
  {/if}
</main>
