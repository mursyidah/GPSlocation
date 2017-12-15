package fileencrypt.geoencrypt.admin.gpslocation;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import fileencrypt.geoencrypt.admin.gpslocation.Database.DatabaseAdapter;


public class StoreLocation extends AppCompatActivity {

    private EditText Latitude;
    private EditText Longitude;
    private Button SaveLoc;

    private DatabaseAdapter dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseAdapter(this);
        dbHelper.open();
        setContentView(R.layout.activity_store_location);
        initControl();
    }
    private void initControl() {
        Latitude = (EditText) findViewById(R.id.editText);
        Longitude = (EditText) findViewById(R.id.editText2);
        SaveLoc = (Button) findViewById(R.id.button2);

        SaveLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterMe(view);
            }
        });
    }
    public void RegisterMe(View view){
        String latitude = Latitude.getText().toString();
        String longitude = Longitude.getText().toString();

        if (latitude.equals("") || longitude.equals("")) {
            Toast.makeText(getApplicationContext(), "Please ensure all fields have been completed", Toast.LENGTH_SHORT).show();
            return;
        }



        Cursor user = dbHelper.fetchUser2(latitude, longitude);
        if (user == null) {
            Toast.makeText(getApplicationContext(), "Database query error", Toast.LENGTH_SHORT).show();

        } else {
            startManagingCursor(user);

            if (user.getCount() > 0) {
                Toast.makeText(getApplicationContext(), "The location is already registered",
                        Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), StoreLocation.class);
                startActivity(i);
                stopManagingCursor(user);
                user.close();

                return;
            }
            stopManagingCursor(user);
            user.close();
            user = dbHelper.fetchUser2(latitude, longitude);
            if (user == null) {
                Toast.makeText(getApplicationContext(), "Database query error",
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                startManagingCursor(user);

                if (user.getCount() > 0) {
                    Toast.makeText(getApplicationContext(), "The location is already registered",
                            Toast.LENGTH_SHORT).show();
                    stopManagingCursor(user);
                    user.close();
                    return;
                }
                stopManagingCursor(user);
                user.close();
            }
            long id = dbHelper.createUser2(latitude, longitude);
            if (id > 0) {
                Toast.makeText(getApplicationContext(), "Your location was created",
                        Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Fail to create new location",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
