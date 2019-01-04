package io.mystudy.tnn.myevmapplication.Vending;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import io.mystudy.tnn.myevmapplication.Application.BaseApplication;
import io.mystudy.tnn.myevmapplication.Application.Dlog;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderRepository {

    private STATUS mStatus = STATUS.HAS_NO_ORDER;
    public enum STATUS{
        HAS_NO_ORDER, HAS_NO_MORE_ORDER, HAS_MORE_ORDER, LOOKING_ORDER
    }

    public STATUS getStatus() {
        return mStatus;
    }

    private static final OrderRepository mInstance = new OrderRepository();

    public static OrderRepository getInstance(){
        return mInstance;
    }

    private OrderRepository(){

    }

    public ArrayList<Order> getHistory(String address) {
        return getHistory(address, 0);
    }

    public ArrayList<Order> getHistory(String address, int skipCount){
        return getHistory(address, Integer.toString( skipCount ));
    }

    public ArrayList<Order> getHistory(String address, String skipCount){
        try{
            Integer.parseInt(skipCount);
        } catch (NumberFormatException e) {
            return null;
        }

        mStatus = STATUS.LOOKING_ORDER;

        String PATH = "/order/history"; // + /:address/:page

        HttpUrl url = HttpUrl.parse( mkUrl( PATH ) )
                .newBuilder()
                .addPathSegment(address)
                .addPathSegment( skipCount )
                .build();

        final Request request = new Request.Builder()
                .url( url )
                .get()
                .build();

        final OkHttpClient client = new OkHttpClient();

        Gson gson = new Gson();
        ArrayList<Order> orderList = null;

        try {
            Response response = client.newCall(request).execute();

            if (response.code() == 204) {
                Dlog.e("주문 내역이 없습니다.");
                mStatus = STATUS.HAS_NO_ORDER;
                return null;
            }

            orderList = gson.fromJson( response.body().string(),
                    new TypeToken< ArrayList<Order> >(){}.getType());

            String data = response.headers().get("isLastPage");
            boolean isLastPage = Boolean.parseBoolean( data );
            Dlog.i("is Last Page : " + isLastPage);

            if ( isLastPage )
                mStatus = STATUS.HAS_NO_MORE_ORDER;
            else
                mStatus = STATUS.HAS_MORE_ORDER;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return orderList;
    }

    public Order askOrder(final Order order) throws IOException {
//        public static final MediaType JSON
//                = MediaType.parse("application/json; charset=utf-8");
//        RequestBody body = RequestBody.create(JSON, order.toJson());  // error return null

        String PATH = "/order/askN";

        RequestBody body = new FormBody.Builder()
                .add("address", order.getAddress())
                .add("token", order.getToken())
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

    private static String mkUrl(String path){
        return BaseApplication.getHost_http() +path;
    }
}
