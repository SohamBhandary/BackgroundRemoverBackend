package com.Soham.removeBG.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveBgResponse {
    private boolean success;
    private HttpStatus statusCode;
    private Object data;
}
