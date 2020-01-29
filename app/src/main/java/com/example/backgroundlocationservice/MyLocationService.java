package com.example.backgroundlocationservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.jaredrummler.android.device.DeviceName;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyLocationService extends BroadcastReceiver {

    public  static final String ACTION_PROCESS_UPDATE = "update";


    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            final String action = intent.getAction();
            if(ACTION_PROCESS_UPDATE.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if(result!=null){
                    Location location = result.getLastLocation();
                    String location_string = new StringBuilder(""+location.getLatitude())
                            .append("/")
                            .append(location.getLongitude())
                            .toString();
                    try {
                        MainActivity.getInstance().updateTextView(location);
                    }catch (Exception ex){
                        //Toast.makeText(context,location_string,Toast.LENGTH_SHORT).show();
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        String deviceName = DeviceName.getDeviceName();
                        //addLocation(String.valueOf(lat),String.valueOf(lng),deviceName);

                    }
                }
            }
        }


    }

    void addLocation(String lat,String lng,String id,String mobile,String pass){
        Api service = NetworkClient.getRetrofitClient().create(Api.class);
        Call<String> call;
        call = service.addLocation(lat,lng,id,mobile,pass);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("respone",String.valueOf(response.body()));

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("error",t.toString());

            }
        });

    }
}
