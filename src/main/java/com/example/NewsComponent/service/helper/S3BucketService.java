package com.example.NewsComponent.service.helper;


import com.example.NewsComponent.dto.request.FileKeyWithOriginalName;
import com.example.NewsComponent.repository.helper.S3BucketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Part;
import java.util.ArrayList;
import java.util.List;


import static com.example.NewsComponent.utils.Not.not;

@Service
public class S3BucketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3BucketService.class);

    @Value("${bucket.name}")
    private String bucketName;

    private final S3BucketRepository s3BucketRepository;

    public S3BucketService(final S3BucketRepository s3BucketRepository) {
        this.s3BucketRepository = s3BucketRepository;
    }

    public List<FileKeyWithOriginalName> saveParts(final List<Part> parts) {
        if (parts != null && not (parts.isEmpty())) {
            List<FileKeyWithOriginalName> fileKeyWithOriginalNames = new ArrayList<>();
            for (Part part: parts) {
                try {
                    String fileKey = s3BucketRepository.save(bucketName, part);
                    fileKeyWithOriginalNames.add(new FileKeyWithOriginalName(fileKey, part.getSubmittedFileName()));
                } catch (Exception exception) {
                    LOGGER.error("Exception in Saving Part {} with Content-Type {} and Size is {} to Bucket",
                            part.getSubmittedFileName(), part.getContentType(), part.getSize(), exception);
                    LOGGER.info("Going To Remove All Already Saved Parts From s3");
                    fileKeyWithOriginalNames.forEach(fileKeyWithOriginalName ->
                            removePartSafely(fileKeyWithOriginalName.fileKey()));
                    throw new IllegalStateException("Can't Save Parts in S3");
                }
            }
            return fileKeyWithOriginalNames;
        } else {
            return List.of();
        }
    }

    public void removePartSafely(final String fileKey) {
        try {
            s3BucketRepository.remove(bucketName, fileKey);
        } catch (Exception exception) {
            LOGGER.error("Exception in Removing File From S3, File Key Is {}", fileKey, exception);
        }
    }

}
