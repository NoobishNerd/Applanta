package com.example.applanta;

import java.math.BigInteger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Hexa {

    public static String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes()));
    }

    public static String fromHex(String hex){
        String result = new String();
        char[] charArray = hex.toCharArray();
        for(int i = 0; i < charArray.length; i=i+2) {
            String st = ""+charArray[i]+""+charArray[i+1];
            char ch = (char)Integer.parseInt(st, 16);
            result = result + ch;
        }
        return result;
    }

    public static JSONArray fromHexToJSONArray(String hex) throws JSONException {
        return new JSONArray(fromHex(hex));
    }

    public static JSONObject fromHexToJSONObject(String hex) throws JSONException {
        return new JSONObject(fromHex(hex));
    }

    public static String JSONArrayToHex(JSONArray arr) {
        return toHex(arr.toString());
    }

    public static String JSONObjectToHex(JSONObject obj) {
        return toHex(obj.toString());
    }
}