package core.modules;

import java.lang.reflect.Array;

public class Indicator {

    public Indicator(){

    }

    public float movingAverage(float[] prices){
        float sum = (float) 0.0;
        float avg = (float) 0.0;

        for (float i: prices) {
            sum = sum + i;
        }
        return avg = sum / prices.length;
    }

}
