package edu.csulb.rob.anacodiam.Activities;

public class Global {
    private static Global instance;

    private FoodData data;

    private Global(){

    }

    public void setFoodData(FoodData d){
        this.data=d;
    }
    public FoodData getFoodData(){
        return this.data;
    }

    public static synchronized Global getInstance(){
        if(instance==null){
            instance=new Global();
        }
        return instance;
    }
}
