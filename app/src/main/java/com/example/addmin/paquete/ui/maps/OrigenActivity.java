package com.example.addmin.paquete.ui.maps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

public class OrigenActivity extends AppCompatActivity implements OnMapReadyCallback,AdapterView.OnItemSelectedListener {

    GoogleMap Mapa;
    double lat,lng ;
    Marker marcador;
    LatLng cordena,cordenada_addres;

    public static final String TAG = OrigenActivity.class.getSimpleName();
    private static final int REQUEST_FINE_LOCATION = 11;
    private static final int REQUEST_COARSE_LOCATION = 11;

    Spinner estados, ciudades,colonias,delegacnines;
    LinearLayout layout;
    ToggleButton toggle;
    SupportMapFragment mapFragment;
    CheckBox recordar;

    @Bind(R.id.calle_txt) EditText calle_txt;
    @Bind(R.id.calle1_txt) EditText calle1_txt;
    @Bind(R.id.calle2_txt) EditText calle2_txt;
    @Bind(R.id.cp_txt) EditText cp_txt;
    @Bind(R.id.num_int_txt) EditText num_int_txt;
    @Bind(R.id.num_ext_txt) EditText num_ext_txt;
    @Bind(R.id.referencia_txt) EditText referencia_txt;
    @Bind(R.id.estado_lb) TextView estado_lb;
    @Bind(R.id.ciudad_lb) TextView ciudad_lb;
    @Bind(R.id.colonia_lb) TextView colonia_lb;
    @Bind(R.id.error_dir) TextView lb_error;

