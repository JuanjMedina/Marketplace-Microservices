package com.marketplace.userservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Generic API response wrapper")
public record ApiResponseDTO<T>(

        @Schema(description = "Response message describing the result", example = "User created successfully")
        String message,

        @Schema(description = "Indicates if the operation was successful", example = "true")
        boolean success,

        @Schema(description = "Response data payload (can be null)")
        T data
) {
}

