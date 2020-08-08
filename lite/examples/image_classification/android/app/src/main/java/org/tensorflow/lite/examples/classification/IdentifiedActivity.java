package org.tensorflow.lite.examples.classification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.anychart.graphics.vector.Image;
import com.squareup.picasso.Picasso;

public class IdentifiedActivity extends AppCompatActivity {

    ImageView identifedIV;
    TextView identifiedTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identified);

        identifedIV = findViewById(R.id.imageViewIdentified);
        identifiedTV = findViewById(R.id.textViewIdentified);

        Intent i = getIntent();
        String url = i.getStringExtra("Image");
        String confidence = i.getStringExtra("Confidence");
        String confidence1 = i.getStringExtra("Confidence1");
        String confidence2 = i.getStringExtra("Confidence2");

        String name = i.getStringExtra("Name");
        String name1 = i.getStringExtra("Name1");
        String name2 = i.getStringExtra("Name2");

        Picasso.get().load(url).into(identifedIV);
        String format = String.format("%-50s %s\n%-50s %s\n%-50s %s\n",name,confidence,name1,confidence1,name2,confidence2);

        identifiedTV.setText(format);
    }
}