package io.mystudy.tnn.myevmapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

public class ChoiceHolderFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_CHOICE_PRICE = "choice_price";
    private static final String ARG_SELECTION_NUMBER = "selection_number";

    private static NumberFormat priceFormat = NumberFormat.getCurrencyInstance( Locale.KOREA );

    int choicedPrice;
    String etherAmount;

    TextView titleView;
    TextView bodyView;
    Button buyButton;

    public ChoiceHolderFragment() {
    }

    public static ChoiceHolderFragment newInstance(int position, int choicedPrice){
        ChoiceHolderFragment fragment = new ChoiceHolderFragment();
        Bundle arguments = new Bundle();
        arguments.putInt( ARG_SELECTION_NUMBER, position);
        arguments.putInt( ARG_CHOICE_PRICE, choicedPrice);
        fragment.setArguments( arguments );
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // attachToRoot 를 false 하지 않으면 !!! FAILED BINDER TRANSACTION !!! 에러 발생
        View rootView = inflater.inflate(R.layout.item_purchase, container, false);

        // init view
        titleView = rootView.findViewById(R.id.item_purchase_title);
        bodyView = rootView.findViewById(R.id.item_purchase_amount);
        buyButton = rootView.findViewById(R.id.item_purchase_button);
        buyButton.setOnClickListener( this );

        // get data
        choicedPrice = getArguments().getInt( ARG_CHOICE_PRICE );

        // set data
        titleView.setText( moneyFormat( choicedPrice ) );

        return rootView;
    }

    @Override
    public void onClick(View v) {
        // viewPager - fragment - button
        ChoiceViewPager pager = (ChoiceViewPager) v.getParent().getParent();
        int position = getArguments().getInt(ARG_SELECTION_NUMBER);

        // 옆에 살짝 보이는 다음 화면 버튼 누를 경우 해당 View로 이동
        if (pager.getCurrentItem() == position ){
            String toastMsg = choicedPrice + "원 선택됨";
            Toast.makeText(getContext(), toastMsg, Toast.LENGTH_SHORT).show();
        } else {
            pager.setCurrentItem( position , true);
        }

//        int choicedPrice = getArguments().getInt( ARG_CHOICE_PRICE );
    }

    String moneyFormat(int money){
        String formatted = priceFormat.format( money );
        formatted = formatted.replace(
                priceFormat.getCurrency().getSymbol(),
                priceFormat.getCurrency().getSymbol()+" "
        );
        return formatted;
    }
}
