package com.moranstart.jugo.database;

import com.moranstart.jugo.algorithm.crossk10_10;


public class PointShuffler {


    public static String PointShuffler(int length, int[] boundry) {

        int[] lats = new int[length];
        int[] lngs = new int[length];
        int latmax = boundry[0];
        int latmin = boundry[1];
        int lngmax = boundry[2];
        int lngmin = boundry[3];
        String shulterxandy = "";
        System.out.println("latmax" + latmax + "latmin" + latmin + "lngmax" + lngmax + "lngmin" + lngmin);
        for (int i = 0; i < lats.length; i++) {
            lats[i] = latmin + (int) (Math.random() * (latmax + 1 - latmin));
            lngs[i] = lngmin + (int) (Math.random() * (lngmax + 1 - lngmin));
            shulterxandy += lats[i] + "-" + lngs[i] + ",";
        }
        return shulterxandy.substring(0, shulterxandy.length() - 1);
    }

    public static int[] PointBoundary(String input) {
        int latmax = 0;
        int lngmax = 0;
        int latmin = Integer.MAX_VALUE;
        int lngmin = Integer.MAX_VALUE;
        int[] Boundary=new int[4] ;
        System.out.println("input value" + input);
        String[] inputxandy = input.split(",");
        int[] lats = new int[inputxandy.length];
        int[] lngs = new int[inputxandy.length];

        // if(times==0) {
        for (int i = 0; i < inputxandy.length; i++) {
            String[] splitxy = inputxandy[i].split("-");
            lats[i] = Integer.valueOf(splitxy[0]);
            lngs[i] = Integer.valueOf(splitxy[1]);
            if (latmax < lats[i]) {
                latmax = lats[i];
                Boundary[0]=latmax;
            }
            if (latmin > lats[i]) {
                latmin = lats[i];
                Boundary[1]=latmin;
            }

            if (lngmax < lngs[i]) {
                lngmax = lngs[i];
                Boundary[2]=lngmax;
            }
            if (lngmin > lngs[i]) {
                lngmin = lngs[i];
                Boundary[3]=lngmin;
            }

        }

        return Boundary;
    }



    }
