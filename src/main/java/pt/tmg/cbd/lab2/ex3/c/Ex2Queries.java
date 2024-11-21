package pt.tmg.cbd.lab2.ex3.c;

import com.mongodb.client.*;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;


public class Ex2Queries {
    public static void main(String[] args){

        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("cbd");
        MongoCollection<Document> collection = database.getCollection("restaurants");


        //Alinea 4
        long count = collection.countDocuments(new Document("localidade", "Bronx"));
        System.out.println("4. Indique o total de restaurantes localizados no Bronx. \nAnswer: "+ count);

        //Alinea 11
        Bson projection = include("nome", "localidade", "gastronomia");
        MongoCursor<Document> cursor = collection.find(or(eq("gastronomia", "American"),eq("gastronomia", "Chinese"))).projection(projection).iterator();


        System.out.println("\n11. Liste o nome, a localidade e a gastronomia dos restaurantes que pertencem ao Bronx e cuja gastronomia é do tipo \"American\" ou \"Chinese\". \nAnswer:");

        try{
            while(cursor.hasNext()){
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }

        //Alínea 17.
        System.out.println("\n17. Liste nome, gastronomia e localidade de todos os restaurantes ordenando por ordem crescente da gastronomia e, em segundo, por ordem decrescente de localidade.");

        cursor = collection.find().projection(projection).sort(Sorts.orderBy(Sorts.ascending("gastronomia"), Sorts.descending("localidade"))).iterator();

        try{
            while(cursor.hasNext()){
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }

        //Alínea 22
        System.out.println("\n22. Conte o total de restaurante existentes em cada localidade. \nAnswer: ");

        cursor = collection.aggregate(Arrays.asList(Aggregates.group("$localidade", Accumulators.sum("numRestaurants",1)))).iterator();

        try{
            while(cursor.hasNext()){
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }

        //Alínea 20
        System.out.println("\n20. Apresente o nome e número de avaliações (numGrades) dos 3 restaurante com mais avaliações. \nAnswer: ");
        projection = include("nome","numGrades");
        cursor = collection.aggregate(Arrays.asList(Aggregates.unwind("$grades"), Aggregates.group("$restaurant_id", Accumulators.first("nome", "$nome"), Accumulators.sum("numGrades",1)), Aggregates.sort(Sorts.descending("numGrades")), Aggregates.limit(3), Aggregates.project(projection))).iterator();

        try{
            while(cursor.hasNext()){
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }
}
