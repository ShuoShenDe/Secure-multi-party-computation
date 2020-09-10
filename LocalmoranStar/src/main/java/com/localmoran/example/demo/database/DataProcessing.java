package com.localmoran.example.demo.database;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DataProcessing {

    public static double sumsqurestandardization(List<Double>  selectdata) {


        List<Double> squaredata = new ArrayList<Double>();

        double squresum=0;
        double sum = 0.000000;

        for (int i=0; i<selectdata.size();i++) {
            sum +=selectdata.get(i);
        }
        double ave = sum/selectdata.size();
        for (double d : selectdata) {
            squaredata.add((d-ave)*(d-ave));
        }

        for (double s : squaredata) {
            squresum +=s;
        }
        return squresum;

    }



    public static List<Integer> standardization(List<Double>  selectdata){

        List<Integer> standdata = new ArrayList<Integer>();

        double std=Math.sqrt(sumsqurestandardization(selectdata)/selectdata.size());
        System.out.println("std value" + std);
        double sum = 0.0;


        for (double d : selectdata) {
            sum +=d;
        }
        double ave = sum/selectdata.size();


        for (double d : selectdata) {
            BigDecimal bd=new BigDecimal((d-ave)*std*100).setScale(0, BigDecimal.ROUND_HALF_UP);
            standdata.add(Integer.parseInt(bd.toString()));
        }



     return standdata;

    }




}
