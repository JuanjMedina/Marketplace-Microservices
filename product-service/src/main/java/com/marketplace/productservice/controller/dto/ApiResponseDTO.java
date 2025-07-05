package com.marketplace.productservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Generic API response wrapper")
@Builder
public record ApiResponseDTO<T>(

        @Schema(description = "Response message describing the result", example = "User created successfully")
        String message,

        @Schema(description = "Indicates if the operation was successful", example = "true")
        boolean success,

        @Schema(description = "Response data payload (can be null)")
        T data
) {
}

