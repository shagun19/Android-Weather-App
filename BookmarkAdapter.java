package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkAdapterView> {
    List<String> headlines;
    Context context;
    ViewGroup viewGroup;
    TextView textView;
    public BookmarkAdapter(Context context, List<String> headlines, TextView textView) {
        this.context = context;
        this.headlines=headlines;
        this.textView = textView;
    }

    @NonNull
    @Override
    public BookmarkAdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.viewGroup=parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_card,parent,false);
        return new BookmarkAdapterView(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getDate(String publishedDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Log.i("PUB DATE",publishedDate);
        LocalDateTime dateTime = LocalDateTime.parse(publishedDate, formatter);
        ZoneId zoneId = ZoneId.of( "America/Los_Angeles" );        //Zone information
        ZonedDateTime zonedDateTimeNews = dateTime.atZone( zoneId );     //Local time in Asia timezone
        String date ;
        if(zonedDateTimeNews.getDayOfMonth()>=1 && zonedDateTimeNews.getDayOfMonth()<=9)
            date = "0"+zonedDateTimeNews.getDayOfMonth()+" "+zonedDateTimeNews.getMonth().toString().toLowerCase();
        else date = zonedDateTimeNews.getDayOfMonth()+" "+zonedDateTimeNews.getMonth().toString().toLowerCase();
        return date;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final BookmarkAdapterView holder, final int position) {
        try {
            final JSONObject obj = new JSONObject(headlines.get(position));
            Log.i("TAG",obj.toString());
            Picasso.get().load(obj.getString("imgUrl")).into(holder.imageView);
            holder.textViewHeadline.setText(obj.getString("headline"));
            String date = "";
            if(obj.getString("date").length()==20){
                date = getDate(obj.getString("date"));
            }
            else date = obj.getString("date");
            String toSpan = date+" | "+obj.getString("newsSource");
            Spannable wordtoSpan = new SpannableString(toSpan);
            int index = toSpan.indexOf("|");
            wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#6200EE")), index, index+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.textViewDate.setText(wordtoSpan);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Main2Activity.class);
                    try {
                        intent.putExtra("headline",obj.getString("headline"));
                        intent.putExtra("img",obj.getString("imgUrl"));
                        intent.putExtra("id",obj.getString("newsId"));
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.cardView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.news_card_dialog);
                    ImageView dialogImage = dialog.findViewById(R.id.dialog_image);
                    TextView headline = dialog.findViewById(R.id.dialog_headline);
                    try {
                        Picasso.get().load(obj.getString("imgUrl")).into(dialogImage);
                        headline.setText(obj.getString("headline"));
                        final ImageView imageView = dialog.findViewById(R.id.dialog_bookmark);
                        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref",0);
                        if(!sharedPreferences.getString(obj.getString("newsId"),"").equals("")){
                            imageView.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
                        }
                        ImageView twitter = dialog.findViewById(R.id.dialog_twitter);
                        twitter.setOnClickListener(new ImageView.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                String text = "Check out this link";
                                String newsUrl = null;
                                try {
                                    newsUrl = "https://content.guardianapis.com/"+obj.getString("newsId");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String url = "http://www.twitter.com/intent/tweet?url="+newsUrl+"&text="+text;
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                context.startActivity(i);
                            }
                        });

                        imageView.setOnClickListener(new ImageView.OnClickListener(){
                             @SuppressLint("ShowToast")
                             @Override
                             public void onClick(View v) {
                                 SharedPreferences sharedPreferences =context.getSharedPreferences("MySharedPref",0);
                                 SharedPreferences.Editor editor = sharedPreferences.edit();
                                 Toast toast;
                                 try {
                                     editor.remove(obj.getString("newsId"));
                                     toast=Toast.makeText(context,"Removing "+obj.getString("headline"),Toast.LENGTH_SHORT);
                                     headlines.remove(position);
                                     notifyItemRemoved(position);
                                     notifyItemRangeChanged(position,headlines.size());
                                     notifyDataSetChanged();
                                     toast.show();
                                     imageView.setBackgroundResource(R.drawable.ic_bookmark_border_large_50dp);
                                     editor.commit();
                                     if(headlines.size()==0){
                                         notifyDataSetChanged();
                                         textView.setVisibility(View.VISIBLE);
                                     }
                                     toast.show();
                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }
                                 editor.commit();
                             }
                         }
                        );

                        dialog.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return false;
                }

            }
            );


        } catch (JSONException e) {
            e.printStackTrace();
        }

        //

        holder.bookmarkIcon.setOnClickListener(new ImageView.OnClickListener(){
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                try {
                    final JSONObject obj = new JSONObject(headlines.get(position));
                    SharedPreferences sharedPreferences =context.getSharedPreferences("MySharedPref",0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Toast toast;
                    editor.remove(obj.getString("newsId"));
                    toast=Toast.makeText(context,"Removing "+obj.getString("headline"),Toast.LENGTH_SHORT);
                    headlines.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,headlines.size());
                    notifyDataSetChanged();
                    toast.show();
                    editor.commit();
                    if(headlines.size()==0){
                        notifyDataSetChanged();
                        textView.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        //

    }




    @Override
    public int getItemCount() {
        return headlines.size();
    }

    public class BookmarkAdapterView extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView textViewHeadline ;
        TextView textViewDate;
        ImageView bookmarkIcon;
        public BookmarkAdapterView(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bookmarkPage_image);
            textViewHeadline = itemView.findViewById(R.id.bookmarkPage_headline);
            textViewDate = itemView.findViewById(R.id.bookmarkPage_section);
            cardView = itemView.findViewById(R.id.bookmarkPage_Card);
            bookmarkIcon = itemView.findViewById(R.id.bookmarkPage_bookmark);
        }
    }

}
