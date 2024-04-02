<script lang="ts">
    import { onMount } from "svelte";
    export let className = "";

    let dataProgress: number = 0; // Initial value

    function randomize(): void {
        dataProgress = Math.floor(Math.random() * 100);
    }

    onMount(() => {
        setTimeout(randomize, 200); // Initial randomize after component mounts
    });
</script>

<div
    class="ko-progress-circle z-100 {` ${className}`}"
    data-progress={dataProgress}
>
    <div class="ko-circle">
        <div class="full ko-progress-circle__slice">
            <div class="ko-progress-circle__fill"></div>
        </div>
        <div class="ko-progress-circle__slice">
            <div class="ko-progress-circle__fill"></div>
            <div class="ko-progress-circle__fill ko-progress-circle__bar"></div>
        </div>
    </div>
    <div class="ko-progress-circle__overlay"></div>
</div>

<style lang="scss">
    $circle-size: 150px;
    $circle-background: #d9d9d9;
    $circle-color: #1291d4;
    $inset-size: 135px;
    $inset-color: #fbfbfb;
    $transition-length: 1s;

    .ko-progress-circle {
        margin: 0px;
        width: $circle-size;
        height: $circle-size;
        z-index: 25;
        position: fixed;
        bottom: 1rem;
        background-color: $circle-background;
        border-radius: 50%;
        .ko-progress-circle__slice,
        .ko-progress-circle__fill {
            width: $circle-size;
            height: $circle-size;
            position: absolute;
            -webkit-backface-visibility: hidden;
            transition: transform $transition-length;
            border-radius: 50%;
        }
        .ko-progress-circle__slice {
            clip: rect(0px, $circle-size, $circle-size, $circle-size/2);
            .ko-progress-circle__fill {
                clip: rect(0px, $circle-size/2, $circle-size, 0px);
                background-color: $circle-color;
            }
        }
        .ko-progress-circle__overlay {
            width: $inset-size;
            height: $inset-size;
            position: absolute;
            margin-left: ($circle-size - $inset-size)/2;
            margin-top: ($circle-size - $inset-size)/2;

            background-color: $inset-color;
            border-radius: 50%;
        }

        $i: 0;
        $increment: 180deg / 100;
        @while $i <= 100 {
            &[data-progress="#{$i}"] {
                .ko-progress-circle__slice.full,
                .ko-progress-circle__fill {
                    transform: rotate($increment * $i);
                }
                .ko-progress-circle__fill.ko-progress-circle__bar {
                    transform: rotate($increment * $i * 2);
                }
                $i: $i + 1;
            }
        }
    }
</style>
