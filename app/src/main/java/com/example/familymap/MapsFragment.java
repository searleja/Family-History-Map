package com.example.familymap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import Models.Event;
import Models.Person;

public class MapsFragment extends Fragment {

    private GoogleMap gMap;

    private Map<String, Event> markersData = new HashMap<>();
    private TextView personDetails;
    private TextView eventDetails;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            gMap=googleMap;
            setMarkers();
        }
    };

    private void setMarkers() {
        Map<String, Event> events = DataCache.getInstance().getAllEvents();
        for (String id : events.keySet()) {
            Event current = events.get(id);
            LatLng latLng = new LatLng(current.getLatitude(), current.getLongitude());
            MarkerOptions mark = new MarkerOptions().position(latLng);
            String eventType = current.getEventType();
            switch (eventType) {
                case "Birth":
                    mark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    break;
                case "Marriage":
                    mark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    break;
                case "Death":
                    mark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    break;
            }
            gMap.addMarker(mark);
        }
    }

    public boolean onMarkerClick(final Marker marker) {
        Event currentEvent = markersData.get(marker.getId());
        Person currentPerson = DataCache.getInstance().findPerson(currentEvent.getPersonID());
        updateEventDetailsView(currentPerson, currentEvent);
        return false;
    }

    private void updateEventDetailsView(Person currentPerson, Event currentEvent) {
        String gender = currentPerson.getGender();
        personDetails.setText(currentPerson.getFirstName() + " " + currentPerson.getLastName());
        eventDetails.setText(currentEvent.getEventType() + ": " + currentEvent.getCity() + ", " + currentEvent.getCountry() + " (" + currentEvent.getYear() + ")");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}