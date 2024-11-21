package pt.tmg.cbd.lab2.ex3.b;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Filters.*;

import java.util.List;


public class Index {
    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("cbd");
        MongoCollection<Document> collection = database.getCollection("restaurantes");

        long noIndex_start = System.nanoTime();
        find(collection, eq("localidade", "Bronx"));
        long localidadeSearch = System.nanoTime() - noIndex_start;
        find(collection, eq("gastronomia", "Portuguese"));
        long gastronomiaSearch = System.nanoTime() - localidadeSearch - noIndex_start;
        find(collection, eq("nome", "Morris Park Bake Shop"));
        long nameSearch = System.nanoTime() - localidadeSearch - gastronomiaSearch - noIndex_start;

        String indexLocalidade = collection.createIndex(Indexes.ascending("localidade"));
        String indexGatronomia = collection.createIndex(Indexes.ascending("gastronomia"));
        String indexNome = collection.createIndex(Indexes.text("nome"));

        long noIndex_start2 = System.nanoTime();
        find(collection, eq("localidade", "Bronx"));
        long localidadeSearch2 = System.nanoTime() - noIndex_start2;
        find(collection, eq("gastronomia", "Portuguese"));
        long gastronomiaSearch2 = System.nanoTime() - localidadeSearch2 - noIndex_start2;
        find(collection, eq("nome", "Morris Park Bake Shop"));
        long nameSearch2 = System.nanoTime() - localidadeSearch2 - gastronomiaSearch2 - noIndex_start2;

        System.out.println("------------ Without Index ----------------");
        System.out.println("Times for query find: ");
        System.out.print("Localidade: " + localidadeSearch + "\n");
        System.out.print("Gatronomia: " + gastronomiaSearch + "\n");
        System.out.print("Nome: " + nameSearch + "\n");
        System.out.println("\n------------ With Index ----------------");
        System.out.println("Times for query find: ");
        System.out.print("Localidade: " + localidadeSearch2 + "\n");
        System.out.print("Gatronomia: " + gastronomiaSearch2 + "\n");
        System.out.print("Nome: " + nameSearch2 + "\n");
    }

    public static Iterable<Document> find(MongoCollection<Document> collection, Bson filter) {
        return collection.find(filter);
    }
}

//Output
//------------ Without Index ----------------
//Times for query find:
//Localidade: 2662062 ns
//Gatronomia: 11384 ns
//Nome: 5797 ns
//
//        ------------ With Index ----------------
//Times for query find:
//Localidade: 11454 ns
//Gatronomia: 2794 ns
//Nome: 3352 ns

//The index search was significantly more efficient.