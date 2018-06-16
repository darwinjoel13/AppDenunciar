package com.djes.altice.appdenunciar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private ImageView btnCamera;
    private Button btnPublicar;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mUbicacion;
    private EditText txtDescripcion;
    private TextView txtUbicacion;
    private Bitmap Image;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Image = imageBitmap;
            btnCamera.setImageBitmap(imageBitmap);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        btnCamera = findViewById(R.id.btnCamera);
        txtUbicacion =  findViewById(R.id.txtUbicacion);
        btnPublicar = findViewById(R.id.btnPublicar);
        txtDescripcion =  findViewById(R.id.txtDescripcion);

        btnPublicar.setOnClickListener(v->{
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            StorageReference denunciaRef = storageRef.child("Denuncia_"+ new Date().getTime());


            // Get the data from an ImageView as bytes
            btnCamera.setDrawingCacheEnabled(true);
            btnCamera.buildDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if(Image==null)
            {
                Drawable drawable = getResources().getDrawable( R.drawable.no_image);
                Bitmap mutableBitmap = Bitmap.createBitmap(340, 160, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(mutableBitmap);
                drawable.setBounds(0, 0, 340, 160);
                drawable.draw(canvas);
                Image = mutableBitmap;
            }
            Image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = denunciaRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String PhotoUrl = taskSnapshot.getDownloadUrl().toString();
                    Double Latitude = mUbicacion.getLatitude();
                    Double Longitude = mUbicacion.getLongitude();
                    GeoPoint GP = new GeoPoint(Latitude,Longitude);
                    String Descripcion = txtDescripcion.getText().toString();
                    String Usuario = "despinosa";
                    Date Fecha = Calendar.getInstance().getTime();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> denuncia = new HashMap<>();
                    denuncia.put("descripcion",Descripcion);
                    denuncia.put("ubicacion",GP);
                    denuncia.put("photoUrl",PhotoUrl);
                    denuncia.put("fecha",Fecha);
                    denuncia.put("usuario",Usuario);

                    db.collection("denuncias").add(denuncia)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    AlertDialog.Builder builder;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        builder = new AlertDialog.Builder(NewActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                                    } else {
                                        builder = new AlertDialog.Builder(NewActivity.this);
                                    }
                                    builder.setTitle("Publicar Denuncia- App Denunciar")
                                            .setMessage("Su Denuncia fue publicada Exitosamente!")
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_info)
                                            .show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });





                }
            });

        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(NewActivity.this, Locale.getDefault());
                                try {
                                    mUbicacion = location;
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    Address obj = addresses.get(0);
                                    txtUbicacion.setText(obj.getSubLocality()+", "+obj.getLocality()+", "+obj.getCountryName());
                                }
                                catch(Exception e)
                                {
                                      e.printStackTrace();
                                }
                            }
                        }
                    });
        }




        btnCamera.setOnClickListener(v->{
            if(checkCameraHardware(NewActivity.this))
            {
                dispatchTakePictureIntent();
            }
        });

    }


    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


}
