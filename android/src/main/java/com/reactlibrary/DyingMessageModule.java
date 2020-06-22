package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.net.URL;
 
import io.socket.client.IO;
import io.socket.client.Socket;

public class DyingMessageModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    Socket socket;

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
        URL url = new URL("http", "192.168.101", 3002, "/");
        socket = IO.socket(url.toURI());
        socket.connect();

        JSONObject data = new JSONObject();
        data.put("message", "hello");
        socket.emit("exit", data);
    }

    @Override
    public String getName() {
        return "DyingMessage";
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }
}
