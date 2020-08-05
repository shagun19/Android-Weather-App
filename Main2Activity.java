package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Main2Activity extends AppCompatActivity {
    Toolbar toolbar;
    NewsCard newsCard = new NewsCard();
    MenuItem item ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_news);
        Intent myIntent = getIntent(); // gets the previously created intent
        String id = myIntent.getStringExtra("id");
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        newsCard.setNewsId(id);
        populateDetailArticles(id);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_article,menu);
        item = menu.findItem(R.id.detail_article_bookmark);
        bookMarkToggle(item);
        MenuItem bookmark = menu.findItem(R.id.detail_article_twitter);
        bookmark.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String text = "Check out this link";
                String newsUrl = "https://content.guardianapis.com/"+getIntent().getStringExtra("id");;
                String url = "http://www.twitter.com/intent/tweet?url="+newsUrl+"&text="+text;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            }
        });
        return true;
    }



    private void populateDetailArticles(String id) {
        setContentView(R.layout.fragment_progress);
        final RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);
        String url=getString(R.string.node_server)+"/detail-article/"+id;
        final JSONObject jsonObject = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONObject response) {
                Log.println(Log.INFO,"RESPONSE_API_CALL",String.valueOf(response));
                try {
                    setContentView(R.layout.detailed_news);
                    toolbar=findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);
                    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                    String imgUrl="https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                    if(response.getJSONObject("response").getJSONObject("content")
                            .getJSONObject("blocks").getJSONObject("main").
                                    getJSONArray("elements").getJSONObject(0).
                                    getJSONArray("assets").length()!=0){
                        imgUrl = response.getJSONObject("response").getJSONObject("content")
                                .getJSONObject("blocks").getJSONObject("main").
                                        getJSONArray("elements").getJSONObject(0).
                                        getJSONArray("assets").getJSONObject(0).
                                        getString("file");
                    }
                    String headline = response.getJSONObject("response").getJSONObject("content")
                            .getString("webTitle");
                    String publishedDate = response.getJSONObject("response").getJSONObject("content")
                            .getString("webPublicationDate");
                    String sectionName = response.getJSONObject("response").getJSONObject("content")
                            .getString("sectionName");
                    String description = response.getJSONObject("response").getJSONObject("content")
                            .getJSONObject("blocks").getJSONArray("body").getJSONObject(0).getString("bodyHtml");
                    String articleUrl = response.getJSONObject("response").getJSONObject("content")
                            .getString("webUrl");
                    newsCard.setTime("");
                    newsCard.setDate(publishedDate);
                    newsCard.setImgUrl(imgUrl);
                    newsCard.setTime(publishedDate);
                    newsCard.setNewsSource(sectionName);
                    newsCard.setHeadline(headline);

                    Log.i("TEST",imgUrl+" "+headline+" "+publishedDate+" "+sectionName+" "+description+" "+articleUrl);
                    getSupportActionBar().setTitle(headline);
                    ImageView imageView = findViewById(R.id.detail_article_img);
                    Picasso.get().load(imgUrl).into(imageView);
                    TextView textViewHeadline = findViewById(R.id.detail_article_headline);
                    textViewHeadline.setText(headline);
                    TextView textViewSectionName = findViewById(R.id.detail_article_sectionName);
                    textViewSectionName.setText(sectionName);
                    TextView textViewPublishedDate = findViewById(R.id.detail_article_publishedDate);
                    String dateInFormat = changeDateFormat(publishedDate.substring(0,10));
                    textViewPublishedDate.setText(dateInFormat);
                    TextView textViewDetail = findViewById(R.id.detail_article_desc);
                    textViewDetail.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
                    String linkUrl = "<a href='"+articleUrl+"'><u>View Full Article</u></a>";
                    TextView textViewArticleUrl = findViewById(R.id.detail_article_link);
                    textViewArticleUrl.setText(Html.fromHtml(linkUrl, Html.FROM_HTML_MODE_COMPACT));
                    textViewArticleUrl.setMovementMethod(LinkMovementMethod.getInstance());
                } catch (JSONException | ParseException e) {
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

    private String changeDateFormat(String date) throws ParseException {
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        return date.split("-")[2]+" "+month.substring(0,3)+" "+date.split("-")[0];
    }

    private void bookMarkToggle(MenuItem item){
        final String id = getIntent().getStringExtra("id");
        SharedPreferences sharedPreferences =getSharedPreferences("MySharedPref",0);
        if(!sharedPreferences.getString(id,"").equals("")){
            item.setIcon(R.drawable.ic_turned_in_black_50dp);
        }
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences sharedPreferences =getSharedPreferences("MySharedPref",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Toast toast;

                if(!sharedPreferences.getString(id,"").equals("")){
                    item.setIcon(R.drawable.ic_bookmark_border_large_50dp);
                    editor.remove(id);
                    toast=Toast.makeText(getApplicationContext(),"Removing "+newsCard.getHeadline(),Toast.LENGTH_SHORT);
                }
                else{
                    toast=Toast.makeText(getApplicationContext(),"Adding "+newsCard.getHeadline(),Toast.LENGTH_SHORT);
                    item.setIcon(R.drawable.ic_turned_in_black_50dp);
                    Gson gson = new Gson();
                    String json = gson.toJson(newsCard);
                    editor.putString(id,json);
                }
                editor.commit();
                toast.show();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
