package com.example.NewsComponent.repository.vertex;

import com.example.NewsComponent.metadata.VertexName;
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
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class FileRepository {
    private final ArangoConverter arangoConverter;
    private final ArangoOperations arangoOperations;

    public FileRepository(ArangoConverter arangoConverter, ArangoOperations arangoOperations) {
        this.arangoConverter = arangoConverter;
        this.arangoOperations = arangoOperations;
    }

    public File saveFile(ArangoDatabase arangoDatabase, String transactionId, File file) {
        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection(VertexName.FILE)
                .insertDocument(arangoConverter.write(file),
                        new DocumentCreateOptions().streamTransactionId(transactionId)
                                .returnNew(true));
        return arangoConverter.read(File.class, createEntity.getNew());

    }

    public File updateFile(ArangoDatabase arangoDatabase,
                           String transactionId,
                           File file) {
        DocumentUpdateEntity<VPackSlice> updateEntity = arangoDatabase.collection(VertexName.FILE)
                .updateDocument(file.getId(),
                        arangoConverter.write(file),
                        new DocumentUpdateOptions()
                                .streamTransactionId(transactionId).returnNew(true));
        return arangoConverter.read(File.class, updateEntity.getNew());
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
        templateFiller.put("files", VertexName.FILE);
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
}
