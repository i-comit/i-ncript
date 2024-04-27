<script>
    import { onMount } from "svelte";
    import { fly } from "svelte/transition";

    let one_second = 1000,
        one_minute = one_second * 60,
        one_hour = one_minute * 60,
        startDate = new Date();

    let currentTime = {
        hours: "00",
        minutes: "00",
        seconds: "00",
    };

    function updateTime() {
        const now = new Date();
        const elapsed = now - startDate;
        let hours = String(Math.floor(elapsed / one_hour)).padStart(2, "0");
        let minutes = String(
            Math.floor((elapsed % one_hour) / one_minute),
        ).padStart(2, "0");
        let seconds = String(
            Math.floor((elapsed % one_minute) / one_second),
        ).padStart(2, "0");

        currentTime = { hours, minutes, seconds };
    }

    onMount(() => {
        const interval = setInterval(updateTime, 1000);
        return () => clearInterval(interval);
    });

    // Split the time into an array of single digits for the keyed each block
    $: digits =
        `${currentTime.hours}${currentTime.minutes}${currentTime.seconds}`.split(
            "",
        );
</script>

<div class="flex items-center !space-x-2 mt-0.5 w-[11rem]">
    <p class="font-semibold text-primary-100 dark:text-primary-200">UPTIME</p>
    <div class="timer-group">
        <div class="face">
            <div id="lazy" class="odometer text-primary-100 dark:text-primary-200">
                {#each digits as digit, index (digit + index)}
                    <div class="digit" in:fly={{ x: 10, duration: 420 }}>
                        {digit}
                    </div>
                    {#if index === 1 || index === 3}
                        <span class="colon">:</span>
                    {/if}
                {/each}
            </div>
        </div>
    </div>
</div>

<style>
    .timer-group {
        margin: 0 auto;
    }

    .face {
        background: rgba(0, 0, 0, 0.1);
        border-radius: 0.5rem;
        padding: 0 8px 0;
        position: relative;
        text-align: center;
    }

    .odometer {
        font-family: sans-serif;
        font-size: 16px;
    }

    .odometer .digit {
        display: inline-block;
        height: 1.2em;
        width: 0.5rem;
        margin: 0 0; /* Adjust the space between digits */
        position: relative;
    }
    .colon {
        padding: 0.1em; /* Space around colons */
        position: relative;
        bottom: 0.05em;
    }
</style>
