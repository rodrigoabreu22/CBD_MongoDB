package pt.tmg.cbd.lab2.ex3.d;

import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;

import java.util.*;


public class RestaurantsDAO {
    private final MongoCollection<Document> mongoCollection;
    public static void main(String[] args){
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("cbd");
        MongoCollection<Document> collection = database.getCollection("restaurants");

        RestaurantsDAO restaurantsDAO= new RestaurantsDAO(collection);

        System.out.println("Número de localidades distintas: "+  restaurantsDAO.countLocalidades());

        System.out.println("\nNúmero de restaurantes por localidade: ");
        for (Map.Entry<String, Integer> entry : restaurantsDAO.countRestByLocalidade().entrySet()) {
            System.out.println(" -> " + entry.getKey() + " - " + entry.getValue());
        }

        System.out.println("\nNome de restaurantes contendo 'Park' no nome:");
        for (String res : restaurantsDAO.getRestWithNameCloserTo("Park")) {
            System.out.println(" -> " + res);
        }
    }

    public RestaurantsDAO(MongoCollection<Document> mongoCollection) {
        this.mongoCollection = mongoCollection;
    }

    public int countLocalidades() {
        // TODO: Implement

        int numLocalidades = 0;
        try (MongoCursor<String> cursor = mongoCollection.distinct("localidade", String.class).iterator()) {
            while (cursor.hasNext()) {
                cursor.next();
                numLocalidades++;
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("Failed to count distinct localidades", e);
        }
        return numLocalidades;
    }

    public Map<String, Integer> countRestByLocalidade() {
        // TODO: Implement
        Map<String, Integer> restaurantsByLocalidade = new HashMap<>();
        MongoCursor<Document> cursor = this.mongoCollection.aggregate(Arrays.asList(Aggregates.group("$localidade", Accumulators.sum("numRestaurants",1)))).iterator();

        String restaurantName;
        int numRestaurants;

        try{
            while(cursor.hasNext()){
                Document next = cursor.next();
                restaurantName = (String) next.get("_id");
                numRestaurants = next.getInteger("numRestaurants");
                restaurantsByLocalidade.put(restaurantName, numRestaurants);
            }
        } catch(Exception e) {
            throw new UnsupportedOperationException();
        }

        cursor.close();

        return restaurantsByLocalidade;
    }

    public List<String> getRestWithNameCloserTo(String name) {
        // TODO: Implement

        List<String> restaurants = new ArrayList<>();
        MongoCursor<Document> cursor = this.mongoCollection.find(regex("nome", "."+name+".", "i")).iterator();

        String resName;

        try{
            while(cursor.hasNext()){
                resName = cursor.next().getString("nome");
                restaurants.add(resName);
            }
        } catch(Exception e) {
            cursor.close();
            throw new UnsupportedOperationException();
        }
        cursor.close();

        return restaurants;
    }
}


//output

/*
Número de localidades distintas: 5

Número de restaurantes por localidade:
 -> Queens - 738
 -> Staten Island - 158
 -> Manhattan - 1883
 -> Brooklyn - 684
 -> Bronx - 309

Nome de restaurantes contendo 'Park' no nome:
 -> Morris Park Bake Shop
 -> New Park Pizzeria & Restaurant
 -> New Parkway Restaurant
 -> Sparks Steak House
 -> Rego Park Cafe
 -> Dyker Park Bagels
 -> Nora'S Park Bench Cafe
 -> Forest Park Golf Course
 -> Tribeca Park Cafe
 -> Morris Park Pizza
 -> The Hallmark Of Battery Park City- Dining Room
 -> Central Park Boathouse
 -> The Park Slope Chipshop
 -> Sunset Park Diner & Donuts
 */
