<!-- Settings.svelte -->
<script lang="ts">
    import { onDestroy, onMount } from "svelte";
    import { get } from "svelte/store";
    import {
        Button,
        ButtonGroup,
        Dropdown,
        DropdownItem,
        Range,
        DarkMode,
        Tooltip,
        Textarea,
        Label,
    } from "flowbite-svelte";
    import { ChevronDownOutline, InfoCircleSolid } from "flowbite-svelte-icons";
    import { buildFileTree } from "../../tools/fileTree";
    import { tooltipTailwindClass } from "../../stores/constantVariables";

    import { AppPage, currentPage } from "../../enums/AppPage";
    import {
        darkLightMode,
        accentColor,
        filterInputs,
        pageLoading,
        maxFileSize,
    } from "../../stores/dynamicVariables";
    import { LogDebug, LogInfo } from "../../../wailsjs/runtime/runtime";
    import { logRetentionTimeStep } from "../../tools/logger";
    import {
        AddInputToFilterTemplate,
        ClearExcludedSlices,
        SaveFileFilters,
    } from "../../../wailsjs/go/main/FileUtils";
    import { formatFileSize } from "../../tools/utils";

    enum LogEntriesRetentionTime {
        Never = "NEVER",
        OneWeek = "1 WEEK",
        TwoWeeks = "2 WEEKS",
        OneMonth = "1 MONTH",
        SixMonths = "6 MONTHS",
        OneYear = "1 YEAR",
    }

    let filterInputLineCt: number = 1;

    let currentLogRetentionTime: LogEntriesRetentionTime =
        LogEntriesRetentionTime.OneMonth;

    let maxFileSizeFormatted: string = "";

    let _currentPage: AppPage;
    currentPage.subscribe((value) => {
        _currentPage = value;
    });

    onMount(() => {
        setfFilterInputLineCt();
    });

    onDestroy(() => {
        SaveFileFilters(get(filterInputs));
    });

    async function readFilterInputs() {
        await ClearExcludedSlices();
        let lines = get(filterInputs)
            .split("\n")
            .filter((line) => line.trim() !== "");
        lines.forEach((_filterInput) => {
            AddInputToFilterTemplate(_filterInput);
        });
        pageLoading.set(true);
        buildFileTree();
        setfFilterInputLineCt();
    }
    function toggleLightDarkMode() {
        darkLightMode.update((v) => !v);
    }

    function keyFilterInputLineCt(event: KeyboardEvent) {
        if (event.code === "Enter") setfFilterInputLineCt();
    }

    function setfFilterInputLineCt() {
        let lines = get(filterInputs).split("\n");
        if (lines.length < 1) {
            filterInputLineCt = 1;
            return;
        }
        if (filterInputLineCt < 5) filterInputLineCt = lines.length;
    }

    function setAccentColor(hexColor: string) {
        accentColor.set(hexColor);
        LogInfo("accent color: " + hexColor);
    }

    function changeMaxFileSize() {
        const minLog = Math.log(1048576); //1MB in binary
        const maxLog = Math.log(17179869184); //16GB binary
        const scale = (maxLog - minLog) / 99; // 99 is the difference in the slider range (100 - 1)
        const bytes = Math.exp(minLog + scale * ($maxFileSize - 1));
        maxFileSizeFormatted = formatFileSize(bytes);
        LogDebug("maxFileSize " + maxFileSizeFormatted);
    }

    function changeLogEntriesRetentionPeriod() {
        switch ($logRetentionTimeStep) {
            case 0:
            default:
                currentLogRetentionTime = LogEntriesRetentionTime.Never;
                break;
            case 1:
                currentLogRetentionTime = LogEntriesRetentionTime.OneWeek;
                break;
            case 2:
                currentLogRetentionTime = LogEntriesRetentionTime.TwoWeeks;
                break;
            case 3:
                currentLogRetentionTime = LogEntriesRetentionTime.OneMonth;
                break;
            case 4:
                currentLogRetentionTime = LogEntriesRetentionTime.SixMonths;
                break;
            case 5:
                currentLogRetentionTime = LogEntriesRetentionTime.OneYear;
                break;
        }
    }
</script>

<div
    id="modal-panel"
    class="rounded-md ml-0.5 mr-1 !mt-0.5 hover:outline outline-1 bg-primary-700 dark:bg-primary-600"
    style="max-height: {_currentPage === AppPage.Login ? '65.75vh' : '97%'};
         margin-bottom: {_currentPage === AppPage.Login ? '2.5vh' : ''};"
