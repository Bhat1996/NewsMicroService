package com.example.NewsComponent.validations.helper;


import com.example.NewsComponent.enums.ValueFillingOption;
import com.example.NewsComponent.enums.FileType;
import com.example.NewsComponent.exceptions.GeneralBadRequestException;
import org.springframework.stereotype.Service;

import javax.servlet.http.Part;
import java.util.Objects;

import static com.example.NewsComponent.utils.Not.not;

@Service
public class FileValidator {

    private final FileTypeChecker fileTypeChecker;

    public FileValidator() {
        ImageFileTypeChecker imageFileTypeChecker = new ImageFileTypeChecker();
        AudioFileTypeChecker audioFileTypeChecker = new AudioFileTypeChecker();
        VideoFileTypeChecker videoFileTypeChecker = new VideoFileTypeChecker();
        DocumentFileTypeChecker documentFileTypeChecker = new DocumentFileTypeChecker();
        InvalidFileTypeChecker invalidFileTypeChecker = new InvalidFileTypeChecker();

        imageFileTypeChecker.setNextChain(audioFileTypeChecker);
        audioFileTypeChecker.setNextChain(videoFileTypeChecker);
        videoFileTypeChecker.setNextChain(documentFileTypeChecker);
        documentFileTypeChecker.setNextChain(invalidFileTypeChecker);

        this.fileTypeChecker = imageFileTypeChecker;
    }

    public void validateFileAs(final FileType fileType,
                               final Part part,
                               final ValueFillingOption valueFillingOption) {
        fileTypeChecker.validate(fileType, part, valueFillingOption);
    }

    @SuppressWarnings("unused")
    private interface FileTypeChecker {
        void setNextChain(FileTypeChecker chain);
        void validate(FileType fileType, Part part, ValueFillingOption valueFillingOption);
    }

    @SuppressWarnings("unused")
    private static class ImageFileTypeChecker implements FileTypeChecker {

        private FileTypeChecker fileTypeChecker;

        @Override
        public void setNextChain(FileTypeChecker nextChain) {
            this.fileTypeChecker = nextChain;
        }

        @Override
        public void validate(FileType fileType, Part part, ValueFillingOption valueFillingOption) {
            if (not (fileType.equals(FileType.IMAGE))) {
                fileTypeChecker.validate(fileType, part, valueFillingOption);
                return;
            }

            if (Objects.isNull(part) && valueFillingOption.equals(ValueFillingOption.NON_MANDATORY)) return;
            if (Objects.isNull(part) && valueFillingOption.equals(ValueFillingOption.MANDATORY)) {
                throw new GeneralBadRequestException("Image Not Provided..!");
            }

            String contentType = part.getContentType();
            if (not (contentType.contains("image"))) {
                throw new GeneralBadRequestException("Required Image Type File, Provided " + getPartType(contentType));
            }
        }
    }

    @SuppressWarnings("unused")
    private static class AudioFileTypeChecker implements FileTypeChecker {

        private FileTypeChecker fileTypeChecker;

        @Override
        public void setNextChain(FileTypeChecker nextChain) {
            this.fileTypeChecker = nextChain;
        }

        @Override
        public void validate(FileType fileType, Part part, ValueFillingOption valueFillingOption) {
            if (not (fileType.equals(FileType.AUDIO))) {
                fileTypeChecker.validate(fileType, part, valueFillingOption);
                return;
            }

            if (Objects.isNull(part) && valueFillingOption.equals(ValueFillingOption.NON_MANDATORY)) return;
            if (Objects.isNull(part) && valueFillingOption.equals(ValueFillingOption.MANDATORY)) {
                throw new GeneralBadRequestException("Audio Not Provided..!");
            }

            String contentType = part.getContentType();
            if (not (contentType.contains("audio"))) {
                throw new GeneralBadRequestException("Required Audio Type File, Provided " + getPartType(contentType));
            }
        }
    }

    @SuppressWarnings("unused")
    private static class VideoFileTypeChecker implements FileTypeChecker {

        private FileTypeChecker fileTypeChecker;

        @Override
        public void setNextChain(FileTypeChecker nextChain) {
            this.fileTypeChecker = nextChain;
        }

        @Override
        public void validate(FileType fileType, Part part, ValueFillingOption valueFillingOption) {
            if (not (fileType.equals(FileType.VIDEO))) {
                fileTypeChecker.validate(fileType, part, valueFillingOption);
                return;
            }

            if (Objects.isNull(part) && valueFillingOption.equals(ValueFillingOption.NON_MANDATORY)) return;
            if (Objects.isNull(part) && valueFillingOption.equals(ValueFillingOption.MANDATORY)) {
                throw new GeneralBadRequestException("Video Not Provided..!");
            }

            String contentType = part.getContentType();
            if (not (contentType.contains("video"))) {
                throw new GeneralBadRequestException("Required Video Type File, Provided " + getPartType(contentType));
            }
        }
    }

    @SuppressWarnings("unused")
    private static class DocumentFileTypeChecker implements FileTypeChecker {

        private FileTypeChecker fileTypeChecker;

        @Override
        public void setNextChain(FileTypeChecker nextChain) {
            this.fileTypeChecker = nextChain;
        }

        @Override
        public void validate(FileType fileType, Part part, ValueFillingOption valueFillingOption) {
            if (not (fileType.equals(FileType.DOCUMENT))) {
                fileTypeChecker.validate(fileType, part, valueFillingOption);
                return;
            }

            if (Objects.isNull(part) && valueFillingOption.equals(ValueFillingOption.NON_MANDATORY)) return;
            if (Objects.isNull(part) && valueFillingOption.equals(ValueFillingOption.MANDATORY)) {
                throw new GeneralBadRequestException("Document Not Provided..!");
            }

            String contentType = part.getContentType();
            if (contentType.contains("image") || contentType.contains("audio") || contentType.contains("video")) {
                throw new GeneralBadRequestException("Required Document Type File, Provided " + getPartType(contentType));
            }
        }
    }

    @SuppressWarnings("unused")
    private static class InvalidFileTypeChecker implements FileTypeChecker {

        @SuppressWarnings("unused")
        private FileTypeChecker fileTypeChecker;

        @Override
        public void setNextChain(FileTypeChecker nextChain) {
            throw new RuntimeException("Not Allowed..!");
        }

        @Override
        public void validate(FileType fileType, Part part, ValueFillingOption valueFillingOption) {
            throw new GeneralBadRequestException(fileType + " Not Supported Yet..!");
        }
    }

    private static String getPartType(String contentType) {
        if (contentType.contains("image")) {
            return "image";
        } else if (contentType.contains("audio")) {
            return "audio";
        } else if (contentType.contains("video")) {
            return "video";
        } else {
            return "doc";
        }
    }
}
