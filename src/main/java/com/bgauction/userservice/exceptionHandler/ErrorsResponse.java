package com.bgauction.userservice.exceptionHandler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorsResponse {

    @Schema(description = "Error HTTP status", example = "400")
    private int status;

    @Schema(description = "Error descriptions",
            example = "{ \"email\": \"This email is already used\", \"username\": \"Username is not valid\" }")
    private Map<String, String> errors;
}
