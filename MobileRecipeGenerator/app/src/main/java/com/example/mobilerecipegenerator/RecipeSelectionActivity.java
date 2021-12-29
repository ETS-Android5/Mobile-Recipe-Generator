package com.example.mobilerecipegenerator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;


public class RecipeSelectionActivity extends AppCompatActivity {

    ListView listView;

    ArrayList<String> recipeNameList = new ArrayList<>();
    ArrayList<String> recipeLinkList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_selection);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.listView);

        String ingredient = getIntent().getStringExtra("ClassificationResult");

        System.out.println("-----------------------" + ingredient);

        Thread thread = new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://www.myrecipes.com/search?q="+ingredient)
                    .get()
                    .build();
            try {
                Document doc = Jsoup.parse(client.newCall(request).execute().body().string());

                Elements recipeResults = doc.getElementsByClass("search-result-title-link");

                for(Element el : recipeResults){
                    recipeNameList.add(el.text());
                    recipeLinkList.add(el.attr("href"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
            System.out.println("Joined");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Reached");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, recipeNameList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(recipeLinkList.get(position)));
            startActivity(browserIntent);
        });
    }
}