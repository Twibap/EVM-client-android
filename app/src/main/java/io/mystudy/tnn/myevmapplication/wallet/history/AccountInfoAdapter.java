package io.mystudy.tnn.myevmapplication.wallet.history;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.mystudy.tnn.myevmapplication.Application.Dlog;
import io.mystudy.tnn.myevmapplication.R;

/**
 * Account 데이터를 RecyclerView에 바인딩한다.
 * 첫 두개 아이템은 각각 Address, Balance를 보여준다.
 * 이후 아이템은 구매 내역을 보여준다.
 */
public class AccountInfoAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_ACCOUNT_INFO = 0;
    private static final int VIEW_TYPE_HEADLINE = 1;
    private static final int VIEW_TYPE_PURCHASE_HISTORY = 2;

    private int mAccountInfoCount = 0;
    private int mHeadlineCount = 0;

    private String mAddress;
    private String mBalance;
    private ArrayList<PurchaseHistory> mItems;

    public AccountInfoAdapter(ArrayList<PurchaseHistory> items){
        mItems = items;
    }

    /**
     * Item 타입을 구별한다.
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        switch (position){
            case 0: // Address
            case 1: // Balance
                return VIEW_TYPE_ACCOUNT_INFO;
            case 2: // History Headline
                return VIEW_TYPE_HEADLINE;
            default:
                return VIEW_TYPE_PURCHASE_HISTORY;
        }
    }

    /**
     * ViewHolder를 생성한다.
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        switch (viewType){
            case VIEW_TYPE_ACCOUNT_INFO:
                mAccountInfoCount++;
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
                return new AccountInfoViewHolder( view );

            case VIEW_TYPE_HEADLINE:
                mHeadlineCount++;
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_info_headline, parent, false);
                return new RecyclerView.ViewHolder( view ) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };

            case VIEW_TYPE_PURCHASE_HISTORY:
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_history, parent, false);
                return new PurchaseHistoryViewHolder( view );
        }
    }

    /**
     * ViewHolder의 데이터를 바꾼다.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Dlog.e("onBindViewHolder : "+position);

        if ( holder instanceof AccountInfoViewHolder ){
            AccountInfoViewHolder itemLayout = ((AccountInfoViewHolder) holder);

            switch (position){
                case 0: // Account address
                    itemLayout.mTitle.setText("Address");
                    itemLayout.mBody.setText(mAddress);
                    break;
                case 1: // Account balance
                    itemLayout.mTitle.setText("Balance");
                    itemLayout.mBody.setText(mBalance);
                    break;
            }

            return;
        }

        if ( holder instanceof PurchaseHistoryViewHolder ){
            int history_position = position - (mAccountInfoCount + mHeadlineCount);
            PurchaseHistory item = mItems.get(history_position);
            PurchaseHistoryViewHolder itemLayout = ((PurchaseHistoryViewHolder) holder);

            itemLayout.mTitle.setText("History "+history_position);
            itemLayout.mBody.setText("블라블라");

            return;
        }

        // else if HEADLINE
        ((TextView) holder.itemView).setText("History");

    }

    @Override
    public int getItemCount() {
        // TODO 갯수 확인하기
        return mAccountInfoCount + mHeadlineCount + mItems.size();
    }

    /**
     * ViewHolder 패턴
     * item layout의 view 객체를 재활용할 수 있게하는 클래스이다.
     *
     * AccountInfoViewHolder
     * 계정의 주소, 잔고 정보를 각각의 Item으로 구성하여 보여준다.
     *
     * PurchaseHistoryViewHolder
     * 구매 내역을 하나의 Item으로 보여준다.
     *
     * HEADLINE은 Textview 하나이므로 RecyclerView.ViewHolder를 그대로 사용한다.
     *
     */
    static class AccountInfoViewHolder extends RecyclerView.ViewHolder{
        private TextView mTitle;
        private TextView mBody;

        AccountInfoViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.item_title_account);
            mBody = itemView.findViewById(R.id.item_body_account);
        }
    }

    static class PurchaseHistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mBody;
        PurchaseHistoryViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.item_title_purchase_history);
            mBody = itemView.findViewById(R.id.item_body_purchase_history);
        }
    }

    public void setmAddress(String address) {
        this.mAddress = address;
        this.notifyDataSetChanged();
    }

    public void setmBalance(String balance) {
        this.mBalance = balance;
        this.notifyDataSetChanged();
    }

}
