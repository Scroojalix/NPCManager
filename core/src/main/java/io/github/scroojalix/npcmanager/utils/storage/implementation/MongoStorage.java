package io.github.scroojalix.npcmanager.utils.storage.implementation;

import java.util.logging.Level;
import java.util.logging.LogManager;

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
import io.github.scroojalix.npcmanager.utils.npc.NPCData;

public class MongoStorage implements StorageImplementation {
    
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
    public boolean isRemote() {
        return true;
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
    }

    private void disableLogging() {
        LogManager l = LogManager.getLogManager();
        l.getLogger("org.mongodb.driver.connection").setLevel(Level.OFF);
        l.getLogger("org.mongodb.driver.management").setLevel(Level.OFF);
        l.getLogger("org.mongodb.driver.cluster").setLevel(Level.OFF);
        l.getLogger("org.mongodb.driver.protocol.insert").setLevel(Level.OFF);
        l.getLogger("org.mongodb.driver.protocol.query").setLevel(Level.OFF);
        l.getLogger("org.mongodb.driver.protocol.update").setLevel(Level.OFF);
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
        Document doc = Document.parse(data.toJson(true)).append("_id", data.getName());
        c.replaceOne(new Document("_id", data.getName()), doc, new ReplaceOptions().upsert(true));
    }

    @Override
    public void removeNPC(String name) {
        MongoCollection<Document> c = this.database.getCollection(collectionName);
        c.deleteOne(new Document("_id", name));
    }

    @Override
    public void restoreNPCs() {
        MongoCollection<Document> c = this.database.getCollection(collectionName);
        for (Document doc : c.find()) {
            NPCData data = NPCData.fromJson(doc.get("_id").toString(), doc.toJson(), true);
            if (data != null) {
                main.npc.spawnNPC(data);
            }
        }
    }

}