package io.mystudy.tnn.myevmapplication.Vending;

import com.google.gson.Gson;

public class Order {

    private String _id;    // 주문번호

    private String address;    // 이더 구입 주소
    private String token;      // FCM token
    private String amount_ether;      // 구입 이더 양 == 0.xxx 개
    private int amount_payment;      // 구입 금액 == xx,000 원
    private String price_id;    // 구입 이더 가격
    private String bill_id;     // 결제 정보

    public Order(String _address,
                 String _token,
                 int _amount,
                 String _price_id){

        this. _id = null;

        address = _address;
        token = _token;
        amount_payment = _amount;
        price_id = _price_id;
        bill_id = null;
    }

    public String itemName(){
        return "Item"+amount_payment;
    }

    /* ====== Getter & Setter ============================================ */
    public void setOrder_id(String order_id) {
        this. _id = order_id;
    }
    public String getOrder_id() {
        return  _id;
    }

    public String getAddress() {
        return address;
    }

    public int getAmount() {
        return amount_payment;
    }
    public String getAmountString(){ return String.valueOf( amount_payment ); }

    public String getPrice_id() {
        return price_id;
    }

    public void setBill_id(String bill_id) {
        this.bill_id = bill_id;
    }
    public String getBill_id() {
        return bill_id;
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getToken() {
        return this.token;
    }
}
