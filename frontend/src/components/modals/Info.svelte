<!-- Info.svelte -->
<script>
    import { Button, Kbd } from "flowbite-svelte";
    import { onMount } from "svelte";
    import { AppPage, currentPage } from "../../enums/AppPage";

    import {
        darkTextColor,
        lightTextColor,
    } from "../../stores/constantVariables";
    import { darkLightMode, accentColor } from "../../stores/dynamicVariables";
    import {
        DownloadSolid,
        GithubSolid,
        InfoCircleSolid,
    } from "flowbite-svelte-icons";

    let cwd = "";
    import { GetAppPath } from "../../../wailsjs/go/main/Getters";
    import InfoVault from "../pages/setup_pages/Info_Vault.svelte";
    import InfoMBox from "../pages/setup_pages/Info_MBox.svelte";

    import wailsLogo from "../../assets/images/wailsLogo.png";
    import svelteLogo from "../../assets/images/svelteLogo.png";
    import tailwindLogo from "../../assets/images/tailwind.svg";

    const appVersion = __APP_VERSION__;

    onMount(async () => {
        cwd = await GetAppPath();
    });
    let _currentPage;
    currentPage.subscribe((value) => {
        _currentPage = value;
    });

    let iconButtonClass =
        "!p-1 text-primary-100 dark:text-primary-200 drop-shadow-[0_1.2px_1.2px_rgba(0,0,0,0.8)] hover:drop-shadow-[0_1.2px_1.2px_rgba(220,220,220,1)]";
</script>

<div
    id="modal-panel"
    class="rounded-md ml-0.5 mr-1 !mt-0.5 hover:outline outline-1 bg-primary-700 dark:bg-primary-600"
    style="max-height: {_currentPage === AppPage.Login
        ? '68vh'
        : '96%'};"
>
    <div
        class="mb-1.5 pb-6 w-1/3 left-1/3 rounded-bl-lg rounded-br-lg font-semibold h-5 bg-primary-400 dark:bg-primary-300
    outline outline-1 outline-primary-100 dark:outline-primary-200"
        style={`position: sticky; top: 0px; color: ${$accentColor};`}
    >
        INFO
    </div>

    <div
        class="px-0 m-0 mt-2 font-semibold text-primary-100 leading-none
                dark:text-primary-200 rounded-md !mx-5 h-4 text-sm"
    >
        v{appVersion}
    </div>

    <div
        class="px-0 m-0 mt-2 bg-primary-500 text-primary-100 dark:text-primary-200
                rounded-md !mx-2 h-4 leading-none text-sm"
    >
        current working directory:
    </div>
    <div class="relative w-full overflow-hidden" style="max-width: 100%;">
        <!-- Ensure the parent doesn't expand & Child div that can scroll horizontally-->
        <div
            class="m-0 whitespace-nowrap overflow-x-auto text-primary-100 dark:text-primary-200
            z-5 drop-shadow-[0_1px_1px_rgba(0,0,0,0.5)]"
            style="scrollbar-width: none;"
        >
            {cwd}
        </div>
    </div>
    <div class="h-2" />
    <div
        class="flex justify-between px-4 text-primary-100 dark:text-primary-200"
    >
        <p class="p-0 m-0">open source repo:</p>
        <Button pill={true} outline={true} class={iconButtonClass}>
            <a href="https://github.com/i-comit/i-ncript" target="_blank">
                <GithubSolid
                    class="w-4 h-4 m-0"
                    color={$darkLightMode ? lightTextColor : darkTextColor}
                /></a
            ></Button
        >
    </div>
    <div class="h-0.5" />
    <div
        class="flex justify-between px-4 text-primary-100 dark:text-primary-200"
    >
        <p class="p-0 m-0">check for updates:</p>
        <Button pill={true} outline={true} class={iconButtonClass}>
            <a
                href="https://sourceforge.net/projects/i-ncript/"
                target="_blank"
            >
                <DownloadSolid
                    class="w-4 h-4 m-0"
                    color={$darkLightMode ? lightTextColor : darkTextColor}
                /></a
            ></Button
        >
    </div>
    <div class="h-4" />
    <Kbd class="px-2 py-0.5 text-md">FRAMEWORKS</Kbd>
    <div class="h-1" />

    <ul
        class="flex justify-center mx-4 space-x-2.5 text-sm text-primary-100 dark:text-primary-200"
    >
        <li class="flex flex-col items-center justify-center">
            GUI
            <a
                href="https://github.com/wailsapp/wails"
                target="_blank"
                class="hover:underline hover:text-primary-500"
            >
                <img
                    src={wailsLogo}
                    alt="A GUI Framework for building applications using Go and Web Technologies"
                    class="p-0.5 mt-0.5 h-8"
                />
                Wails
            </a>
        </li>
        <li class="flex flex-col items-center justify-center">
            Frontend
            <a
                href="https://github.com/sveltejs/svelte"
                target="_blank"
                class="hover:underline hover:text-primary-500"
            >
                <img
                    src={svelteLogo}
                    alt="Cybernetically enhanced web apps"
                    class="p-0.5 mt-0.5 h-8 !ml-1.5"
                />
                Svelte
            </a>
        </li>
        <li class="flex flex-col items-center justify-center">
            CSS
            <a
                href="https://github.com/tailwindlabs/tailwindcss"
                target="_blank"
                class="hover:underline hover:text-primary-500"
            >
                <img
                    src={tailwindLogo}
                    alt="A utility-first CSS framework for rapid UI development."
                    class="p-1 mt-0.5 h-8"
                />
                Tailwind
            </a>
        </li>
    </ul>

    {#if $currentPage === AppPage.Vault}
        <h2 class="divider line glow text-primary-100 dark:text-primary-200">
            VAULT <InfoCircleSolid class="pl-1" />
        </h2>
        <div
            class="!text-sm !text-left px-2 text-primary-100 dark:text-primary-200"
        >
            <InfoVault />
        </div>
    {:else if $currentPage === AppPage.Mbox}
        <h2 class="divider line glow text-primary-100 dark:text-primary-200">
            M-BOX <InfoCircleSolid class="pl-1" />
        </h2>
        <div
            class="!text-sm !text-left px-2 text-primary-100 dark:text-primary-200"
        >
            <InfoMBox />
        </div>
    {:else}
        <div class="h-1" />
    {/if}
</div>

<style lang="scss">
    // https://codepen.io/benknight/pen/zxxeay
    .divider {
        display: flex;
        &:before,
        &:after {
            content: "";
            flex: 1;
        }
    }
    .line {
        align-items: center;
        margin: 0.5em 0em;

        &:before,
        &:after {
            height: 2px;
            margin: 0 0.8em;
        }
    }

    .glow {
        &:before,
        &:after {
            height: 2px;
            filter: blur(2px);
            -webkit-filter: blur(2px);
            border-radius: 10%;
        }

        &:before {
            background: linear-gradient(
                to right,
                rgb(188, 188, 188),
                rgb(65, 65, 65)
            );
        }

        &:after {
            background: linear-gradient(
                to left,
                rgb(188, 188, 188),
                rgb(65, 65, 65)
            );
        }
    }
</style>
