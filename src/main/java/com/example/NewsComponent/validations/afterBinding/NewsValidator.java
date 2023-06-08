package com.example.NewsComponent.validations.afterBinding;

import com.example.NewsComponent.configuration.properties.FileValidation;
import com.example.NewsComponent.dto.request.FileInputWithPart;
import com.example.NewsComponent.enums.FileType;
import com.example.NewsComponent.enums.ValueFillingOption;
import com.example.NewsComponent.exceptions.GeneralBadRequestException;
import com.example.NewsComponent.validations.helper.FileSizeValidator;
import com.example.NewsComponent.validations.helper.FileValidator;
import com.example.NewsComponent.validations.helper.NewsImageValidation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Service
@RequiredArgsConstructor
public class NewsValidator {
    private final FileValidation fileValidation;
    private final FileValidator fileValidator;

    private final FileSizeValidator fileSizeValidator;

    public void validateMultipartFile(final FileInputWithPart fileInputWithPart,
                                          final HttpServletRequest httpServletRequest) {
        throwExceptionIfOnlyImagePostedInNews(fileInputWithPart);


        throwExceptionIfFileMaxImageCountExceed(fileInputWithPart.getImages());
        validateImageTypes(fileInputWithPart.getImages());

        throwExceptionIfFileMaxAudioCountExceed(fileInputWithPart.getAudios());
        validateAudioTypes(fileInputWithPart.getAudios());

        throwExceptionIfFileMaxVideoCountExceed(fileInputWithPart.getVideos());
        validateVideoTypes(fileInputWithPart.getVideos());

        throwExceptionIfFileMaxDocumentCountExceed(fileInputWithPart.getDocuments());
        validateDocumentTypes(fileInputWithPart.getDocuments());
        fileSizeValidator.validateSize(fileInputWithPart, httpServletRequest);
    }

    private void throwExceptionIfOnlyImagePostedInNews(final FileInputWithPart fileInputWithPart) {
        NewsImageValidation newsImageValidation =
                NewsImageValidation
                        .builder()
                        .doesImageExist(isNotEmpty(fileInputWithPart.getImages()))
                        .doesAudioExist(isNotEmpty(fileInputWithPart.getAudios()))
                        .doesVideoExist(isNotEmpty(fileInputWithPart.getVideos()))
                        .doesDocumentExist(isNotEmpty(fileInputWithPart.getDocuments()))
                        .build();

        newsImageValidation.validate();
    }


    private void throwExceptionIfFileMaxImageCountExceed(final List<Part> images) {
        if (CollectionUtils.isEmpty(images)) return;

        int totalImageInThisQuestion = images.size();
        Integer numberOfImagesAllowed = fileValidation.getNumberOfImagesAllowed();
        if (totalImageInThisQuestion <= numberOfImagesAllowed) return;

        throw new GeneralBadRequestException("News Image Count Must Not Exceed " + numberOfImagesAllowed + " Limit");
    }

    private void validateImageTypes(final List<Part> images) {
        if (CollectionUtils.isEmpty(images)) return;

        images.forEach(part ->
            fileValidator.validateFileAs(FileType.IMAGE, part, ValueFillingOption.NON_MANDATORY)
        );
    }

    private void throwExceptionIfFileMaxAudioCountExceed(final List<Part> audios) {
        if (CollectionUtils.isEmpty(audios)) return;

        int totalAudioInThisQuestion = audios.size();
        Integer numberOfAudiosAllowed = fileValidation.getNumberOfAudiosAllowed();
        if (totalAudioInThisQuestion <= numberOfAudiosAllowed) return;

        throw new GeneralBadRequestException("News Audio Count Must Not Exceed " + numberOfAudiosAllowed + " Limit");
    }

    private void validateAudioTypes(final List<Part> audios) {
        if (CollectionUtils.isEmpty(audios)) return;

        audios.forEach(part ->
            fileValidator.validateFileAs(FileType.AUDIO, part, ValueFillingOption.NON_MANDATORY)
        );
    }

    private void throwExceptionIfFileMaxVideoCountExceed(final List<Part> videos) {
        if (CollectionUtils.isEmpty(videos)) return;

        int totalVideoInThisQuestion = videos.size();
        Integer numberOfVideosAllowed = fileValidation.getNumberOfVideosAllowed();
        if (totalVideoInThisQuestion <= numberOfVideosAllowed) return;

        throw new GeneralBadRequestException("News Video Count Must Not Exceed " + numberOfVideosAllowed + " Limit");
    }

    private void validateVideoTypes(final List<Part> videos) {
        if (CollectionUtils.isEmpty(videos)) return;

        videos.forEach(part ->
            fileValidator.validateFileAs(FileType.VIDEO, part, ValueFillingOption.NON_MANDATORY)
        );
    }

    private void throwExceptionIfFileMaxDocumentCountExceed(final List<Part> documents) {
        if (CollectionUtils.isEmpty(documents)) return;

        int totalDocumentInThisQuestion = documents.size();
        Integer numberOfDocumentsAllowed = fileValidation.getNumberOfDocumentsAllowed();
        if (totalDocumentInThisQuestion <= numberOfDocumentsAllowed) return;

        throw new GeneralBadRequestException("News Document Count Must Not Exceed " + numberOfDocumentsAllowed + " Limit");
    }

    private void validateDocumentTypes(final List<Part> documents) {
        if (CollectionUtils.isEmpty(documents)) return;

        documents.forEach(part ->
            fileValidator.validateFileAs(FileType.DOCUMENT, part, ValueFillingOption.NON_MANDATORY)
        );
    }

}

