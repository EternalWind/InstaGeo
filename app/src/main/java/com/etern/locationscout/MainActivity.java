package com.etern.locationscout;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.GridView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        LocationListener,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static final int PERMISSION_REQUEST_LOCATION = 10096;
    private static final int REQUEST_CHECK_SETTINGS = 2001;
    private boolean mIsRequestingLocationUpdates = false;
    private IPhotoSource photoSource = new PhotoSource500px("Q5Nm8FzowE4WHSqNDlXJhpP7suipUUV8N3cfLZ4e", this);
    private PhotoListAdapter imgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        photoSource.init();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();

        GridView layout = getPhotoLayout();

        imgAdapter = new PhotoListAdapter(this, 0);

        layout.setAdapter(imgAdapter);
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        //state.putParcelableArrayList("imgAdapter", imgAdapter.getItems());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsRequestingLocationUpdates && mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsRequestingLocationUpdates) {
            stopLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mIsRequestingLocationUpdates = false;
    }

    private void loadImages() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        loadImages(mLastLocation);
    }

    private LocationRequest createLocationRequest() {
        LocationRequest req = new LocationRequest();
        req.setInterval(10000);
        req.setFastestInterval(5000);
        req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return req;
    }

    private GridView getPhotoLayout() {
        GridView grid = (GridView)findViewById(R.id.PhotoLayout);
        return grid;
    }

    private void loadImages(Location location) {
        if (location != null) {
            Geolocation geo = new Geolocation();
            geo.latitude = location.getLatitude();
            geo.longitude = location.getLongitude();
            geo.range = 1;
            geo.unit = Geolocation.RangeUnit.KILO_METER;

            imgAdapter.clear();

            photoSource.searchByLocation(geo, new IPhotoSource.IPhotoSearchListener() {
                @Override
                public void onSucceed(List<Photo> photos) {
                    Helpers.log(MainActivity.this, String.format("Done! %s photos are returned.", photos.size()));

                    GridView layout = getPhotoLayout();
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    imgAdapter.setItemSize(size.x / 3);

                    for (int i = 0; i < photos.size(); ++i) {
                        imgAdapter.add(photos.get(i));
                    }
                }
            });
        }
    }

    private void startLocationUpdates() {
        LocationRequest req = createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(req);
        PendingResult<LocationSettingsResult> result = LocationServices
                .SettingsApi
                .checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, MainActivity.this.createLocationRequest(), MainActivity.this);

                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });

        mIsRequestingLocationUpdates = true;
    }

    private String geolocationToName(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && addresses.size() > 0) {
                Address addr = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                for (int i = 0; i <= addr.getMaxAddressLineIndex(); ++i) {
                    addressFragments.add(addr.getAddressLine(i));
                }

                return TextUtils.join(System.getProperty("line.separator"), addressFragments);
            }
        } catch (IOException ioException) {

        } catch (IllegalArgumentException illegalArgumnentException) {

        }

        return "";
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);

            return;
        }

        //loadImages();

        if (!mIsRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadImages();

                    if (!mIsRequestingLocationUpdates) {
                        startLocationUpdates();
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LocationRequest req = createLocationRequest();
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == RESULT_OK) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                            mGoogleApiClient, req, this);
                }
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void searchPhotos(View view) {
        loadImages(mLastLocation);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }
}
