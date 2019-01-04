package io.mystudy.tnn.myevmapplication.wallet.history;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.mystudy.tnn.myevmapplication.R;

class AccountInfoViewHolder extends RecyclerView.ViewHolder{
    private TextView mTitle;
    private TextView mBody;

    AccountInfoViewHolder(View itemView) {
        super(itemView);
        mTitle = itemView.findViewById(R.id.item_title_account);
        mBody = itemView.findViewById(R.id.item_body_account);
    }

    public void setTitle(String title) {
        this.mTitle.setText(title);
    }

    public void setBody(String body) {
        this.mBody.setText(body);
    }
}
