package fileencrypt.geoencrypt.admin.gpslocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import fileencrypt.geoencrypt.admin.gpslocation.Database.DatabaseAdapter;


public class FetchLocation extends AppCompatActivity {

    private static Button getLoc, Next;
    private static TextView nLat, nLongi;
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    private DatabaseAdapter dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseAdapter(this);
        dbHelper.open();
        setContentView(R.layout.activity_fetch_location);

        initControls();

        getLoc = (Button)findViewById(R.id.btngetlocation);
        getLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                getLocation();
            }
        });
    }

    private void initControls(){
        nLat =  (TextView) findViewById(R.id.editText);
        nLongi = (TextView) findViewById(R.id.editText2);
        Next = (Button) findViewById(R.id.btnext);

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogMeIn();
            }
        });
    }

    private void LogMeIn(){
        String thisLatitude = nLat.getText().toString();
        String thisLongitude = nLongi.getText().toString();

        Cursor theUser = dbHelper.fetchUser2(thisLatitude, thisLongitude);
        if(theUser!=null){
            stopManagingCursor(theUser);
            if (theUser.getCount() > 0){
                startManagingCursor(theUser);
                theUser.close();
                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(),
                        "Incorrect Latitude or Longitude!",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            stopManagingCursor(theUser);
            theUser.close();

        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Database query error",
                    Toast.LENGTH_SHORT).show();
        }
    }


    void getLocation(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                double latti = location.getLatitude();
                double longi = location.getLongitude();

                ((TextView)findViewById(R.id.editText)).setText(""+latti);
                ((TextView)findViewById(R.id.editText2)).setText(""+ longi);

            } else {
                ((TextView)findViewById(R.id.editText)).setText("Unable to find correct location");
                ((TextView)findViewById(R.id.editText2)).setText("Unable to find correct location");
            }
        }
    }


    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }

    }


}
