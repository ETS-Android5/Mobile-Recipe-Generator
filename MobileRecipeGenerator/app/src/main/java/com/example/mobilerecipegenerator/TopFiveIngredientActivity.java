package com.example.mobilerecipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TopFiveIngredientActivity extends AppCompatActivity {

    ListView listView;

    String[] topFiveResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topfive_ingredients);

        topFiveResults = getIntent().getStringArrayExtra("TopFiveResults");

        listView = findViewById(R.id.listView);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, topFiveResults);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedClassification = Helper.adjustClassificationName(topFiveResults[position]);
            Intent changeToRecipeScreen = new Intent(TopFiveIngredientActivity.this, RecipeSelectionActivity.class);
            changeToRecipeScreen.putExtra("ClassificationResult", selectedClassification);
            startActivity(changeToRecipeScreen);
        });
    }
}