package org.tensorflow.lite.examples.classification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class IdentificationListActivity extends Fragment {

    ListView lv;
    Identification_list_Adapter identificationAdapter;
    ArrayList<Identification> identificationList = new ArrayList<Identification>();

    ArrayList<String> identificationIDList = new ArrayList<>();

    private final String COLLECTION_KEY = "identification";

    private FirebaseFirestore db;
    DocumentReference IdentifcationRef;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_identification_list, container,false);
        lv = view.findViewById(R.id.lv_identification);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Identification selectedIdentification = identificationList.get(position);

                Intent i = new Intent(getActivity(),IdentificationDetailsActivity.class);
                i.putExtra("pos",position);
                i.putExtra("identification",selectedIdentification);
                i.putExtra("FirebaseID", identificationIDList);
                startActivity(i);


            }
        });

        db = FirebaseFirestore.getInstance();
        identificationAdapter = new Identification_list_Adapter(getContext(),R.layout.identification_row,identificationList);

        db.collection(COLLECTION_KEY).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot document: task.getResult()){
                        Log.d("IdentificationList", document.getId() + " => " + document.getData());

                        String identification = document.getId();
                        identificationIDList.add(identification);
                        RetrieveData(identification);

                    }

                }
                else{
                    Log.d("IdentificationActivity", "Error getting documents: ", task.getException());
                }

            }
        });

        identificationAdapter.addAll(identificationList);
        return view;
    }

    private void RetrieveData(String identification) {

        IdentifcationRef = db.collection("identification").document(identification);
        IdentifcationRef.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String diseaseName = documentSnapshot.getString("diseaseName");
                Double percentage = documentSnapshot.getDouble("percentage");
                String strPercentage = percentage + "";
                Float fltPercentage = Float.parseFloat(strPercentage);

                identificationList.add(new Identification(diseaseName,fltPercentage));
                lv.setAdapter(identificationAdapter);
                Toast.makeText(getActivity(), diseaseName + fltPercentage, Toast.LENGTH_SHORT).show();

            }
        });

    }

}
