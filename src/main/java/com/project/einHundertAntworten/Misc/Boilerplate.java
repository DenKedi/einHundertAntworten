package com.project.einHundertAntworten.Misc;

import com.mongodb.*;
import com.mongodb.client.*;
import com.project.einHundertAntworten.Game.GameObject;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Boilerplate {
    static MongoDatabase database;

    List<GameObject> gameObjects = new ArrayList<>();

    public Boilerplate(){
        String connectionString = "mongodb+srv://kediprg:Kuriboh@webcluster.fy8tk7i.mongodb.net/?retryWrites=true&w=majority";
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Send a ping to confirm a successful connection
                database = mongoClient.getDatabase("100FragenGame");
                database.runCommand(new Document("ping", 1));
                System.out.println("Pinged your deployment. You successfully connected to MongoDB! Database: ");

                MongoCollection<Document> gameobjectsCollection = database.getCollection("gameobjects");

                // Retrieve all documents in the collection
                FindIterable<Document> documents = gameobjectsCollection.find();


                for (Document document : documents) {
                    // Access the fields in each document
                    ObjectId objectId = document.getObjectId("_id"); // Use getObjectId for the _id field
                    String answerText = document.getString("answerText");
                    List<String> questionSet = document.getList("questionSet", String.class);
                    // Perform actions with the retrieved data
                    System.out.println("Object ID: " + objectId);
                    System.out.println("Answer Text: " + answerText);
                    for (String question : questionSet) {
                        System.out.println("Question: " + question);
                    }
                }


            } catch (MongoException e) {
                e.printStackTrace();
            }

        }
    }
}
