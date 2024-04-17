<!-- https://codepen.io/izhik/pen/yKQOrp -->
<script>
    import { darkLightMode, accentColor } from "../../stores/dynamicVariables";
    import { lightBGColor, darkBGColor } from "../../stores/constantVariables";
    import { pageLoading } from "../../stores/dynamicVariables";
</script>

{#if $pageLoading}
    <div
        class="fixed items-center justify-center mt-6 w-full left-1/2 mr-10 z-60"
        style="--wails-draggable: drag; background-color:transparent"
    >
        <div id="oval-spinner">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 24">
                <linearGradient
                    id="a"
                    gradientUnits="userSpaceOnUse"
                    y1="10"
                    x2="60"
                    y2="10"
                >
                    <stop offset="0" stop-color={$accentColor} />
                    <stop
                        offset="100%"
                        stop-color={`${$darkLightMode ? lightBGColor : darkBGColor}`}
                    />
                </linearGradient>

                <path
                    class="spinner"
                    d="M22 2h10c5.5 0 10 4.5 10 10s-4.5 10-10 10H12C6.5 22 2 17.5 2 12S6.5 2 12 2h10"
                    fill="none"
                    stroke="url(#a)"
                    stroke-linecap="round"
                    stroke-width="2"
                    stroke-miterlimit="10"
                />
            </svg>
        </div>
    </div>
{/if}

<style>
    #oval-spinner {
        height: 120px;
        width: 300px;
    }
    .spinner {
        stroke-dasharray: 52;
        stroke-dashoffset: 104;
        animation: spin 1s linear infinite;
    }

    @keyframes spin {
        to {
            stroke-dashoffset: 0;
        }
    }

    @keyframes gradm {
        from {
            offset: 0;
        }
        to {
            offset: 1;
        }
    }
</style>
