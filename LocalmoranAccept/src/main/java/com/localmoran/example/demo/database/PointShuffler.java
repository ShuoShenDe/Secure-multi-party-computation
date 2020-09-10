package com.localmoran.example.demo.database;

public class PointShuffler {


    static int latmax = Integer.MIN_VALUE;
    static int lngmax = Integer.MIN_VALUE;
    static int latmin = Integer.MAX_VALUE;
    static int lngmin = Integer.MAX_VALUE;

    public static String PointShuffler(String input,int times) {

        System.out.println("input value" + input);
        String[] inputxandy = input.split(",");
        int[] lats = new int[inputxandy.length];
        int[] lngs = new int[inputxandy.length];

        String shulterxandy="";

        if(times==0) {
            for (int i = 0; i < inputxandy.length; i++) {
                String[] splitxy = inputxandy[i].split("-");
                lats[i] = Integer.valueOf(splitxy[0]);
                lngs[i] = Integer.valueOf(splitxy[1]);
                if (latmax < lats[i]) {
                    latmax = lats[i];
                }
                if (latmin > lats[i]) {
                    latmin = lats[i];
                }

                if (lngmax < lngs[i]) {
                    lngmax = lngs[i];
                }
                if (lngmin > lngs[i]) {
                    lngmin = lngs[i];
                }
            }
        }
        System.out.println("latmax" + latmax+"latmin"+latmin+"lngmax"+lngmax+"lngmin"+lngmin);
        for (int i = 0; i < lats.length; i++) {
            lats[i]=latmin+(int)(Math.random()*(latmax+1-latmin));
            lngs[i]=lngmin+(int)(Math.random()*(lngmax+1-lngmin));
            shulterxandy+=lats[i]+"-"+lngs[i]+",";
        }
        return shulterxandy.substring(0,shulterxandy.length()-1);
    }
}
