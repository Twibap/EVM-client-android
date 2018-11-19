package io.mystudy.tnn.myevmapplication.websocket;

import android.util.Log;

import com.google.gson.Gson;

import io.mystudy.tnn.myevmapplication.Application.Dlog;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public abstract class PriceListener extends WebSocketListener {

    public static final int NORMAL_CLOSURE_STATUS = 1000;
    private static final String TAG = PriceListener.class.getSimpleName();

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Dlog.i("WebSocket Open");
        Log.i(TAG, "onOpen: WebSocket Open");

        webSocket.send("Hi EVM!");
//        webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !");
    }
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Dlog.i("Receiving : " + text);
        Log.i(TAG, "onMessage: "+ text);

        Gson gson = new Gson();
        Price price = gson.fromJson(text, Price.class);

        showMessage(price);
    }
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Dlog.i("Receiving bytes : " + bytes.hex());
        Log.i(TAG, "onMessage bytes: "+ bytes.hex());
    }
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        Dlog.i("Closing : " + code + " / " + reason);
        Log.i(TAG, "onClosing: "+ code +" / "+reason);
    }
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Dlog.e("Error : " + t.getMessage());
        Log.e(TAG, "onFailure: "+ t.getMessage());
    }

    public abstract void showMessage(Price text);

    public class Price{
        private String _id;
        private int   evm_price;

        public String getId() {
            return _id;
        }
        public int getEvm_price() {
            return evm_price;
        }
    }
}
