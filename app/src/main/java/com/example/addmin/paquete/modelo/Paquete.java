package com.example.addmin.paquete.modelo;


import com.example.addmin.paquete.R;

public class Paquete {
    private String ancho;
    private String largo;
    private String altura;
    private String Peso;
    private String contenido;
    private int imagen;

    public String getAncho() {
        return ancho;
    }
    public String getAltura() {
        if(altura == "")altura = "0";
        return altura;
    }
    public String getContenido() {
        return contenido;
    }
    public int getImagen() {
        if(altura != null)
            imagen = R.drawable.paquete;
        else imagen = R.drawable.sobre;
        return imagen;
    }
    public String getLabel_altura() { String  label = "";
        return label;
    }
    public String getlabel_paqute(int i) { String  label = "Paquete "+ (i+1);
        return label;
    }
    public String getLargo() {
        return largo;
    }
    public String getPeso() {
        return Peso;
    }

}