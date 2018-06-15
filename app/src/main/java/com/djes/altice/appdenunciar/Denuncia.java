package com.djes.altice.appdenunciar;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Denuncia {

    private String Descripcion;
    private Date Fecha;
    private String PhotoUrl;
    private GeoPoint Ubicacion;
    private String Usuario;

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.Descripcion = descripcion;
    }

    public Date getFecha() {
        return Fecha;
    }

    public void setFecha(Date fecha) {
        this.Fecha = fecha;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.PhotoUrl = photoUrl;
    }

    public GeoPoint getUbicacion() {
        return Ubicacion;
    }

    public void setUbicacion(GeoPoint ubicacion) {
        this.Ubicacion = ubicacion;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        this.Usuario = usuario;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("descripcion", Descripcion);
        result.put("fecha", Fecha);
        result.put("photoUrl", PhotoUrl);
        result.put("ubicacion", Ubicacion);
        result.put("usuario", Usuario);

        return result;
    }
}
