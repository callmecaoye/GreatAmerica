package com.promeets.android.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by SHASHANK on 11-12-2015.
 */
public final class NumberFormatUtil {

    private static NumberFormatUtil instance = null;

    private NumberFormatUtil(){}

    public synchronized static NumberFormatUtil getInstance(){
        if(instance==null)
            instance = new NumberFormatUtil();
        return instance;
    }
    public Long getLong(String value){
        return  new Long(value);
    }
    public Double getDouble(String value){
        return  new Double(value);
    }
    public Integer getInt(String value){
       try{
           return  new Integer(value);
       }catch(Exception ex){
           return 0;
       }
    }

    public Float getFloat(String value){
        try{
            return  new Float(value);
        }catch(Exception ex){
            return 0f;
        }
    }

    public String getCurrency(String value){
        try {
            NumberFormat defaultFormat = new DecimalFormat("#,###,###.##");
            return defaultFormat.format(new Double(value));
        } catch (Exception ex) {
            return "0";
        }
    }

    public String getCurrencyTest(String value){
        try {
            double money = new Double(value);
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            return formatter.format(money);
        } catch (Exception ex) {
            return "0";
        }
    }

    public String getCurrency(Double value){
        try {
            NumberFormat defaultFormat = new DecimalFormat("#,###,###.##");
            return defaultFormat.format(value);
        } catch (Exception ex) {
            return "0";
        }
    }

    public String getTime(String value){
        try {
            NumberFormat defaultFormat = new DecimalFormat("##.##");
            return defaultFormat.format(new Double(value));
        } catch (Exception ex) {
            return "0";
        }
    }
}
