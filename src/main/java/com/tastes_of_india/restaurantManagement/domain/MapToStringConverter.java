package com.tastes_of_india.restaurantManagement.domain;

import com.google.gson.Gson;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.HashMap;
import java.util.Map;

@Converter
public class MapToStringConverter implements AttributeConverter<Map<String,Object>,String> {

    private final Gson gson;

    public MapToStringConverter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String convertToDatabaseColumn(Map<String, Object> stringObjectMap) {
        return gson.toJson(stringObjectMap);
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String s) {
        Map<String,Object> detailMap=new HashMap<>();
        return gson.fromJson(s,detailMap.getClass());
    }
}
