package com.tastes_of_india.restaurantManagement.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import java.util.Set;

public class DynamicPropertyFilter extends SimpleBeanPropertyFilter {

    private final Set<String> ignoreProperties;

    public DynamicPropertyFilter(Set<String> ignoreProperties) {
        this.ignoreProperties = ignoreProperties;
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider prov, PropertyWriter writer) throws Exception {
        if(ignoreProperties.contains(writer.getName())){
            return;
        }
        writer.serializeAsField(pojo,jgen,prov);
    }
}
