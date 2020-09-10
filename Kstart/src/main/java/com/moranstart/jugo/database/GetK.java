package com.moranstart.jugo.database;

public class GetK {

    public static int[] GetK(long[] out, int d){

        int[] crosskd=new int[d];

        for(int i=0;i<out.length;i++){
            for(int k=0;k<d;k++){
                if(out[i]<(k+1)*50*(k+1)*50){
                    crosskd[k]=crosskd[k]+1;
                }
            }
        }
        return crosskd;

    }
}
