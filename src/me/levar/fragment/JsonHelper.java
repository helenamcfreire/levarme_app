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

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public static JSONArray getJsonArrayNodeData(Response response) {

        JSONArray jsonArray = null;
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
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("fql_result_set");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public static JSONObject getJsonObject(JSONArray jsonArray, int i) {
        JSONObject obj = null;
        try {
            obj = jsonArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static JSONObject getJsonObject(JSONObject jsonObject, String attr) {
        JSONObject obj = null;
        try {
            obj = jsonObject.getJSONObject(attr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static String getString(JSONObject obj, String attr) {
        String value = null;
        try {
            value = obj.getString(attr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

}