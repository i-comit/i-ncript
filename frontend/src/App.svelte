<script>
  import Login from "./components/Login.svelte";
  import Vault from "./components/Vault.svelte";
  import N_Box from "./components/NBox.svelte";
  import O_Box from "./components/OBox.svelte";
  import { AppPage } from "./enums/AppPage";
  import { currentPage } from "./stores/currentPage";

  let loggedIn = false;
  let _page;
  currentPage.subscribe((value) => {
    _page = value;
  });

  function handleLoginSuccess() {
    loggedIn = true;
  }
</script>

<main class="rounded-2xl">
  {#if !loggedIn}
    <Login on:loginSuccess={handleLoginSuccess} />
  {:else}
    {#if _page === AppPage.Vault}
      <Vault />
    {:else if _page === AppPage.NBox}
      <N_Box />
    {:else if _page === AppPage.OBox}
      <O_Box />
    {/if}
  {/if}
</main>
