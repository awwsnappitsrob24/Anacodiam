package edu.csulb.rob.anacodiam.Activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.csulb.rob.anacodiam.R;

public class CustomFoodAdapter extends BaseAdapter {

    public Context context;
    public ArrayList<Food> myFoodList;

    public CustomFoodAdapter(Context context, ArrayList<Food> myFoodList) {
        super();
        this.context = context;
        this.myFoodList = myFoodList;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView foodImageView;
        TextView txtFoodName;
    }

    @Override
    public int getCount() {
        return myFoodList.size();
    }

    @Override
    public Object getItem(int i) {
        return myFoodList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.food_row, parent, false);
            holder = new ViewHolder();
            holder.txtFoodName = (TextView) convertView.findViewById(R.id.foodName);
            holder.txtFoodName.setFocusable(false);
            holder.txtFoodName.setFocusableInTouchMode(false);
            holder.foodImageView = (ImageView) convertView.findViewById(R.id.foodImage);
            holder.foodImageView.setFocusable(false);
            holder.foodImageView.setFocusableInTouchMode(false);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtFoodName.setText(myFoodList.get(position).getFoodName());
        Picasso.with(context).load(myFoodList.get(position).getFoodImageURL()).into(holder.foodImageView);

        return convertView;

    }

}
