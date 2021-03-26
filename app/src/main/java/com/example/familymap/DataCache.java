package com.example.familymap;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.*;

import Models.*;

public class DataCache {

    private static DataCache instance;

    private AuthToken authToken;

    private Person user;

    private final ArrayList<Person> allPeople = new ArrayList<>();

    private final Set<Person> immediateFamilyMales = new HashSet<>();
    private final Set<Person> immediateFamilyFemales = new HashSet<>();

    private final Set<Person> fatherSideMales = new HashSet<>();
    private final Set<Person> fatherSideFemales = new HashSet<>();
    private final Set<Person> motherSideMales = new HashSet<>();
    private final Set<Person> motherSideFemales = new HashSet<>();

    private final Set<String> eventTypes = new HashSet<>();

    private DataCache() {}

    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    //for login assignment (easier)
    public ArrayList<Person> insertPeople(JSONArray persons) {
        Gson g = new Gson();
        Log.d("RETRIEVAL", "inserting into cache");
        try {
            for (int i = 0; i < persons.length(); i++) {
                Log.d("RETRIEVAL", "loop");
                Person person = g.fromJson(persons.getString(i), Person.class);
                allPeople.add(person);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return allPeople;
    }

    //for login assignment (easier)
    public Person findPerson(String id) {
        for (Person p : allPeople) {
            if (p.getPersonID().equals(id)) {
                return p;
            }
        }
        return null;
    }



    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public Person getUser() {
        return user;
    }

    public void setUser(Person user) {
        this.user = user;
    }

    public Set<Person> getImmediateFamilyMales() {
        return immediateFamilyMales;
    }

    public Set<Person> getImmediateFamilyFemales() {
        return immediateFamilyFemales;
    }

    public Set<Person> getFatherSideMales() {
        return fatherSideMales;
    }

    public Set<Person> getFatherSideFemales() {
        return fatherSideFemales;
    }

    public Set<Person> getMotherSideMales() {
        return motherSideMales;
    }

    public Set<Person> getMotherSideFemales() {
        return motherSideFemales;
    }

    public Set<String> getEventTypes() {
        return eventTypes;
    }
}
