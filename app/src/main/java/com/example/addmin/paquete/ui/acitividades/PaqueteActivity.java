package com.example.addmin.paquete.ui.acitividades;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.addmin.paquete.R;
import com.example.addmin.paquete.tools.Constantes;
import com.example.addmin.paquete.web.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import butterknife.ButterKnife;
import butterknife.Bind;

public class PaqueteActivity extends AppCompatActivity {

    @Bind(R.id.ancho_txt) EditText ancho_txt;
    @Bind(R.id.largo_txt) EditText largo_txt;
    @Bind(R.id.altura_txt) EditText altura_txt;
    @Bind(R.id.peso_txt) EditText peso_txt;
    @Bind(R.id.descripcion_cont_txt) EditText descripicion_txt;

    CheckBox fragil_txt;
    Spinner embalajes_txt;
    String descripcion_inpt,ancho_inpt,largo_inpt,altura_inpt,peso_inpt, embalaje_inpt,id_solicitud;
    Double peso_vol,ancho,largo,altura;
    boolean fragil_inpt,embalaje_inpt_b;

    public static final String TAG = PaqueteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paquete);

        Bundle bundle = getIntent().getExtras();
        id_solicitud =bundle.getString("id_solicitud");

        ButterKnife.bind(this);

        fragil_txt = (CheckBox)findViewById(R.id.fragil_ch);
        embalajes_txt = (Spinner)findViewById(R.id.embalajes);
        cargarAdaptador();
    }
    
    public void cargarAdaptador() {
        VolleySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(Request.Method.GET,Constantes.SELECT_EMBALAJES, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuesta(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.toString());
                            }
                        }));
    }

    public boolean camposVacios() {
        boolean valid = true;
        ancho_inpt = ancho_txt.getText().toString();
        largo_inpt = largo_txt.getText().toString();
        altura_inpt = altura_txt.getText().toString();
        peso_inpt = peso_txt.getText().toString();
        descripcion_inpt = descripicion_txt.getText().toString();

        if (ancho_inpt.isEmpty()||ancho_inpt.length() == 0)
        {
            ancho_txt.setError("Ingresa un numero correcto");
            valid = false;
        }
        else
        {
            if(Float.parseFloat(ancho_inpt) == 0.0){
                ancho_txt.setError("Ingresa numero mayor a 0"); }
            else {ancho_txt.setError(null);}
        }

        if (largo_inpt.isEmpty()||largo_inpt.length() == 0)
        {largo_txt.setError("Ingresa un numero correcto");
            valid = false;
        }
        else
        {
            if(Float.parseFloat(largo_inpt) == 0.0){
                largo_txt.setError("Ingresa numero mayor a 0"); }
            else {largo_txt.setError(null);}
        }

        if (altura_inpt.isEmpty()||altura_inpt.length() == 0)
        {altura_txt.setError("Ingresa un numero correcto");
            valid = false;
        }
        else
        {
            if(Float.parseFloat(altura_inpt) == 0.0){
                altura_txt.setError("Ingresa numero mayor a 0"); }
            else {altura_txt.setError(null);}
        }

        if (peso_inpt.isEmpty()||peso_inpt.length() == 0)
        {peso_txt.setError("Ingresa un numero correcto");
            valid = false;
        }
        else
        {
            if(Float.parseFloat(peso_inpt) == 0.0){
                peso_txt.setError("Ingresa numero mayor a 0"); }
            else {peso_txt.setError(null);}
        }

        if (descripcion_inpt.isEmpty())
        {descripicion_txt.setError("No puedes ser vacio");
            valid = false;
        }
        else
        {
            descripicion_txt.setError(null);
        }
        return valid;
    }

    public void guardar() {
        HashMap<String, String> mapeo = new HashMap<>();
        mapeo.put("solicitud_id_solicitud",id_solicitud);
        mapeo.put("ancho",ancho_inpt);
        mapeo.put("largo",largo_inpt);
        mapeo.put("altura",altura_inpt);
        mapeo.put("peso",peso_inpt);
        mapeo.put("fragil",String.valueOf(fragil_inpt));
        mapeo.put("embalaje",String.valueOf(embalaje_inpt_b));
        mapeo.put("tipo_embalaje_id_tipoEmbalaje",embalaje_inpt);
        mapeo.put("contenido", descripcion_inpt);
        mapeo.put("pesoVolumetrico",String.valueOf(peso_vol));
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

    public void guardar_paquete(View view)
    {
        if (!camposVacios()) { return;}

        ancho = Double.parseDouble(ancho_inpt);
        largo = Double.parseDouble(largo_inpt);
        altura = Double.parseDouble(altura_inpt);

        peso_vol = (ancho * largo * altura)/(6000);
        fragil_inpt = fragil_txt.isChecked();
        embalaje_inpt_b = false;
        embalaje_inpt = "0";
        int posicion = embalajes_txt.getSelectedItemPosition();
        if(posicion > 0)
        {
            StringWithTag embalaje_tag = (StringWithTag) embalajes_txt.getItemAtPosition(posicion);
            embalaje_inpt = embalaje_tag.tag.toString();
            embalaje_inpt_b = true;
        }
        guardar();
    }
    
    private void procesarRespuesta(JSONObject response) {
        try {
            String estado = response.getString("estado");
            switch (estado) {
                case "1":
                    JSONArray lista_embalajes = response.getJSONArray("tipo_embalajes");
                    List<StringWithTag> embalajes = new ArrayList<StringWithTag>();
                    embalajes.add(new StringWithTag("Ninguna",""));
                    for(int i = 0; i < lista_embalajes.length(); i++){
                        embalajes.add(new StringWithTag(lista_embalajes.getJSONObject(i).getString("descripcion"),lista_embalajes.getJSONObject(i).getString("id_tipoEmbalaje")));
                    }
                    ArrayAdapter<StringWithTag> adapter = new ArrayAdapter<StringWithTag>
                            (this, android.R.layout.simple_spinner_dropdown_item, embalajes);
                    embalajes_txt.setAdapter(adapter);
                    embalajes_txt.setSelection(0);
                    break;
                case "2" :
                    String mensaje = response.getString("mensaje");
                    Toast.makeText(PaqueteActivity.this,mensaje,LENGTH_LONG).show();
                    break;
            }
        }
        catch (JSONException e) { Log.d(TAG, e.getMessage()); }
    }

    private void procesarRespuestaInsert(JSONObject response) {
        try {
            String estado = response.getString("estado");
            String mensaje = response.getString("mensaje");
            switch (estado) {
                case "1":
                    final ProgressDialog progressDialog = new ProgressDialog(PaqueteActivity.this,
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

    public class StringWithTag {
        public String string;
        public Object tag;

        public StringWithTag(String stringPart, Object tagPart) {
            string = stringPart;
            tag = tagPart;
        }

        @Override
        public String toString() {
            return string;
        }
    }


}
