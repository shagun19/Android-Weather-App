package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class PageAdapter extends FragmentPagerAdapter {

    private  int numTabs;
    private Context context;

    public PageAdapter(@NonNull FragmentManager fm, int numTabs, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context= context;
        this.numTabs=numTabs;
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                HomeFragment homeFragmentWorld = new HomeFragment();
                Bundle bundleWorld = new Bundle();
                bundleWorld.putString("category", "world");
                homeFragmentWorld.setArguments(bundleWorld);
                return homeFragmentWorld;

            case 1:
                HomeFragment homeFragmentBusiness = new HomeFragment();
                Bundle bundleBusiness = new Bundle();
                bundleBusiness.putString("category", "business" );
                homeFragmentBusiness.setArguments(bundleBusiness);
                return homeFragmentBusiness;
            case 2:
                HomeFragment homeFragmentPolitics = new HomeFragment();
                Bundle bundlePolitics = new Bundle();
                bundlePolitics.putString("category", "politics" );
                homeFragmentPolitics.setArguments(bundlePolitics);
                return homeFragmentPolitics;
            case 3:
                HomeFragment homeFragmentSport = new HomeFragment();
                Bundle bundleSport = new Bundle();
                bundleSport.putString("category", "sport" );
                homeFragmentSport.setArguments(bundleSport);
                return homeFragmentSport;
            case 4:
                HomeFragment homeFragmentTech = new HomeFragment();
                Bundle bundleTech = new Bundle();
                bundleTech.putString("category", "technology" );
                homeFragmentTech.setArguments(bundleTech);
                return homeFragmentTech;
            case 5:
                HomeFragment homeFragmentScience = new HomeFragment();
                Bundle bundleScience = new Bundle();
                bundleScience.putString("category", "science" );
                homeFragmentScience.setArguments(bundleScience);
                return homeFragmentScience;
            default:
                return null;


        }


    }

    @Override
    public int getCount() {
        return numTabs;
    }

    public int getItemPosition(@NonNull Object object){
        return POSITION_NONE;
    }
}
