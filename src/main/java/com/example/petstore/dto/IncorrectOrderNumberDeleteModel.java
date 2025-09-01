
/*
 * Copyright (c) 2025 Krzysztof Grabowski.
 */

package com.example.petstore.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IncorrectOrderNumberDeleteModel {
    private String code;
    private String type;
    private String message;
}