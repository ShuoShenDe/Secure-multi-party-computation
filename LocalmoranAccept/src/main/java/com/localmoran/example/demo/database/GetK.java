package com.localmoran.example.demo.database;

public class GetK {


    public static int[] GetK(long[] out, int d){

        int[] crosskd=new int[d];

        for(int i=0;i<out.length;i++){
            for(int k=0;k<d;k++){
             if(out[i]<(k+1)*1000*1000){
                 crosskd[i]+=1;
             }
            }
        }
        return crosskd;

    }
}
