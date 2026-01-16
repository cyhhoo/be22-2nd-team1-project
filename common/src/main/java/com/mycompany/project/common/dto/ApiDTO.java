package com.mycompany.project.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiDTO<T> {
    private String status;
    private String message;
    private T data;

    public ApiDTO(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiDTO<T> success(T data) {
        return new ApiDTO<>("SUCCESS", "?붿껌???깃났?곸쑝濡?泥섎━?섏뿀?듬땲??", data);
    }

    public static <T> ApiDTO<T> success(String message, T data) {
        return new ApiDTO<>("SUCCESS", message, data);
    }

    public static <T> ApiDTO<T> error(String message) {
        return new ApiDTO<>("ERROR", message, null);
    }

}
