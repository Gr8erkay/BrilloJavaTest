package com.gr8erkay.brillojavatest2;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PancakeShopCC {

    //This is the Concurrent Class Version
    private static final int NUM_INTERVALS = 10;
    private static final int NUM_USERS = 3;
    private static final int MAX_PANCAKES_PER_USER = 5;
    private static final int MAX_PANCAKES_PER_INTERVAL = 12;

    public static void main(String[] args) {
        Random random = new Random();
        int totalPancakesMade = 0;
        int totalPancakesEaten = 0;
        int totalPancakesWasted = 0;
        int totalOrdersNotMet = 0;

        for (int i = 1; i <= NUM_INTERVALS; i++) {
            System.out.println("Interval " + i + ":");

            CompletableFuture<Integer> pancakesMadeFuture = CompletableFuture.supplyAsync(() ->
                    random.nextInt(MAX_PANCAKES_PER_INTERVAL + 1));

            CompletableFuture<Integer>[] eatPancakesFutures = new CompletableFuture[NUM_USERS];
            for (int j = 0; j < NUM_USERS; j++) {
                eatPancakesFutures[j] = CompletableFuture.supplyAsync(() -> {
                    int pancakesRequested = random.nextInt(MAX_PANCAKES_PER_USER + 1);
                    return Math.min(pancakesRequested, pancakesMadeFuture.join());
                });
            }

            try {
                int pancakesMade = pancakesMadeFuture.get();
                totalPancakesMade += pancakesMade;

                int[] pancakesEatenByUsers = new int[NUM_USERS];
                for (int j = 0; j < NUM_USERS; j++) {
                    int pancakesEaten = eatPancakesFutures[j].get();
                    pancakesEatenByUsers[j] = pancakesEaten;
                    totalPancakesEaten += pancakesEaten;
                    pancakesMade -= pancakesEaten;
                }

                if (pancakesMade > 0) {
                    totalPancakesWasted += pancakesMade;
                } else if (pancakesMade < 0) {
                    totalOrdersNotMet -= pancakesMade;
                }

                System.out.println("Starting time: " + ((i - 1) * 30) + " seconds");
                System.out.println("Ending time: " + (i * 30) + " seconds");
                System.out.println("Shopkeeper met the needs of the 3 users: " + (pancakesMade >= 0));
                System.out.println("Pancakes wasted: " + pancakesMade);
                System.out.println("Pancake orders not met: " + (-pancakesMade));
                System.out.println("----------------------------------------");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Total pancakes made: " + totalPancakesMade);
        System.out.println("Total pancakes eaten: " + totalPancakesEaten);
        System.out.println("Total pancakes wasted: " + totalPancakesWasted);
        System.out.println("Total pancake orders not met: " + totalOrdersNotMet);
    }
}
