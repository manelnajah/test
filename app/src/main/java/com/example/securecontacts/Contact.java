package com.example.securecontacts;

import org.json.JSONException;
import org.json.JSONObject;

public class Contact {
    private String name;
    private String phone;

    public Contact() {}

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        try {
            o.put("name", name);
            o.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return o;
    }

    public static Contact fromJson(JSONObject o) {
        try {
            String name = o.optString("name", "");
            String phone = o.optString("phone", "");
            return new Contact(name, phone);
        } catch (Exception e) {
            e.printStackTrace();
            return new Contact("", "");
        }
    }
}
