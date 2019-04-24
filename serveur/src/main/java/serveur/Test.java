package serveur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test {


    public static void main(String[] args) {
        // Create a HashMap object called capitalCities
        HashMap<Integer, String> capitalCities = new HashMap<Integer, String>();

        // Add keys and values (Country, City)
        capitalCities.put(1, "London");
        capitalCities.put(2, "Berlin");
        capitalCities.put(3, "Oslo");
        capitalCities.put(4, "Washington DC");

        List<String> values = new ArrayList<String>(capitalCities.values());
        List<Integer> keys = new ArrayList<Integer>(capitalCities.keySet());

        System.out.println(values);
        System.out.println(keys);

    }
}
