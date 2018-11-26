package io.mystudy.tnn.myevmapplication.Vending;

import com.google.gson.Gson;

import java.io.IOException;

import io.mystudy.tnn.myevmapplication.Application.BaseApplication;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderRepository {

    public Order askOrder(final Order order) throws IOException {
//        public static final MediaType JSON
//                = MediaType.parse("application/json; charset=utf-8");
//        RequestBody body = RequestBody.create(JSON, order.toJson());  // error return null

        String PATH = "/order/askN";

        RequestBody body = new FormBody.Builder()
                .add("address", order.getAddress())
                .add("amount", order.getAmountString())
                .add("price_id", order.getPrice_id())
                .build();

        Request request = new Request.Builder()
                .url( mkUrl(PATH) )
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();

        Response response = client.newCall(request).execute();
        Gson gson = new Gson();

        return gson.fromJson(response.body().string(), Order.class);
    }

    public String verifyPayment(Bill bill) throws IOException {

        String PATH = "/order/payment";

        RequestBody body = new FormBody.Builder()
                .add("receipt_id", bill.getReceipt_id())
                .add("order_id", bill.getOrder_id())
                .add("item_name", bill.getItem_name())
                .add("price", bill.getAmountString())
                .add("pg", bill.getPg())
                .add("method", bill.getMethod())
                .add("request_at", bill.getRequested_at())
                .add("purchased_at", bill.getPurchased_at())
                .build();

        Request request = new Request.Builder()
                .url( mkUrl( PATH ))
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    private String mkUrl(String path){
        return BaseApplication.getHost_order() +path;
    }
}
