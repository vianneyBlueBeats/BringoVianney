package com.example.addmin.paquete.ui.acitividades;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.addmin.paquete.ui.fragmentos.PaquetesFragment;
import com.example.addmin.paquete.R;

public class PaquetesMainActivity extends AppCompatActivity {

    String id_solicitud ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paquetes);
        Bundle bundle = getIntent().getExtras();
        id_solicitud =bundle.getString("id_solicitud");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_paquetes,PaquetesFragment.createInstance(id_solicitud), "PaquetesFragment")
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in,R.anim.push_right_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK || resultCode == 203) {
                PaquetesFragment fragment = (PaquetesFragment) getSupportFragmentManager().
                        findFragmentByTag("PaquetesFragment");
                fragment.cargarAdaptador();
            }
    }
}
