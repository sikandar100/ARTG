package com.example.dell.vrtg;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class Search extends Fragment {

    EditText keyword;
    Button SearchBtn;
    String SearchedTextKeyword;
    DatabaseReference PointOfInterests;
   // JSONArray MainArray = new JSONArray();



    public Search() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Search into ARTG");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        keyword = (EditText)view.findViewById(R.id.searchText);
        SearchBtn = (Button)view.findViewById(R.id.searchBtn);

        SearchedTextKeyword = keyword.getText().toString().trim();

        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searching();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

public void searching ()
{

    /*final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    final SharedPreferences.Editor editor = sharedPreferences.edit();*/
    final JSONArray MainArray = new JSONArray();
    SearchedTextKeyword = keyword.getText().toString().trim();

    PointOfInterests = FirebaseDatabase.getInstance().getReference("POI");
    PointOfInterests.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            for (DataSnapshot POISnapshot : dataSnapshot.getChildren()) {

                PoiDetail poiDetail = POISnapshot.getValue(PoiDetail.class);
                String fromFireBase = poiDetail.getPlaceName().toString().trim();

                if(fromFireBase.equals(SearchedTextKeyword)) {
                    JSONObject jsonObj = new JSONObject();
                    try {

                        jsonObj.put("latitude", poiDetail.getLatitude());
                        jsonObj.put("longitude", poiDetail.getLongitude());
                        jsonObj.put("altitude", poiDetail.getAltitude());
                        jsonObj.put("place", poiDetail.getPlaceName());
                        jsonObj.put("data", poiDetail.getDescription());

                        MainArray.put(jsonObj);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getContext().getApplicationContext().getString(R.string.searchedPoint),MainArray.toString());
            editor.commit();
            Log.d("testing", "onDataChange: "+ MainArray.toString());
            String FinalPoints = sharedPreferences.getString(getContext().getString(R.string.searchedPoint),"0");
            Log.d("testing1", "onDataChange: "+FinalPoints);

            if(MainArray != null && MainArray.length() > 0) {
                editor.putString(getContext().getApplicationContext().getString(R.string.searchedOrNot),"Yes");
                editor.commit();
                Intent i=new Intent(getContext(),SplashPremissionsActivity.class);
                startActivity(i);
                //Toast.makeText(getContext(), "SearchComplete: ", Toast.LENGTH_SHORT).show();

            }
            else
            {
                Toast.makeText(getContext(), "Nothing Found: Plz Try Again", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });



}

}
