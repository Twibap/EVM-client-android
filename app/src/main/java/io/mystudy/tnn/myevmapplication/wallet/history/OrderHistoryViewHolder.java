package io.mystudy.tnn.myevmapplication.wallet.history;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import io.mystudy.tnn.myevmapplication.R;

class OrderHistoryViewHolder extends RecyclerView.ViewHolder {

    private static DateFormat dateFormat = DateFormat.getDateInstance();    // 2019. 1. 1.
    private static NumberFormat numberformat = NumberFormat.getNumberInstance(Locale.KOREA);
    private static String symbol = numberformat.getCurrency().getSymbol();

    private TextView mTitle;
    private TextView mBody;
    private TextView mDate;

    OrderHistoryViewHolder(View itemView) {
        super(itemView);

        mTitle = itemView.findViewById(R.id.item_title_purchase_history);
        mBody = itemView.findViewById(R.id.item_body_purchase_history);
        mDate = itemView.findViewById(R.id.item_date_purchase_history);
    }

    void setPaymentAmount(int paymentAmount){
        String money = symbol+" "+numberformat.format(paymentAmount);
        mTitle.setText(money);
    }

    void setGoodsAmount(String body) {
        mBody.setText(body);
    }

    void setDate(Date date){
        String strDate = dateFormat.format(date);
        mDate.setText( strDate );
    }

}