>
    <div
        class="mb-1.5 pb-6 w-1/2 left-1/4 rounded-bl-lg rounded-br-lg font-semibold h-5 bg-primary-400 dark:bg-primary-300
                outline outline-1 outline-primary-100 dark:outline-primary-200"
        style={`position: sticky; top: 0px; color: ${$accentColor};`}
    >
        SETTINGS
    </div>
    <div
        class="flex justify-between my-1 rounded-lg mx-1"
        style={`background-color: ${$accentColor}`}
    >
        <button class="!p-0" on:pointerdown={() => toggleLightDarkMode()}>
            <DarkMode btnClass="w-6 h-6 rounded-lg text-md p-2" />
        </button>
        <div id="languages">
            <Button
                data-placement="left-start"
                style={`background-color: ${$accentColor}`}
                >Languages<ChevronDownOutline
                    class="w-5 h-5 text-white dark:text-white"
                /></Button
            >
            <Dropdown class="">
                <DropdownItem
                    defaultClass="font-medium py-1 px-1 text-sm hover:bg-gray-100 dark:hover:bg-gray-400"
                    >English</DropdownItem
                >
                <DropdownItem
                    defaultClass="font-medium py-1 px-1 text-sm hover:bg-gray-100 dark:hover:bg-gray-400"
                    >French</DropdownItem
                >
                <DropdownItem
                    defaultClass="font-medium py-1 px-1 text-sm hover:bg-gray-100 dark:hover:bg-gray-400"
                    >German</DropdownItem
                >
                <DropdownItem
                    defaultClass="font-medium py-1 px-1 text-sm hover:bg-gray-100 dark:hover:bg-gray-400"
                    >Urdu</DropdownItem
                >
            </Dropdown>
        </div>
    </div>

    <div class="h-1" />
    <div id="darkLightMode"></div>
    <div id="accentColor flex" class="p-0 m-0">
        <Label class="mb-0.5 text-md">accent colors:</Label>
        <ButtonGroup>
            <Button
                class="rounded-l-lg"
                style="background-color: #95C7DB"
                on:click={() => setAccentColor("#95C7DB")}
            />
            <Button
                style="background-color: #E9C456"
                on:click={() => setAccentColor("#E9C456")}
            ></Button>
            <Button
                style="background-color: #D43820"
                on:click={() => setAccentColor("#D43820")}
            />
            <Button
                style="background-color: #31A51C"
                on:click={() => setAccentColor("#31A51C")}
            />
            <Button
                class="rounded-r-lg"
                style="background-color: #CF33ED"
                on:click={() => setAccentColor("#CF33ED")}
            />
        </ButtonGroup>
    </div>
    <div class="h-2" />
    {#if $currentPage !== AppPage.Login}
        <div class="mx-2">
            <div class="flex justify-center items-center space-x-1">
                <Label class="mb-0.5 text-md">file filters:</Label>
                <InfoCircleSolid
                    slot="icon"
                    class="w-4 h-4 text-primary-100 dark:text-primary-200"
                />
                <Tooltip>
                    <p>*/.git/objects/*</p>
                    <p>/VAULT/*/docs</p>
                    <p>*.jpg</p>
                    <p>*Image.png</p>
                </Tooltip>
            </div>
            <Textarea
                id="textarea-id"
                placeholder="*/.git/objects/*"
                unWrappedClass="text-xs"
                bind:value={$filterInputs}
                rows={filterInputLineCt}
                on:blur={readFilterInputs}
                on:keyup={(event) => {
                    keyFilterInputLineCt(event);
                }}
            />
        </div>
    {/if}
    <div class="px-4">
        <div class="flex justify-between mt-1.5">
            <Label class="text-md">maximum file size:</Label>
            <Label class="text-md">{maxFileSizeFormatted}</Label>
        </div>
        <Range
            min="1"
            max="100"
            bind:value={$maxFileSize}
            size="md"
            on:change={changeMaxFileSize}
        />
    </div>
    <div class="px-4">
        <Label class="mt-3 text-md text-left">delete logs older than:</Label>
        <Range
            min="0"
            max="5"
            bind:value={$logRetentionTimeStep}
            size="md"
            on:change={changeLogEntriesRetentionPeriod}
        />
        <Tooltip placement="bottom" class={tooltipTailwindClass} arrow={false}
            >{currentLogRetentionTime}</Tooltip
        >
    </div>
</div>
