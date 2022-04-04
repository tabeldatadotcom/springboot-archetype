package com.maryanto.dimas.archetype.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class DownloadDTO {

    @Data
    public static class DownloadObjectRequest {
        @NotEmpty
        @NotNull
        private String objectId;
    }
}
