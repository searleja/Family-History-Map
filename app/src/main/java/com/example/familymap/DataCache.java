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

    private final Map<String, Person> allPeople = new HashMap<>();
    private final Map<String, Event> allEvents = new HashMap<>();

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
    public boolean insertPeople(JSONArray persons, String personID) {
        Gson g = new Gson();
        try {
            for (int i = 0; i < persons.length(); i++) {
                Person person = g.fromJson(persons.getString(i), Person.class);
                allPeople.put(person.getPersonID(), person);
            }
            setPaternalSides(personID);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void setPaternalSides(String personID) {
        Person current = findPerson(personID);
        Person father = findPerson(current.getFatherID());
        Person mother = findPerson(current.getMotherID());
        if (father != null) {
            fatherSideMales.add(father);
            motherSideFemales.add(mother);
            Person temp = findPerson(father.getFatherID());
            while (temp != null) {
                fatherSideMales.add(temp);
                temp = findPerson(temp.getFatherID());
            }
            temp = findPerson(father.getMotherID());
            while (temp != null) {
                fatherSideFemales.add(temp);
                temp = findPerson(temp.getMotherID());
            }

            //mothers side
            temp = findPerson(mother.getFatherID());
            while (temp != null) {
                motherSideMales.add(temp);
                temp = findPerson(temp.getFatherID());
            }
            temp = findPerson(mother.getMotherID());
            while (temp != null) {
                motherSideFemales.add(temp);
                temp = findPerson(temp.getMotherID());
            }
        }
    }

    //for login assignment (easier)
    public Person findPerson(String id) {
        for (String s : allPeople.keySet()) {
            Person p = allPeople.get(s);
            if (p.getPersonID().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public boolean insertEvents(JSONArray events, String personID) {
        Gson g = new Gson();
        try {
            for (int i = 0; i < events.length(); i++) {
                Event event = g.fromJson(events.getString(i), Event.class);
                allEvents.put(event.getEventID(), event);
            }
            Log.d("EVENTS", String.valueOf(allEvents.size()));
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Map<String, Event> getAllEvents() {
        return allEvents;
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
