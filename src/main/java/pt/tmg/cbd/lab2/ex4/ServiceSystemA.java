package pt.tmg.cbd.lab2.ex4;

import com.mongodb.client.*;
import org.bson.Document;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ServiceSystemA {
    private static int DEFAULT_LIMIT = 5;
    private static int DEFAULT_TIMESLOT = 30;

    private static final MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private static MongoDatabase db = mongoClient.getDatabase("cbd");


    private static final String[] available_prods = {"car", "bicycle", "ball", "hat", "fridge", "pizza", "water", "milk"};

    //9private static final Jedis jedis = new Jedis();
    private static int limit;
    private static int timeslot;

    public static void main(String[] args) throws IOException {
        MongoCollection<Document> collection = db.getCollection("atendimentos");
        collection.drop();
        db.createCollection("atendimentos");
        collection = db.getCollection("atendimentos");

        //set the variables
        setLimit(DEFAULT_LIMIT);
        setTimeslot(DEFAULT_TIMESLOT);

        String username;

        Scanner sc = new Scanner(System.in);

        while(true) {
            System.out.print("Insert client username (Type 'quit' to quit): ");
            String usernameInput = usernameLoop(sc);
            if (checkIfQuit(usernameInput)) {
                break;
            }
            username = usernameInput;

            if (!userExists(username, collection)){
                addNewUser(username, collection);
            }
            productPurchaseLoop(sc, username, collection);
        }

    }

    public static String usernameLoop(Scanner sc){
        String usernameInput = sc.nextLine();
        while(usernameInput.isEmpty()) {
            System.out.println("Error. You must insert a client username.");
            System.out.print("Insert client username (Type 'quit' to quit): ");
            usernameInput = sc.nextLine();
        }
        return usernameInput;
    }

    public static boolean userExists(String usernameInput, MongoCollection<Document> collection){
        if (collection.countDocuments()==0) {
            return false;
        }
        MongoCursor<Document> cursor = collection.find(new Document("username", usernameInput)).iterator();
        if (cursor.hasNext()){
            return true;
        }
        return false;
    }

    public static void registarProduto(String username, String produto, MongoCollection<Document> collection) {
        long currentTime = System.currentTimeMillis() / 1000;
        Document document;
        boolean newUser = false;

        document = getUser(username, collection);

        if (document == null || document.get("compras") == null) {
            newUser = true;
        }

        if (!newUser) {
            List<Document> compras = (List<Document>) document.get("compras");

            long recentPurchases = compras.stream()
                    .filter(compra -> (currentTime - compra.getLong("time")) <= timeslot)
                    .count();

            if (recentPurchases >= limit) {
                System.err.println("[" + username + "] You have exceeded the limit of " + limit + " products in " + timeslot + " seconds.\n");
                return;
            }
        }

        collection.updateOne(Filters.eq("username", username),
                Updates.push("compras", new Document("produto", produto).append("time", currentTime)));

        System.out.println("[" + username + "] Product: " + produto + " purchased successfully.\n ");
    }

    public static Document addNewUser(String username, MongoCollection<Document> collection) {
        Document document = new Document("username", username)
                .append("compras", List.of());
        collection.insertOne(document);

        return document;
    }

    public static Document getUser(String username, MongoCollection<Document> collection){
        FindIterable<Document> iterable = collection.find(new Document("username", username));
        return iterable.first();
    }

    public static Boolean checkIfQuit(String usernameInput){
        return usernameInput.equalsIgnoreCase("quit");
    }

    public static void productPurchaseLoop(Scanner sc, String username, MongoCollection<Document> collection){
        System.out.println("Hi " + username);
        System.out.println("\nHere is the list of available products today: ");
        availableProdsMenu();
        System.out.println("Insert the id of the product to buy. (Type 'exit' to quit)");

        while (sc.hasNextLine()){
            String line = sc.nextLine();
            if (line.equals("exit")) {
                System.out.println("Thank you!\n");
                return;
            }
            else {
                int id = getProductId(line);
                if (id == 0){
                    System.out.println("Error. You must insert a valid client id.\n ");
                    System.out.println("Insert the id of the product to buy. (Type 'exit' to quit)");
                    continue;
                }
                registarProduto(username, available_prods[id-1], collection);
            }
            System.out.println("Insert the id of the product to buy. (Type 'exit' to quit)");
        }
    }

    public static void availableProdsMenu(){
        for (int i = 0; i < available_prods.length; i++) {
            System.out.println((i+1) + ". " + available_prods[i]);
        }
    }

    public static int getProductId(String id_str){
        id_str = id_str.strip();
        try {
            int id = Integer.parseInt(id_str);
            if (id >= 1 && id <= available_prods.length) {
                return id;
            }
            return 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void setLimit(int newLimit){
        System.out.println("Purchase limit set to " + newLimit + " products.");
        limit = newLimit;
    }

    public static int getLimit(){
        return limit;
    }

    public static void setTimeslot(int newTimeslot){
        System.out.println("Purchase time set to " + newTimeslot + " seconds.");
        timeslot = newTimeslot;
    }

    public static int getTimeslot(){
        return timeslot;
    }
}
