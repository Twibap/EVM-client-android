package io.mystudy.tnn.myevmapplication.wallet.history;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import io.mystudy.tnn.myevmapplication.R;

class OrderHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static DateFormat dateFormat = DateFormat.getDateInstance();    // 2019. 1. 1.
    private static NumberFormat numberformat = NumberFormat.getNumberInstance(Locale.KOREA);
    private static String symbol = numberformat.getCurrency().getSymbol();

    // 아이템 레이아웃이 선택되면 onClickListener를 통해 setVisibilityInfo를 실행한다.
    // Transaction Hash와 Block 번호를 보여주는 Layout이 보여진다.
    private ConstraintLayout mItemLayout;
    private OrderHistoryViewHolder.OnClickListener onClickListener;

    private TextView mTitle;
    private TextView mBody;
    private TextView mDate;

    private View mDivider;

    private ConstraintLayout mTxLayout;
    private TextView mTxTitle;
    private TextView mTxBody;

    private ConstraintLayout mBlockLayout;
    private TextView mBlockTitle;
    private TextView mBlockBody;

    OrderHistoryViewHolder(View itemView, OnClickListener onClickListener ) {
        super(itemView);

        mTitle = itemView.findViewById(R.id.item_title_purchase_history);
        mBody = itemView.findViewById(R.id.item_body_purchase_history);
        mDate = itemView.findViewById(R.id.item_date_purchase_history);

        mItemLayout = itemView.findViewById(R.id.item_layout_purchase_history);
        this.onClickListener = onClickListener;

        mDivider = itemView.findViewById(R.id.divider);

        mTxLayout = itemView.findViewById(R.id.item_include_layout_tx_info);
        mTxTitle = mTxLayout.findViewById(R.id.item_title_purchase_history_info);
        mTxBody = mTxLayout.findViewById(R.id.item_body_purchase_history_info);

        mBlockLayout = itemView.findViewById(R.id.item_include_layout_block_info);
        mBlockTitle = mBlockLayout.findViewById(R.id.item_title_purchase_history_info);
        mBlockBody = mBlockLayout.findViewById(R.id.item_body_purchase_history_info);

        mTxTitle.setText(R.string.item_title_transaction);
        mBlockTitle.setText(R.string.item_title_block);

        setVisibilityInfo( false );

        mItemLayout.setOnClickListener(this);
        mTxLayout.setOnClickListener(this);
        mBlockLayout.setOnClickListener(this);
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

    void setTransaction(String transactionHash){
        mTxBody.setText( transactionHash );
    }

    void setBlock(String blockHash){
        mBlockBody.setText( blockHash );
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.item_layout_purchase_history:
                // AccountInfoAdapter 에서 구현된 함수를 실행한다.
                onClickListener.onClick(this);
                break;
            case R.id.item_include_layout_tx_info:
                if (mTxBody.getText() != null)
                    showTransactionInfo();
                else {
                    mTxBody.setText("아직 Transaction이 발행되지 않았습니다.");
                }
                break;
            case R.id.item_include_layout_block_info:
                if (mBlockBody.getText() != null)
                    showBlockInfo();
                else {
                    mBlockBody.setText("아직 Transaction이 블록에 포함되지 않았습니다.");
                }
                break;
        }
    }

    /**
     * ViewHolder 내에서 호출하면 다른 아이템에서도 Visibility가 적용된다.
     * interface를 활용해 Adapter에서 호출한다.
     * @param show
     */
    void setVisibilityInfo(boolean show){
        if (show){
            mDivider.setVisibility( View.VISIBLE );
            mTxLayout.setVisibility( View.VISIBLE );
            mBlockLayout.setVisibility( View.VISIBLE );
        } else {
            mDivider.setVisibility( View.GONE );
            mTxLayout.setVisibility( View.GONE );
            mBlockLayout.setVisibility( View.GONE );
        }
    }

    /**
     * Transaction 정보를 보여주는 웹 사이트로 이동한다.
     */
    void showTransactionInfo(){
        Context context = mTxBody.getContext();
        String txHash = mTxBody.getText().toString();
        Uri txInfoUri = Uri.parse( context.getString( R.string.url_tx_info_ropsten)+"/"+txHash);
        Intent txInfoIntent = new Intent(Intent.ACTION_VIEW, txInfoUri);
        txInfoIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );

        context.startActivity( txInfoIntent );
    }

    /**
     * Block 정보를 보여주는 웹 사이트로 이동한다.
     */
    void showBlockInfo(){
        Context context = mBlockBody.getContext();
        String blockNumber = mBlockBody.getText().toString();
        Uri blockInfoUri = Uri.parse( context.getString( R.string.url_block_info_ropsten )+"/"+blockNumber);
        Intent blockInfoIntent = new Intent(Intent.ACTION_VIEW, blockInfoUri);
        blockInfoIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );

        context.getApplicationContext().startActivity( blockInfoIntent );
    }

    public interface OnClickListener {
        void onClick(OrderHistoryViewHolder historyViewHolder);
    }
}
