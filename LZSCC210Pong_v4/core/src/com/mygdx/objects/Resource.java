package com.mygdx.objects;

public class Resource extends InteractiveObject {

    private String name;
    private Type type;
    private int amount;

    public enum Type {
        Fuel,
        Food,
        ConstructionMaterial,
        Blueprint
    }


    public Resource(String name, String type) {
        this.name = name;
        try {
            this.type = Type.valueOf(type); 
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid resource type: " + type);
            this.type = Type.Food; 
        }
        this.amount = 0;
    }


    // amount 
    public void add(int value) {
        this.amount += value;
    }

    // Remove amount of the resource (returns true if successful)
    public boolean remove(int value) {
        if (amount >= value) {
            this.amount -= value;
            return true;
        }
        return false; 
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    // Override toString() to provide better string representation
    @Override
    public String toString() {
        return name + " (" + type + ") - Amount: " + amount;
    }
}
