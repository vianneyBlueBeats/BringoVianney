package com.example.addmin.paquete.ui.acitividades;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.addmin.paquete.R;
import com.example.addmin.paquete.ui.fragmentos.CotizacionFragment;

public class CotizacionActivity extends AppCompatActivity {

    String id_solicitud = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cotizacion);

        Bundle bundle = getIntent().getExtras();
        id_solicitud =bundle.getString("id_solicitud");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, CotizacionFragment.createInstance(id_solicitud), "CotizacionFragment")
                    .commit();
        }
    }
}
