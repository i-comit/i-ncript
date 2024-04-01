export enum FileTypes {
    Unknown = "unknown",
    Image = "image",
    Document = "document",
    Video = "video",
    Audio = "audio",
    CGModel = "cgModel",
    Encrypted = "encrypted",

}

import { LogDebug } from "../../wailsjs/runtime/runtime"

import { writable } from 'svelte/store';

const extensionToFileType: Record<string, FileTypes> = {
    // Image types
    'png': FileTypes.Image,
    'jpg': FileTypes.Image,
    'jpeg': FileTypes.Image,
    'gif': FileTypes.Image,
    'tif': FileTypes.Image,
    'tiff': FileTypes.Image,
    'bmp': FileTypes.Image,
    'webp': FileTypes.Image,
    // Document types
    'pdf': FileTypes.Document,
    'txt': FileTypes.Document,
    'doc': FileTypes.Document,
    'docx': FileTypes.Document,
    'odt': FileTypes.Document,
    'rtf': FileTypes.Document,
    // Video types
    'mov': FileTypes.Video,
    'mp4': FileTypes.Video,
    'avi': FileTypes.Video,
    'mkv': FileTypes.Video,
    'webm': FileTypes.Video,
    'flv': FileTypes.Video,
    // Audio types - added these lines
    'mp3': FileTypes.Audio,
    'wav': FileTypes.Audio,
    'aac': FileTypes.Audio,
    'ogg': FileTypes.Audio,
    'flac': FileTypes.Audio,
    'm4a': FileTypes.Audio,
    // CGModel types
    'gltf': FileTypes.CGModel,
    'stl': FileTypes.CGModel,
    'blend': FileTypes.CGModel,
    'obj': FileTypes.CGModel,
    'fbx': FileTypes.CGModel,
    //Encrypted file
    'enc': FileTypes.Encrypted,
};

export function getFileType(relPath: string): FileTypes {
    // Extract the file extension
    const extension = relPath.split('.').pop()?.toLowerCase() || '';
    // Determine the file type based on the extension
    LogDebug("file type is " + extension)
    return extensionToFileType[extension] || FileTypes.Unknown;
}