package io.mystudy.tnn.myevmapplication.manual;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;

import io.mystudy.tnn.myevmapplication.AccountActivity;
import io.mystudy.tnn.myevmapplication.R;
import io.mystudy.tnn.myevmapplication.wallet.MnemonicActivity;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements View.OnClickListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_IS_LAST_SECTION = "is_last_section";

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber, boolean isLastSection) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putBoolean(ARG_IS_LAST_SECTION, isLastSection);
        fragment.setArguments(args);
        return fragment;
    }

    ImageView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean isLastSection = getArguments().getBoolean(ARG_IS_LAST_SECTION);

        View rootView;
        rootView = inflater.inflate(R.layout.fragment_ask_wallet, container, false);

        ImageView imageView = rootView.findViewById(R.id.imageView);
        TextView textView = (TextView) rootView.findViewById(R.id.textView);
        Button btYesHaveWallet = rootView.findViewById(R.id.btYes);
        Button btNoHaveWallet = rootView.findViewById(R.id.btNo);
        btYesHaveWallet.setOnClickListener(this);
        btNoHaveWallet.setOnClickListener(this);

        MultiTransformation multi = new MultiTransformation<>(
//                new CropTransformation(200, 200),
                new RoundedCornersTransformation(250, 8, RoundedCornersTransformation.CornerType.ALL)
        );

        if (isLastSection){
            btYesHaveWallet.setVisibility(View.VISIBLE);
            btNoHaveWallet.setVisibility(View.VISIBLE);
            Glide.with(this).load( getResources().getDrawable(R.drawable.wellcome_wallet))
                    .apply( RequestOptions.bitmapTransform(multi)).into(imageView);
//            imageView.setImageBitmap( BitmapFactory.decodeResource(getResources(), R.drawable.wellcome_wallet) );
        } else {
            btYesHaveWallet.setVisibility(View.INVISIBLE);
            btNoHaveWallet.setVisibility(View.INVISIBLE);
            Glide.with(this).load( getResources().getDrawable(R.drawable.wellcome_coin))
                    .apply( RequestOptions.bitmapTransform(multi)).into(imageView);
//            imageView.setImageBitmap( BitmapFactory.decodeResource(getResources(), R.drawable.wellcome_coin) );
        }

        String wellcomeMsg = getResources().getStringArray(R.array.manual_msg)[ getArguments().getInt(ARG_SECTION_NUMBER)-1 ];
        textView.setText(wellcomeMsg);

//        textView.setText(getString(R.string., getArguments().getInt(ARG_SECTION_NUMBER)));

        return rootView;
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            // 지갑 주소를 입력한다.
            case R.id.btYes:
                intent = new Intent(getContext(), AccountActivity.class);
                break;

            // 지갑을 만든다.
            case R.id.btNo:
                intent = new Intent(getContext(), MnemonicActivity.class);
                break;
        }

        startActivityForResult(intent, 0);
    }

}
