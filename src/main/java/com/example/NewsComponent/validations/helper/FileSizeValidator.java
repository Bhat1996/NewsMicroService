package com.example.NewsComponent.validations.helper;


import com.example.NewsComponent.exceptions.GeneralBadRequestException;
import com.example.NewsComponent.configuration.properties.FileValidation;
import com.example.NewsComponent.dto.request.FileInputWithPart;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileSizeValidator {
    private final FileValidation fileValidation;

    public void validateSize(final FileInputWithPart fileInputWithPart,
                             final HttpServletRequest httpServletRequest) {
        validateImageSize(fileInputWithPart.getImages());
        validateVideoSize(fileInputWithPart.getVideos());
        validateAudioSize(fileInputWithPart.getAudios());
        validateDocumentSize(fileInputWithPart.getDocuments());

        if (httpServletRequest.getContentType().contains("multipart/")) {
            validateMultipartWholeRequestSize(httpServletRequest);
        } else {
            validateNonMultipartRequest(httpServletRequest);
        }
    }

    private void validateImageSize(final List<Part> images) {
        if (CollectionUtils.isEmpty(images)) {
            return;
        }
        final String maxImageSizeAllowed = fileValidation.getMaxImageSizeAllowed();
        final DataSize imageMaxAllowedDataSize = DataSize.parse(maxImageSizeAllowed);
        images.forEach(image -> {
            final DataSize dataSizeOfThisImage = DataSize.ofBytes(image.getSize());

            if (dataSizeOfThisImage.compareTo(imageMaxAllowedDataSize) > 0) {
                throw new GeneralBadRequestException("News Can't Accept Image Size More Than " + maxImageSizeAllowed);
            }
        });
    }

    private void validateVideoSize(final List<Part> videos) {
        if (CollectionUtils.isEmpty(videos)) {
            return;
        }
        final String maxVideoSizeAllowed = fileValidation.getMaxVideoSizeAllowed();
        final DataSize videoMaxAllowedDataSize = DataSize.parse(maxVideoSizeAllowed);
        videos.forEach(video -> {
            final DataSize dataSizeOfThisVideo = DataSize.ofBytes(video.getSize());

            if (dataSizeOfThisVideo.compareTo(videoMaxAllowedDataSize) > 0) {
                throw new GeneralBadRequestException("News Can't Accept Video Size More Than " + maxVideoSizeAllowed);
            }
        });
    }

    private void validateAudioSize(final List<Part> audios) {
        if (CollectionUtils.isEmpty(audios)) {
            return;
        }
        final String maxAudioSizeAllowed = fileValidation.getMaxAudioSizeAllowed();
        final DataSize audioMaxAllowedDataSize = DataSize.parse(maxAudioSizeAllowed);
        audios.forEach(audio -> {
            final DataSize dataSizeOfThisAudio = DataSize.ofBytes(audio.getSize());

            if (dataSizeOfThisAudio.compareTo(audioMaxAllowedDataSize) > 0) {
                throw new GeneralBadRequestException("News Can't Accept Audio Size More Than " + maxAudioSizeAllowed);
            }
        });
    }

    private void validateDocumentSize(final List<Part> documents) {
        if (CollectionUtils.isEmpty(documents)) {
            return;
        }
        final String maxDocumentSizeAllowed = fileValidation.getMaxDocumentSizeAllowed();
        final DataSize documentMaxAllowedDataSize = DataSize.parse(maxDocumentSizeAllowed);
        documents.forEach(document -> {
            final DataSize dataSizeOfThisDocument = DataSize.ofBytes(document.getSize());

            if (dataSizeOfThisDocument.compareTo(documentMaxAllowedDataSize) > 0) {
                throw new GeneralBadRequestException("News Can't Accept Document Size More Than " + maxDocumentSizeAllowed);
            }
        });
    }

    private void validateNonMultipartRequest(final HttpServletRequest httpServletRequest) {
        DataSize maxAllowedSizeOfNonPartRequest = DataSize.parse(fileValidation.getNonMultipartRequestSizeAllowed());
        DataSize sizeOfCurrentQuestion = DataSize.ofBytes(httpServletRequest.getContentLengthLong());

        if (sizeOfCurrentQuestion.compareTo(maxAllowedSizeOfNonPartRequest) > 0) {
            throw new GeneralBadRequestException("Too Large Request, Limit Is " +
                    fileValidation.getNonMultipartRequestSizeAllowed());
        }
    }

    private void validateMultipartWholeRequestSize(final HttpServletRequest httpServletRequest) {
        DataSize maxRequestSizeAllowedForQuestion = DataSize.parse(fileValidation.getMultipartRequestSizeAllowed());
        DataSize requestSizeOfCurrentQuestion = DataSize.ofBytes(httpServletRequest.getContentLengthLong());

        if (requestSizeOfCurrentQuestion.compareTo(maxRequestSizeAllowedForQuestion) > 0) {
            throw new GeneralBadRequestException("News Request Can't Exceed Size Limit Of " +
                    fileValidation.getMultipartRequestSizeAllowed());
        }
    }
}
