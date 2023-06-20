package com.gr8erkay.brillojavatest2;

import java.util.Random;

public class PancakeShopNCC {

    //This is the Non-Concurrent Class Version
    public static void main(String[] args) {
        RandomData();
    }

    static void RandomData() {
        Random random = new Random();
        int totalPancakesMade = 0;
        int totalPancakesEaten = 0;
        int totalPancakesWasted = 0;
        int totalOrdersNotMet = 0;
        int users = 3;
        int maxPancakesPerUser = 5;
        int maxPancakesPerInterval = 12;

        for (int i = 1; i <= 10; i++) {
            System.out.println("Interval " + i + ":");
            int pancakesMade = random.nextInt(maxPancakesPerInterval + 1);
            totalPancakesMade += pancakesMade;

            int[] pancakesEatenByUsers = new int[users];
            for (int j = 0; j < users; j++) {
                int pancakesRequested = random.nextInt(maxPancakesPerUser + 1);
                pancakesEatenByUsers[j] = Math.min(pancakesRequested, pancakesMade);
                totalPancakesEaten += pancakesEatenByUsers[j];
                pancakesMade -= pancakesEatenByUsers[j];
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
        }

        System.out.println("Total pancakes made: " + totalPancakesMade);
        System.out.println("Total pancakes eaten: " + totalPancakesEaten);
        System.out.println("Total pancakes wasted: " + totalPancakesWasted);
        System.out.println("Total pancake orders not met: " + totalOrdersNotMet);
    }
}
