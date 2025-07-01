package com.marketplace.userservice.controller.dto;

public record ApiResponseDTO<T>(String message, boolean success, T data) {
}
