import { LogInfo } from "../../wailsjs/runtime/runtime";
import { leftCtrlDown, pointerDown } from "./utils";

export function ctrlLeftDown(event: KeyboardEvent) {
    if (event.code === "ControlLeft") {
        leftCtrlDown.set(true);
    }
}

export function ctrlLeftUp(event: KeyboardEvent) {
    if (event.code === "ControlLeft") {
        leftCtrlDown.set(false);
    }
}
export function onMouseDown(event: MouseEvent) {
    if (event.button === 0) {
        pointerDown.set(true);
        LogInfo("Mouse down");
    }
}
export function onMouseUp(event: MouseEvent) {
    if (event.button === 0) {
        pointerDown.set(false);
        LogInfo("Mouse up");
    }
}
export function onTouchStart(event: TouchEvent) {
    pointerDown.set(true);
    // LogInfo("Pointer down");
}

export function onTouchEnd(event: TouchEvent) {
    pointerDown.set(false);
    // LogInfo("Pointer up");
}