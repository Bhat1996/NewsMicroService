package com.example.NewsComponent.utils;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

import static com.example.NewsComponent.utils.Not.not;


public class ArangoIdUtils {

    public static final String KEY_DELIMITER = "/";
    private static final int VALID_ARANGO_ID_LENGTH = 2;

    private ArangoIdUtils() {
        throw new UnsupportedOperationException("No Object For This Class..!");
    }

    public static String getArangoId(@NonNull String collectionName,
                                     @NonNull String documentId) {
        Objects.requireNonNull(collectionName, "Collection Name is Must to Get Arango Id");
        Objects.requireNonNull(documentId, "Document Id is must to create Arango Id");
        return collectionName + KEY_DELIMITER + documentId;
    }

    public static boolean isValidArangoId(String arangoId) {
        if (StringUtils.isBlank(arangoId)) {
            return false;
        }

        String[] split = arangoId.split(KEY_DELIMITER);
        return split.length == VALID_ARANGO_ID_LENGTH;
    }

    public static String getKeyFromArangoId(String arangoId) {
        if (not (isValidArangoId(arangoId)))
            throw new IllegalStateException("Invalid ArangoId Provided " + arangoId);

        return arangoId.split(KEY_DELIMITER)[1];
    }

    public static List<String> getKeysFromArangoIds(@NonNull List<String> arangoIds) {
        Objects.requireNonNull(arangoIds);
        return arangoIds.stream()
                .map(ArangoIdUtils::getKeyFromArangoId)
                .toList();
    }
}
