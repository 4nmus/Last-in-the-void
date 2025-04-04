package com.mygdx.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.helpers.BodyHelper;
import com.mygdx.helpers.Constants;
import com.mygdx.helpers.ContactType;
import com.mygdx.pong.PongGame;
import com.mygdx.screens.GameScreen;
import com.mygdx.pong.PongGame;
import com.mygdx.objects.InteractiveObject;
//import com.mygdx.assets.Texture;
import java.util.Random;

public class Planet extends InteractiveObject{

    private String name;
    private Type type;
    private int tier;
    private int size;
    //private Texture texture;
    Random rand = new Random();


    enum Type {
        Gas,
        Mineral,
        Organic,
        Star,
        Event
    }


    //Set types for easy access
    Type gas = Type.Gas;
    Type min = Type.Mineral;
    Type org = Type.Organic;
    Type star = Type.Star;

    //Manual planet maker, pass through generate to add more data
    public Planet(String name, Type star2, int size, int tier) {
        this.name = name;
        this.type = star2;
        this.size = size;
        this.tier = tier;
    }

    //Empty constructor
    public Planet(){
        name = "test";
    }

    //Method for printing planets for debugging purposes
    public void printPlanet(){
        System.out.println("Name: " + this.name);
        System.out.println("Type: " + this.type);
        System.out.println("Size: " + this.size);
        System.out.println("Tier: " + this.tier);
    }

    //Basic planet consructor for fully random planet with the exception of name and tier which is to be inherited from Star System
    public Planet generatePlanet(String name, int systemTier, int pos) {
        String new_name = name + " " + pos;

        Type planet_type;
        //Generate resource type of the planet
        double typeVal = rand.nextDouble(3);
        if (0 <= typeVal && typeVal < 1) 
            {planet_type = gas;}
        else if (1<= typeVal && typeVal < 2) 
            {planet_type = min;}
        else 
            {planet_type = org;}
        
        //Generates random size for the planet's texture, to be applied later
        int new_size = rand.nextInt(100-1)+1;

        //To be implimented later when some way of tracking player progression exists, potentially inherited from Star System
        int planet_tier = rand.nextInt(systemTier-1) + 1;

        Planet new_planet = new Planet(new_name, planet_type, new_size, planet_tier);

        return new_planet; // Added to ensure the program compiles, can change later

    }

    public static void main(String[] args){
        //Test the generatePlanet method
        Planet test = new Planet();
        Planet generated = test.generatePlanet("System", 3, 1);
        generated.printPlanet();
    }

}