package pt.tmg.cbd.lab2.ex4;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import redis.clients.jedis.Jedis;
import org.bson.Document;

public class Ex4c {
    private static final Jedis jedis = new Jedis();
    public static String OBJECT = "objects";

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost", 6379);

        //test redis
        long redis_start1 = System.currentTimeMillis();
        jedis.set(OBJECT, "object1");
        long redis_end1 = System.currentTimeMillis();

        long redis_start2 = System.currentTimeMillis();
        jedis.get(OBJECT);
        long redis_end2 = System.currentTimeMillis();

        long redis_start3 = System.currentTimeMillis();
        jedis.del(OBJECT);
        long redis_end3 = System.currentTimeMillis();

        jedis.close();

        System.out.println("------------- Redis results --------------");
        System.out.println("Write operation time: " + (redis_end1 - redis_start1) + " ms");
        System.out.println("Read operation time: " + (redis_end2 - redis_start2) + " ms");
        System.out.println("Remove operation time: " + (redis_end3 - redis_start3) + " ms");

        //test mongoDB
        try(MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")){

            MongoDatabase mongoDatabase = mongoClient.getDatabase("cbd");
            MongoCollection<Document> collection = mongoDatabase.getCollection("test");

            long mongodb_start1 = System.currentTimeMillis();
            collection.insertOne(new Document("object", "object1"));
            long mongodb_end1 = System.currentTimeMillis();

            long mongodb_start2 = System.currentTimeMillis();
            collection.find(new Document("object", "object1"));
            long mongodb_end2 = System.currentTimeMillis();

            long mongodb_start3 = System.currentTimeMillis();
            collection.deleteOne(new Document("object", "object1"));
            long mongodb_end3 = System.currentTimeMillis();

            System.out.println("\n------------- Mongo results --------------");
            System.out.println("Write operation time: " + (mongodb_end1 - mongodb_start1) + " ms");
            System.out.println("Read operation time: " + (mongodb_end2 - mongodb_start2) + " ms");
            System.out.println("Remove operation time: " + (mongodb_end3 - mongodb_start3) + " ms");

        }
    }
}


/*
------------- Redis results --------------
Write operation time: 13 ms
Read operation time: 1 ms
Remove operation time: 0 ms

------------- Mongo results --------------
Write operation time: 48 ms
Read operation time: 2 ms
Remove operation time: 3 ms
 */