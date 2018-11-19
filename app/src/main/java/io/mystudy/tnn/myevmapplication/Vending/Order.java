package io.mystudy.tnn.myevmapplication.Vending;

import com.google.gson.Gson;

public class Order {

    private String _id;    // 주문번호

    private String address;    // 이더 구입 주소
    private int amount;      // 구입 량 == xx,000 원
    private String price_id;    // 구입 가격
    private String bill_id;     // 결제 정보

    public Order(String _address,
                 int _amount,
                 String _price_id){

        this. _id = null;

        address = _address;
        amount = _amount;
        price_id = _price_id;
        bill_id = null;
    }

    public String itemName(){
        return "Item"+amount;
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
        return amount;
    }
    public String getAmountString(){ return String.valueOf( amount ); }

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
}
