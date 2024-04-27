<!-- https://codepen.io/eZ0/pen/eZXNzd -->
<script lang="ts">
    import { largeFilePercent } from "../../stores/dynamicVariables.ts";

    export let className = "";
    export let dataProgress: number;
    export let _style = "";
    export let overlayText: string = "";
</script>

{#if $largeFilePercent > 0 && $largeFilePercent < 100}
    <div
        class="fixed w-full h-full pointer-events-none
        bg-primary-100 dark:bg-primary-200 opacity-10"
        style="z-index:36;"
    />
    <div
        class="ko-progress-circle z-5 {` ${className}`} h-full
        bg-primary-300 dark:bg-primary-400 pointer-events-none"
        data-progress={dataProgress}
        style={_style}
    >
        <div class="ko-circle">
            <div class="full ko-progress-circle__slice">
                <div class="ko-progress-circle__fill"></div>
            </div>
            <div class="ko-progress-circle__slice">
                <div class="ko-progress-circle__fill"></div>
                <div
                    class="ko-progress-circle__fill ko-progress-circle__bar"
                ></div>
            </div>
        </div>
        <div
            class="ko-progress-circle__overlay flex items-center justify-center
                    text-ellipsis bg-primary-700 dark:bg-primary-600"
        >
            <span class="truncate px-1 text-sm">{overlayText}</span>
        </div>
    </div>
{/if}

<style lang="scss">
    @use "sass:math";
    $circle-size: 142px;
    $circle-color: #1291d4;
    $inset-size: 130px;
    $transition-length: 0.1s;

    .ko-progress-circle {
        margin: 0px;
        width: $circle-size;
        height: $circle-size;
        z-index: 15;
        position: fixed;
        bottom: 1.3rem;
        border-radius: 50%;
        opacity: 1;
        .ko-progress-circle__slice,
        .ko-progress-circle__fill {
            width: $circle-size;
            height: $circle-size;
            position: absolute;
            // -webkit-backface-visibility: hidden;
            transition: transform $transition-length;
            border-radius: 50%;
        }
        .ko-progress-circle__slice {
            clip: rect(0px, $circle-size, $circle-size, calc($circle-size / 2));
            .ko-progress-circle__fill {
                clip: rect(0px, calc($circle-size / 2), $circle-size, 0px);
                background-color: $circle-color;
            }
        }
        .ko-progress-circle__overlay {
            width: $inset-size;
            height: $inset-size;
            position: absolute;
            margin-left: math.div($circle-size - $inset-size, 2);
            margin-top: math.div($circle-size - $inset-size, 2);
            border-radius: 50%;
            ::slotted(*) {
                font-size: 1px !important; // Tailwind equivalent: text-xs
                margin: 0; // Tailwind equivalent: m-0
                padding: 0; // Tailwind equivalent: p-0
            }
        }

        $i: 0;
        $increment: math.div(180deg, 100);
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
