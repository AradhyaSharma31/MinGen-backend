package com.MinGen.MinGen_backend.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class ResourceNotFoundException extends RuntimeException {
    private String message;
}
