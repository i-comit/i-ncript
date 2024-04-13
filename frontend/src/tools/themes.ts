import { LogInfo } from "../../wailsjs/runtime/runtime";
import { darkBGColor, lightBGColor, darkTextColor, lightTextColor, lightShadow_Dark, darkShadow_Dark, lightShadow_Light, darkShadow_Light } from "../stores/constantVariables";

//BG(background-color) element properties
export function darkLightBGOnHTML(darkLightMode: boolean): void {
    if (darkLightMode)
        document.documentElement.style.setProperty("--bg-color", darkBGColor,);
    else
        document.documentElement.style.setProperty("--bg-color", lightBGColor,);
}

export function darkLightBGOnElement(darkLightMode: boolean, element: HTMLElement) {
    if (!element) return
    if (darkLightMode)
        element.style.setProperty("--bg-color", darkBGColor,);
    else
        element.style.setProperty("--bg-color", lightBGColor,);
}

export function darkLightBGOnId(darkLightMode: boolean, idName: string) {
    const element = document.getElementById(idName);
    if (!element) return
    if (darkLightMode)
        element?.style.setProperty("--bg-color", darkBGColor,);
    else
        element?.style.setProperty("--bg-color", lightBGColor,);
}
//END OF BG(background-color) element propetyies

export function darkLightTextOnElement(darkLightMode: boolean, element: HTMLElement) {
    if (!element) { LogInfo("no element found"); return }
    if (darkLightMode)
        element.style.setProperty("--text-color", darkTextColor,);
    else
        element.style.setProperty("--text-color", lightTextColor,);
}

export function darkLightTextOnId(darkLightMode: boolean, idName: string) {
    const element = document.getElementById(idName);
    if (!element) return
    if (darkLightMode)
        element?.style.setProperty("--text-color", darkTextColor,);
    else
        element?.style.setProperty("--text-color", lightTextColor,);
}


export function darkLightTextOnClasses(darkLightMode: boolean, className: string) {
    const elements = document.getElementsByClassName(className);
    if (!elements) return; // Early return if container is not found
    for (const _element of elements) {
        // Type assertion
        const htmlElement = _element as HTMLElement;
        if (darkLightMode)
            htmlElement.style.setProperty("--text-color", darkTextColor);
        else
            htmlElement.style.setProperty("--text-color", lightTextColor);

    }
}

export function darkLightShadowOnNeuButton(darkLightMode: boolean, neuButton: HTMLButtonElement) {
    if (!neuButton) return;
    if (darkLightMode) {
        neuButton.style.setProperty("--light-shadow", lightShadow_Dark);
        neuButton.style.setProperty("--dark-shadow-rgb", darkShadow_Dark);
    } else {
        neuButton.style.setProperty("--light-shadow", lightShadow_Light);
        neuButton.style.setProperty("--dark-shadow-rgb", darkShadow_Light);
    }
}

export function darkLightShadowOnIcons(darkLightMode: boolean) {
    const elements = document.getElementsByClassName("icon");
    if (!elements) return; // Early return if container is not found
    for (const _element of elements) {
        const _icon = _element as HTMLElement;

        if (darkLightMode) {
            _icon.style.setProperty("--light-shadow", lightShadow_Dark);
            _icon.style.setProperty("--dark-shadow-rgb", darkShadow_Dark);
        } else {
            _icon.style.setProperty("--light-shadow", lightShadow_Light);
            _icon.style.setProperty("--dark-shadow-rgb", darkShadow_Light);
        }
    }
}