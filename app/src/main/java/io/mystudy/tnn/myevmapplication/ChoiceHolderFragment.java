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

    private static NumberFormat priceFormat = NumberFormat.getCurrencyInstance( Locale.KOREA );

    TextView titleView;
    TextView bodyView;
    Button buyButton;

    public ChoiceHolderFragment() {
    }

    public static ChoiceHolderFragment newInstance(int choicePrice){
        ChoiceHolderFragment fragment = new ChoiceHolderFragment();
        Bundle arguments = new Bundle();
        arguments.putInt( ARG_CHOICE_PRICE, choicePrice);
        fragment.setArguments( arguments );
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // attachToRoot 를 false 하지 않으면 !!! FAILED BINDER TRANSACTION !!! 에러 발생
        View rootView = inflater.inflate(R.layout.item_purchase, container, false);

        titleView = rootView.findViewById(R.id.item_purchase_title);
        bodyView = rootView.findViewById(R.id.item_purchase_amount);
        buyButton = rootView.findViewById(R.id.item_purchase_button);
        buyButton.setOnClickListener( this );

        // set data
        titleView.setText( moneyFormat( getArguments().getInt( ARG_CHOICE_PRICE ) ) );

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int choicedPrice = getArguments().getInt( ARG_CHOICE_PRICE );
        String toastMsg = choicedPrice + "원 선택됨";
        Toast.makeText(getContext(), toastMsg, Toast.LENGTH_SHORT).show();
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
