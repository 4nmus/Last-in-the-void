package com.mygdx.events;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import com.mygdx.objects.Event;
import com.mygdx.objects.Alien;
import com.mygdx.objects.Planet;
import com.mygdx.objects.Player;

public class AggressiveAlienEncounterEvent extends Event {

    private final Alien alien;
    private final Planet planet;
    private final Random random = new Random();

    // --- Configuration Constants ---
    private static final String ORGANIC_RESOURCE_BASE_NAME = "Biomass";
    private static final String GAS_RESOURCE_BASE_NAME = "Fuel";
    private static final String MINERAL_RESOURCE_BASE_NAME = "Building Materials";
    private static final String UNKNOWN_RESOURCE_BASE_NAME = "Exotic Matter";

    // Combat parameters
    private static final int ATTACK_BASE_SUCCESS_CHANCE = 60; // Base chance to hit alien
    private static final int PLAYER_TAKES_DAMAGE_BASE = 15;   // Base damage if player fails attack
    private static final int PLAYER_TAKES_DAMAGE_RANGE = 10;  // Random addition to damage
    private static final int FLEE_DAMAGE_BASE = 5;
    private static final int FLEE_DAMAGE_RANGE = 5;
    private static final int FLEE_SUCCESS_CHANCE = 85; // Chance to successfully flee

    // Rarity definitions
    private enum RarityType {
        COMMON("Common", 1.0, 1.0),          // Name, Probability to drop if tier allows, Quantity multiplier
        UNCOMMON("Uncommon", 0.75, 0.8),
        RARE("Rare", 0.5, 0.6),
        EPIC("Epic", 0.25, 0.4),
        LEGENDARY("Legendary", 0.1, 0.2);

        private final String displayName;
        private final double dropProbability; // Base probability if this rarity is considered
        private final double quantityMultiplier;

        RarityType(String displayName, double dropProbability, double quantityMultiplier) {
            this.displayName = displayName;
            this.dropProbability = dropProbability;
            this.quantityMultiplier = quantityMultiplier;
        }

        public String getDisplayName() { return displayName; }
        public double getDropProbability() { return dropProbability; }
        public double getQuantityMultiplier() { return quantityMultiplier; }

        public static List<RarityType> getApplicableRarities(int planetTier) {
            // Planet tier effectively limits the highest rarity type available
            // Tier 1: Common only
            // Tier 2: Common, Uncommon
            // Tier 3: Common, Uncommon, Rare
            // ... and so on
            int maxRarityOrdinal = Math.min(planetTier -1 , RarityType.values().length - 1);
            if (maxRarityOrdinal < 0) return Collections.emptyList();

            List<RarityType> applicable = new ArrayList<>();
            for(RarityType rt : RarityType.values()){
                if(rt.ordinal() <= maxRarityOrdinal){
                    applicable.add(rt);
                } else {
                    break;
                }
            }
            return applicable;
        }
    }


    /**
     * Constructor for an aggressive alien encounter.
     * @param alien The specific alien encountered.
     * @param planet The planet where the encounter occurs.
     */
    public AggressiveAlienEncounterEvent(Alien alien, Planet planet) {
        super("Aggressive Alien Encounter on " + planet.getName(),
              String.format("You've encountered an aggressive %s (Strength: %d, Aggression: %d) on %s. It looks hostile!",
                            alien.getName(), alien.getStrength(), alien.getAggressionLevel(), planet.getName()));
        this.alien = alien;
        this.planet = planet;

        addAttackChoice();
        addFleeChoice();
    }

    private void addAttackChoice() {
        // Success chance can be influenced by alien's attributes (e.g., aggression or a dodge stat)
        // For simplicity, let's use a base chance slightly modified by alien aggression.
        // Lower aggression = easier to hit. Higher aggression = harder.
        // Assuming alien.getAggressionLevel() is 0-100.
        int dynamicSuccessChance = ATTACK_BASE_SUCCESS_CHANCE - (alien.getAggressionLevel() / 5); // e.g. aggression 50 -> -10% chance
        dynamicSuccessChance = Math.max(10, Math.min(90, dynamicSuccessChance)); // Clamp chance

        addChoice("Attack the " + alien.getName(), dynamicSuccessChance,
            player -> { // Success Action
                String lootMessage = harvestLoot(player);
                setSuccessMessage(String.format("You bravely fought and defeated the %s!\n%s",
                                                alien.getName(), lootMessage));
                planet.setExplored(true); // Or a more specific flag like planet.setAlienDefeated(true)
            },
            player -> { // Failure Action
                // Damage taken can be influenced by alien's strength
                int damage = PLAYER_TAKES_DAMAGE_BASE + random.nextInt(PLAYER_TAKES_DAMAGE_RANGE + 1)
                             + (alien.getStrength() / 10); // Alien strength adds to damage
                player.updateStat(Player.Stats.HEALTH, -damage);
                setFailureMessage(String.format("The %s overpowered you! Its strength was formidable. " +
                                                "You take %d damage to your hull integrity.",
                                                alien.getName(), damage));
                planet.setExplored(true); // Event resolved, planet state changes
            }
        );
    }

