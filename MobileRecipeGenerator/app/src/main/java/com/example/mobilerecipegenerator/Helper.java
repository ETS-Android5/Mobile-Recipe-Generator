package com.example.mobilerecipegenerator;

public class Helper {
    public static final String UPLOAD_URL = "http://192.168.1.88:5001/predict";
    //public static final String UPLOAD_URL = "http://10.0.2.2:5000/predict";

    public static String adjustClassificationName(String currentName){
        String newName = currentName;

        if(newName.contains("Apple")) newName = "Apple";
        else if(newName.contains("Avocado")) newName = "Avocado";
        else if(newName.contains("Banana")) newName = "Banana";
        else if(newName.contains("Cherry")) newName = "Cherry";
        else if(newName.contains("Grape")) newName = "Grape";
        else if(newName.contains("Pepper")) newName = "Bell Pepper";
        else if(newName.contains("Mango")) newName = "Mango";
        else if(newName.contains("Peach")) newName = "Peach";
        else if(newName.contains("Pear")) newName = "Pear";
        else if(newName.contains("Pineapple")) newName = "Pineapple";
        else if(newName.contains("Plum")) newName = "Plum";
        else if(newName.contains("Onion")) newName = "Onion";
        else if(newName.contains("Potato")) newName = "Potato";
        else if(newName.contains("Strawberry")) newName = "Strawberry";
        else if(newName.contains("Tomato")) newName = "Tomato";

        return newName;
    }
}
