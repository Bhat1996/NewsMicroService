package com.example.NewsComponent.metadata;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class VertexName {
    public static final String COUNTRY = "countries";

    public static final String STATE = "states";

    public static final String DISTRICT = "districts";

    public static final String TEHSIL = "tehsils";

    public static final String VILLAGE = "villages";

    public  static final String NEWS ="news";

    public static List<String> namesAsList() {
        Class<VertexName> vertexNameClass = VertexName.class;
        Field[] declaredFields = vertexNameClass.getDeclaredFields();

        List<String> names = new ArrayList<>(declaredFields.length);
        for(Field field : declaredFields) {
            try {
                Object o = field.get(VertexName.class);
                String name = (String) o;
                names.add(name);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Vertex collection creation interrupted due to " + field.getName());
            }
        }
        return names;
    }
}
