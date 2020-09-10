package com.moranstart.jugo.database;

import java.util.Arrays;
import java.util.List;

import java.util.ArrayList;


public class DataProcessing {

    public static double sumsqurestandardization(List<Double> selectdata) {


        List<Double> squaredata = new ArrayList<Double>();

        int squresum = 0;
        double sum = 0.0;

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


    public static List<Integer> standardization(List<Double> selectdata) {

        List<Integer> standdata = new ArrayList<Integer>();


        double sum = 0.0;


        for (double d : selectdata) {
            sum += d;
        }
        double ave = sum / selectdata.size();

        for (double d : selectdata) {
            standdata.add(new Double((d - ave) * 100).intValue());
        }


        return standdata;

    }


    public static Double[][] afterprocess(Double[][] result) {


        Double[][] AfterProcessData = new Double[4][result[0].length];
        AfterProcessData[0] = result[0];

        AfterProcessData[1] = ArraysMean(result);
        AfterProcessData[2] = ArraysSection(result)[5];
        AfterProcessData[3] = ArraysSection(result)[result.length - 5];

        System.out.println("AfterProcessData:" + Arrays.toString(AfterProcessData[1]));

        for(int p=0;p<AfterProcessData.length;p++){
            for(int q=0;q<AfterProcessData[0].length;q++){
                AfterProcessData[p][q]=41903010.00*AfterProcessData[p][q]/100.00;

            }

        }

        return AfterProcessData;
    }

    //zhuan zhi
    public static Double[][] reverse(Double[][] temp) {

        Double[][] rever=new Double[temp[0].length][temp.length];
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[i].length; j++) {
                rever[j][i]=temp[i][j];
            }
        }
        return rever;
    }


    public static Double[] ArraysMean(Double[][] result) {

        Double[] Mean = new Double[result[0].length];

        for (int i = 0; i < result[0].length; i++) {
            double sum = 0;
            for (int j = 1; j < result.length; j++) {
                sum += result[j][i];
            }
            Mean[i] = sum / (result.length-1);
        }
        System.out.println("Mean:" + Arrays.toString(Mean));
        return Mean;
    }


    public static Double[][] ArraysSection(Double[][] result) {

        Double[][] reverses = reverse(result);

        for (int i = 0; i < reverses.length; i++) {
            Arrays.sort(reverses[i]);
        }

        reverses = reverse(reverses);

        System.out.println("ArraysSection:" + Arrays.toString(reverses));

        return reverses;
    }

}
