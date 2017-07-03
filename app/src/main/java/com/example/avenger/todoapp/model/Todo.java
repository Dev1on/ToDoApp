package com.example.avenger.todoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class Todo implements Serializable {

    private static final long serialVersionUID = -6410064189686738560L;

    private long id;

    private String name;
    private String description;

    private long expiry;
    private boolean done;
    private boolean favourite;
    private List<String> contacts;
    private Location location;

    public Todo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Todo todo = (Todo) o;

        if (id != todo.id) return false;
        if (expiry != todo.expiry) return false;
        if (done != todo.done) return false;
        if (favourite != todo.favourite) return false;
        if (name != null ? !name.equals(todo.name) : todo.name != null) return false;
        if (description != null ? !description.equals(todo.description) : todo.description != null)
            return false;
        if (contacts != null ? !contacts.equals(todo.contacts) : todo.contacts != null)
            return false;
        return location != null ? location.equals(todo.location) : todo.location == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (expiry ^ (expiry >>> 32));
        result = 31 * result + (done ? 1 : 0);
        result = 31 * result + (favourite ? 1 : 0);
        result = 31 * result + (contacts != null ? contacts.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "{Todo " + this.id + " " + this.name + ", " + this.description + this.expiry
                + "}";
    }

    public static class LatLng implements Serializable {

        private double lat;
        private double lng;

        public LatLng() {

        }

        public LatLng(long lat,long lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

    }

    public static class Location implements Serializable {

        private String name;

        private LatLng latlng;

        public Location() {

        }

        public Location(String name,LatLng latlng) {
            this.name = name;
            this.latlng = latlng;
        }

        public LatLng getLatlng() {
            return latlng;
        }

        public void setLatlng(LatLng latlng) {
            this.latlng = latlng;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static Comparator<Todo> doneComparator = Comparator.comparing(Todo::isDone);

    public static Comparator<Todo> dateImportanceComparator = Comparator.comparing(Todo::isDone).thenComparing(Todo::getExpiry).thenComparing(Todo::isFavourite);

    public static Comparator<Todo> importanceDateComparator = Comparator.comparing(Todo::isDone).thenComparing(Todo::isFavourite).thenComparing(Todo::getExpiry);
}