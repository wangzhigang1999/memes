package com.bupt.dailyhaha.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.bupt.dailyhaha.aspect.Audit;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;

import java.net.UnknownHostException;
import java.util.Map;

import static java.net.InetAddress.getLocalHost;

public final class MongoAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    static MongoClient mongoClient;
    static String databaseName = "memes";

    static String connectionString = System.getenv().getOrDefault("mongoUri", "");

    String collectionName = "events";

    static String hostname;


    static {
        String env = System.getenv().getOrDefault("env", "dev");
        if (env.equals("prod")) {
            ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
            MongoClientSettings settings = MongoClientSettings.builder().
                    applyConnectionString(new ConnectionString(connectionString)).
                    serverApi(serverApi).build();
            mongoClient = MongoClients.create(settings);
        } else {
            databaseName = "memes-dev";
            mongoClient = MongoClients.create(connectionString);
        }

        try {
            hostname = getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "unknown";
        }

    }


    @Override
    protected void append(ILoggingEvent eventObject) {

        // convert to map
        String formattedMessage = eventObject.getFormattedMessage();
        long timeStamp = eventObject.getTimeStamp();

        //YYYY-MM-DD HH:MM:SS in Asia/Shanghai
        String formattedTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timeStamp));


        mongoClient.getDatabase(databaseName).getCollection(collectionName).insertOne(new Document(
                Map.of(
                        "applicationName", "memes",
                        "instance", Audit.instanceUUID,
                        "hostname", hostname,
                        "timestamp", timeStamp,
                        "formattedTime", formattedTime,
                        "logger", eventObject.getLoggerName(),
                        "level", eventObject.getLevel().toString(),
                        "thread", eventObject.getThreadName(),
                        "message", formattedMessage
                )
        ));

    }
}