    private void addFleeChoice() {
        // Flee success chance could be influenced by player's ship speed vs alien speed, or alien aggression
        int dynamicFleeChance = FLEE_SUCCESS_CHANCE - (alien.getAggressionLevel() / 10); // More aggressive = harder to flee
        dynamicFleeChance = Math.max(25, Math.min(95, dynamicFleeChance));

        addChoice("Attempt to Flee", dynamicFleeChance,
            player -> { // Success Action
                int damage = FLEE_DAMAGE_BASE + random.nextInt(FLEE_DAMAGE_RANGE + 1);
                // Slight damage even on successful flee if alien is very aggressive
                if (alien.getAggressionLevel() > 75) damage += random.nextInt(5);

                player.updateStat(Player.Stats.HEALTH, -damage);
                setSuccessMessage(String.format("You managed to escape from the %s, but suffered %d damage to your hull integrity in the hasty retreat.",
                                                alien.getName(), damage));
                planet.setExplored(true);
            },
            player -> { // Failure Action
                // More significant damage if fleeing fails
                int damage = PLAYER_TAKES_DAMAGE_BASE + random.nextInt(PLAYER_TAKES_DAMAGE_RANGE + 1)
                             + (alien.getStrength() / 5); // Alien gets a solid hit
                player.updateStat(Player.Stats.HEALTH, -damage);
                setFailureMessage(String.format("Your attempt to flee from the %s failed! It caught up and dealt %d damage.",
                                                alien.getName(), damage));
                // Potentially, failing to flee could lead back to an attack choice or a forced attack.
                // For now, it's just damage and event resolution.
                planet.setExplored(true);
            }
        );
    }

    private String getBaseResourceTypeForPlanet() {
        switch (planet.getType()) {
            case Gas: return GAS_RESOURCE_BASE_NAME;
            case Mineral: return MINERAL_RESOURCE_BASE_NAME;
            case Organic: return ORGANIC_RESOURCE_BASE_NAME;
            default: return UNKNOWN_RESOURCE_BASE_NAME; // Fallback for unknown planet types
        }
    }

    private String harvestLoot(Player player) {
        String baseResourceType = getBaseResourceTypeForPlanet();
        int baseQuantity = calculateBaseResourceQuantity();
        StringBuilder resultMessage = new StringBuilder("You scavenge the area and find:\n");
        boolean resourcesFound = false;

        List<RarityType> possibleRarities = RarityType.getApplicableRarities(planet.getTier());

        for (RarityType rarity : possibleRarities) {
            // Check if this rarity of resource drops
            if (random.nextDouble() < rarity.getDropProbability()) {
                String resourceName = rarity.getDisplayName() + " " + baseResourceType;

                // Quantity calculation: base * rarity_multiplier * some_randomness
                // Alien strength could influence loot quality/quantity
                double alienBonusMultiplier = 1.0 + (alien.getStrength() / 200.0); // e.g. strength 50 -> 1.25x
                
                int amount = (int) (baseQuantity * rarity.getQuantityMultiplier() * alienBonusMultiplier);
                // Add some minor variation
                amount += random.nextInt(Math.max(1, amount / 4)) - (amount / 8);
                amount = Math.max(1, amount); // Ensure at least 1 unit

                player.addItemToInventory(resourceName, amount);
                resultMessage.append(String.format("- %d unit(s) of %s\n", amount, resourceName));
                resourcesFound = true;
            }
        }

        if (!resourcesFound) {
            // Consider giving a very small, common consolation prize or specific message
            String consolationResource = RarityType.COMMON.getDisplayName() + " " + baseResourceType;
            int consolationAmount = Math.max(1, baseQuantity / 5 + random.nextInt(3));
            player.addItemToInventory(consolationResource, consolationAmount);
            resultMessage.append(String.format("- %d unit(s) of %s (minor traces)\n", consolationAmount, consolationResource));
            // If even this is not desired, use:
            // resultMessage.append("No significant resources could be salvaged.");
        }

        return resultMessage.toString().trim();
    }

    private int calculateBaseResourceQuantity() {
        // Base amount from planet size, with some randomness and influence from planet tier.
        // Planet tier could act as a multiplier for base quantity.
        int tierMultiplier = Math.max(1, planet.getTier());
        int baseAmount = (planet.getSize() / 2) * tierMultiplier;

        // Add some randomness (+-20% of base)
        int randomFactor = 0;
        if (baseAmount > 0) {
            randomFactor = random.nextInt(Math.max(1, baseAmount / 5)) - (baseAmount / 10);
        } else { // if baseAmount is 0 (e.g. very small planet)
            baseAmount = 5; // default small amount
            randomFactor = random.nextInt(3)-1;
        }
        
        return Math.max(5, baseAmount + randomFactor); // Ensure a minimum practical amount
    }
}
