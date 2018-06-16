package com.djes.altice.appdenunciar;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DenunciaRecyclerAdapter extends RecyclerView.Adapter<DenunciaRecyclerAdapter.MyRecycleItemViewHolder>{

    private final List<Denuncia> items;
    private final Context context;



    @Override
    public int getItemViewType(int position) {
        return R.layout.denuncia_view;
    }

    @Override
    public void onBindViewHolder(MyRecycleItemViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public DenunciaRecyclerAdapter(Context context, List<Denuncia> fruits)
    {
        this.context=context;
        this.items = fruits;
    }


    @Override
    public MyRecycleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(viewType,parent,false);
        MyRecycleItemViewHolder holder = new MyRecycleItemViewHolder(view,items,context);
        return holder;
    }

    public static class MyRecycleItemViewHolder extends RecyclerView.ViewHolder
    {

        private TextView txtUbicacion;
        private TextView txtDescripcion;
        private TextView txtFecha;
        private ImageView imgDenuncia;
        private ImageView imgShare;
        private ImageView imgPin;
        private Context _context;
        public MyRecycleItemViewHolder(View itemView, List<Denuncia> items,Context context)
        {
            super(itemView);
            txtUbicacion = (TextView) itemView.findViewById(R.id.txtUbicacion);
            txtDescripcion = (TextView) itemView.findViewById(R.id.txtDescripcion);
            txtFecha = (TextView) itemView.findViewById(R.id.txtFecha);
            imgShare = (ImageView) itemView.findViewById(R.id.imgShare);
            imgPin = (ImageView) itemView.findViewById(R.id.imgPin);
            imgDenuncia = (ImageView) itemView.findViewById(R.id.imgDenuncia);
            _context=context;
        }

        public void bind(Denuncia item) {
                                    Geocoder geocoder = new Geocoder(_context, Locale.getDefault());
                                    try {
                                        List<Address> addresses = geocoder.getFromLocation(item.getUbicacion().getLatitude(), item.getUbicacion().getLongitude(), 1);
                                        Address obj = addresses.get(0);
                                        SimpleDateFormat SDF = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy 'a las' HH:mm", Locale.getDefault());
                                        txtUbicacion.setText(obj.getSubLocality()+", "+obj.getLocality()+", "+obj.getCountryName());
                                        txtDescripcion.setText("Caso: "+item.getDescripcion());
                                        txtFecha.setText("Fecha: "+SDF.format(item.getFecha()));
                                        imgShare.setImageResource(R.drawable.share);
                                        imgPin.setImageResource(R.drawable.pin);


                                        Glide.with(_context).load(item.getPhotoUrl())
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


}
