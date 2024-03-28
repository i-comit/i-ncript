<script lang="ts">
  import Login from "./components/Login.svelte";
  import Vault from "./components/Vault.svelte";
  import N_Box from "./components/NBox.svelte";
  import O_Box from "./components/OBox.svelte";
  import WrongDir from "./components/WrongDir.svelte";

  import { AppPage } from "./enums/AppPage.ts";
  import { currentPage } from "./stores/currentPage.ts";
  import { onMount } from "svelte";
  import { GetDirName } from "../wailsjs/go/main/Getters";
  import { LogMessage } from "../wailsjs/go/main/Logger";

  let loggedIn = false;
  let _page: AppPage;
  currentPage.subscribe((value) => {
    _page = value;
  });

  function handleLoginSuccess() {
    loggedIn = true;
    currentPage.set(AppPage.Vault);
    LogMessage("LOGGED IN")
  }
  let isRightDir = false;

  onMount(async () => {
    isRightDir = await GetDirName();
    LogMessage(isRightDir.toString());
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
