package com.example.NewsComponent.service.transaction;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.StreamTransactionEntity;
import com.arangodb.model.StreamTransactionOptions;
import com.arangodb.springframework.core.ArangoOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
@Service
public class TransactionalWrapper {
    private final ArangoOperations arangoOperations;

    private ArangoDatabase arangoDatabase;

    @Value("${arango.db.name}")
    private String dbName;

    public TransactionalWrapper(ArangoOperations arangoOperations) {
        this.arangoOperations = arangoOperations;
    }

    public <T> T executeInsideTransaction(Set<String> collectionsNameToActivateTransaction, Action<T> action) {
        arangoDatabase = arangoOperations.driver()
                .db(dbName);
        String transactionId = getTransactionId(collectionsNameToActivateTransaction);
        try {
            T result = action.perform(arangoDatabase, transactionId);
            arangoDatabase.commitStreamTransaction(transactionId);
            return result;
        } catch (RuntimeException runtimeException) {
            if (transactionId != null) {
                arangoDatabase.abortStreamTransaction(transactionId);
            }
            throw runtimeException;
        }
    }

    private String getTransactionId(Set<String> strings) {
        StreamTransactionEntity streamTransactionEntity =
                arangoDatabase.beginStreamTransaction(
                        new StreamTransactionOptions()
                                .writeCollections(strings.toArray(new String[0]))
                );
        return streamTransactionEntity.getId();
    }
}