    String calle_rt ="",calle1_rt="",calle2_rt="",num_int_rt="",num_ext_rt="",cp_rt="", estado_rt="",ciudad_rt="",
            colonia_rt="", delegacin_rt="", latitud_rt="",longitud_rt="",referencia_rt = "";
    String calle_mp,num_ext_mp,estado_mp,ciudad_mp,colonia_mp,cp_mp;
    String calle_inpt,calle1_inpt,calle2_inpt,num_int_inpt,num_ext_inpt,cp_inpt,estado_inpt,ciudad_inpt,colonia_inpt,delegacion_inpt,
            referencia_inpt,estado_inpt_sp,ciudad_inpt_sp,colonia_inpt_sp,delegacion_inpt_sp;
    String valida_latitude,valida_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_origen);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        ButterKnife.bind(this);

        estados = (Spinner) findViewById(R.id.estado_txt);
        ciudades = (Spinner) findViewById(R.id.ciudad_txt);
        colonias = (Spinner) findViewById(R.id.colonia_txt);
        delegacnines = (Spinner) findViewById(R.id.delegacion_txt);
        ciudades.setVisibility(View.INVISIBLE);
        colonias.setVisibility(View.INVISIBLE);
        delegacnines.setVisibility(View.INVISIBLE);
        layout = (LinearLayout)findViewById(R.id.mapa_layout);
        toggle = (ToggleButton) findViewById(R.id.mapa_toggle);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
                if (toggle.isChecked()==(true))
                {   layout.setVisibility(View.VISIBLE);
                    params.height = 600;
                } else {
                    layout.setVisibility(View.INVISIBLE);
                    params.height = 0;
                }
                mapFragment.getView().setLayoutParams(params);
            }
        });
        recordar = (CheckBox)findViewById(R.id.preferencias);
        cargarAdaptador("",0);
    }

    public void actualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            agregarMarcador(lat, lng);
        }
    }

    public void agregarMarcador(double lat, double lng) {
        if(latitud_rt != "") {lat = Double.parseDouble(latitud_rt);lng = Double.parseDouble(longitud_rt);}
        cordena = new LatLng(lat, lng);
        Mapa.getUiSettings().setZoomControlsEnabled(true);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(cordena, 15);
        if (marcador != null) marcador.remove();
        marcador = Mapa.addMarker(new MarkerOptions().position(cordena).title("Origen").draggable(true));
        Mapa.animateCamera(miUbicacion);
        if(latitud_rt == ""){
            calle_mp="";num_ext_mp="";estado_mp="";ciudad_mp="";colonia_mp="";cp_mp="";
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
        calle_inpt = calle_txt.getText().toString();
        calle1_inpt = calle1_txt.getText().toString();
        calle2_inpt = calle2_txt.getText().toString();
        num_int_inpt = num_int_txt.getText().toString();
        num_ext_inpt = num_ext_txt.getText().toString();
        cp_inpt = cp_txt.getText().toString();
        referencia_inpt = referencia_txt.getText().toString();

        if (calle_inpt.isEmpty()||calle_inpt.length() == 0)
        {
            calle_txt.setError("No puede ser vacio");
            valid = false;
        }
        else
        { calle_txt.setError(null);}

        if (calle1_inpt.isEmpty()||calle1_inpt.length() == 0)
        {
            calle1_txt.setError("No puede ser vacio");
            valid = false;
        }
        else
        { calle1_txt.setError(null);}

        if (num_ext_inpt.isEmpty()||num_ext_inpt.length() == 0)
        {
            num_ext_txt.setError("No puede ser vacio");
            valid = false;
        }
        else
        { num_ext_txt.setError(null);}

        if (cp_inpt.isEmpty()||cp_inpt.length() == 0)
        {
            cp_txt.setError("No puede ser vacio");
            valid = false;
        }
        else
        { cp_txt.setError(null);}

        if (referencia_inpt.isEmpty()||referencia_inpt.length() == 0)
        {
            referencia_txt.setError("No puede ser vacio");
            valid = false;
        }
        else
        { referencia_txt.setError(null);}

        int posicion_col = colonias.getCount();
        int posicioncol = colonias.getSelectedItemPosition();
        if((posicion_col != 0)&&(posicioncol != 0))
        {
            StringWithTag col_tag = (StringWithTag) colonias.getItemAtPosition(posicioncol);
            colonia_inpt = col_tag.tag.toString();
            colonia_inpt_sp = col_tag.string;
            colonia_lb.setError(null);
        }
        else
        {
            colonia_inpt = "";
            colonia_inpt_sp = "";
            colonia_lb.setError("Selecciona una opcion");
            valid = false;
        }


        int posicion_ciudad = ciudades.getCount();
        int posicionciudad = ciudades.getSelectedItemPosition();
        if((posicion_ciudad != 0)&&(posicionciudad != 0))
        {
            StringWithTag ciudad_tag = (StringWithTag) ciudades.getItemAtPosition(posicionciudad);
            ciudad_inpt = ciudad_tag.tag.toString();
            ciudad_inpt_sp = ciudad_tag.string;
            ciudad_lb.setError(null);
        }
        else
        {
            ciudad_inpt = "";
            ciudad_inpt_sp = "";
            ciudad_lb.setError("Selecciona una opcion");
            valid = false;
        }

        int posicion_estado = estados.getCount();
        int posicionestado = estados.getSelectedItemPosition();
        if((posicion_estado != 0)&&(posicionestado != 0))
        {
            StringWithTag estado_tag = (StringWithTag) estados.getItemAtPosition(posicionestado);
            estado_inpt = estado_tag.tag.toString();
            estado_inpt_sp = estado_tag.string;
            estado_lb.setError(null);
        }
        else
        {
            estado_inpt = "";
            estado_inpt_sp = "";
            estado_lb.setError("Selecciona una opcion");
            valid = false;
        }

        int posicion_del = delegacnines.getCount();
        int posiciondel = delegacnines.getSelectedItemPosition();
        if((posicion_del != 0)&&(posiciondel != 0))
        {
            StringWithTag del_tag = (StringWithTag) delegacnines.getItemAtPosition(posiciondel);
            delegacion_inpt = del_tag.tag.toString();
            delegacion_inpt_sp = del_tag.string;
        }
        else{delegacion_inpt = "";}
        return valid;
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else { requestPermissions(); return false;}
    }

    private String completarDireccion(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                calle_mp = returnedAddress.getThoroughfare();
                calle_mp = calle_mp.replace("Calle","");
                calle_txt.setText(calle_mp);
                num_ext_mp =returnedAddress.getSubThoroughfare();
                num_ext_txt.setText(num_ext_mp);
                cp_mp = returnedAddress.getPostalCode();
                cp_txt.setText(cp_mp);
                estado_mp = returnedAddress.getAdminArea();
                ciudad_mp = returnedAddress.getLocality();
                colonia_mp = returnedAddress.getSubLocality();
            } else {
                calle_txt.setText("");
                num_ext_txt.setText("");
                cp_txt.setText("");
                estado_mp = "";
                ciudad_mp = "";
                colonia_mp = "";
            }
        } catch (Exception e) {e.printStackTrace(); }
        return strAdd;
    }

    public void guardar_datos(View view) {
        if (!camposVacios()) {return;}
             LatLng valida = georeferencia_Valida();
        if(valida != null){lb_error.setVisibility(View.INVISIBLE);guardarOrigen();}
        else{lb_error.setVisibility(View.VISIBLE);}
    }

    public void guardarOrigen() {
        HashMap<String, String> mapeo = new HashMap<>();
        mapeo.put("id", "");
        mapeo.put("calle", calle_inpt);
        mapeo.put("cruz1", calle1_inpt);
        mapeo.put("cruz2", calle2_inpt);
        mapeo.put("numExt", num_ext_inpt);
        mapeo.put("numInt", num_int_inpt);
        mapeo.put("CP", cp_inpt);
        mapeo.put("estado", estado_inpt);
        mapeo.put("ciudad", ciudad_inpt);
        mapeo.put("colonia", colonia_inpt);
        mapeo.put("delegacion", delegacion_inpt);
        mapeo.put("referencia",referencia_inpt );
        mapeo.put("latitud", String.valueOf(cordena.latitude));
        mapeo.put("longitud", String.valueOf(cordena.longitude));
        mapeo.put("latitud_direccion", String.valueOf(cordenada_addres.latitude));
        mapeo.put("longitud_direccion", String.valueOf(cordenada_addres.longitude));
        JSONObject jobject = new JSONObject(mapeo);

        Log.d(TAG, jobject.toString());
        VolleySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(Request.Method.POST, Constantes.INSERT_SERVICIO, jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                final ProgressDialog progressDialog = new ProgressDialog(OrigenActivity.this,
                                        R.style.AppTheme_Dark_Dialog);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("Ubicando tu sucursal...");
                                progressDialog.show();

                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                progressDialog.dismiss();
                                                procesarRespuestaInsert(response);
                                            }
                                        }, 3000);
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
        SharedPreferences preferencias = getSharedPreferences("origen", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("calle", calle_inpt);
        editor.putString("calle1", calle1_inpt);
        editor.putString("calle2", calle2_inpt);
        editor.putString("num_int", num_int_inpt);
        editor.putString("num_ext", num_ext_inpt);
        editor.putString("cp", cp_inpt);
        editor.putString("estado", estado_inpt_sp);
        editor.putString("ciudad", ciudad_inpt_sp);
        editor.putString("colonia", colonia_inpt_sp);
        editor.putString("delegacion", delegacion_inpt_sp);
        editor.putString("referencia", referencia_inpt);
        editor.putString("latitud", valida_latitude);
        editor.putString("longitud", valida_longitude);
        editor.putBoolean("check",true);
        editor.commit();
    }

    public LatLng generarGeolocalizacion(Context context,String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) { return null;}
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            cordenada_addres = new LatLng(location.getLatitude(), location.getLongitude() );
        } catch (Exception ex) { ex.printStackTrace();}
        return cordenada_addres;
    }

    public LatLng georeferencia_Valida()
    {
        StringBuilder Address_lt = new StringBuilder("");
        Address_lt.append(calle_inpt).append(",");
        Address_lt.append(colonia_inpt_sp).append(",");
        Address_lt.append(cp_inpt).append(",");
        Address_lt.append(ciudad_inpt_sp).append(",");
        Address_lt.append(estado_inpt_sp);
        LatLng valida = generarGeolocalizacion(OrigenActivity.this, Address_lt.toString());
        return valida;
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}

        @Override
        public void onProviderEnabled(String s) {}

        @Override
        public void onProviderDisabled(String s) {}
    };

    public void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            actualizarUbicacion(location);
        }else{
            agregarMarcador(20.9673702, -89.59258569999997);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.estado_txt:
                if(position > 0) {
                    ciudades.setSelection(0);
                    colonias.setSelection(0);
                    delegacnines.setSelection(0);
                    StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                    String estado =  s.tag.toString();
                    cargarAdaptador(estado, 1);
                    ciudades.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ciudad_txt:
                if(position > 0) {
                    colonias.setSelection(0);
                    delegacnines.setSelection(0);
                    StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                    String ciudad =  s.tag.toString();
                    cargarAdaptador(ciudad, 2);
                    colonias.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.colonia_txt:
                if(position > 0) {
                    delegacnines.setSelection(0);
                    StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                    String colonia =  s.tag.toString();
                    cargarAdaptador(colonia, 3);
                    delegacnines.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        recupera_Sp();
        Mapa = googleMap;
        Mapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                calle_rt = "";calle_txt.setText(" ");calle1_txt.setText(" ");calle2_txt.setText(" ");num_int_txt.setText(" ");num_ext_rt = "";cp_txt.setText("");
                estado_rt = "";ciudad_rt = "";colonia_rt = "";delegacin_rt = ""; latitud_rt = "";longitud_rt = "";
                agregarMarcador(latLng.latitude, latLng.longitude);
            }
        });
        checkPermissions();
        if(checkPermissions()){miUbicacion();}
        else {agregarMarcador(19.425154 ,-88.439941);}

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    miUbicacion();
                } else {}
            }
        }
    }

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
                        case 0: estados.setAdapter(adapter);
                            if(estado_rt != "") {
                                for (int ii = 0; ii < localidades.size(); ii++) {
                                    String estos_s = localidades_lista.get(ii);
                                    if (comparador.compare(estos_s, estado_rt) == 0)
                                        estados.setSelection(ii);
                                    ciudades.setSelection(0);
                                }
                            } else {
                                for (int ii = 0; ii < localidades.size(); ii++) {
                                    String estados_s = localidades_lista.get(ii);
                                    if(estado_mp != null){
                                    if (comparador.compare(estados_s,estado_mp)==0)
                                        estados.setSelection(ii);
                                    ciudades.setSelection(0);
                                }}
                            }
                            estados.setOnItemSelectedListener(this);
                            break;
                        case 1:
                            ciudades.setAdapter(adapter);
                            if(ciudad_rt != "") {
                                for (int ii = 0; ii < localidades.size(); ii++) {
                                    String ciudad_s = localidades_lista.get(ii);
                                    if (comparador.compare(ciudad_s,ciudad_rt)==0)
                                        ciudades.setSelection(ii);
                                }
                            }else {
                                for (int ii = 0; ii < localidades.size(); ii++) {
                                    String ciudad_s = localidades_lista.get(ii);
                                   if(ciudad_mp != null){
                                    if (comparador.compare(ciudad_s,ciudad_mp)==0)
                                        ciudades.setSelection(ii);
                                }}
                            }
                            ciudades.setOnItemSelectedListener(this);
                            break;
                        case 2:
                            colonias.setAdapter(adapter);
                            if(colonia_rt != "") {
                                for (int ii = 0; ii < localidades.size(); ii++) {
                                    String colonia_s = localidades_lista.get(ii);
                                    if (comparador.compare(colonia_s,colonia_rt)==0)
                                        colonias.setSelection(ii);
                                }
                            }else {
                                for (int ii = 0; ii < localidades.size(); ii++) {
                                    String colonia_s = localidades_lista.get(ii);
                                   if(colonia_mp != null){
                                    if (comparador.compare(colonia_s,colonia_mp)==0)
                                        colonias.setSelection(ii);
                                }}
                            }
                            colonias.setOnItemSelectedListener(this);
                            break;
                        case 3:
                            delegacnines.setAdapter(adapter);
                            if(delegacin_rt != "") {
                                for (int ii = 0; ii < localidades.size(); ii++) {
                                    String delegacion_s = localidades_lista.get(ii);
                                    if (comparador.compare(delegacion_s,delegacin_rt)==0)
                                        delegacnines.setSelection(ii);
                                }
                            }
                            break;
                    }
                    break;
                case "2":
                    switch (opcion) {
                        case 0:
                            ciudades.setSelection(0);
                            colonias.setSelection(0);
                            delegacnines.setSelection(0);
                            ciudad_mp = "";ciudad_rt = "";
                            colonia_mp = "";colonia_rt = "";
                            ciudades.setVisibility(View.INVISIBLE);
                            colonias.setVisibility(View.INVISIBLE);
                            delegacnines.setVisibility(View.INVISIBLE);
                            break;
                        case 1:
                            ciudades.setSelection(0);
                            colonias.setSelection(0);
                            delegacnines.setSelection(0);
                            ciudad_mp = "";ciudad_rt = "";
                            colonia_mp = "";colonia_rt = "";
                            ciudades.setVisibility(View.INVISIBLE);
                            colonias.setVisibility(View.INVISIBLE);
                            delegacnines.setVisibility(View.INVISIBLE);
                            break;
                        case 2:
                            colonia_mp = "";colonia_rt = "";
                            colonias.setSelection(0);
                            delegacnines.setSelection(0);
                            colonias.setVisibility(View.INVISIBLE);
                            delegacnines.setVisibility(View.INVISIBLE);
                            break;
                        case 3:
                            delegacnines.setSelection(0);
                            delegacnines.setVisibility(View.INVISIBLE);
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
                    String id_solicitud = response.getString("id_solicitud");
                    if(recordar.isChecked()){
                        guardarShareprefernces();
                    }else{
                        SharedPreferences settings = getSharedPreferences("origen", Context.MODE_PRIVATE);
                        settings.edit().clear().commit();
                    }
                    Intent i=new Intent(OrigenActivity.this,DestinoActivity.class);
                    i.putExtra("id_solicitud",id_solicitud);
                    startActivity(i);
                    overridePendingTransition(R.anim.push_right_in,R.anim.push_left_out);
                    break;
                case "2": Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    public  void recupera_Sp()
    {
        SharedPreferences prefe= getSharedPreferences("origen", Context.MODE_PRIVATE);
        calle_rt = prefe.getString("calle","");
        calle_txt.setText(calle_rt);
        calle1_rt = prefe.getString("calle1","");
        calle1_txt.setText(calle1_rt);
        calle2_rt = prefe.getString("calle2","");
        calle2_txt.setText(calle2_rt);
        num_int_rt = prefe.getString("num_int","");
        num_int_txt.setText(num_int_rt);
        num_ext_rt = prefe.getString("num_ext","");
        num_ext_txt.setText(num_ext_rt);
        cp_rt = prefe.getString("cp","");
        cp_txt.setText(cp_rt);
        estado_rt = prefe.getString("estado", "");
        ciudad_rt = prefe.getString("ciudad", "");
        colonia_rt = prefe.getString("colonia", "");
        delegacin_rt = prefe.getString("delegacion", "");
        latitud_rt = prefe.getString("latitud","");
        longitud_rt = prefe.getString("longitud","");
        referencia_rt = prefe.getString("referencia","");
        referencia_txt.setText(referencia_rt);
        boolean valor = prefe.getBoolean("check",false);
        recordar.setChecked(valor);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
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