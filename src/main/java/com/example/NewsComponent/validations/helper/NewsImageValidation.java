package com.example.NewsComponent.validations.helper;


import com.example.NewsComponent.exceptions.GeneralBadRequestException;
import com.example.NewsComponent.utils.Not;
import lombok.Builder;

@Builder
public class NewsImageValidation {
    private final boolean doesImageExist;
    private final boolean doesAudioExist;
    private final boolean doesVideoExist;
    private final boolean doesDocumentExist;
    private final boolean doesTextExist;

    public void validate() {
        if (doesImageExist) {
            boolean imageIsWithSupportiveFields =
                    doesTextExist || doesAudioExist || doesVideoExist || doesDocumentExist;
            if (Not.not (imageIsWithSupportiveFields)) {
                String message = """
                        Only Image Not Allowed,
                        Please Provide Text, Audio, Video
                        Or Document With The Image
                        """;
                throw new GeneralBadRequestException(message);
            }
        }
    }
}
