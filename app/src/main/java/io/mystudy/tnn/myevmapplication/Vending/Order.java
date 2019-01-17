package io.mystudy.tnn.myevmapplication.Vending;

import com.google.gson.Gson;

import org.web3j.utils.Convert;

import java.util.Date;

// Schema for Order data on vending server
//    address: String,
//    token: String,
//    amount_ether: String,   // Wei
//    amount_payment: Number,
//    price_id: ObjectId,
//    bill_id: { type : ObjectId, default : null },
//    ordered_at: { type: Date, default: Date.now },
//    txHash: {type: String, default: null},
//    bkHash: {type: String, default: null}
public class Order {

    private String _id;    // 주문번호

    private String address;    // 이더 구입 주소
    private String token;      // FCM token
    private String amount_ether;      // 구입 이더 양 == 0.xxx 개
    private int amount_payment;      // 구입 금액 == xx,000 원
    private String price_id;    // 구입 이더 가격
    private String bill_id;     // 결제 정보
    private Date ordered_at;    // 주문 시점
    private String tx_hash;     // 이더 발행 Transaction hash
    private String bk_hash;     // tx_hash가 포함된 Block hash

    private int status;
    private String errMsg;

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

    public String getAmount_ether(){
        return Convert.fromWei(amount_ether, Convert.Unit.ETHER).toString() + " Ether";
    }

    public Date getOrdered_at() {
        return ordered_at;
    }

    public String getTx_hash() {
        return tx_hash;
    }

    public String getBk_hash() {
        return bk_hash;
    }

    public int getStatus() {
        return status;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
