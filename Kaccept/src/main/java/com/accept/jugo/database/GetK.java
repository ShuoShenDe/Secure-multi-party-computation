package com.accept.jugo.database;

import java.util.ArrayList;
import java.util.List;

public class GetK {


    public static int[] GetK(long[] out, int d){

        int[] crosskd=new int[d];

        for(int i=0;i<out.length;i++){
            for(int k=0;k<d;k++){
             if(out[i]<(k+1)*(k+1)*50*50){
                 crosskd[i]+=1;
             }
            }
        }
        return crosskd;

    }
}
