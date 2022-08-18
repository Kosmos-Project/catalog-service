package com.kosmos.catalog_service.common.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileType {
    GENERAL_FILE("general"),
    IMAGE_FILE("image"),
    VIDEO_FILE("video"),
    AUDIO_FILE("audio");

    private final String value;
}
