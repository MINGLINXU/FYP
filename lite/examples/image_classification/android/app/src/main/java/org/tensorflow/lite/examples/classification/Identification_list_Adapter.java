package org.tensorflow.lite.examples.classification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Identification_list_Adapter extends ArrayAdapter<Identification> {


    ArrayList<Identification> identificationList;
    Context context;
    FirebaseAuth fbAuth;
    FirebaseFirestore fbFirestore;
    int resource;
    ImageView ivImage;
    TextView tvName, tv_percentage;


    public Identification_list_Adapter(Context context,int resource, ArrayList<Identification> identificationList) {
        super(context, resource, identificationList);
        this.context = context;
        this.identificationList = identificationList;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(resource, parent, false);


        ivImage = rowView.findViewById(R.id.iv_Image);
        tvName = rowView.findViewById(R.id.tv_Name);
        tv_percentage = rowView.findViewById(R.id.tv_percentage);
        fbAuth = FirebaseAuth.getInstance();
        fbFirestore = FirebaseFirestore.getInstance();

        Identification currentItem = identificationList.get(position);

        tvName.setText(currentItem.getdiseaseName());
        tv_percentage.setText(currentItem.getPercentage().toString() + "%");



        return rowView;
    }


}

