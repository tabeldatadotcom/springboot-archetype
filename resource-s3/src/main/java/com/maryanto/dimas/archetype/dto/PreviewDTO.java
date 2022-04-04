package com.maryanto.dimas.archetype.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

public class PreviewDTO {

    @Data
    public static class PresignedUrlRequest {
        @NotNull
        @NotEmpty
        private String objectId;
        @NotNull
        private Integer duration;
        @NotNull
        private TimeUnit unit;
    }

    @Data
    @Builder
    public static class PresignedUrlResponse {
        private String url;
        private String bucketName;
        private String objectId;
    }
}
