package org.tensorflow.lite.examples.classification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
    Button btnLogout;

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
        btnLogout = view.findViewById(R.id.btn_logout);

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

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder
                        .setMessage("Are You Sure You Want To Logout?")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getContext(), LoginActivity.class));
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {



                            }
                        });
                alertDialogBuilder.show();
            }
        });



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
                String image = documentSnapshot.getString("image");

                identificationList.add(new Identification(diseaseName,fltPercentage, image));
                Log.d("Loop List size: ",identificationList.size() + "");
                lv.setAdapter(identificationAdapter);

            }
        });

    }

    @Override
    public void onStart() {
        db = FirebaseFirestore.getInstance();
        identificationAdapter = new Identification_list_Adapter(getContext(),R.layout.identification_row,identificationList);
        identificationList.clear();
        identificationIDList.clear();



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
        Log.d("List size: ",identificationList.size() + "");
        Log.d("ID size: ",identificationIDList.size() +"");
        identificationAdapter.notifyDataSetChanged();


        super.onStart();
    }
}
