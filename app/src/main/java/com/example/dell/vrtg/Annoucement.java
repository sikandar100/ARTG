package com.example.dell.vrtg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Annoucement extends AppCompatActivity {

    Button prcBtn;
    DatabaseReference AllPoints;
    JSONArray MainArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annoucement);
        this.setTitle("Announcement");

        prcBtn = (Button) findViewById(R.id.proceedBtn);

        prcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Plz Wait till Application gets All POIs!",Toast.LENGTH_LONG).show();

                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                AllPoints = FirebaseDatabase.getInstance().getReference("POI");
                AllPoints.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot POISnapshot : dataSnapshot.getChildren()) {

                            PoiDetail poiDetail = POISnapshot.getValue(PoiDetail.class);
                            System.out.println("testing " + poiDetail.getLatitude());

                            JSONObject jsonObj = new JSONObject();
                            try {

                                jsonObj.put("latitude", poiDetail.getLatitude());
                                jsonObj.put("longitude", poiDetail.getLongitude());
                                jsonObj.put("altitude", poiDetail.getAltitude());
                                jsonObj.put("place", poiDetail.getPlaceName());
                                jsonObj.put("data", poiDetail.getDescription());

                                MainArray.put(jsonObj);
                                Log.d("objectTesting", "points : " + MainArray.toString());

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                        editor.putString(getString(R.string.Points), MainArray.toString());
                        editor.commit();
                        Log.d("testing123", "onDataChange: ");
                        String FinalPoints = sharedPreferences.getString(getString(R.string.Points),"0");
                        Log.d("objectTesting3", "points : " + FinalPoints);

                        Intent i=new Intent(getApplicationContext(),SplashPremissionsActivity.class);
                        startActivity(i);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

            }
        });


    }
}
