package com.accept.jugo.database;

import java.util.List;

import java.util.ArrayList;


public class DataProcessing {

    public static List<Integer> standardization(List<Double>  selectdata){

        List<Integer> standdata = new ArrayList<Integer>();
        double sum = 0.0;
        for (double d : selectdata) {
            sum +=d;
        }
        double ave = sum/selectdata.size();


        for (double d : selectdata) {
            standdata.add(new Double((d-ave)*100).intValue());
        }
     return standdata;

    }


}
