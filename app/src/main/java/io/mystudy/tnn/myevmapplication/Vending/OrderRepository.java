package io.mystudy.tnn.myevmapplication.Vending;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderRepository {

    // URL Const Vals
    String SCHEME = "http";
    String AUTHORITY = "172.16.5.66";
    String PORT = "3000";
    String PATH = "/order/askN";
    // URI
    String url = SCHEME+"://"+AUTHORITY+":"+PORT+PATH;
//    Uri uri = new Uri.Builder()
//            .scheme(SCHEME)
//            .authority(AUTHORITY)
//            .path(PATH)
////            .appendQueryParameter("city", "130010")
//            .build();

    public Order askOrder(final Order order) throws IOException {
//        public static final MediaType JSON
//                = MediaType.parse("application/json; charset=utf-8");
//        RequestBody body = RequestBody.create(JSON, order.toJson());  // error return null
        RequestBody body = new FormBody.Builder()
                .add("address", order.getAddress())
                .add("amount", order.getAmountString())
                .add("price_id", order.getPrice_id())
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();

        Response response = client.newCall(request).execute();
        Gson gson = new Gson();

        return gson.fromJson(response.body().string(), Order.class);
    }

    public void confirmOrder(){

    }

}
