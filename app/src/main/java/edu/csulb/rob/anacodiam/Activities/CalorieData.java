package edu.csulb.rob.anacodiam.Activities;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public abstract class CalorieData implements Parcelable {

    protected ArrayList<CalorieDataObject> data;

    public CalorieData(){
        data = new ArrayList<CalorieDataObject>();
    }

    public CalorieData(Parcel in){
        data = (ArrayList<CalorieDataObject>) in.readSerializable();
    }

    public String getName(int index){
        return data.get(index).getName();
    }

    public int getCalories(int index){
        return data.get(index).getCalories();
    }

    public Date getDate(int index){
        return data.get(index).getDate();
    }

    public int getSize(){
        return data.size();
    }

    public void addRecord(String inputName, int inputCalories, Date inputDate){
        data.add(new CalorieDataObject(inputName, inputCalories, inputDate));
    }

    public void removeRecord(int index){
        data.remove(index);
    }

    public void sortByDate(){
        Collections.sort(data);
    }

    @Override
    public String toString(){
        String returnString = "";

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

        for(int i = 0; i < data.size(); i++){
            returnString = returnString + data.get(i).toString() + "\n";
        }

        if(returnString.equals("")){
            return returnString;
        }else {
            return returnString.substring(0, returnString.length() - 1);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(data);
    }


    protected class CalorieDataObject implements Comparable<CalorieDataObject> {
        protected String name;
        protected int calories;
        protected Date date;

        public CalorieDataObject(String inputName, int inputCalories, Date inputDate){
            name = inputName;
            calories = inputCalories;
            date = inputDate;
        }

        public String getName(){
            return name;
        }

        public int getCalories(){
            return calories;
        }

        public Date getDate(){
            return date;
        }

        @Override
        public int compareTo(CalorieDataObject o) {
            if(date.compareTo(o.date) < 0){
                return -1;
            }else if(date.compareTo(o.date) > 0){
                return 1;
            }else{
                return 0;
            }
        }

        @Override
        public String toString(){
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

            return sdf.format(date) + ":  " + name+ "  --  Calories: " + calories;
        }
    }
}