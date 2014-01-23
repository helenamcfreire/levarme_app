package me.levar.fragment;

import com.facebook.Response;
import com.facebook.model.GraphObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 12/01/14
 * Time: 19:52
 * To change this template use File | Settings | File Templates.
 */
public class JsonHelper {


    public static JSONArray newJsonArray(String json) {

        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public static JSONArray getJsonArrayNodeData(Response response) {

        JSONArray jsonArray = new JSONArray();
        if (response != null) {
            GraphObject graphObject = response.getGraphObject();
            if (graphObject != null) {
                JSONObject jsonObject = graphObject.getInnerJSONObject();
                if (jsonObject != null) {
                    try {
                        jsonArray = jsonObject.getJSONArray("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return jsonArray;
    }

    public static JSONArray getFqlResultSet(JSONObject jsonObject) {
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = jsonObject.getJSONArray("fql_result_set");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public static JSONObject getJsonObject(JSONArray jsonArray, int i) {
        JSONObject obj = new JSONObject();
        try {
            obj = jsonArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static JSONObject getJsonObject(JSONObject jsonObject, String attr) {
        JSONObject obj = new JSONObject();
        try {
            obj = jsonObject.getJSONObject(attr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static String getString(JSONObject obj, String attr) {
        String value = "";
        try {
            value = obj.getString(attr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static boolean getBoolean(JSONObject obj, String attr) {
        boolean value = false;
        try {
            value = obj.getBoolean(attr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

}