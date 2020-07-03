package org.tensorflow.lite.examples.classification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DashboardActivity extends Fragment {

    private static String TAG = "DashboardActivity";

    int count = 0;

    ArrayList<Float> yData = new ArrayList<Float>();
    ArrayList<String> xData = new ArrayList<String>();

    ArrayList<String> labels = new ArrayList<>();


    //private float[] yData = {25.3f, 10.6f, 66.76f, 44.32f, 46.01f, 16.89f, 23.9f};
    // private String[] xData = {"Mitch", "Jessica" , "Mohammad" , "Kelsey", "Sam", "Robert", "Ashley"};
    PieChart pieChart;

    TextView tvTitle;

    FirebaseFirestore fbFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        pieChart = (PieChart) view.findViewById(R.id.idPieChart);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        fbFirestore = FirebaseFirestore.getInstance();

        labels.add("0 Pepper bell Bacterial spot");
        labels.add("1 Pepper bell healthy");
        labels.add("2 Potato Early blight");
        labels.add("3 Potato healthy");
        labels.add("4 Potato Late_blight");
        labels.add("5 Tomato Bacterial spot");
        labels.add("6 Tomato Early blight");
        labels.add("7 Tomato healthy");
        labels.add("8 Tomato Late blight");
        labels.add("9 Tomato Leaf Mold");
        labels.add("10 Tomato Septoria leaf spot");
        labels.add("11 Tomato Spider mites Two-spotted spider mite");
        labels.add("12 Tomato Target Spot");
        labels.add("13 Tomato Tomato mosaic virus");
        labels.add("14 Tomato Tomato Yellow Leaf Curl Virus");


        yData.add(25.3f);
        yData.add(10.6f);
        yData.add(66.76f);
        yData.add(44.32f);
        yData.add(46.01f);
        yData.add(16.89f);
        yData.add(23.9f);

        xData.add("Mitch");
        xData.add("Jessica");
        xData.add("Mohammad");
        xData.add("Kelsey");
        xData.add("Sam");
        xData.add("Robert");
        xData.add("AShley");

        fbFirestore.collection("identified").whereEqualTo("newDiseaseName", "8 Tomato Late blight").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "testing " + document.getId() + " => " + document.getData());
                                count++;

                            }
                            Toast.makeText(getContext(), "There are " + count + "results for 8 Tomato Late blight", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



        tvTitle.setText("");

        pieChart.getDescription().setText("Sales by employee (In Thousands $) ");
        pieChart.setRotationEnabled(true);
        //pieChart.setUsePercentValues(true);
        //pieChart.setHoleColor(Color.BLUE);
        //pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Super Cool Chart");
        pieChart.setCenterTextSize(10);
        //pieChart.setDrawEntryLabels(true);
        //pieChart.setEntryLabelTextSize(20);
        //More options just check out the documentation!

        addDataSet();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: Value select from chart.");
                Log.d(TAG, "onValueSelected: " + e.toString());
                Log.d(TAG, "onValueSelected: " + h.toString());

                int pos1 = e.toString().indexOf("y: ");
                String sales = e.toString().substring(pos1 + 3);

                for(int i = 0; i < yData.size(); i++){
                    if(yData.get(i) == Float.parseFloat(sales)){
                        pos1 = i;
                        break;
                    }
                }
                // String employee = xData[pos1 + 1];
                String employee = xData.get(pos1);
                Toast.makeText(getContext(), "Employee " + employee + "\n" + "Sales: $" + sales + "K", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        return view;

    }

    private void addDataSet() {
        Log.d(TAG, "addDataSet started");
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for(int i = 0; i < yData.size(); i++){
            yEntrys.add(new PieEntry(yData.get(i) , i));
        }

        for(int i = 1; i < xData.size(); i++){
            xEntrys.add(xData.get(i));
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "Employee Sales");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);
        colors.add(Color.LTGRAY);
        colors.add(Color.MAGENTA);

        pieDataSet.setColors(colors);

        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
