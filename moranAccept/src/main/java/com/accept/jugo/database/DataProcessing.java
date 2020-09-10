package com.accept.jugo.database;

import java.util.List;

import java.util.ArrayList;


public class DataProcessing {

    public static List<Integer> standardization(List<Double> selectdata) {

        List<Integer> standdata = new ArrayList<Integer>();


        double std=Math.sqrt(sumsqurestandardization(selectdata)/selectdata.size());
        System.out.println("std value" + std);
        double sum = 0.0;


        for (double d : selectdata) {
            sum += d;
        }
        double ave = sum / selectdata.size();

        for (double d : selectdata) {
            standdata.add(new Double((d - ave) /std * 1000).intValue());
        }



        return standdata;

    }

    public static double sumsqurestandardization(List<Double> selectdata) {


        List<Double> squaredata = new ArrayList<Double>();

        double squresum = 0.0;
        double sum = 0.000000;

        for (double d : selectdata) {
            sum += d;
        }
        double ave = sum / selectdata.size();
        for (double d : selectdata) {
            squaredata.add(new Double((d - ave) * (d - ave)));
        }

        for (double s : squaredata) {
            squresum += s;
        }
        return squresum;

    }


}
