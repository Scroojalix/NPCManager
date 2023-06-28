package io.github.scroojalix.npcmanager.storage.implementation;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import org.bson.Document;

import com.google.common.base.Strings;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.storage.misc.JsonParser;
import io.github.scroojalix.npcmanager.storage.misc.StorageImplementation;
import io.github.scroojalix.npcmanager.utils.Messages;
import io.github.scroojalix.npcmanager.utils.Settings;

public class MongoStorage implements StorageImplementation.RemoteStorage {
    
    private final NPCMain main;
    private final String address;
    private final String databaseName;
    private final String collectionName;
    private final String username;
    private final String password;

    private final int connectionTimeout;
    private final boolean useSSL;
    private final String connectionString;

    private MongoClient client;
    private MongoDatabase database;

    public MongoStorage(NPCMain main) {
        this.main = main;
        this.address = Settings.DATABASE_ADDRESS.get();
		this.databaseName = Settings.DATABASE_NAME.get();
        this.collectionName = Settings.DATABASE_TABLE_NAME.get();
		this.username = Settings.DATABASE_USERNAME.get();
        this.password = Settings.DATABASE_PASSWORD.get();
        
        this.connectionTimeout = Settings.DATABASE_CONNECTION_TIMEOUT.get();
        this.useSSL = Settings.DATABASE_USE_SSL.get();

        this.connectionString = Settings.MONGODB_CONNECTION_STRING.get();
    }

    @Override
    public @Nonnull String getImplementationName() {
        return "MongoDB";
    }

    @Override
    public boolean isConnected() {
        return client != null;
    }

    @Override
    public boolean exists(@Nonnull String name) {
        if (!isConnected()) return false;
        try {
            MongoCollection<Document> c = this.database.getCollection(this.collectionName);
            return c.countDocuments(new Document("name", name)) == 1;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean init() {
        disableLogging();
        if (!Strings.isNullOrEmpty(this.connectionString)) {
            this.client = new MongoClient(new MongoClientURI(this.connectionString));
        } else {
            MongoCredential credential = null;
            if (!Strings.isNullOrEmpty(this.username)) {
                credential = MongoCredential.createCredential(
                    this.username,
                    this.databaseName,
                    Strings.isNullOrEmpty(this.password) ? null : this.password.toCharArray()
                );
            }
                
            String[] addressSplit = this.address.split(":");
            String host = addressSplit[0];
            int port = addressSplit.length > 1 ? Integer.parseInt(addressSplit[1]) : 27017;
            ServerAddress address = new ServerAddress(host, port);
            
            MongoClientOptions options = MongoClientOptions
                .builder()
                .sslEnabled(useSSL)
                .connectTimeout(connectionTimeout*1000)
                .build();
                
            if (credential == null) {
                this.client = new MongoClient(address, options);
            } else {
                this.client = new MongoClient(address, credential, options);
            }
        }
            
        this.database = this.client.getDatabase(databaseName);
        return this.database != null;
    }
        
    private void disableLogging() {
        //Small temporary workaround for when SLF4J is in the classpath - usually on paper servers.
        try {
            Class.forName("org.slf4j.Logger");
            main.sendDebugMessage(Level.WARNING, "Could not disable logging for MongoDB as slf4j is in the classpath.");
        } catch (ClassNotFoundException e) {
            Logger.getLogger("org.mongodb.driver.cluster").setLevel(java.util.logging.Level.OFF);
            Logger.getLogger("org.mongodb.driver.connection").setLevel(java.util.logging.Level.OFF);
        }
    }

    @Override
    public boolean shutdown() {
        if (this.client == null) return false;
        this.client.close();
        return true;
    }

    @Override
    public boolean saveNPC(@Nonnull NPCData data) {
        MongoCollection<Document> c = this.database.getCollection(this.collectionName);
        Document doc = Document.parse(JsonParser.toJson(data, true));
        return c.replaceOne(new Document("name", data.getName()), doc, new ReplaceOptions().upsert(true)).wasAcknowledged();
    }

    @Override
    public boolean removeNPC(@Nonnull String name) {
        MongoCollection<Document> c = this.database.getCollection(collectionName);
        return c.deleteOne(new Document("name", name)).wasAcknowledged();
    }

    @Override
    public boolean restoreAllNPCs() {
        MongoCollection<Document> c = this.database.getCollection(collectionName);
        for (Document doc : c.find()) {
            try {
                NPCData data = JsonParser.fromJson(doc.get("name").toString(), doc.toJson(), true);
                if (data != null) {
                    main.npc.spawnNPC(data);
                }
            } catch (Exception e) {
                Messages.printNPCRestoreError(main, doc.get("name").toString(), e);
            }
        }
        return true;
    }

}