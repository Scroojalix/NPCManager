package io.github.scroojalix.npcmanager.common.storage.implementation;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Strings;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;

import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.common.npc.NPCData;
import io.github.scroojalix.npcmanager.common.storage.implementation.interfaces.RemoteStorage;
import io.github.scroojalix.npcmanager.common.storage.implementation.interfaces.StorageImplementation;
import io.github.scroojalix.npcmanager.common.storage.misc.JsonParser;

public class MongoStorage implements StorageImplementation, RemoteStorage {
    
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
        FileConfiguration config = main.getConfig();
        this.address = config.getString("data.address");
        this.databaseName = config.getString("data.database");
        this.collectionName = config.getString("data.table-name");
        this.username = config.getString("data.username");
        this.password = config.getString("data.password");

        this.connectionTimeout = config.getInt("data.connection-timeout");
        this.useSSL = config.getBoolean("data.useSSL");
        this.connectionString = config.getString("data.mongodb-connection-string");
    }

    @Override
    public String getImplementationName() {
        return "MongoDB";
    }

    @Override
    public boolean isConnected() {
        return client != null;
    }

    @Override
    public boolean exists(String name) {
        if (!isConnected())
            return false;
        try {
            MongoCollection<Document> c = this.database.getCollection(this.collectionName);
            return c.countDocuments(new Document("name", name)) == 1;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public void init() {
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
        disableLogging();
    }
        
    private void disableLogging() {
        //Small temporary workaround for when SLF4J is in the classpath - usually on paper servers.
        try {
            Class.forName("org.slf4j.Logger");
            main.log(Level.WARNING, "Could not disable logging for MongoDB as slf4j is in the classpath.");
        } catch (ClassNotFoundException e) {
            Logger.getLogger("org.mongodb.driver.cluster").setLevel(java.util.logging.Level.OFF);
            Logger.getLogger("org.mongodb.driver.connection").setLevel(java.util.logging.Level.OFF);
        }
    }

    @Override
    public void shutdown() {
        if (this.client != null) {
            this.client.close();
        }
    }

    @Override
    public void saveNPC(NPCData data) {
        MongoCollection<Document> c = this.database.getCollection(this.collectionName);
        Document doc = Document.parse(JsonParser.toJson(data, true));
        c.replaceOne(new Document("name", data.getName()), doc, new ReplaceOptions().upsert(true));
    }

    @Override
    public void removeNPC(String name) {
        MongoCollection<Document> c = this.database.getCollection(collectionName);
        c.deleteOne(new Document("name", name));
    }

    @Override
    public void restoreNPCs() {
        MongoCollection<Document> c = this.database.getCollection(collectionName);
        for (Document doc : c.find()) {
            NPCData data = JsonParser.fromJson(doc.get("name").toString(), doc.toJson(), true);
            if (data != null) {
                main.npc.spawnNPC(data);
            }
        }
    }

}