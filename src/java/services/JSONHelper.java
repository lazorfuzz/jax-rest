/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;

/**
 *
 * @author leon
 */
public class JSONHelper {
    ArrayList<Item> itemList;
    Item s1, s2, s3;
    
    public JSONHelper() {
        itemList = new ArrayList<>();
        initializeItems();
    }
    
    public void update(int id, String content) {
        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject obj = reader.readObject();
        Item item = getItemById(id);
        int newId = obj.getInt("id", item.getId());
        String newName = obj.getString("name", item.getName());
        String newDesc = obj.getString("description", item.getDescription());
        String newImage = obj.getString("image", item.getImage());
        item.setId(newId);
        item.setName(newName);
        item.setDescription(newDesc);
        item.setImage(newImage);
        objToJSON();
    }
    
    public void delete(int id) {
        Item item = getItemById(id);
        System.out.println("REMOVING " + item.getName());
        itemList.remove(item);
        objToJSON();
    }
    
    public Item getItemById(int id) {
        Item requestedItem = itemList.stream().filter((item) -> {
            return item.getId() == id;
        }).findFirst().get();
        return requestedItem;
    }
    
    public String getItemJSONById(int id) {
        Item requestedItem = itemList.stream().filter((item) -> {
            return item.getId() == id;
        }).findFirst().get();
        if (requestedItem == null) return null;
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonObjectBuilder obj = factory.createObjectBuilder();
        obj.add("id", requestedItem.getId())
                .add("name", requestedItem.getName())
                .add("description", requestedItem.getDescription())
                .add("image", requestedItem.getImage());
        return obj.build().toString();
    }
    
    public String objToJSON() {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonArrayBuilder list = factory.createArrayBuilder();
        itemList.stream().forEach((Item item) -> {
            list.add(factory.createObjectBuilder()
                    .add("id", item.getId())
                    .add("name", item.getName())
                    .add("description", item.getDescription())
                    .add("image", item.getImage()));
        });
        JsonArray built = list.build();
        try (OutputStream out = new FileOutputStream("Items.json"); JsonWriter writer = Json.createWriter(out)) {
            writer.writeArray(built);
            return built.toString();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JSONHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JSONHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private void initializeItems() {
        try (InputStream in = new FileInputStream("Items.json"); JsonReader reader = Json.createReader(in)) {
            JsonArray array = reader.readArray();
            for (int i = 0; i < array.size(); i++) {
                JsonObject obj = array.getJsonObject(i);
                itemList.add(new Item(obj.getInt("id"), obj.getString("name"), obj.getString("description"), obj.getString("image")));
            }
        } catch (IOException ex) {
            Logger.getLogger(JSONHelper.class.getName()).log(Level.SEVERE, null, ex);
            s1 = new Item(1, "Nikon Camera", "Nikon p510 CoolPix, Rarely Used $237,Listed on Mercari. Free shipping in the USA.", "https://i.redd.it/8bnm2gjijj731.jpg");
            s2 = new Item(2, "iPhone XR", "iPhone XR Blue Up For Sale... Anyone interested Don't be afraid to send me a message. ( I can include more pictures) Thanks.", "https://i.redd.it/enqs061x38731.jpg");
            s3 = new Item(3, "Yeezys", "Yeezy 700 vanta for sale . Looking to get back what I paid for them £100 these are UK size 8 but they are to tight for my feet. PM for more info", "https://i.redd.it/rl6m1bby4q631.jpg");
            itemList.add(s1);
            itemList.add(s2);
            itemList.add(s3);
        }
    }
}
