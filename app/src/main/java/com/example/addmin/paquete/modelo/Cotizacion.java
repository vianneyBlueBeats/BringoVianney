package com.example.addmin.paquete.modelo;

import com.example.addmin.paquete.R;


public class Cotizacion {
    private String precio;
    private String precio_embalaje;
    private String label_embalaje;
    private String operador_logistico;
    private int imagen;

    public Cotizacion(String precio, String precio_embalaje, String operador_logistico, int imagen, String label_embalaje){
        this.precio = precio;
        this.operador_logistico = operador_logistico;
        this.precio_embalaje = precio_embalaje;
        this.imagen = imagen;
        this.label_embalaje = label_embalaje;
    }
    public int getImagen() {
        switch (operador_logistico) {
            case "fedex": imagen = R.drawable.fedex;
                break;
            case "dipak": imagen = R.drawable.dipak;
                break;
            case "dhl": imagen = R.drawable.dhl;
                break;
        }
        return imagen;
    }
    public String getLabel_embalaje() { String  label = "";
        return label;
    }
    public String getOperador_logistico() {
        return operador_logistico;
    }
    public String getPrecio() {
        return precio;
    }
    public String getPrecio_embalaje() {
        return precio_embalaje;
    }

}