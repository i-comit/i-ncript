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
    import { ChevronDownOutline } from "flowbite-svelte-icons";
    import { buildFileTree } from "../../tools/fileTree";
    import { tooltipTailwindClass } from "../../stores/constantVariables";

    import { AppPage, currentPage } from "../../enums/AppPage";
    import {
        darkLightMode,
        accentColor,
        filterInputs,
        pageLoading,
    } from "../../stores/dynamicVariables";
    import { LogInfo } from "../../../wailsjs/runtime/runtime";
    import { logRetentionTimeStep } from "../../tools/logger";
    import {
        AddInputToFilterTemplate,
        ClearExcludedSlices,
        SaveFileFilters,
    } from "../../../wailsjs/go/main/FileUtils";

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

    function readFilterInputs() {
        ClearExcludedSlices().finally(() => {
            let lines = get(filterInputs)
                .split("\n")
                .filter((line) => line.trim() !== "");
            lines.forEach((_filterInput) => {
                AddInputToFilterTemplate(_filterInput);
            });
            pageLoading.set(true);
            buildFileTree();
            setfFilterInputLineCt();
        });
    }
    function toggleLightDarkMode() {
        darkLightMode.update((v) => !v);
    }

    function keyFilterInputLineCt(event: KeyboardEvent) {
        if (event.code === "Enter") setfFilterInputLineCt();
    }

    function setfFilterInputLineCt() {
        let lines = get(filterInputs).split("\n");
        LogInfo("Line count " + lines.length);
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
    class="rounded-md ml-0.5 mr-1 !mb-0.5 hover:outline outline-1 bg-primary-700 dark:bg-primary-600"
    style="max-height: {_currentPage === AppPage.Login
        ? '67vh'
        : '96%'};  margin-top: 0.16rem"
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
        <p class="p-0 m-0 text-primary-100 dark:text-primary-200">
            accent color
        </p>
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
    <div class="mx-2">
        <Label for="textarea-id" class="mb-0.5">filters</Label>
        <Textarea
            id="textarea-id"
            placeholder="*/.git/*"
            unWrappedClass="text-xs"
            bind:value={$filterInputs}
            rows={filterInputLineCt}
            on:blur={readFilterInputs}
            on:keyup={(event) => {
                keyFilterInputLineCt(event);
            }}
        />
    </div>
    <div class="px-5">
        <p class="p-0 text-sm text-primary-100 dark:text-primary-200">
            delete log entries older than:
        </p>
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
