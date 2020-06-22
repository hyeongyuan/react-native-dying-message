package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import io.socket.client.IO;
import io.socket.client.Socket;

import android.util.Log;

public class DyingMessageModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    private static final String TAG = "RNDyingMessage";
    Socket socket;
    String url;
    String eventName;
    JSONObject data;

    private final ReactApplicationContext reactContext;

    public DyingMessageModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(this);
        this.reactContext = reactContext;
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {
        if (url == null || eventName == null || data == null) {
            return;
        }
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = false;

            socket = IO.socket(url, opts);
            socket.connect();

            socket.emit(eventName, data);
        } catch (URISyntaxException exception) {
            Log.e(TAG, "Socket Initialization error: ", exception);
        } finally {
            url = null;
            eventName = null;
            data = null;
        }
    }

    @Override
    public String getName() {
        return "DyingMessage";
    }

    @ReactMethod
    public void write(String to, String event, ReadableMap message) {        
        try {
            data = convertMapToJson(message);
            url = to;
            eventName = event;
        } catch (JSONException exception) {
            Log.e(TAG, "convertMapToJson error: ", exception);
        }
    }

    @ReactMethod
    public void delete() {        
        url = null;
        eventName = null;
        data = null;
    }

    private static JSONObject convertMapToJson(ReadableMap readableMap) throws JSONException {
        JSONObject object = new JSONObject();
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            switch (readableMap.getType(key)) {
                case Null:
                    object.put(key, JSONObject.NULL);
                    break;
                case Boolean:
                    object.put(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    object.put(key, readableMap.getDouble(key));
                    break;
                case String:
                    object.put(key, readableMap.getString(key));
                    break;
                case Map:
                    object.put(key, convertMapToJson(readableMap.getMap(key)));
                    break;
                case Array:
                    object.put(key, convertArrayToJson(readableMap.getArray(key)));
                    break;
            }
        }
        return object;
    }

    private static JSONArray convertArrayToJson(ReadableArray readableArray) throws JSONException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < readableArray.size(); i++) {
            switch (readableArray.getType(i)) {
                case Null:
                    break;
                case Boolean:
                    array.put(readableArray.getBoolean(i));
                    break;
                case Number:
                    array.put(readableArray.getDouble(i));
                    break;
                case String:
                    array.put(readableArray.getString(i));
                    break;
                case Map:
                    array.put(convertMapToJson(readableArray.getMap(i)));
                    break;
                case Array:
                    array.put(convertArrayToJson(readableArray.getArray(i)));
                    break;
            }
        }
        return array;
    }
}
