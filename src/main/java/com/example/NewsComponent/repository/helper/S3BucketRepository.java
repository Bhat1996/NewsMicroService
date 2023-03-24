package com.example.NewsComponent.repository.helper;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.NewsComponent.exceptions.InternalServerException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.servlet.http.Part;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;

@Repository
public class S3BucketRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(S3BucketRepository.class);

    private final AmazonS3 amazonS3;
    private final String parentProjectName;
    private final String applicationName;

    public S3BucketRepository(final AmazonS3 amazonS3,
                              final @Value("${parent.project.name}") String parentProjectName,
                              final @Value("${application.name}") String applicationName) {
        this.amazonS3 = amazonS3;
        this.parentProjectName = parentProjectName;
        this.applicationName = applicationName;
    }

    public String save(final String bucketName, final Part part) {
        try {
            ObjectMetadata metadata = getObjectMetadata(part);
            String fileKey = getFileKey(part);
            PutObjectRequest putRequest = new PutObjectRequest(bucketName, fileKey, part.getInputStream(), metadata);
            amazonS3.putObject(putRequest);
            return fileKey;
        } catch (IOException e) {
            LOGGER.error("Exception in Saving File of type {} and File name is {}",
                    part.getContentType(),
                    part.getSubmittedFileName(), e);
            throw new InternalServerException("Exception in Saving File " + part.getSubmittedFileName());
        }
    }

    @NotNull
    private ObjectMetadata getObjectMetadata(final Part part) {
        String contentType = part.getContentType();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(part.getSize());
        metadata.setContentType(contentType);
        return metadata;
    }

    @NotNull
    private String getFileKey(final Part part) {
        String mediaFolder = detectInWhichFolderFileShouldSave(part);
        String submittedFileName = part.getSubmittedFileName();
        return parentProjectName +
                "/" +
                applicationName +
                "/" + mediaFolder +
                "/" + new Date().getTime() +
                "_" + getRandomString() +
                "_" + submittedFileName;
    }

    private String detectInWhichFolderFileShouldSave(final Part part) {
        String contentType = part.getContentType();
        if (contentType.contains("image")) {
            return "img";
        } else if (contentType.contains("audio")) {
            return "audio";
        } else if (contentType.contains("video")) {
            return "vod";
        } else {
            return "doc";
        }
    }

    private String getRandomString() {
        StringBuilder otp = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 10; i++) {
            otp.append(secureRandom.nextInt(0, 10));
        }
        return otp.toString();
    }

    public void remove(final String bucketName,
                       final String fileKey) {
        amazonS3.deleteObject(bucketName, fileKey);
    }
}
