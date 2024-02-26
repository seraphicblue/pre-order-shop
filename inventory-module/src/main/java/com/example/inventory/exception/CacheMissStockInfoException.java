package com.example.inventory.exception;

public class CacheMissStockInfoException extends RuntimeException {
    public CacheMissStockInfoException(String message) {
        super(message);
    }
}