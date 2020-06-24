package org.tensorflow.lite.examples.classification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class IdentificationListActivity extends Fragment {

    ListView lv;
    Identification_list_Adapter identificationAdapter;
    ArrayList<Identification> identificationList = new ArrayList<Identification>();
    private final String COLLECTION_KEY = "identification";

    private FirebaseFirestore db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_identification_list, container,false);
        lv = view.findViewById(R.id.lv_identification);

        db = FirebaseFirestore.getInstance();
        identificationAdapter = new Identification_list_Adapter(getContext(),R.layout.identification_row,identificationList);

        db.collection(COLLECTION_KEY).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot document: task.getResult()){
                        Log.d("IdentificationList", document.getId() + " => " + document.getData());


//                        Identification identity = document.toObject(Identification.class);
//                        identificationList.add(identity);
                        Identification identificationA = new Identification("data", (float) 99.1212);
                        identificationList.add(identificationA);



                    }
                    lv.setAdapter(identificationAdapter);
                }
                else{
                    Log.d("IdentificationActivity", "Error getting documents: ", task.getException());
                }

            }
        });




        identificationAdapter.addAll(identificationList);
        return view;
    }

}
