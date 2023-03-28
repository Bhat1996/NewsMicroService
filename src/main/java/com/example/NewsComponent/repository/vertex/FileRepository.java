package com.example.NewsComponent.repository.vertex;

import com.arangodb.entity.MultiDocumentEntity;
import com.example.NewsComponent.dto.internal.FileResults;
import com.example.NewsComponent.utils.ArangoIdUtils;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentUpdateEntity;
import com.arangodb.model.AqlQueryOptions;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.model.DocumentUpdateOptions;
import com.arangodb.springframework.core.ArangoOperations;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.vertex.File;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.example.NewsComponent.metadata.VertexName.FILE;
import static java.util.stream.Collectors.toMap;

@Repository
public class FileRepository {

    @Value("${cloudfront.url}")
    private String cloudFrontUrl;
    private final ObjectMapper objectMapper;
    private final ArangoConverter arangoConverter;
    private final ArangoOperations arangoOperations;

    public FileRepository(ObjectMapper objectMapper,
                          ArangoConverter arangoConverter,
                          ArangoOperations arangoOperations) {
        this.objectMapper = objectMapper;
        this.arangoConverter = arangoConverter;
        this.arangoOperations = arangoOperations;
    }


    public File saveFile(ArangoDatabase arangoDatabase, String transactionId, File file) {
        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection(FILE)
                .insertDocument(arangoConverter.write(file),
                        new DocumentCreateOptions().streamTransactionId(transactionId)
                                .returnNew(true));
        return arangoConverter.read(File.class, createEntity.getNew());

    }

    public File updateFile(ArangoDatabase arangoDatabase,
                           String transactionId,
                           File file) {
        DocumentUpdateEntity<VPackSlice> updateEntity = arangoDatabase.collection(FILE)
                .updateDocument(file.getId(),
                        arangoConverter.write(file),
                        new DocumentUpdateOptions()
                                .streamTransactionId(transactionId).returnNew(true));
        return arangoConverter.read(File.class, updateEntity.getNew());
    }

    public List<File> saveFiles(final ArangoDatabase arangoDatabase,
                                final String transactionId,
                                final List<File> files) {
        if (CollectionUtils.isEmpty(files)) return List.of();

        List<VPackSlice> vPackSlices = files.stream().map(arangoConverter::write).toList();

        MultiDocumentEntity<DocumentCreateEntity<VPackSlice>> multiDocumentEntity = arangoDatabase.collection(FILE)
                .insertDocuments(vPackSlices,
                        new DocumentCreateOptions()
                                .streamTransactionId(transactionId)
                                .returnNew(true));

        List<File> savedFiles = multiDocumentEntity.getDocuments()
                .stream()
                .map(DocumentCreateEntity::getNew)
                .map(vPackSlice -> arangoConverter.read(File.class, vPackSlice))
                .toList();

        Map<String, Object> fileMap = savedFiles.stream().collect(toMap(File::getArangoId, Function.identity()));
        // TODO: 28-03-2023 check here
//        streamPublisher.publish(fileMap);
        return savedFiles;
    }

    public static String getFileIdFilter(final String fileId) {
        final String keyOrId = ArangoIdUtils.isValidArangoId(fileId) ? "_id" : "_key";
        return "FILTER file." + keyOrId + " == '" + fileId + "'";
    }

    public File deleteFile(ArangoDatabase arangoDatabase, String transactionId, String mediaId) {
        String queryTemplate = """
                FOR file IN ${files}
                    ${fileIdFilter}
                    UPDATE {
                        _key: file._key,
                        status: 'DELETED'
                       } IN ${files} RETURN NEW
                """;
        Map<String, String> templateFiller = new HashMap<>();
        templateFiller.put("files", FILE);
        templateFiller.put("fileIdFilter", getFileIdFilter(mediaId));

        StringSubstitutor substitutor = new StringSubstitutor(templateFiller);
        String finalQuery = substitutor.replace(queryTemplate);

        ArangoCursor<File> cursor =
                arangoDatabase.query(finalQuery, new AqlQueryOptions().streamTransactionId(transactionId), File.class);

        try (cursor) {
            return cursor.stream().findFirst().orElseThrow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<FileResults> getFiles(List<String> ids) {
        String query = """
                FOR doc IN news
                FILTER doc._key in ${keys}
                   LET imageUrls= (
                        FOR file IN 1..1
                        OUTBOUND doc
                        newsHasFile
                        FILTER file.fileType == 'image'
                        RETURN {
                            id: file._key,
                            url: CONCAT('${cloudFrontUrl}', "/", file.fileKey),
                            fileType:'image'
                        }
                    )
                   LET videoUrls= (
                        FOR file IN 1..1
                        OUTBOUND doc
                        newsHasFile
                        FILTER file.fileType == 'video'
                        RETURN {
                            id: file._key,
                            url: CONCAT('${cloudFrontUrl}', "/", file.fileKey),
                            fileType:'video'
                        }
                   )
                                
                   LET audioUrls= (
                        FOR file IN 1..1
                        OUTBOUND doc
                        newsHasFile
                        FILTER file.fileType == 'audio'
                        RETURN {
                            id: file._key,
                            url: CONCAT('${cloudFrontUrl}', "/", file.fileKey),
                            fileType:'audio'
                        }
                   )
                                
                   LET documentUrls= (
                        FOR file IN 1..1
                        OUTBOUND doc
                        newsHasFile
                        FILTER file.fileType == 'document'
                        RETURN {
                            id: file._key,
                            url: CONCAT('${cloudFrontUrl}', "/", file.fileKey),
                            fileType:'document'
                        }
                    )
                RETURN {
                    id: doc._key,
                    imageUrls:imageUrls,
                    videoUrls:videoUrls,
                    audioUrls:audioUrls,
                    documentUrls:documentUrls
                }
                """;

        Map<String, String> template = new HashMap<>();
        template.put("cloudFrontUrl", cloudFrontUrl);
        template.put("keys", objectMapper.writeValueAsString(ids) );

        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        String replace = stringSubstitutor.replace(query);

        ArangoCursor<FileResults> cursor = arangoOperations.query(replace, FileResults.class);
       try(cursor){
           return cursor.asListRemaining();
       }catch(IOException e){
           throw new RuntimeException(e);
       }

    }
}
