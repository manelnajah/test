package com.example.securecontacts;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ContactStorage {
    private static final String FILENAME = "contacts.json";
    private Context context;

    public ContactStorage(Context context) {
        this.context = context;
    }

    public List<Contact> loadContacts() {
        List<Contact> list = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();
            fis.close();

            String content = sb.toString();
            if (content.isEmpty()) return list;

            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(Contact.fromJson(o));
            }
        } catch (Exception e) {
            // fichier absent ou erreur -> retourne liste vide
            e.printStackTrace();
        }
        return list;
    }

    public void saveContacts(List<Contact> contacts) {
        try {
            JSONArray arr = new JSONArray();
            for (Contact c : contacts) arr.put(c.toJson());
            String data = arr.toString();

            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
