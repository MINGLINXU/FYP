package org.tensorflow.lite.examples.classification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class IdentificationDetailsActivity extends AppCompatActivity {
    TextView tvTitle;
    RadioGroup rg;
    RadioButton rb;
    Button btnUpdate;
    Spinner spinner;
    ImageView ivImage;

    String text;

    FirebaseFirestore fbFirestore;
    CollectionReference IdentifiedRef;
    FirebaseStorage fbStorage;

    private static final String TAG = "DetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification_details);

        rg = findViewById(R.id.rg);

        btnUpdate = findViewById(R.id.btn_Update);
        spinner = findViewById(R.id.spinner);
        spinner.setVisibility(View.INVISIBLE);
        tvTitle = findViewById(R.id.tv_identified);
        ivImage = findViewById(R.id.iv_photo);

        fbFirestore = FirebaseFirestore.getInstance();
        fbStorage = FirebaseStorage.getInstance();
        IdentifiedRef = fbFirestore.collection("identified");

        Intent i = getIntent();
        Identification ids = (Identification) i.getSerializableExtra("identification");

        tvTitle.setText("Identified as : " + ids.getdiseaseName());

        btnUpdate.setEnabled(false);

        Log.d("checkingImage", ids.getImage()+"");
        Picasso.get().load(ids.getImage()).into(ivImage);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                rb=(RadioButton)findViewById(checkedId);

                btnUpdate.setEnabled(true);

                text = rb.getText() +"";

                if(text.equals("Correct")){
                    spinner.setVisibility(View.INVISIBLE);
                }else {
                    spinner.setVisibility(View.VISIBLE);

                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToFireBase();

            }
        });

    }

    private void sendDataToFireBase() {

        Intent i = getIntent();
        Identification ids = (Identification) i.getSerializableExtra("identification");

        if(text.equals("Correct")){
            String disease = ids.getdiseaseName();
            Identified identified = new Identified(ids.getdiseaseName(), disease, ids.getImage());
            IdentifiedRef.add(identified)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(IdentificationDetailsActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                            deleteFromFirebase();
                            Intent i = new Intent(IdentificationDetailsActivity.this,BottomNavActivity.class);
                            startActivity(i);
                        }

                        private void deleteFromFirebase() {

        Intent i = getIntent();

        int pos = i.getIntExtra("pos",0);
        ArrayList<String> firebaseID = i.getStringArrayListExtra("FirebaseID");

        fbFirestore.collection("identification").document(firebaseID.get(pos))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot "+ firebaseID.get(pos) + "successfully deleted!");
//                        Intent i = new Intent(IdentificationDetailsActivity.this,BottomNavActivity.class);
//                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
                            }
                    });
        }else {
            String disease = spinner.getSelectedItem().toString();

            if (disease.equalsIgnoreCase("Not-applicable")){
                Intent intent = getIntent();

                int pos = intent.getIntExtra("pos", 0);
                ArrayList<String> firebaseID = intent.getStringArrayListExtra("FirebaseID");

                fbFirestore.collection("identification").document(firebaseID.get(pos))
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent i = new Intent(IdentificationDetailsActivity.this, BottomNavActivity.class);
                                startActivity(i);
                                Log.d(TAG, "DocumentSnapshot " + firebaseID.get(pos) + "successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
            }else {
                Identified identified = new Identified(ids.getdiseaseName(), disease, ids.getImage());
                IdentifiedRef.add(identified)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(IdentificationDetailsActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                deleteFromFirebase();
                            }

                            private void deleteFromFirebase() {
                                Intent i = getIntent();

                                int pos = i.getIntExtra("pos", 0);
                                ArrayList<String> firebaseID = i.getStringArrayListExtra("FirebaseID");

                                fbFirestore.collection("identification").document(firebaseID.get(pos))
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent i = new Intent(IdentificationDetailsActivity.this, BottomNavActivity.class);
                                                startActivity(i);
                                                Log.d(TAG, "DocumentSnapshot " + firebaseID.get(pos) + "successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(IdentificationDetailsActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, e.toString());
                            }
                        });
            }
        }

    }
}
