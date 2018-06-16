package com.djes.altice.appdenunciar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private FirestoreRecyclerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView rv;
    private FirebaseFirestore db;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        getDenuncias();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(HomeActivity.this, NewActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        rv = findViewById(R.id.myRecyclerView);
        rv.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    private void getDenuncias()
    {
        Query query = db.collection("denuncias").orderBy("fecha", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Denuncia> response = new FirestoreRecyclerOptions.Builder<Denuncia>()
                .setQuery(query, Denuncia.class)
                .build();


            adapter = new FirestoreRecyclerAdapter<Denuncia,DenunciasHolder>(response) {
            @Override
            public void onBindViewHolder(DenunciasHolder holder, int position, Denuncia model) {
                holder.bind(model);
            }
            @Override
            public DenunciasHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.denuncia_view, group, false);
                return new DenunciasHolder(view);
            }
            @Override
            public void onError(FirebaseFirestoreException e) {

            }
        };
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
    }


    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File("/sdcard/Denuncias/"+"Denuncia" + ".jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();


        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    public void fn_share(String path) {

        File file = new File("/mnt/" + path);

        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(intent, "Share Image"));


    }


    public class DenunciasHolder extends RecyclerView.ViewHolder {
        private TextView txtUbicacion;
        private TextView txtDescripcion;
        private TextView txtFecha;
        private ImageView imgDenuncia;
        private ImageView imgShare;
        private ImageView imgPin;
        private LinearLayout _view;

        public DenunciasHolder(View itemView) {
            super(itemView);
            _view=itemView.findViewById(R.id.my_denuncia);
            txtUbicacion = (TextView) itemView.findViewById(R.id.txtUbicacion);
            txtDescripcion = (TextView) itemView.findViewById(R.id.txtDescripcion);
            txtFecha = (TextView) itemView.findViewById(R.id.txtFecha);
            imgShare = (ImageView) itemView.findViewById(R.id.imgShare);
            imgPin = (ImageView) itemView.findViewById(R.id.imgPin);
            imgDenuncia = (ImageView) itemView.findViewById(R.id.imgDenuncia);
        }

        private void bind(Denuncia model) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(model.getUbicacion().getLatitude(), model.getUbicacion().getLongitude(), 1);
                Address obj = addresses.get(0);
                SimpleDateFormat SDF = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy 'a las' HH:mm", Locale.getDefault());
                txtUbicacion.setText(obj.getSubLocality()+", "+obj.getLocality()+", "+obj.getCountryName());
                txtDescripcion.setText("Caso: "+model.getDescripcion());
                txtFecha.setText("Fecha: "+SDF.format(model.getFecha()));
                imgShare.setImageResource(R.drawable.share);
                imgPin.setImageResource(R.drawable.pin);

                File dir = new File("/sdcard/Denuncias/");
                try {
                    if (dir.mkdir()) {
                        System.out.println("Directoryted");
                    } else {
                        System.out.println("Directoryot created");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                imgShare.setOnClickListener(v->{
                    Bitmap bitmap1 = loadBitmapFromView(_view, _view.getWidth(), _view.getHeight());
                    saveBitmap(bitmap1);
                    String str_screenshot = "/sdcard/Denuncias/"+"Denuncia"+".jpg";

                    fn_share(str_screenshot);
                });


                Glide.with(getApplicationContext()).load(model.getPhotoUrl())
                        .apply(new RequestOptions()
                                .override(340,160).fitCenter())
                        .into(imgDenuncia);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
