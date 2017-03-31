package com.example.addmin.paquete.ui.acitividades;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
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

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.widget.Toast.LENGTH_LONG;

public class TiporecolActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = TiporecolActivity.class.getSimpleName();
    Spinner tipo_recol,horas;
    String hora_recol_inpt,tipo_recol_intp,id_solicitud,hora,id_hora,fecha,dia,estado_dia,id_urgente;
    JSONArray urgente,hora_Sig,fin_dia,horarios;
    TableRow row_horas,row_sugerencia,row_horas_label;
    TextView label_sugerencia;

    @Bind(R.id.label_recoleccion) TextView label_tipo_recol;
    @Bind(R.id.label_hora) TextView label_hora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiporecol);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        id_solicitud =bundle.getString("id_solicitud");

        label_sugerencia = (TextView)findViewById(R.id.label_sugerncia);
        row_horas = (TableRow) findViewById(R.id.row_horarios);
        row_horas_label = (TableRow) findViewById(R.id.row_horario_label);
        row_sugerencia = (TableRow)findViewById(R.id.row_sugerencia);
        tipo_recol = (Spinner)findViewById(R.id.tipo_recoleccion);
        horas = (Spinner)findViewById(R.id.horarios_spinner);
        horas.setEnabled(false);
        cargarAdaptador();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in,R.anim.push_right_out);
    }

    private void adaptar_horas(JSONArray lista_horarios) throws JSONException {
        List<StringWithTag> horarios = new ArrayList<StringWithTag>();
        for(int i = 0; i < lista_horarios.length(); i++){
            horarios.add(new StringWithTag(lista_horarios.getJSONObject(i).getString("hora_ini"),lista_horarios.getJSONObject(i).getString("id_ventana")));
        }
        ArrayAdapter<TiporecolActivity.StringWithTag> adapter = new ArrayAdapter<StringWithTag>
                (TiporecolActivity.this, android.R.layout.simple_list_item_activated_1,horarios);
        horas.setAdapter(adapter);
    }

    public void cargarAdaptador() {
        VolleySingleton.getInstance(TiporecolActivity.this).addToRequestQueue(
                new JsonObjectRequest(Request.Method.GET,Constantes.SELECT_TIPO_RECOL, null,
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

    private void cargador_horas(String dia, String hora) {
        VolleySingleton.getInstance(TiporecolActivity.this).addToRequestQueue(
                new JsonObjectRequest(Request.Method.GET,Constantes.SELECT_HORARIOS +"?dia="+dia+"&hora="+hora, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuesta_Horario(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.toString());
                            }
                        }));

    }

    public boolean campos_vacios() {
        boolean valid = true;

        int posicion = tipo_recol.getSelectedItemPosition();
        if(posicion > 0)
        {
            StringWithTag Tipo_recol_tag = (StringWithTag) tipo_recol.getItemAtPosition(posicion);
            tipo_recol_intp = Tipo_recol_tag.tag.toString();
            label_tipo_recol.setError(null);
            int posicion_h = horas.getSelectedItemPosition();
            StringWithTag horas_tag = (StringWithTag) horas.getItemAtPosition(posicion_h);
            hora_recol_inpt = horas_tag.tag.toString();
        }
        else{valid = false; label_tipo_recol.setError("Selecciona una opcion");}
        return valid;
    }

    public void guardar_datos_recol(View view) {
        if (!campos_vacios()) {return;}
        validar();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.tipo_recoleccion:
                if(position > 0) {
                    StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                    String tipo = s.tag.toString();
                    switch (tipo) {
                        case "1":
                            try {
                                adaptar_horas(urgente);
                                horas.setEnabled(false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "2": try {
                            adaptar_horas(hora_Sig);
                            horas.setEnabled(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                            break;
                        case "3": try {
                            adaptar_horas(horarios);
                            horas.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                            break;
                        case "4": try {
                            adaptar_horas(fin_dia);
                            horas.setEnabled(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                            break;
                    }
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void procesarRespuesta(JSONObject response) {
        try {
            String estado = response.getString("estado");
            switch (estado) {
                case "1":
                    JSONArray lista_tipoRecol = response.getJSONArray("tipo_recol");
                    dia = response.getString("dia");
                    hora = response.getString("hora");
                    id_hora = response.getString("id_hora");
                    fecha = response.getString("fecha");
                    estado_dia = response.getString("estado_dia");
                    if(estado_dia == "1"){
                        label_sugerencia.setText("Las sucursales estan cerradas.\n Los horarios visualizados son de la siguiente fecha \n"+fecha);
                        row_sugerencia.setVisibility(View.VISIBLE);
                        horas.setAdapter(null);
                    }
                    List<StringWithTag> tipoRecol = new ArrayList<StringWithTag>();
                    tipoRecol.add(new StringWithTag("Selecciona una opcion",""));
                    for(int i = 0; i < lista_tipoRecol.length(); i++){
                        tipoRecol.add(new StringWithTag(lista_tipoRecol.getJSONObject(i).getString("descripcion"),lista_tipoRecol.getJSONObject(i).getString("id_tipoRecoleccion")));
                    }
                    ArrayAdapter<TiporecolActivity.StringWithTag> adapter = new ArrayAdapter<StringWithTag>
                            (TiporecolActivity.this, android.R.layout.simple_list_item_activated_1, tipoRecol);
                    tipo_recol.setAdapter(adapter);
                    tipo_recol.setSelection(0);
                    tipo_recol.setOnItemSelectedListener(this);
                    cargador_horas(dia,hora);
                    break;
                case "2" :
                    String mensaje = response.getString("mensaje");
                    Toast.makeText(TiporecolActivity.this,mensaje,LENGTH_LONG).show();
                    break;
            }
        }
        catch (JSONException e) { Log.d(TAG, e.getMessage()); }
    }

    private void procesarRespuesta_Horario(JSONObject response) {
        try {
            String estado = response.getString("estado");
            switch (estado) {
                case "1":
                    urgente = response.getJSONArray("urgente_h");
                    id_urgente = response.getString("id_urgente");
                    horarios = response.getJSONArray("horarios");
                    hora_Sig = response.getJSONArray("siguiente_hora_h");
                    fin_dia = response.getJSONArray("fin_dia_h");
                    break;
                case "2":
                    String mensaje = response.getString("mensaje");
                    Toast.makeText(TiporecolActivity.this, mensaje, LENGTH_LONG).show();
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
                        Intent i = new Intent(TiporecolActivity.this, CotizacionActivity.class);
                        i.putExtra("id_solicitud",id_solicitud);
                        startActivity(i);
                    overridePendingTransition(R.anim.push_right_in,R.anim.push_left_out);
                    break;
                case "2":
                    row_horas_label.setVisibility(View.VISIBLE);
                    tipo_recol.setSelection(3);
                    horarios= response.getJSONArray("horarios");
                    break;
                case "3":
                    urgente = response.getJSONArray("urgente_h");
                    id_urgente = response.getString("id_urgente");
                    hora_Sig = response.getJSONArray("siguiente_hora_h");
                    fin_dia = response.getJSONArray("fin_dia_h");
                    horarios= response.getJSONArray("horarios");
                    dia = response.getString("dia");
                    hora = response.getString("hora");
                    fecha = response.getString("fecha");
                    label_sugerencia.setText("No contamos con horarios disponibles.\n Los horarios visualizados son de la siguiente fecha \n"+fecha);
                    tipo_recol.setSelection(0);
                    row_sugerencia.setVisibility(View.VISIBLE);
                    row_horas_label.setVisibility(View.VISIBLE);
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

    public void validar() {
        HashMap<String, String> mapeo = new HashMap<>();// Mapeo previo
        mapeo.put("id_solicitud",id_solicitud);
        mapeo.put("tipo_recol",tipo_recol_intp);
        mapeo.put("hora_promesa",hora_recol_inpt);
        mapeo.put("hora","07:01:00");//hora);
        mapeo.put("dia_semana",dia);
        mapeo.put("id_urgente",id_urgente);
        mapeo.put("id_sucursal","1");
        mapeo.put("fecha_promesa","2017-03-01");//fecha);
        JSONObject jobject = new JSONObject(mapeo);

        Log.d(TAG, jobject.toString());
        VolleySingleton.getInstance(TiporecolActivity.this).addToRequestQueue(
                new JsonObjectRequest(Request.Method.POST, Constantes.VALIDAR_RECOL, jobject,
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
}
