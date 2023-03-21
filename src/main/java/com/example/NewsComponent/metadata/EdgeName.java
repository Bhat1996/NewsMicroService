package com.example.NewsComponent.metadata;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EdgeName {

  public static final String NEWS_IS_FOR_LOCATION = "newsIsForLocation";

    public static final String NEWS_HAS_INTEREST="newsHasInterest";

    public static final String NEWS_HAS_HASHTAG="newsHasHashTag";


    public static List<String> namesAsList() {
        Class<EdgeName> edgeNameClass = EdgeName.class;
        Field[] declaredFields = edgeNameClass.getDeclaredFields();

        List<String> names = new ArrayList<>(declaredFields.length);
        for (Field field: declaredFields) {
            try {
                Object o = field.get(EdgeName.class);
                String name = (String) o;
                names.add(name);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Edge collection creation interrupted due to " + field.getName());
            }
        }
        return names;
    }
}
