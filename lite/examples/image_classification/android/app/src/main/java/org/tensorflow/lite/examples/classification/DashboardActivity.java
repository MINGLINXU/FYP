package org.tensorflow.lite.examples.classification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends Fragment {
    private static String TAG = "DashboardActivity";

    List<DataEntry> dataEntries = new ArrayList<>();

    private final String COLLECTION_KEY = "identified";

    AnyChartView anyChartView;
    FirebaseFirestore fbFirestore;
    DocumentReference IdentifiedRef;
    FirebaseStorage fbStorage;
    Spinner spinner;
    Button btnDownload;
    ArrayList<Identified> identifiedList = new ArrayList<Identified>();
    ArrayList<String> identifiedIDList = new ArrayList<>();

    int tempnum = 0;


    ArrayList<String> labels = new ArrayList<>();
    ArrayList<Integer> count = new ArrayList<>();

    ArrayList<String> filteredLabels = new ArrayList<>();
    ArrayList<Integer> filteredCount = new ArrayList<>();


    ArrayList<String> diseaseNames = new ArrayList<>();
    int num = 0;
    String newName = "";
    String category;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        anyChartView = view.findViewById(R.id.any_chart_view);
        fbFirestore = FirebaseFirestore.getInstance();
        fbStorage = FirebaseStorage.getInstance();
        spinner = view.findViewById(R.id.spinnerNames);
        btnDownload = view.findViewById(R.id.btnDownload);

        checkPermission();

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "download toast", Toast.LENGTH_SHORT).show();
                downloadImages();
            }
        });

        diseaseNames.add("Pepper bell Bacterial spot");
        diseaseNames.add("Pepper bell healthy");
        diseaseNames.add("Potato Early blight");
        diseaseNames.add("Potato healthy");
        diseaseNames.add("Potato Late_blight");
        diseaseNames.add("Tomato Bacterial spot");
        diseaseNames.add("Tomato Early blight");
        diseaseNames.add("Tomato healthy");
        diseaseNames.add("Tomato Late blight");
        diseaseNames.add("Tomato Leaf Mold");
        diseaseNames.add("Tomato Septoria leaf spot");
        diseaseNames.add("Tomato Spider mites Two-spotted spider mite");
        diseaseNames.add("Tomato Target Spot");
        diseaseNames.add("Tomato Tomato mosaic virus");
        diseaseNames.add("Tomato Tomato Yellow Leaf Curl Virus");

        retrieveData();


        Pie pie = AnyChart.pie();
        pie.title("Number of identified diseases");

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Disease Names")
                .padding(0d, 0d, 10d, 0d);

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
                filteredLabels.clear();
                filteredCount.clear();
                dataEntries.clear();
                if (!category.equals("Filter by...")) {
                    for (int i = 0; i < labels.size(); i++) {
                        if (labels.get(i).contains(category)) {
                            filteredLabels.add(labels.get(i));
                            filteredCount.add(count.get(i));
                        }
                    }

                    Log.d("occur", "pie setup");
                    for (int i = 0; i < filteredLabels.size(); i++) {
                        Log.d("pie label", filteredLabels + "");
                        Log.d("pie count", filteredCount + "");
                        dataEntries.add(new ValueDataEntry(filteredLabels.get(i), filteredCount.get(i)));
                        pie.data(dataEntries);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    private void downloadImages() {

        fbFirestore.collection(COLLECTION_KEY).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot document: task.getResult()){
                        Log.d("IdentificationList", document.getId() + " => " + document.getData());

                        String identified = document.getId();
                        identifiedIDList.add(identified);
                        RetrieveData(identified);

                    }

                }
                else{
                    Log.d("IdentificationActivity", "Error getting documents: ", task.getException());
                }

            }
        });
    }

    private void RetrieveData(String identified) {
        IdentifiedRef = fbFirestore.collection("identified").document(identified);
        IdentifiedRef.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String oldDiseaseName = documentSnapshot.getString("oldDiseaseName");
                String newDiseaseName = documentSnapshot.getString("newDiseaseName");
                String image = documentSnapshot.getString("image");
                String documentID = documentSnapshot.getId();

                identifiedList.add(new Identified(oldDiseaseName, newDiseaseName, image));
                Log.d("Loop List size: ",identifiedList.size() + "");

                StorageReference imageRef = fbStorage.getReferenceFromUrl(image);

                String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PlantDiseaseImages/";

                File folder = new File(folderLocation);
                if (folder.exists() == false){
                    boolean result = folder.mkdir();
                    if (result == true){
                        Log.d("File Read/Write", "Folder created");
                    }
                }

                final File rootPath = new File(folderLocation, newDiseaseName);

                if (!rootPath.exists()) {
                    rootPath.mkdirs();
                }

                tempnum +=1;

                final File localFile = new File(rootPath, documentID + ".jpg");

                imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener <FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.e("firebase ", ";local tem file created  created " + localFile.toString());

                        if (!isVisible()){
                            return;
                        }

                        if (localFile.canRead()){

                        }

                        Toast.makeText(getContext(), "Download Completed", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getContext(), "External storage/" + newDiseaseName + "/" + documentID + ".jpg", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                        Toast.makeText(getContext(), "Download Incompleted", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }


    private void retrieveData() {
        for (int i = 0; i < diseaseNames.size(); i++) {
            String name = diseaseNames.get(i);

            fbFirestore.collection("identified").whereEqualTo("newDiseaseName", name).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    newName = document.getString("newDiseaseName");
                                    Log.d("occur", "Firestore");
                                    Log.d(TAG, "testing " + document.getId() + " => " + document.getData() + " name :" + newName);
                                    num++;
                                }

                                Log.d(TAG, " Labels testing " + labels);
                                if (num > 0) {
                                    count.add(num);
                                }

                                Log.d(TAG, " count testing " + count);

                                if (!newName.equals("")) {
                                    labels.add(newName);
                                }
                                newName = "";
                                num = 0;

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }


    }

    public boolean checkPermission() {

        int permissionCheck_Storage = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck_Storage != PermissionChecker.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return false;
        }

        return true;
    }


}
