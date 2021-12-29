package com.example.mobilerecipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class IngredientVerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_verification);

        TextView classificationResultTitle = findViewById(R.id.classificationResult);
        Button incorrect = findViewById(R.id.incorrectButton);
        Button correct = findViewById(R.id.correctButton);

        String[] topFiveResults = getIntent().getStringArrayExtra("ClassificationResults");
        String classification = topFiveResults[0];
        classificationResultTitle.setText(classification);

        incorrect.setOnClickListener(v -> {
            Intent changeToIncorrectIngredientScreen = new Intent(IngredientVerificationActivity.this, TopFiveIngredientActivity.class);
            changeToIncorrectIngredientScreen.putExtra("TopFiveResults", topFiveResults);
            startActivity(changeToIncorrectIngredientScreen);
        });

        correct.setOnClickListener(v -> {
            String classificationY = Helper.adjustClassificationName(classification);
            Intent changeToRecipeScreen = new Intent(IngredientVerificationActivity.this, RecipeSelectionActivity.class);
            changeToRecipeScreen.putExtra("ClassificationResult", classificationY);
            startActivity(changeToRecipeScreen);
        });
    }
}