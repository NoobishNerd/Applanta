package com.example.applanta;

import org.json.JSONException;
import org.json.JSONObject;

public class Record {
    private int id;
    private int pitch;
    private String plantName;
    private String name;
    private String code;

    public Record(int id, int pitch, String plantName, String name, String code) {
        this.id = id;
        this.pitch = pitch;
        this.plantName = plantName;
        this.name = name;
        this.code = code;
    }

    public Record(JSONObject jsonRecord) throws JSONException {
        JSONObject plantObject = jsonRecord.getJSONObject("Plant");
        this.id = jsonRecord.getInt("id");
        this.pitch = plantObject.getInt("pitch");
        this.plantName = plantObject.getString("name");
        this.name = jsonRecord.getString("name");
        this.code = jsonRecord.getString("code");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
