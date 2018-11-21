package edu.csulb.rob.anacodiam.Activities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class FoodData extends CalorieData implements Parcelable {

    public final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public FoodData createFromParcel(Parcel source) {
            return new FoodData(source);
        }

        @Override
        public FoodData[] newArray(int size) {
            return new FoodData[size];
        }
    };

    public FoodData(){
        super();
    }

    public FoodData(Parcel in){
        super(in);
    }

    public float getCarbohydrates(int index){ return ((FoodDataObject) data.get(index)).getCarbohydrates(); }

    public float getFats(int index){ return ((FoodDataObject) data.get(index)).getFats(); }

    public float getProtein(int index){ return ((FoodDataObject) data.get(index)).getProtein(); }

    public void addRecord(String inputName, int inputCalories, Date inputDate, float inputCarbohydrates, float inputFats, float inputProtein){
        data.add(new FoodDataObject(inputName, inputCalories, inputDate, inputCarbohydrates, inputFats, inputProtein));
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


    protected class FoodDataObject extends CalorieDataObject implements Comparable<CalorieDataObject> {
        protected float carbohydrates;
        protected float fats;
        protected float protein;

        public FoodDataObject(String inputName, int inputCalories, Date inputDate, float inputCarbohydrates, float inputFats, float inputProtein){
            super(inputName, inputCalories, inputDate);

            carbohydrates = inputCarbohydrates;
            fats = inputFats;
            protein = inputProtein;
        }

        public float getCarbohydrates(){
            return carbohydrates;
        }

        public float getFats(){
            return fats;
        }

        public float getProtein(){
            return protein;
        }

        @Override
        public int compareTo(CalorieDataObject o) {
            return super.compareTo(o);
        }

        @Override
        public String toString(){
            String returnString = super.toString();

            return returnString + "  --  Carbohydrates: " + carbohydrates + "  --  Fats: " + fats + "  --  Protein: " + protein;
        }
    }
}