package edu.csulb.rob.anacodiam.Activities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ExerciseData extends CalorieData implements Parcelable {
    public final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public ExerciseData createFromParcel(Parcel source) {
            return new ExerciseData(source);
        }

        @Override
        public ExerciseData[] newArray(int size) {
            return new ExerciseData[size];
        }
    };

    public ExerciseData(){
        super();
    }

    public ExerciseData(Parcel in){
        super(in);
    }

    public float getCarbohydrates(int index){ return ((FoodData.FoodDataObject) data.get(index)).getCarbohydrates(); }

    public float getFats(int index){ return ((FoodData.FoodDataObject) data.get(index)).getFats(); }

    public float getProtein(int index){ return ((FoodData.FoodDataObject) data.get(index)).getProtein(); }

    public void addRecord(String inputName, int inputCalories, Date inputDate){
        //data.add(new FoodData.FoodDataObject(inputName, inputCalories, inputDate, inputCarbohydrates, inputFats, inputProtein));
    }

    @Override
    public void removeRecord(int index){
        super.removeRecord(index);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }


    protected class ExerciseDataObject extends CalorieDataObject implements Comparable<CalorieDataObject> {

        public ExerciseDataObject(String inputName, int inputCalories, Date inputDate){
            super(inputName, inputCalories, inputDate);
        }

        @Override
        public int compareTo(CalorieDataObject o) {
            return super.compareTo(o);
        }

        @Override
        public String toString(){
            String returnString = super.toString();

            return returnString;
        }
    }
}
