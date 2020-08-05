package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<NewsCard> updatesNewsCard = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final String category = this.getArguments().getString("category");
        final String query = this.getArguments().getString("query");
        final View[] view = {inflater.inflate(R.layout.fragment_progress, container, false)};
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url=getString(R.string.node_server);
        if(category!=null && !category.equals(""))  url = url+category;
        if(query!=null && !query.equals(""))  {
            Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            url = url+"/search/"+query;
        }
        final String finalUrl = url;
        final MyAdapter[] myAdapter = new MyAdapter[1];
        final String finalUrl1 = url;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                final ViewGroup viewGroup = (ViewGroup)view[0];
                viewGroup.removeAllViews();
                view[0] = inflater.inflate(R.layout.fragment_home, container, false);
                final RecyclerView recycler = view[0].findViewById(R.id.recyclerView);
                viewGroup.addView(view[0]);

                Log.println(Log.INFO,"RESPONSE_API_CALL",String.valueOf(response));
                JSONArray results;
                try{
                    results = response.getJSONObject("response").getJSONArray("results");
                    final List<NewsCard> newsCards = new ArrayList<>();
                    for(int i=0;i<10;i++){
                        NewsCard newsCard = new NewsCard();
                        try{
                            newsCard.setNewsId(results.getJSONObject(i).getString("id"));
                            newsCard.setHeadline(results.getJSONObject(i).getString("webTitle"));
                            if(category!=null && category.equals("latest")) {
                                if(results.getJSONObject(i).getJSONObject("fields").has("thumbnail"))
                                    newsCard.setImgUrl(results.getJSONObject(i).getJSONObject("fields").getString("thumbnail"));
                                else newsCard.setImgUrl("https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png");
                            }
                            else if(results.getJSONObject(i).getJSONObject("blocks")
                                    .getJSONObject("main").getJSONArray("elements").getJSONObject(0).
                                            getJSONArray("assets").length()!=0)newsCard.setImgUrl(results.getJSONObject(i).getJSONObject("blocks")
                                    .getJSONObject("main").getJSONArray("elements").getJSONObject(0).
                                            getJSONArray("assets").getJSONObject(0).
                                            getString("file"));
                            else newsCard.setImgUrl("https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png");
                            String publishedDateOriginal = results.getJSONObject(i).getString("webPublicationDate");
                            List<String> dates = getTimeDifference(publishedDateOriginal);
                            String publishedDate = dates.get(0);
                            String date = dates.get(1);
                            newsCard.setTime(publishedDate);
                            newsCard.setDate(date);
                            newsCard.setNewsSource(results.getJSONObject(i).getString("sectionName"));
                            newsCards.add(newsCard);
                        }catch (JSONException | InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    myAdapter[0] = new MyAdapter(getActivity(), newsCards);
                    recycler.setAdapter(myAdapter[0]);
                    recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                    final SwipeRefreshLayout swipeRefreshLayouts = view[0].findViewById(R.id.swiperefresh_items);
                    swipeRefreshLayouts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            //myAdapter.clear();
                            refresh(finalUrl1,category);
                            Log.i("SIZE",updatesNewsCard.size()+"");
                            if(updatesNewsCard.size()!=0){
                                myAdapter[0] = new MyAdapter(getActivity(), updatesNewsCard);
                                recycler.setAdapter(myAdapter[0]);
                                myAdapter[0].notifyDataSetChanged();
                            }
                            else{
                                myAdapter[0] = new MyAdapter(getActivity(), newsCards);
                                recycler.setAdapter(myAdapter[0]);
                                myAdapter[0].notifyDataSetChanged();

                            }

                            swipeRefreshLayouts.setRefreshing(false);
                        }
                    });




                }catch (JSONException e){
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
        return view[0];

    }

    private void  test(List<NewsCard> newsCards){
        for(int i=0;i<10;i++){
           // newsCards.remove(0);
        }


    }

    private void refresh(String url, final String category){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                Log.println(Log.INFO,"RESPONSE_API_CALL",String.valueOf(response));
                JSONArray results;
                try{
                    results = response.getJSONObject("response").getJSONArray("results");
                    List<NewsCard> newsCards= new ArrayList<>();
                    for(int i=0;i<10;i++){
                        NewsCard newsCard = new NewsCard();
                        try{
                            newsCard.setNewsId(results.getJSONObject(i).getString("id"));
                            newsCard.setHeadline(results.getJSONObject(i).getString("webTitle"));
                            if(category!=null && category.equals("latest")) {
                                if(results.getJSONObject(i).getJSONObject("fields").has("thumbnail"))
                                    newsCard.setImgUrl(results.getJSONObject(i).getJSONObject("fields").getString("thumbnail"));
                                else newsCard.setImgUrl("https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png");
                            }
                            else if(results.getJSONObject(i).getJSONObject("blocks")
                                    .getJSONObject("main").getJSONArray("elements").getJSONObject(0).
                                            getJSONArray("assets").length()!=0)newsCard.setImgUrl(results.getJSONObject(i).getJSONObject("blocks")
                                    .getJSONObject("main").getJSONArray("elements").getJSONObject(0).
                                            getJSONArray("assets").getJSONObject(0).
                                            getString("file"));
                            else newsCard.setImgUrl("https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png");
                            String publishedDateOriginal = results.getJSONObject(i).getString("webPublicationDate");
                            List<String> dates = getTimeDifference(publishedDateOriginal);
                            String publishedDate = dates.get(0);
                            String date = dates.get(1);
                            newsCard.setTime(publishedDate);
                            newsCard.setDate(date);
                            newsCard.setNewsSource(results.getJSONObject(i).getString("sectionName"));
                            newsCards.add(newsCard);
                        }catch (JSONException | InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    Log.i("CHECK HERE ---?",newsCards.size()+"");
                    updatesNewsCard=new ArrayList<>(newsCards);

                }catch (JSONException e){
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
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<String> getTimeDifference(String publishedDate) throws InterruptedException {
        //2020-04-21T20:08:15Z
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        LocalDateTime dateTime = LocalDateTime.parse(publishedDate, formatter);
        ZoneId zoneId = ZoneId.of( "America/Los_Angeles" );        //Zone information
        ZonedDateTime zonedDateTimeNews = dateTime.atZone( zoneId );     //Local time in Asia timezone
        String date ;
        if(zonedDateTimeNews.getDayOfMonth()>=1 && zonedDateTimeNews.getDayOfMonth()<=9)
            date = "0"+zonedDateTimeNews.getDayOfMonth()+" "+zonedDateTimeNews.getMonth().toString().toLowerCase();
        else date = zonedDateTimeNews.getDayOfMonth()+" "+zonedDateTimeNews.getMonth().toString().toLowerCase();
        LocalDateTime localDateTimeCurrent = LocalDateTime.now();            //Local date time
        ZonedDateTime zonedDateTimeCurrent = localDateTimeCurrent.atZone( zoneId );
        String timeDiff = Duration.between( zonedDateTimeNews , zonedDateTimeCurrent ).toString();
        Log.println(Log.INFO,"MSG",timeDiff);
        String timeDiffFormatted="";
        if(timeDiff.contains("H")){
            timeDiffFormatted=timeDiff.split("H")[0].split("PT")[1];
            if(timeDiffFormatted.contains("-")) timeDiffFormatted=timeDiffFormatted.split("-")[1];
            timeDiffFormatted+="h ago";
        }
        else if(timeDiff.contains("M") && timeDiff.contains("S")){
            timeDiffFormatted=timeDiff.split("M")[0].split("PT")[1];
            if(timeDiffFormatted.contains("-")) timeDiffFormatted=timeDiffFormatted.split("-")[1];
            timeDiffFormatted+="m ago";
        }
        else if(timeDiff.contains("S")){
            Log.i("debug",timeDiff);
            Log.i("debug 11",timeDiff.split("S")[0]);
            timeDiffFormatted=timeDiff.split("S")[0].split("PT")[1].split("\\.")[0];
            Log.i("debug 11",timeDiffFormatted);

            if(timeDiffFormatted.contains("-")) timeDiffFormatted=timeDiffFormatted.split("-")[1];
            timeDiffFormatted+="s ago";
        }
        return Arrays.asList(timeDiffFormatted,date);
    }
}
