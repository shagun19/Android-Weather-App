package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    List<NewsCard> newsCards;
    public  MyAdapter(Context context, List<NewsCard> newsCards){
        this.context = context;
        this.newsCards=newsCards;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.fragment_row, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        SharedPreferences sharedPreferences =context.getSharedPreferences("MySharedPref",0);
        Picasso.get().load(newsCards.get(position).getImgUrl()).into(holder.cardImage);
        holder.headline.setText(newsCards.get(position).getHeadline());
        String toSpan = newsCards.get(position).getTime()+" | "+newsCards.get(position).getNewsSource();
        Spannable wordtoSpan = new SpannableString(toSpan);
        int index = toSpan.indexOf("|");
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#6200EE")), index, index+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.dateSection.setText(wordtoSpan);

        if(!sharedPreferences.getString(newsCards.get(position).getNewsId(),"").equals("")){
            holder.bookmark.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
        }
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.news_card_dialog);
                ImageView dialogImage = dialog.findViewById(R.id.dialog_image);
                TextView headline = dialog.findViewById(R.id.dialog_headline);
                Picasso.get().load(newsCards.get(position).getImgUrl()).into(dialogImage);
                headline.setText(newsCards.get(position).getHeadline());
                final ImageView imageView = dialog.findViewById(R.id.dialog_bookmark);
                SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref",0);
                if(!sharedPreferences.getString(newsCards.get(position).getNewsId(),"").equals("")){
                    imageView.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
                }
                ImageView twitter = dialog.findViewById(R.id.dialog_twitter);
                twitter.setOnClickListener(new ImageView.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        String text = "Check out this link";
                        String newsUrl = "https://content.guardianapis.com/"+newsCards.get(position).getNewsId();
                        String url = "http://www.twitter.com/intent/tweet?url="+newsUrl+"&text="+text;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        context.startActivity(i);
                    }
                });
                dialog.show();

               imageView.setOnClickListener(new ImageView.OnClickListener(){
                   @SuppressLint("ShowToast")
                   @Override
                   public void onClick(View v) {
                       SharedPreferences sharedPreferences =context.getSharedPreferences("MySharedPref",0);
                       SharedPreferences.Editor editor = sharedPreferences.edit();
                       Toast toast;
                       if(!sharedPreferences.getString(newsCards.get(position).getNewsId(),"").equals("")){
                           imageView.setBackgroundResource(R.drawable.ic_bookmark_border_black_24dp);
                           holder.bookmark.setBackgroundResource(R.drawable.ic_bookmark_border_black_24dp);
                           editor.remove(newsCards.get(position).getNewsId());
                           toast=Toast.makeText(context,"Removing "+newsCards.get(position).getHeadline(),Toast.LENGTH_SHORT);
                       }
                       else{
                           toast=Toast.makeText(context,"Adding "+newsCards.get(position).getHeadline(),Toast.LENGTH_SHORT);
                           imageView.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
                           holder.bookmark.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
                           Gson gson = new Gson();
                           String json = gson.toJson(newsCards.get(position));
                           editor.putString(newsCards.get(position).getNewsId(),json);
                       }
                       editor.commit();
                       toast.show();
                        }
                    }
                );

                return false;
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Main2Activity.class);
                intent.putExtra("headline",newsCards.get(position).getHeadline());
                intent.putExtra("img",newsCards.get(position).getImgUrl());
                intent.putExtra("id",newsCards.get(position).getNewsId());
                context.startActivity(intent);
            }
        });
        holder.bookmark.setOnClickListener(new ImageView.OnClickListener(){
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences =context.getSharedPreferences("MySharedPref",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Toast toast;
                if(!sharedPreferences.getString(newsCards.get(position).getNewsId(),"").equals("")){
                    holder.bookmark.setBackgroundResource(R.drawable.ic_bookmark_border_black_24dp);
                    editor.remove(newsCards.get(position).getNewsId());
                    toast=Toast.makeText(context,"Removing "+newsCards.get(position).getHeadline(),Toast.LENGTH_SHORT);
                }
                else{
                    toast=Toast.makeText(context,"Adding "+newsCards.get(position).getHeadline(),Toast.LENGTH_SHORT);
                    holder.bookmark.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
                    Gson gson = new Gson();
                    String json = gson.toJson(newsCards.get(position));
                    editor.putString(newsCards.get(position).getNewsId(),json);
                }
                editor.commit();
                toast.show();

           }
        }
        );

    }

    public void clear() {
        newsCards.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<NewsCard> list) {
        newsCards.addAll(list);
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return newsCards.size();
    }

    public class MyViewHolder  extends  RecyclerView.ViewHolder{
        TextView headline ;
        TextView dateSection;
        ImageView cardImage;
        ImageView bookmark;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.card_image);
            headline = itemView.findViewById(R.id.headline);
            dateSection = itemView.findViewById(R.id.date_section);
            bookmark = itemView.findViewById(R.id.bookmark);
            cardView = itemView.findViewById(R.id.news_card);

        }
    }
}
