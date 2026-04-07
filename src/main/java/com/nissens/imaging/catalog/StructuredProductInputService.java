package com.nissens.imaging.catalog;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StructuredProductInputService {

    public String applyTemplate(String template, Map<String, String> inputs) {
        String result = template;

        if (inputs != null) {
            for (Map.Entry<String, String> entry : inputs.entrySet()) {
                String key = "{" + entry.getKey() + "}";
                String value = entry.getValue() == null ? "" : entry.getValue();
                result = result.replace(key, value);
            }
        }

        return result;
    }
}