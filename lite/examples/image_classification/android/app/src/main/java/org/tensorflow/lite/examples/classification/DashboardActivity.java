package org.tensorflow.lite.examples.classification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;
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

    AnyChartView anyChartView;
    FirebaseFirestore fbFirestore;
    CollectionReference IdentifiedRef;
    FirebaseStorage fbStorage;
    Spinner spinner;
    Button btnDownload;

//    String[] months = {"Jan","feb","Mar","abc","efg","6","77","88","99","10","11","12","13","14","15"};
//    int[] earnings = {100,200,300,234,567,456,100,200,300,234,567,456,100,200,300};

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
        IdentifiedRef = fbFirestore.collection("identified");
        spinner = view.findViewById(R.id.spinnerNames);
        btnDownload = view.findViewById(R.id.btnDownload);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "download toast", Toast.LENGTH_SHORT).show();
//                downloadImages();
            }
        });

        diseaseNames.add("0 Pepper bell Bacterial spot");
        diseaseNames.add("1 Pepper bell healthy");
        diseaseNames.add("2 Potato Early blight");
        diseaseNames.add("3 Potato healthy");
        diseaseNames.add("4 Potato Late_blight");
        diseaseNames.add("5 Tomato Bacterial spot");
        diseaseNames.add("6 Tomato Early blight");
        diseaseNames.add("7 Tomato healthy");
        diseaseNames.add("8 Tomato Late blight");
        diseaseNames.add("9 Tomato Leaf Mold");
        diseaseNames.add("10 Tomato Septoria leaf spot");
        diseaseNames.add("11 Tomato Spider mites Two-spotted spider mite");
        diseaseNames.add("12 Tomato Target Spot");
        diseaseNames.add("13 Tomato Tomato mosaic virus");
        diseaseNames.add("14 Tomato Tomato Yellow Leaf Curl Virus");

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
        StorageReference imageRef = fbStorage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/fyp-plant-disease-detection.appspot.com/o/images%2F07-22-20%2004-11-29?alt=media&token=82cb032f-f85b-48de-93aa-9a098a0fd382");

        String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImageTest";

        File folder = new File(folderLocation);
        if (folder.exists() == false){
            boolean result = folder.mkdir();
            if (result == true){
                Log.d("File Read/Write", "Folder created");
            }
        }

        final File rootPath = new File(folderLocation, "ImageTest");

        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }


        final File localFile = new File(rootPath, "Nature.jpg");

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
                Toast.makeText(getContext(), "Internal storage/MADBO/Nature.jpg", Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                Toast.makeText(getContext(), "Download Incompleted", Toast.LENGTH_LONG).show();
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

}
