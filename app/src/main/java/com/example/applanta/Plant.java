package com.example.applanta;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Plant implements Serializable {
    private int id;
    private String name;
    private String nature;
    private String species;
    private String desc;
    private int pitch;
    //private String photo;

    public Plant(String name, String nature, String species, String desc) {
        this.id = 0;
        this.name = name;
        this.nature = nature;
        this.species = species;
        this.desc = desc;
        this.pitch = 0;
        //this.photo = photo;
    }

    public Plant(JSONObject jasonPlant, String usage) throws JSONException {
        if(usage.equals("collection")){
            JSONObject plantObject = jasonPlant.getJSONObject("Plant");
            this.name = plantObject.getString("name");
            this.nature = plantObject.getJSONObject("Nature").getString("desc");
            this.species = plantObject.getString("species");
            this.desc = plantObject.getString("desc");
            this.id = 0;
            this.pitch = 0;
            //this.photo = plantObject.getString("photo");
        }else if(usage.equals("interaction")){
            this.id = jasonPlant.getInt("id");
            this.name = jasonPlant.getString("name");
            this.nature = jasonPlant.getJSONObject("Nature").getString("desc");
            this.species = jasonPlant.getString("species");
            this.desc = jasonPlant.getString("desc");
            this.pitch = jasonPlant.getInt("pitch");
            //this.photo = plantObject.getString("photo");
        }
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }
}

