package io.mystudy.tnn.myevmapplication.wallet.history;

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
public class PurchaseHistory {
    String address;
    String token;
    String amount_ether;
    int amount_payment;
    String price_id;
    String bill_id;
    Date ordered_at;
    String tx_hash;
    String block_hash;
}
