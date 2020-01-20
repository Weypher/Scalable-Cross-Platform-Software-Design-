/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CWK3;

import org.apache.commons.math3.fitting.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Pengbo Liu
 */
public class MeasurementData {
    
    // is the data loaded.
    private boolean isLoaded = false;
    
    // original data
    private ArrayList<Double> arrOriginalData = new ArrayList<Double>();
    
    // data after binning
    private ArrayList<TwoTuple<Double, Integer>> arrBinnedData = new ArrayList<TwoTuple<Double, Integer>>();
    
    // data after fitting
    private ArrayList<TwoTuple<Double, Double>> arrFittedData = new ArrayList<TwoTuple<Double, Double>>();
    
    // fitting parameters
    private double[] parameters = null;
    
    /**
     * reset all data members.
     */
    public void reset() {
        arrOriginalData = new ArrayList<Double>();
        arrBinnedData   = new ArrayList<TwoTuple<Double, Integer>>();
        arrFittedData   = new ArrayList<TwoTuple<Double, Double>>();
        parameters      = null;
    }
    
    /**
     * bin
     * @param bin_type 
     */
    public void bin(String bin_type) {
        arrBinnedData   = new ArrayList<TwoTuple<Double, Integer>>();
        // default number of bin
        int k = 20;
        
        // size of original data
        int n = arrOriginalData.size();
        
        if (bin_type.equals("Sturge’s formula")) {
            k = (int) (1 + 3.3 * Math.log(n));      // 38.99265403440
        } else if (bin_type.equals("Rice rule")) {
            k = (int)(2 * Math.pow(n, 1.0 / 3));    // 92.8317766723
        } else if (bin_type.equals("Square root choice")) {
            k = (int)Math.sqrt(n);  // 316.2277660168
        }
        System.out.println("bin type: " + bin_type + ", n: " + n + ", k: " + k);

        // find min & max
        double min = arrOriginalData.get(0);
        double max = min;
        
        for (int i = 1; i < arrOriginalData.size(); i++) {
            double current = arrOriginalData.get(i);
            if (current < min) {
                min = current;
            }
            if (current > max)  {
                max = current;
            }
        }
        
        // bin size
        double bin_size = (max - min) / k;
        System.out.println("min: " + min + ", max: " + max + ", bin_size: " + bin_size);
        
        HashMap<Double, Integer> map = new HashMap<Double, Integer>();
        /*
        for (int i =0 ; i < k + 1; i++) {
            double start = min + i * bin_size;
            map.put(start, 0);
        }
        */
        
        // binning
        for (double d : arrOriginalData) {
            int bin_num = (int) ((d - min) / bin_size);
            double start = min + bin_num * bin_size;
            // int cnt = map.get(start);
            
            // update count of this bin
            int cnt = 0 ;
            if (map.get(start) != null) {
                cnt = map.get(start);
            }
            
            map.put(start, cnt + 1);
        }
        System.out.println("after bin, the size of map is: " + map.size());
        
        // sort by key
        Set set = map.keySet();
        Object[] arr_bin = set.toArray();
        Arrays.sort(arr_bin);
        
        // normalise
        for (Object key : arr_bin){
            int value =  (int) (map.get(key) / bin_size / arrOriginalData.size());
            // System.out.println("after bin and sort, key: " + key + ", cnt: " + map.get(key) + ", value: " + value);
            TwoTuple<Double, Integer> tp = new TwoTuple<Double, Integer>((Double)key,  value);
            arrBinnedData.add(tp);
        }
    }
    
    /**
     * fit
     */
    public void fit() {
        arrFittedData = new ArrayList<TwoTuple<Double, Double>>();
        
        // ref: UnitTest_MarquardtFittingNormal_Example
        
        WeightedObservedPoints obs = new WeightedObservedPoints();
        for(int i = 0; i < arrBinnedData.size(); i++) {
            obs.add(arrBinnedData.get(i).first, arrBinnedData.get(i).second);
        }
        
        parameters = GaussianCurveFitter.create().fit(obs.toList());
        
        // make new value, using fitting parameters
        for(int i = 0; i < arrBinnedData.size(); i++) {
            double x =  arrBinnedData.get(i).first;
            double xx = (x - parameters[1]) / parameters[2];
            double value = parameters[0] * Math.pow(Math.E, -0.5 * xx * xx);
            TwoTuple<Double, Double> tp = new TwoTuple<Double, Double>((Double)arrBinnedData.get(i).first,  value);
            arrFittedData.add(tp);
        }
        
        
    }
    
    /**
     * getArrBinnedData
     * @return 
     */
    public ArrayList<TwoTuple<Double, Integer>> getArrBinnedData() {
        System.out.println("final binned data size: " + arrBinnedData.size());
        return arrBinnedData;
    }
    
    /**
     * getArrFittedData
     * @return 
     */
    public ArrayList<TwoTuple<Double, Double>> getArrFittedData() {
        return arrFittedData;
    }
    
    /**
     * getParamaters
     * @return 
     */
    public double[] getParamaters() {
        return parameters;
    }

    /**
     * setIsLoaded
     * @param s 
     */
    public void setIsLoaded(boolean s) {
        isLoaded = s;
    }
    
    /**
     * getIsLoaded
     * @return 
     */
    public boolean getIsLoaded() {
        return isLoaded;
    }
    
    /**
     * addOriginalData
     * @param line 
     */
    public void addOriginalData(String line) {
        line = line.trim();
        Double d = Double.parseDouble(line);
        arrOriginalData.add(d);
    }
    
    /**
     * getMean
     * @return 
     */
    public double getMean() {
        int size = arrOriginalData.size();
        double sum = 0;
        for (int i = 0; i < size; i++) {
            sum += arrOriginalData.get(i);
        }
        return sum / size;
    }
    
    /**
     * getVariance
     * @return 
     */
    public  double getVariance() {
        int size = arrOriginalData.size();
        double avg = getMean();
        double ss = 0;
        for (int i = 0; i < size; i++) {
                ss += (arrOriginalData.get(i) - avg) * (arrOriginalData.get(i) - avg);
        }
        return ss / size;
    }

    /**
     * getStd
     * @return 
     */
    public double getStd() {
        return Math.sqrt(getVariance());
    }

    
    /**
     * getMedian
     * @return 
     */
    public double getMedian() {
	double j = 0;
        // sort
        Collections.sort(arrOriginalData);
        int size = arrOriginalData.size();
        if(size % 2 == 1){
            // odd
            j = arrOriginalData.get((size-1)/2);
        }else {
            // even
            j = (arrOriginalData.get(size/2-1) + arrOriginalData.get(size/2))/2;
        }
        return j;
    }

}
