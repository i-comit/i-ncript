<script>
  import LoginForm from '../src/components/LoginForm.svelte';
  import MainForm from '../src/components/MainForm.svelte';
  
  let loggedIn = false;

  // Function to handle successful login
  function handleLoginSuccess() {
    loggedIn = true;
  }
</script>

<main>
  {#if !loggedIn}
    <LoginForm on:loginSuccess={handleLoginSuccess} />
  {:else}
    <MainForm />
  {/if}
</main>