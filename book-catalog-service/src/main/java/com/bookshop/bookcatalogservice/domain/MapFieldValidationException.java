package com.bookshop.bookcatalogservice.domain;

import lombok.Getter;
import org.springframework.validation.MapBindingResult;

import java.util.Map;

@Getter
public class MapFieldValidationException extends RuntimeException{
    private final MapBindingResult mapBindingResult;
    public MapFieldValidationException(String message, MapBindingResult mapBindingResult) {
        super(message);
        this.mapBindingResult = mapBindingResult;
    }
}
