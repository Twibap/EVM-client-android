package io.mystudy.tnn.myevmapplication.Vending;

/**
 * {
 *   "action": "BootpayDone",
 *   "receipt_id": "5bf39c11ed32b3421a5f8e2a",
 *   "amount": 33000,
 *   "card_no": null,
 *   "card_code": null,
 *   "card_name": null,
 *   "card_quota": null,
 *   "params": null,
 *   "item_name": "Item33000",
 *   "order_id": "5bf39c1001d71f1eee395f2d",
 *   "url": "https://app.bootpay.co.kr",
 *   "payment_name": "카카오페이",
 *   "pg_name": "카카오페이",
 *   "pg": "kakao",
 *   "method": "easy",
 *   "method_name": "카카오페이",
 *   "requested_at": "2018-11-20 14:30:57",
 *   "purchased_at": "2018-11-20 14:31:14",
 *   "status": 1
 * }
 */
public class Bill {
    String action;
    String receipt_id;
    int amount;
    String card_no;
    String card_code;
    String card_name;
    String card_quota;
    String params;
    String item_name;
    String order_id;
    String url;
    String payment_name;
    String pg_name;
    String pg;
    String method;
    String method_name;
    String requested_at;
    String purchased_at;
    int status;

    public String getReceipt_id() {
        return receipt_id;
    }

    public String getAmountString() {
        return String.valueOf(amount);
    }

    public String getItem_name() {
        return item_name;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getPg() {
        return pg;
    }

    public String getMethod() {
        return method;
    }

    public String getRequested_at() {
        return requested_at;
    }

    public String getPurchased_at() {
        return purchased_at;
    }
}
