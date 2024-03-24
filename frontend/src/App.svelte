<script>
  import Login from './components/Login.svelte';
  import Vault from '../src/components/Vault.svelte';
  
  let loggedIn = false;

  // Function to handle successful login
  function handleLoginSuccess() {
    loggedIn = true;
  }
</script>

<main>
  {#if !loggedIn}
    <Login on:loginSuccess={handleLoginSuccess} />
  {:else}
    <Vault />
  {/if}
</main>