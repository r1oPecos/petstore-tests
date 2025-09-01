package com.example.petstore.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderModel {
    private long id;
    private long petId;
    private int quantity;
    private String shipDate;
    private String status;
    private boolean complete;
}