package com.example.addmin.paquete.ui.maps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.addmin.paquete.R;
import com.example.addmin.paquete.tools.Constantes;
import com.example.addmin.paquete.ui.acitividades.PaquetesMainActivity;
import com.example.addmin.paquete.ui.acitividades.TiporecolActivity;
import com.example.addmin.paquete.web.VolleySingleton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DestinoActivity extends AppCompatActivity implements OnMapReadyCallback,AdapterView.OnItemSelectedListener
{

    GoogleMap Mapa_d;
    Marker marcador_d;
    LatLng cordena_d,cordenada_addres_d;// variables mapa

    Spinner estados_d, ciudades_d,colonias_d,delegacnines_d;
    LinearLayout layout_d;
    ToggleButton toggle_d;
    SupportMapFragment mapFragment_d;
    CheckBox recordar_d;

    @Bind(R.id.calle_txt_d) EditText calle_txt_d ;
    @Bind(R.id.calle1_txt_d) EditText calle1_txt_d ;
    @Bind(R.id.calle2_txt_d) EditText calle2_txt_d;
    @Bind(R.id.num_ext_txt_d) EditText num_ext_txt_d;
    @Bind(R.id.num_int_txt_d) EditText num_int_txt_d;
    @Bind(R.id.cp_txt_d) EditText cp_txt_d ;
    @Bind(R.id.referencia_txt_d) EditText referencia_txt_d;
    @Bind(R.id.estado_lb_d) TextView estado_lb_d;
    @Bind(R.id.ciudad_lb_d) TextView ciudad_lb_d;
    @Bind(R.id.colonia_lb_d) TextView colonia_lb_d;
    @Bind(R.id.error_dir_d) TextView lb_error;

    public static final String TAG = DestinoActivity.class.getSimpleName();

    String calle_rt_d ="",calle1_rt_d="",calle2_rt_d="",num_int_rt_d="",num_ext_rt_d="",cp_rt_d="", estado_rt_d="",ciudad_rt_d="",
                colonia_rt_d="", delegacin_rt_d="", latitud_rt_d="",longitud_rt_d="",referencia_rt_d = "";//variables recuperadas SharePreferences
    String calle_mp_d,num_ext_mp_d,estado_mp_d,ciudad_mp_d,colonia_mp_d,cp_mp_d;//variables obtenidas por la ubicacion.
    String calle_inpt_d,calle1_inpt_d,calle2_inpt_d,num_int_inpt_d,num_ext_inpt_d,cp_inpt_d,estado_inpt_d,ciudad_inpt_d,
                colonia_inpt_d,delegacion_inpt_d,referencia_inpt_d,colonia_inpt_sp_d,estado_inpt_sp_d,ciudad_inpt_sp_d,delegacion_inpt_sp_d;
    String onclick_map,valida_latitude,valida_longitude,id_solicitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_destino);

            Bundle bundle = getIntent().getExtras();
            id_solicitud =bundle.getString("id_solicitud");

            mapFragment_d = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa_d);
            mapFragment_d.getMapAsync(DestinoActivity.this);//agregando mapa

            ButterKnife.bind(this);

            estados_d = (Spinner) findViewById(R.id.estado_txt_d);
            ciudades_d = (Spinner) findViewById(R.id.ciudad_txt_d);
            colonias_d = (Spinner) findViewById(R.id.colonia_txt_d);
            delegacnines_d = (Spinner) findViewById(R.id.delegacion_txt_d);
            ciudades_d.setVisibility(View.INVISIBLE);
            colonias_d.setVisibility(View.INVISIBLE);
            delegacnines_d.setVisibility(View.INVISIBLE);
            layout_d = (LinearLayout)findViewById(R.id.mapa_layout_d);
            toggle_d = (ToggleButton) findViewById(R.id.mapa_toggle_d);
           toggle_d.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup.LayoutParams params = mapFragment_d.getView().getLayoutParams();
                    if (toggle_d.isChecked()==(true))
                    {   layout_d.setVisibility(View.VISIBLE);
                        params.height = 600;
                    } else {
                        layout_d.setVisibility(View.INVISIBLE);
                        params.height = 0;
                    }
                    mapFragment_d.getView().setLayoutParams(params);
                }
            });
            recordar_d = (CheckBox)findViewById(R.id.preferencias_d);
            cargarAdaptador("",0);//cargando estado,ciudad,colonoa y delegacion
        }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in,R.anim.push_right_out);
    }

    public void agregarMarcador(double lat, double lng) {
            cordena_d = new LatLng(lat, lng);
            Mapa_d.getUiSettings().setZoomControlsEnabled(true);
            CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(cordena_d, 15);
            if (marcador_d != null) marcador_d.remove();
            marcador_d = Mapa_d.addMarker(new MarkerOptions().position(cordena_d).title("Destino").draggable(true));
            Mapa_d.animateCamera(miUbicacion);
            if(latitud_rt_d == "" && onclick_map == "1"){
                calle_mp_d="";num_ext_mp_d="";estado_mp_d="";ciudad_mp_d="";colonia_mp_d="";cp_mp_d="";
                completarDireccion(lat, lng);
            }
            cargarAdaptador("",0);
        }

    public void cargarAdaptador(String valor, final int opcion) {
            String variable = "";
            switch (opcion) {
                case 0: variable = Constantes.SELECT_LOCALIDADES;
                    break;
                case 1: variable = Constantes.SELECT_LOCALIDADES +"?valor="+valor+"&opcion="+1;
                    break;
                case 2: variable = Constantes.SELECT_LOCALIDADES +"?valor="+valor+"&opcion="+2;
                    break;
                case 3: variable = Constantes.SELECT_LOCALIDADES +"?valor="+valor+"&opcion="+3;
                    break;
            }

            VolleySingleton.getInstance(this).addToRequestQueue(
                    new JsonObjectRequest(Request.Method.GET,variable, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    procesarRespuesta(response,opcion);
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
            calle_inpt_d = calle_txt_d.getText().toString();
            calle1_inpt_d = calle1_txt_d.getText().toString();
            calle2_inpt_d = calle2_txt_d.getText().toString();
            num_int_inpt_d = num_int_txt_d.getText().toString();
            num_ext_inpt_d = num_ext_txt_d.getText().toString();
            cp_inpt_d = cp_txt_d.getText().toString();
            referencia_inpt_d = referencia_txt_d.getText().toString();

            if (calle_inpt_d.isEmpty()||calle_inpt_d.length() == 0)
            {
                calle_txt_d.setError("No puede ser vacio");
                valid = false;
            }
            else
            { calle_txt_d.setError(null);}
            //cruzamiento 1
            if (calle1_inpt_d.isEmpty()||calle1_inpt_d.length() == 0)
            {
                calle1_txt_d.setError("No puede ser vacio");
                valid = false;
            }
            else
            { calle1_txt_d.setError(null);}
            //numerio exterior
            if (num_ext_inpt_d.isEmpty()||num_ext_inpt_d.length() == 0)
            {
                num_ext_txt_d.setError("No puede ser vacio");
                valid = false;
            }
            else
            { num_ext_txt_d.setError(null);}
            //codigo postal
            if (cp_inpt_d.isEmpty()||cp_inpt_d.length() == 0)
            {
                cp_txt_d.setError("No puede ser vacio");
                valid = false;
            }
            else
            { cp_txt_d.setError(null);}
            // referencia
            if (referencia_inpt_d.isEmpty()||referencia_inpt_d.length() == 0)
            {
                referencia_txt_d.setError("No puede ser vacio");
                valid = false;
            }
            else
            { referencia_txt_d.setError(null);}


            int posicion_col = colonias_d.getCount();
            int posicioncol = colonias_d.getSelectedItemPosition();
            if((posicion_col != 0)&&(posicioncol != 0))
            {
                StringWithTag col_tag = (StringWithTag) colonias_d.getItemAtPosition(posicioncol);
                colonia_inpt_d = col_tag.tag.toString();
                colonia_inpt_sp_d = col_tag.string;
                colonia_lb_d.setError(null);
            }
            else
            {
                colonia_inpt_d = "";
                colonia_inpt_sp_d = "";
                colonia_lb_d.setError("Selecciona una opcion");
                valid = false;
            }

            int posicion_ciudad = ciudades_d.getCount();
            int posicionciudad = ciudades_d.getSelectedItemPosition();
            if((posicion_ciudad != 0)&&(posicionciudad != 0))
            {
                StringWithTag ciudad_tag = (StringWithTag) ciudades_d.getItemAtPosition(posicionciudad);
                ciudad_inpt_d = ciudad_tag.tag.toString();
                ciudad_inpt_sp_d = ciudad_tag.string;
                ciudad_lb_d.setError(null);
            }
            else
            {
                ciudad_inpt_d = "";
                ciudad_inpt_sp_d = "";
                ciudad_lb_d.setError("Selecciona una opcion");
                valid = false;
            }

            int posicion_estado = estados_d.getCount();
            int posicionestado = estados_d.getSelectedItemPosition();
            if((posicion_estado != 0)&&(posicionestado != 0))
            {
                StringWithTag estado_tag = (StringWithTag) estados_d.getItemAtPosition(posicionestado);
                estado_inpt_d = estado_tag.tag.toString();
                estado_inpt_sp_d = estado_tag.string;
                estado_lb_d.setError(null);
            }
            else
            {
                estado_inpt_d = "";
                estado_inpt_sp_d = "";
                estado_lb_d.setError("Selecciona una opcion");
                valid = false;
            }

            int posicion_del = delegacnines_d.getCount();
            int posiciondel = delegacnines_d.getSelectedItemPosition();
            if((posicion_del != 0)&&(posiciondel != 0))
            {
                StringWithTag del_tag = (StringWithTag) delegacnines_d.getItemAtPosition(posiciondel);
                delegacion_inpt_d = del_tag.tag.toString();
                delegacion_inpt_sp_d = del_tag.string;
            }
            else{delegacion_inpt_d = "";}

            referencia_inpt_d = referencia_txt_d.getText().toString();
            return valid;
        }

    private String completarDireccion(double LATITUDE, double LONGITUDE) {
            String strAdd = "";
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    calle_mp_d = returnedAddress.getThoroughfare();
                    calle_mp_d = calle_mp_d.replace("Calle","");
                    calle_txt_d.setText(calle_mp_d);
                    num_ext_mp_d =returnedAddress.getSubThoroughfare();
                    num_ext_txt_d.setText(num_ext_mp_d);
                    cp_mp_d = returnedAddress.getPostalCode();
                    cp_txt_d.setText(cp_mp_d);
                    estado_mp_d = returnedAddress.getAdminArea();
                    ciudad_mp_d = returnedAddress.getLocality();
                    colonia_mp_d = returnedAddress.getSubLocality();
                } else {}
            } catch (Exception e) {e.printStackTrace(); }
            return strAdd;
        }

    public void guardar_datos(View view) {
            if (!camposVacios()) {return;}
                LatLng valida = georeferencia_Valida();
                if(valida != null){lb_error.setVisibility(View.INVISIBLE);guardarDestino();}
                else{lb_error.setVisibility(View.VISIBLE);}
        }

    public void guardarDestino() {
            HashMap<String, String> mapeo = new HashMap<>();// Mapeo previo
            mapeo.put("id", id_solicitud);
            mapeo.put("calle", calle_inpt_d);
            mapeo.put("cruz1", calle1_inpt_d);
            mapeo.put("cruz2", calle2_inpt_d);
            mapeo.put("numExt", num_ext_inpt_d);
            mapeo.put("numInt", num_int_inpt_d);
            mapeo.put("CP", cp_inpt_d);
            mapeo.put("estado", estado_inpt_d);
            mapeo.put("ciudad", ciudad_inpt_d);
            mapeo.put("colonia", colonia_inpt_d);
            mapeo.put("delegacion", delegacion_inpt_d);
            mapeo.put("referencia",referencia_inpt_d );
            if( cordena_d == null) {
                mapeo.put("longitud", "");
                mapeo.put("latitud", "");
            }
            else
            {
                mapeo.put("longitud", String.valueOf(cordena_d.longitude));
                mapeo.put("latitud",String.valueOf(cordena_d.latitude));
            }
            mapeo.put("latitud_direccion", String.valueOf(cordenada_addres_d.latitude));
            mapeo.put("longitud_direccion", String.valueOf(cordenada_addres_d.longitude));
            JSONObject jobject = new JSONObject(mapeo);

            Log.d(TAG, jobject.toString());
            VolleySingleton.getInstance(this).addToRequestQueue(
                    new JsonObjectRequest(Request.Method.POST, Constantes.INSERT_SERVICIO, jobject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(final JSONObject response) {
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

    public void guardarShareprefernces(){
            SharedPreferences preferencias = getSharedPreferences("destino", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("calle", calle_inpt_d);
            editor.putString("calle1", calle1_inpt_d);
            editor.putString("calle2", calle2_inpt_d);
            editor.putString("num_int", num_int_inpt_d);
            editor.putString("num_ext", num_ext_inpt_d);
            editor.putString("cp", cp_inpt_d);
            editor.putString("estado", estado_inpt_sp_d);
            editor.putString("ciudad", ciudad_inpt_sp_d);
            editor.putString("colonia", colonia_inpt_sp_d);
            editor.putString("delegacion", delegacion_inpt_sp_d);
            editor.putString("referencia", referencia_inpt_d);
            editor.putString("latitud",valida_latitude);
            editor.putString("longitud",valida_longitude);
            editor.putBoolean("check",true);
            editor.commit();
        }

    public LatLng generarGeolocalizacion(Context context, String strAddress) {
            Geocoder coder = new Geocoder(context);
            List<Address> address;
            try {
                address = coder.getFromLocationName(strAddress, 5);
                if (address == null) { return null;}
                Address location = address.get(0);
                location.getLatitude();
                location.getLongitude();
                cordenada_addres_d = new LatLng(location.getLatitude(), location.getLongitude() );
            } catch (Exception ex) { ex.printStackTrace(); }
            return cordenada_addres_d;
        }

    public LatLng georeferencia_Valida() {
        StringBuilder Address_lt = new StringBuilder("");
        Address_lt.append(calle_inpt_d).append(",");
        Address_lt.append(colonia_inpt_sp_d).append(",");
        Address_lt.append(cp_inpt_d).append(",");
        Address_lt.append(ciudad_inpt_sp_d).append(",");
        Address_lt.append(estado_inpt_sp_d);
        LatLng valida = generarGeolocalizacion(DestinoActivity.this, Address_lt.toString());
        return valida;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()){
                case R.id.estado_txt_d:
                    if(position > 0) {
                        ciudades_d.setSelection(0);
                        colonias_d.setSelection(0);
                        delegacnines_d.setSelection(0);
//                        ciudad_mp_d = "";ciudad_rt_d = "";colonia_mp_d = "";colonia_rt_d = "";
                        StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                        String estado =  s.tag.toString();
                        cargarAdaptador(estado, 1);
                        ciudades_d.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.ciudad_txt_d:
                    if(position > 0) {
                        colonias_d.setSelection(0);
                        delegacnines_d.setSelection(0);
  //                      colonia_mp_d = "";colonia_rt_d = "";
                        StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                        String ciudad =  s.tag.toString();
                        cargarAdaptador(ciudad, 2);
                        colonias_d.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.colonia_txt_d:
                    if(position > 0) {
                        delegacnines_d.setSelection(0);
                        StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                        String colonia =  s.tag.toString();
                        cargarAdaptador(colonia, 3);
                        delegacnines_d.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }

    @Override
    public void onMapReady(GoogleMap googleMap) {
            recupera_Sp();//recuperar Sharepreferences
            Mapa_d = googleMap;
            //if(latitud_rt_d != ""){agregarMarcador(latitud_rt_d, longitud_rt_d);}
            if(latitud_rt_d != "") {agregarMarcador(Double.parseDouble(latitud_rt_d),Double.parseDouble(longitud_rt_d));}
            Mapa_d.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    calle_rt_d = "";calle_txt_d.setText(" ");calle1_txt_d.setText(" ");calle2_txt_d.setText(" ");num_int_txt_d.setText(" ");num_ext_rt_d = "";cp_txt_d.setText("");
                    estado_rt_d = "";ciudad_rt_d = "";colonia_rt_d = "";delegacin_rt_d = ""; latitud_rt_d = "";longitud_rt_d = "";onclick_map = "1";
                    agregarMarcador(latLng.latitude, latLng.longitude);
                }
            });
        }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private void procesarRespuesta(JSONObject response,int opcion) {
            try {
                String estado = response.getString("estado");
                switch (estado) {
                    case "1":
                        String id = "";
                        switch (opcion) {
                            case 0: id = "id_estado";break;
                            case 1: id = "id_ciudad";break;
                            case 2: id = "id_colonia";break;
                            case 3: id = "id_delegacion";break;
                        }
                        JSONArray lista_localidades = response.getJSONArray("estados");
                        List<StringWithTag> localidades = new ArrayList<StringWithTag>();
                        List<String> localidades_lista = new ArrayList<String>();
                        JSONArray lista_localidades1 = response.getJSONArray("estados");
                        localidades_lista.add("Selecciona una opcion");
                        localidades.add(new StringWithTag("Selecciona una opcion",""));
                        for(int i = 0; i < lista_localidades1.length(); i++){
                            localidades_lista.add(lista_localidades.getJSONObject(i).getString("nombre"));
                            localidades.add(new StringWithTag(lista_localidades.getJSONObject(i).getString("nombre"), lista_localidades.getJSONObject(i).getString(id)));
                        }
                        ArrayAdapter<StringWithTag> adapter = new ArrayAdapter<StringWithTag>
                                (this, android.R.layout.simple_spinner_dropdown_item, localidades);
                        Collator comparador = Collator.getInstance ();
                        comparador.setStrength(Collator.PRIMARY);
                        switch (opcion) {
                            case 0: estados_d.setAdapter(adapter);
                                if(estado_rt_d != "") {
                                    for (int ii = 0; ii < localidades.size(); ii++) {
                                        String estos_s = localidades_lista.get(ii);
                                        if (comparador.compare(estos_s, estado_rt_d) == 0)
                                            estados_d.setSelection(ii);
                                    }
                                } else {
                                    for (int ii = 0; ii < localidades.size(); ii++) {
                                        String estados_s = localidades_lista.get(ii);
                                        if(estado_mp_d != null){
                                        if (comparador.compare(estados_s,estado_mp_d)==0)
                                            estados_d.setSelection(ii);
                                    }}
                                }
                                estados_d.setOnItemSelectedListener(this);
                                break;
                            case 1:
                                ciudades_d.setAdapter(adapter);
                                if(ciudad_rt_d != "") {
                                    for (int ii = 0; ii < localidades.size(); ii++) {
                                        String ciudad_s = localidades_lista.get(ii);
                                        if (comparador.compare(ciudad_s,ciudad_rt_d)==0)
                                            ciudades_d.setSelection(ii);
                                    }
                                }else {
                                    for (int ii = 0; ii < localidades.size(); ii++) {
                                        String ciudad_s = localidades_lista.get(ii);
                                        if(ciudad_mp_d != null){
                                        if (comparador.compare(ciudad_s,ciudad_mp_d)==0)
                                            ciudades_d.setSelection(ii);
                                    }}
                                }
                                ciudades_d.setOnItemSelectedListener(this);
                                break;
                            case 2:
                                colonias_d.setAdapter(adapter);
                                if(colonia_rt_d != "") {
                                    for (int ii = 0; ii < localidades.size(); ii++) {
                                        String colonia_s = localidades_lista.get(ii);
                                        if (comparador.compare(colonia_s,colonia_rt_d)==0)
                                            colonias_d.setSelection(ii);
                                    }
                                }else {
                                    for (int ii = 0; ii < localidades.size(); ii++) {
                                        String colonia_s = localidades_lista.get(ii);
                                        if(colonia_mp_d != null){
                                        if (comparador.compare(colonia_s,colonia_mp_d)==0)
                                            colonias_d.setSelection(ii);
                                    }}
                                }
                                colonias_d.setOnItemSelectedListener(this);
                                break;
                            case 3:
                                delegacnines_d.setAdapter(adapter);
                                if(delegacin_rt_d != "") {
                                    for (int ii = 0; ii < localidades.size(); ii++) {
                                        String delegacion_s = localidades_lista.get(ii);
                                        if (comparador.compare(delegacion_s,delegacin_rt_d)==0)
                                            delegacnines_d.setSelection(ii);
                                    }
                                }
                                break;
                        }
                        break;
                    case "2":
                        switch (opcion) {
                            case 0:
                                ciudades_d.setSelection(0);
                                colonias_d.setSelection(0);
                                delegacnines_d.setSelection(0);
                                ciudad_mp_d = "";ciudad_rt_d = "";
                                colonia_mp_d = "";colonia_rt_d = "";
                                ciudades_d.setVisibility(View.INVISIBLE);
                                colonias_d.setVisibility(View.INVISIBLE);
                                delegacnines_d.setVisibility(View.INVISIBLE);
                                break;
                            case 1:
                                ciudades_d.setSelection(0);
                                colonias_d.setSelection(0);
                                delegacnines_d.setSelection(0);
                                ciudad_mp_d = "";ciudad_rt_d = "";
                                colonia_mp_d = "";colonia_rt_d = "";
                                ciudades_d.setVisibility(View.INVISIBLE);
                                colonias_d.setVisibility(View.INVISIBLE);
                                delegacnines_d.setVisibility(View.INVISIBLE);
                                break;
                            case 2:
                                colonias_d.setSelection(0);
                                delegacnines_d.setSelection(0);
                                colonia_mp_d = "";colonia_rt_d = "";
                                colonias_d.setVisibility(View.INVISIBLE);
                                delegacnines_d.setVisibility(View.INVISIBLE);
                                break;
                            case 3: delegacnines_d.setVisibility(View.INVISIBLE);
                                break;
                        }
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
                       valida_latitude = response.getString("latitud_valida");
                        valida_longitude = response.getString("longitud_valida");
                        if(recordar_d.isChecked()){
                            guardarShareprefernces();
                        }else{
                            SharedPreferences settings = getSharedPreferences("destino", Context.MODE_PRIVATE);
                            settings.edit().clear().commit();
                        }
                        Intent i=new Intent(this,PaquetesMainActivity.class);
                        i.putExtra("id_solicitud",id_solicitud);
                        startActivity(i);
                        overridePendingTransition(R.anim.push_right_in,R.anim.push_left_out);
                        break;
                    case "2": Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                        break;
                }
            } catch (JSONException e) {e.printStackTrace();}
        }

    public  void recupera_Sp() {
        SharedPreferences prefe= getSharedPreferences("destino", Context.MODE_PRIVATE);
        calle_rt_d = prefe.getString("calle","");
        calle_txt_d.setText(calle_rt_d);
        calle1_rt_d = prefe.getString("calle1","");
        calle1_txt_d.setText(calle1_rt_d);
        calle2_rt_d = prefe.getString("calle2","");
        calle2_txt_d.setText(calle2_rt_d);
        num_int_rt_d = prefe.getString("num_int","");
        num_int_txt_d.setText(num_int_rt_d);
        num_ext_rt_d = prefe.getString("num_ext","");
        num_ext_txt_d.setText(num_ext_rt_d);
        cp_rt_d = prefe.getString("cp","");
        cp_txt_d.setText(cp_rt_d);
        estado_rt_d = prefe.getString("estado", "");
        ciudad_rt_d = prefe.getString("ciudad", "");
        colonia_rt_d = prefe.getString("colonia", "");
        delegacin_rt_d = prefe.getString("delegacion", "");
        latitud_rt_d = prefe.getString("latitud","");
        longitud_rt_d = prefe.getString("longitud","");
        referencia_rt_d = prefe.getString("referencia","");
        referencia_txt_d.setText(referencia_rt_d);
        boolean valor = prefe.getBoolean("check",false);
        recordar_d.setChecked(valor);
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

