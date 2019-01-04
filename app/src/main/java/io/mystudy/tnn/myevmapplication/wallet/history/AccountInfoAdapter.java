package io.mystudy.tnn.myevmapplication.wallet.history;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import io.mystudy.tnn.myevmapplication.Application.Dlog;
import io.mystudy.tnn.myevmapplication.R;
import io.mystudy.tnn.myevmapplication.Vending.Order;
import io.mystudy.tnn.myevmapplication.Vending.OrderRepository;

/**
 * Account 데이터를 RecyclerView에 바인딩한다.
 * 첫 두개 아이템은 각각 Address, Balance를 보여준다.
 * 이후 아이템은 구매 내역을 보여준다.
 */
public class AccountInfoAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_ACCOUNT_INFO = 0;
    private static final int VIEW_TYPE_HEADLINE = 1;
    private static final int VIEW_TYPE_HISTORY= 2;
    private static final int VIEW_TYPE_HISTORY_MESSAGE= 3;

    // Address, Balance, History header, History message
    private static final int ACCOUNT_VIEW_COUNT = 2;
    private static final int HEADER_VIEW_COUNT = 1;
    private static final int MESSAGE_VIEW_COUNT = 1;
    private static final int DEFAULT_VIEW_COUNT = // 4
            ACCOUNT_VIEW_COUNT +
            HEADER_VIEW_COUNT +
            MESSAGE_VIEW_COUNT;

    private String mAddress;
    private String mBalance;
    private ArrayList<Order> mItems;
    private OrderRepository.STATUS mRepositoryStatus = OrderRepository.STATUS.LOOKING_ORDER;

    private ProgressBar mProgressBar;

    public AccountInfoAdapter(ArrayList<Order> items, ProgressBar progressBar){
        mItems = items;
        mProgressBar = progressBar;
        mProgressBar.setIndeterminate(true);
    }

    /**
     * Item 타입을 구별한다.
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1){
            return VIEW_TYPE_HISTORY_MESSAGE;
        }

        switch (position){
            case 0: // Address
            case 1: // Balance
                return VIEW_TYPE_ACCOUNT_INFO;
            case 2: // History Headline
                return VIEW_TYPE_HEADLINE;
            default:
                return VIEW_TYPE_HISTORY;
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
                return new AccountInfoViewHolder( view );

            case VIEW_TYPE_HEADLINE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_info_headline, parent, false);
                return new RecyclerView.ViewHolder( view ) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };

            case VIEW_TYPE_HISTORY_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_message, parent, false);
                return new RecyclerView.ViewHolder( view ) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };

            case VIEW_TYPE_HISTORY:
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_history, parent, false);
                return new OrderHistoryViewHolder( view );
        }
    }

    /**
     * ViewHolder의 데이터를 바꾼다.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Dlog.e("onBindViewHolder : "+(position +1)+" / "+getItemCount());

        if ( (position +1) == getItemCount() ){
            TextView stateMessageView = (TextView) holder.itemView;

            switch ( mRepositoryStatus ){
                case LOOKING_ORDER:
                    stateMessageView.setText(R.string.msg_history_looking);
                    return;
                case HAS_NO_ORDER:
                    stateMessageView.setText(R.string.msg_history_no);
                    return;
                case HAS_NO_MORE_ORDER:
                    stateMessageView.setText(R.string.msg_history_no_more);
                    return;
                default:
                    return;
            }
        }

        if ( holder instanceof AccountInfoViewHolder ){
            AccountInfoViewHolder itemLayout = ((AccountInfoViewHolder) holder);

            switch (position){
                case 0: // Account address
                    itemLayout.setTitle("Address");
                    itemLayout.setBody(mAddress);
                    break;
                case 1: // Account balance
                    itemLayout.setTitle("Balance");
                    itemLayout.setBody(mBalance);
                    break;
            }

            return;
        }

        if ( holder instanceof OrderHistoryViewHolder &&
                mItems.size() > 0){
            int history_position = position - (ACCOUNT_VIEW_COUNT+HEADER_VIEW_COUNT);
            Order item = mItems.get(history_position);
            OrderHistoryViewHolder itemLayout = ((OrderHistoryViewHolder) holder);

            itemLayout.setPaymentAmount(item.getAmount());
            itemLayout.setDate(item.getOrdered_at());
            itemLayout.setGoodsAmount(item.getAmount_ether());

            return;
        }

        // else if HEADLINE
        if (position == 2)  // 세번째
            ((TextView) holder.itemView).setText("History");

    }

    @Override
    public int getItemCount() {
        return DEFAULT_VIEW_COUNT + mItems.size();
    }

    /**
     * ViewHolder 패턴
     * item layout의 view 객체를 재활용할 수 있게하는 클래스이다.
     *
     * AccountInfoViewHolder
     * 계정의 주소, 잔고 정보를 각각의 Item으로 구성하여 보여준다.
     *
     * OrderHistoryViewHolder
     * 구매 내역을 하나의 Item으로 보여준다.
     *
     * HEADLINE은 Textview 하나이므로 RecyclerView.ViewHolder를 그대로 사용한다.
     *
     */

    public void setmAddress(String address) {
        this.mAddress = address;
        this.notifyDataSetChanged();
    }

    public void setmBalance(String balance) {
        this.mBalance = balance;
        this.notifyDataSetChanged();
    }

    public void addAll(ArrayList<Order> orders) {
        if (orders != null)
            mItems.addAll( orders );
    }

    public void setStateMessage(OrderRepository.STATUS status){
        mRepositoryStatus = status;
        this.notifyDataSetChanged();
    }

    public void showProgressBar(boolean show){
        if ( show )
            mProgressBar.setVisibility(View.VISIBLE);
        else
            mProgressBar.setVisibility(View.GONE);
    }

}
