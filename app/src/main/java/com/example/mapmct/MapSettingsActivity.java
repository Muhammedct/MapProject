package com.example.mapmct;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.Random;

public class MapSettingsActivity extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseDatabase db = null;

    FirebaseAuth auth = null;
    GoogleMap map;

    String eklenenKonumAdi = "";

    // İzin İstek Sonucu


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        int izin1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int izin2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (izin1 == PackageManager.PERMISSION_GRANTED && izin2 == PackageManager.PERMISSION_GRANTED)
        {
            map.setMyLocationEnabled(true);


        }
        else
        {
            AlertDialog.Builder adb = new AlertDialog.Builder(MapSettingsActivity.this);
            adb.setTitle("Konum Devre Dışı")
                    .setMessage("Konum Hizmetini Açmak İçin, Uygulamayı Baştan Başlatın Veya İzinler Sekmesine Gidin")
                    .show();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_settings);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        SupportMapFragment smf = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        smf.getMapAsync(this);

        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener()
        {
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser currUser = firebaseAuth.getCurrentUser();
                if (currUser == null)
                {
                    startActivity(new Intent(MapSettingsActivity.this, MainActivity.class));
                    finish();
                }

            }
        });
    }


    public void onMapReady(GoogleMap googleMap)
    {
        Log.e("x","Harita OK");
        map = googleMap;

        db.getReference("/konumlar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Toast.makeText(MapSettingsActivity.this,"Değişim Gerçekleşti", Toast.LENGTH_SHORT)
                        .show();
                map.clear();
                Iterator<DataSnapshot> konumlarIt = dataSnapshot.getChildren().iterator();

                while (konumlarIt.hasNext())
                {
                    DataSnapshot ds = konumlarIt.next();
                    Data konumData = ds.getValue(Data.class);
                    Log.e("x","Data Geldi");
                    Log.e("x",konumData.baslik+" "+konumData.lat+","+konumData.lng);

                    MarkerOptions mo = new MarkerOptions()
                            .title(konumData.baslik)
                            .position(new LatLng(konumData.lat, konumData.lng))
                            .snippet(konumData.gonderen+"\n"+" tarafından işaretlendi")
                            .draggable(true);

                    map.addMarker(mo);


                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*
            Eğer Bu Cihazın Android Sürümü 23 Üzeriyse Veya Altıysa

         */
        if (Build.VERSION.SDK_INT >= 23)
        {
            int izin1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int izin2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (izin1 == PackageManager.PERMISSION_GRANTED && izin2 == PackageManager.PERMISSION_GRANTED)
            {
                map.setMyLocationEnabled(true);
            }
            else
            {
                // Kullanıcıdan Konum İzni İste
                ActivityCompat.requestPermissions(
                        MapSettingsActivity.this,
                        new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
                        1234
                );
            }
        }
        else
        {
            map.setMyLocationEnabled(true);
        }

        //41.042247, 29.009267
        LatLng llBAU = new LatLng(41.042247, 29.009267);

        // Camera Yonetimi
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(llBAU, 18));


        // InfoWİndow Click Listener
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker)
            {
                String msg = marker.getTitle()+" --> "+marker.getSnippet();
                Toast.makeText(MapSettingsActivity.this, msg, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        // OnLongClick Listener
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()
        {
            public void onMapLongClick(LatLng latLng)
            {
                final LatLng konum = latLng;
                final String gonderen = auth.getCurrentUser().getEmail();
                final EditText et = new EditText(MapSettingsActivity.this);
                AlertDialog.Builder adb = new AlertDialog.Builder(MapSettingsActivity.this);
                adb.setTitle("Marker Rengi")
                        .setView(et)
                        .setPositiveButton("Tamam", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.dismiss();
                                eklenenKonumAdi =  et.getText().toString();

                                if (eklenenKonumAdi.isEmpty())
                                {

                                    return;
                                }

                                Data d = new Data(eklenenKonumAdi,gonderen, konum.latitude, konum.longitude);
                                db.getReference("/konumlar").push().setValue(d);

                            }
                        })
                        .show();


            }
        });

        // Marker Drag Listener
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

             marker.setIcon(BitmapDescriptorFactory
                     .defaultMarker(250));
                Log.e("x","Started @"+marker.getPosition());
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                Log.e("x","Currently @"+marker.getPosition());
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                marker.setIcon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                Log.e("x","Dropped @"+marker.getPosition());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Log Out")
                .setIcon(android.R.drawable.ic_menu_delete)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String ad = item.getTitle().toString();
        if (ad.equals("Log Out"))
        {
            auth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }
}
