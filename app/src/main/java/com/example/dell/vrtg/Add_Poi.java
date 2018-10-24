package com.example.dell.vrtg;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static android.content.ContentValues.TAG;


public class Add_Poi extends Fragment {

    EditText place;
    EditText desc;
    Button addbtn;
    LocationManager locManager;
    DatabaseReference PointOfInterests;
    private static final int PERMISSIONS_REQUEST = 123;
    // TODO: Rename parameter arguments, choose names that match

    public Add_Poi() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Add POI To ARTG");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add__poi, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        PointOfInterests = FirebaseDatabase.getInstance().getReference("POI");

        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        } else {
            Toast.makeText(getContext(),"All Permissions granted",Toast.LENGTH_SHORT).show();
        }

        place = (EditText)view.findViewById(R.id.placeName);
        desc = (EditText) view.findViewById(R.id.description);
        addbtn = (Button) view.findViewById(R.id.btnAdd);

        Location location = getMyLocation();

        final double lati = location.getLatitude();
        final double longi = location.getLongitude();
        final double alti = location.getAltitude();
        Log.i("cordinates", "=: " + lati + " , " + longi + " , " + alti);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    addlocation(lati,longi,alti);
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    public void addlocation(double lat,double lon,double alt)
    {
        String placeIs = place.getText().toString().trim();
        String des = desc.getText().toString().trim();
        if(!TextUtils.isEmpty(placeIs) && !TextUtils.isEmpty(des))
        {
            Log.d("Started", "addlocation: Yes");
            String id= PointOfInterests.push().getKey();
            Log.d("KeyGenerated", "addlocation: Yes");

            PoiDetail poiDetail = new PoiDetail(lat,lon,alt,placeIs,des);
            Log.d("ObjectCreated", "addlocation: Yes");
            PointOfInterests.child(id).setValue(poiDetail);
            Toast.makeText(getContext(),"POI Added in DataBase Successfully",Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getContext(),"Some Field is Empty Plz fill the All Fields!",Toast.LENGTH_SHORT).show();
        }

    }

    public Location getMyLocation() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE); // Get location from GPS if it's available
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
        }
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // Location wasn't found, check the next most accurate place for the current location
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            // Finds a provider that matches the criteria
            String provider = lm.getBestProvider(criteria, true);
            // Use the provider to get the last known location
            myLocation = lm.getLastKnownLocation(provider);
        }
        return myLocation;
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            checkPermissions();
        }
    }

    private void checkPermissions() {
        String[] ungrantedPermissions = requiredPermissionsStillNeeded();
        if (ungrantedPermissions.length == 0) {
            Toast.makeText(getContext(),"Permission Granted",Toast.LENGTH_SHORT).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(ungrantedPermissions, PERMISSIONS_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private String[] requiredPermissionsStillNeeded() {

        Set<String> permissions = new HashSet<String>();
        for (String permission : getRequiredPermissions()) {
            permissions.add(permission);
        }
        for (Iterator<String> i = permissions.iterator(); i.hasNext();) {
            String permission = i.next();
            if (getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d(SplashPremissionsActivity.class.getSimpleName(),
                        "Permission: " + permission + " already granted.");
                i.remove();
            } else {
                Log.d(SplashPremissionsActivity.class.getSimpleName(),
                        "Permission: " + permission + " not yet granted.");
            }
        }
        return permissions.toArray(new String[permissions.size()]);
    }

    public String[] getRequiredPermissions() {
        String[] permissions = null;
        try {
            permissions = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),
                    PackageManager.GET_PERMISSIONS).requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (permissions == null) {
            return new String[0];
        } else {
            return permissions.clone();
        }
    }


}
