package com.apcs2.midtermmoblie;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

public class LandMark {


    public String get_locationID() {
        return _locationID;
    }

    public void set_locationID(String _locationID) {
        this._locationID = _locationID;
    }
    private String _locationID;
    private String _name;
    private String _description;
    private LatLng _latLng;
    private String _phone;
    private String _emergencyLevel;//1 la bt , 2 la medium, 3 la high
    private String _userId;
    private String _startDate;
    private String _address;
    // Use for direct
    private ArrayList<Polyline> directPolyLines;

    public void setLatLng(LatLng latLng) {
        this._latLng = latLng;
    }

    public LatLng getLatLng() {
        return _latLng;
    }

    public LandMark(String name, String description, String phone, LatLng latLng, String emergencyLevel, ArrayList<Polyline> directPolyLines, String userId, String startDate, String address) {
        this._name = name;
        this._description = description;
        this._phone = phone;
        this._latLng = latLng;
        this._emergencyLevel = emergencyLevel;
        this.directPolyLines = directPolyLines;
        this._userId = userId;
        this._startDate = startDate;
        this._address = address;
    }

    public void setName(String name) {
        this._name = name;
    }

    public void setDescription(String description) {
        this._description = description;
    }

    public String getName() {
        return _name;
    }

    public String getDescription() {
        return _description;
    }

    public String get_phone() {
        return _phone;
    }

    public void set_phone(String _phone) {
        this._phone = _phone;
    }

    public String get_emergencyLevel() {
        return _emergencyLevel;
    }

    public void set_emergencyLevel(String _emergencyLevel) {
        this._emergencyLevel = _emergencyLevel;
    }

    public ArrayList<Polyline> getPolyLines() {
        return directPolyLines;
    }

    public void setPolyLines(ArrayList<Polyline> polyLines) {
        this.directPolyLines = polyLines;
    }

    public String getUserId() {
        return _userId;
    }

    public void setUserId(String userId) {
        this._userId = userId;
    }

    public String get_startDate() {
        return _startDate;
    }

    public void set_startDate(String _startDate) {
        this._startDate = _startDate;
    }

    public String getAddress() {
        return _address;
    }

    public void set_address(String address) {
        this._address = address;
    }
}
