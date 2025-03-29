package com.mygdx.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.helpers.Constants;


public class Player{
    private String name;
    private Player player;
    private double health = 100;
	private double fuel = 100;
    private double oxygen = 100;

    private Inventory inventory;
    //private Array<String> inventory = new Array<>();

    enum Stats {
        HEALTH, FUEL, OXYGEN;
    }

    public void setHealth(double val) {
    if (val < 0) {
        this.health = 0;
    } else if (val > 100) {
        this.health = 100;
    } else {
        this.health = val;
    }
}

    public double getHealth() {
        return (double) this.health;
    }

	public void setFuel(double val) {
    if (val < 0) {
        this.fuel = 0;
    } else if (val > 100) {
        this.fuel = 100;
    } else {
        this.fuel = val;
    }
}

    public double getFuel() {
        return (double) this.fuel;
    }

    public void setOxygen(double val) {
    if (val < 0) {
        this.oxygen = 0;
    } else if (val > 100) {
        this.oxygen = 100;
    } else {
        this.oxygen = val;
    }
}

    public double getOxygen() {
        return (double) this.oxygen;
    }    

    public void setName(String playerName) {
    if (playerName != null && !playerName.isEmpty()) {
        this.name = playerName;
    } else {
        System.out.println("Error");
    }
}

	public void updateStat(Stats stat, double val) {
		double old_val;
		double new_val;
		if (stat == Stats.HEALTH) {
			old_val = this.health;
			new_val = old_val + val;
			this.setHealth(new_val);
		}
		else if (stat == Stats.FUEL) {
			old_val = this.fuel;
			new_val = old_val + val;
			this.setFuel(new_val);
		}
		else if (stat == Stats.OXYGEN) {
			old_val = this.oxygen;
			new_val = old_val + val;
			this.setOxygen(new_val);
		}	
	}


    // Interaction with Inventory
    public Inventory getInventory() {
        if (inventory == null) {
            inventory = new Inventory(50);
        }
        return inventory;
    }

    public void addItemToInventory(String item) {
        inventory.addItem(item);
    }

    public void removeItemFromInventory(String item, int quantity) {
        inventory.removeItem(item, quantity);
    }

    public void showInventory() {
		inventory.showInventory();
	}


    public static void main(String[] args){
        Player tester = new Player();

        Stats health = Stats.HEALTH;
        Stats fuel = Stats.FUEL;
        Stats oxygen = Stats.OXYGEN;

        tester.updateStat(health, -40);
        tester.updateStat(fuel, -37.5);
        tester.updateStat(oxygen, 30);

        System.out.println(tester.getHealth());
        System.out.println(tester.getFuel());
        System.out.println(tester.getOxygen());

        

    }
}
