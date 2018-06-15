package com.djes.altice.appdenunciar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<Denuncia> listDenuncias = new ArrayList<Denuncia>();

        db.collection("denuncias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                for(Object o:document.getData().values())
                                {
                                    listDenuncias.add((Denuncia) o);
                                }
                            }
                        } else {

                        }
                    }
                });

        RecyclerView rv = findViewById(R.id.myRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL,false));
        rv.setAdapter(new DenunciaRecyclerAdapter(getApplicationContext(),listDenuncias));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(HomeActivity.this, NewActivity.class);
                startActivity(intent);
            }
        });
    }

}
