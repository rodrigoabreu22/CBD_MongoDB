package pt.tmg.cbd.lab2.ex3.a;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SimpleQueries {
    public static void main(String[] args) {

        try {
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

            MongoDatabase database = mongoClient.getDatabase("cbd");
            MongoCollection<Document> collection = database.getCollection("restaurants");


            //create restaurant document
            Document restaurant = new Document("nome", "Tasquinha do bito")
                    .append("localidade", "Aveiro")
                    .append("gastronomia", "Portuguese")
                    .append("localidade", "Aveiro")
                    .append("address", new Document("building", "2000")
                            .append("coord", List.of(-10.00, 10.00))
                            .append("rua", "Rua da pega")
                            .append("zipcode", "9020"))
                    .append("grades", List.of(new Document("date", new Document("$date", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()))).append("grade", "A").append("score", 2),
                            new Document("date", new Document("$date", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()))).append("grade", "B").append("score", 3))
                    );

        if(testInsertRestaurant(restaurant,collection)) {
            System.out.println("Operação de inserção bem sucedida.");
        }

        //find restaurant to alter and then remove
        Document restaurantToAlter = new Document("nome", "Tasquinha do bito");
        Document restaurantToRemove = new Document("nome", "Tasquinha do bito");

        //update query
        if(testEditRestaurant(restaurantToAlter, collection)){
            System.out.println("Operação de atualização bem sucedida.");
        }

        //delete query
        if(testRemoveRestaurant(restaurantToRemove,collection)) {
            System.out.println("Operação de remoção bem sucedida.");
        }

        }catch(Exception e) {
            System.err.println("Erro ao estabelecer conexão com o servidor.");
        }
    }

    public static boolean testInsertRestaurant(Document restaurant, MongoCollection<Document> collection) {
        try {
            collection.insertOne(restaurant);
            return true;
        } catch (Exception e) {
            System.err.println("Erro na operação de inserção de um restaurante.");
            return false;
        }
    }

    public static boolean testEditRestaurant(Document restaurant, MongoCollection<Document> collection) {
        try{
            collection.updateOne(restaurant, new Document("$set", new Document("gastronomia", "Lagarto cozinhado")));
            return true;
        }catch(Exception e){
            System.err.println("Erro na operação de atualização de um restaurante.");
            return false;
        }
    }

    public static boolean testRemoveRestaurant(Document restaurant, MongoCollection<Document> collection) {
        try{
            collection.deleteOne(restaurant);
            return true;
        }catch(Exception e){
            System.err.println("Erro na operação de remoção de um restaurante.");
            return false;
        }
    }
}
