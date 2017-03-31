package com.example.addmin.paquete.ui.acitividades;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.addmin.paquete.R;
import com.example.addmin.paquete.tools.Constantes;
import com.example.addmin.paquete.web.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Bind;

public class SobreActivity extends AppCompatActivity {

    @Bind(R.id.anchos_txt) EditText ancho_txt_s;
    @Bind(R.id.largos_txt) EditText largo_txt_s;
    @Bind(R.id.pesos_txt) EditText peso_txt_s;
    @Bind(R.id.descripcion_conts_txt) EditText descripicion_txt_s;

    CheckBox fragil_txt_s;
    String descripcion_inpt_s,ancho_inpt_s,largo_inpt_s,peso_inpt_s,id_solicitud;
    Double ancho_s,largo_s;
    boolean fragil_inpt_s;

    public static final String TAG = SobreActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        Bundle bundle = getIntent().getExtras();
        id_solicitud =bundle.getString("id_solicitud");

        ButterKnife.bind(this);

        fragil_txt_s = (CheckBox)findViewById(R.id.fragils_ch);
    }

    public boolean camposVacios() {
        boolean valid = true;
        ancho_inpt_s = ancho_txt_s.getText().toString();
        largo_inpt_s = largo_txt_s.getText().toString();
        peso_inpt_s = peso_txt_s.getText().toString();
        descripcion_inpt_s = descripicion_txt_s.getText().toString();

        if (ancho_inpt_s.isEmpty()||ancho_inpt_s.length() == 0)
        {
            ancho_txt_s.setError("Ingresa un numero correcto");
            valid = false;
        }
        else
        {
            if(Float.parseFloat(ancho_inpt_s) == 0.0){
                ancho_txt_s.setError("Ingresa numero mayor a 0"); }
            else {ancho_txt_s.setError(null);}
        }

        if (largo_inpt_s.isEmpty()||largo_inpt_s.length() == 0)
        {largo_txt_s.setError("Ingresa un numero correcto");
            valid = false;
        }
        else
        {
            if(Float.parseFloat(largo_inpt_s) == 0.0){
                largo_txt_s.setError("Ingresa numero mayor a 0"); }
            else {largo_txt_s.setError(null);}
        }

        if (peso_inpt_s.isEmpty()||peso_inpt_s.length() == 0)
        {peso_txt_s.setError("Ingresa un numero correcto");
            valid = false;
        }
        else
        {
            if(Float.parseFloat(peso_inpt_s) == 0.0){
                peso_txt_s.setError("Ingresa numero mayor a 0"); }
            else {peso_txt_s.setError(null);}
        }

        if (descripcion_inpt_s.isEmpty())
        {descripicion_txt_s.setError("No puedes ser vacio");
            valid = false;
        }
        else
        {
            descripicion_txt_s.setError(null);
        }
        return valid;
    }

    public void guardar_paquete(View view)
    {
        if (!camposVacios()) { return;}

        ancho_s = Double.parseDouble(ancho_inpt_s);
        largo_s = Double.parseDouble(largo_inpt_s);

        fragil_inpt_s = fragil_txt_s.isChecked();
        guardar();
    }

    public void guardar() {
        HashMap<String, String> mapeo = new HashMap<>();
        mapeo.put("solicitud_id_solicitud",id_solicitud);
        mapeo.put("ancho",ancho_inpt_s);
        mapeo.put("largo",largo_inpt_s);
        mapeo.put("altura",null);
        mapeo.put("peso",peso_inpt_s);
        mapeo.put("fragil",String.valueOf(fragil_inpt_s));
        mapeo.put("embalaje","0");
        mapeo.put("tipo_embalaje_id_tipoEmbalaje","0");
        mapeo.put("contenido", descripcion_inpt_s);
        mapeo.put("pesoVolumetrico",null);
        JSONObject jobject = new JSONObject(mapeo);

        Log.d(TAG, jobject.toString());
        VolleySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(Request.Method.POST, Constantes.INSERT_PAQUETE, jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaInsert(response);
                            }
                        } ,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage());
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }

    private void procesarRespuestaInsert(JSONObject response) {
        try {
            String estado = response.getString("estado");
            String mensaje = response.getString("mensaje");
            switch (estado) {
                case "1":

                    final ProgressDialog progressDialog = new ProgressDialog(SobreActivity.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Guardando...");
                    progressDialog.show();
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                }
                            }, 3000);
                    break;
                case "2": Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

}
