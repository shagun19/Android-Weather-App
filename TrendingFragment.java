package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrendingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrendingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LineChart lineChart;

    public TrendingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrendingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrendingFragment newInstance(String param1, String param2) {
        TrendingFragment fragment = new TrendingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_trending, container, false);
        lineChart = view.findViewById(R.id.trend_chart);
        lineChart.setActivated(true);
        lineChart.setNoDataText("");
        lineChart.getLegend().setTextSize(20);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        final String[] searchQuery = {""};

        //
        final EditText searchTerm = view.findViewById(R.id.searchTerm);
        searchTerm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchQuery[0] = searchTerm.getText().toString();
                Log.i("TERM",searchQuery[0]);
                String url = getString(R.string.node_server)+"/googleTrends/"+searchQuery[0];
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("QUERY ",searchQuery[0]);
                            if(searchQuery[0].equals("")) searchQuery[0]="coronavirus";
                            lineChart.getXAxis().setDrawGridLines(false);
                            lineChart.getAxisLeft().setDrawGridLines(false);
                            lineChart.getAxisRight().setDrawGridLines(false);

                            ArrayList<Entry> yValues = new ArrayList<>();
                            JSONArray values = response.getJSONArray("values");
                            for(int i=0;i<values.length();i++){
                                yValues.add(new Entry(i,(float)values.getInt(i)));
                            }
                            LineDataSet lineDataSet1 = new LineDataSet(yValues, "Trending chart for "+searchQuery[0]);
                            lineDataSet1.setCircleSize(3f);
                            lineDataSet1.setDrawVerticalHighlightIndicator(false);
                            lineDataSet1.setDrawHorizontalHighlightIndicator(false);
                            lineDataSet1.setCircleHoleColor(Color.parseColor("#6200EE"));
                            lineDataSet1.setColor(Color.parseColor("#6200EE"));
                            lineDataSet1.setCircleColor(Color.parseColor("#6200EE"));
                            lineDataSet1.setFillAlpha(110);
                            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                            dataSets.add(lineDataSet1);
                            LineData lineData = new LineData(dataSets);
                            lineChart.setData(lineData);
                            lineChart.invalidate();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                requestQueue.add(jsonObjectRequest);
                return false;
            }
        });
        Log.i("AGAU","Here "+searchQuery[0]);
        if(searchQuery[0].equals(""))searchQuery[0]="coronavirus";


        String url = getString(R.string.node_server)+"/googleTrends/"+searchQuery[0];
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("QUERY ",searchQuery[0]);
                    lineChart.getXAxis().setDrawGridLines(false);
                    lineChart.getAxisLeft().setDrawGridLines(false);
                    lineChart.getAxisRight().setDrawGridLines(false);

                    ArrayList<Entry> yValues = new ArrayList<>();
                    JSONArray values = response.getJSONArray("values");
                    for(int i=0;i<values.length();i++){
                        yValues.add(new Entry(i,(float)values.getInt(i)));
                    }
                    LineDataSet lineDataSet1 = new LineDataSet(yValues, "Trending chart for "+searchQuery[0]);
                    lineDataSet1.setDrawVerticalHighlightIndicator(false);
                    lineDataSet1.setDrawHorizontalHighlightIndicator(false);
                    lineDataSet1.setCircleSize(3f);
                    lineDataSet1.setCircleHoleColor(Color.parseColor("#6200EE"));
                    lineDataSet1.setColor(Color.parseColor("#6200EE"));
                    lineDataSet1.setCircleColor(Color.parseColor("#6200EE"));
                    lineDataSet1.setFillAlpha(110);
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(lineDataSet1);
                    LineData lineData = new LineData(dataSets);
                    lineChart.setData(lineData);
                    lineChart.invalidate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
        return view;
    }
}
