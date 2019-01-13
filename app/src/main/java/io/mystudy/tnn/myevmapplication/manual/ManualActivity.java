package io.mystudy.tnn.myevmapplication.manual;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.rd.PageIndicatorView;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.math.ec.ECPoint;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import io.mystudy.tnn.myevmapplication.Application.Dlog;
import io.mystudy.tnn.myevmapplication.R;

public class ManualActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * Viewpager page indicator
     */
    private PageIndicatorView mPageIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        ECNamedCurveParameterSpec CURVE_PARAMS = ECNamedCurveTable.getParameterSpec("secp256k1");
        Dlog.e("Param N : "+CURVE_PARAMS.getN().toString(16));
        Dlog.e("Param H : "+CURVE_PARAMS.getH().toString(16));
        Dlog.e("Param G : "+CURVE_PARAMS.getG().getXCoord().toBigInteger().toString(16));
        Dlog.e("          "+CURVE_PARAMS.getG().getYCoord().toBigInteger().toString(16));
        Dlog.e("Param A : "+CURVE_PARAMS.getCurve().getA().toBigInteger().toString(16));
        Dlog.e("Param B : "+CURVE_PARAMS.getCurve().getB().toBigInteger().toString(16));

//        String p = "115792089237316195423570985008687907853269984665640564039457584007908834671663";
//        BigInteger P = new BigInteger( p , 10 );
        String p = "1e99423a4ed27608a15a2616a2b0e9e52ced330ac530edcc32c8ffc6a526aedd";
        BigInteger P = new BigInteger( p , 16 );

        ECPoint point = CURVE_PARAMS.getG().multiply( P );
        point = point.normalize();

//        byte[] point_x = ArrayUtils.concatByteArrays( new byte[]{0x00}, point.getXCoord().getEncoded());
//        byte[] point_y = ArrayUtils.concatByteArrays( new byte[]{0x00}, point.getYCoord().getEncoded());
        byte[] point_x = point.getXCoord().getEncoded();
        byte[] point_y = point.getYCoord().getEncoded();
//        BigInteger bigPoint_X = point.getAffineXCoord().toBigInteger();
//        BigInteger bigPoint_Y = point.getAffineYCoord().toBigInteger();
        BigInteger bigPoint_X = new BigInteger(1, point_x);
        BigInteger bigPoint_Y = new BigInteger(1, point_y);
        BigInteger result_X = bigPoint_X.pow(3).add( CURVE_PARAMS.getCurve().getB().toBigInteger() ).mod(P);
        BigInteger result_Y = bigPoint_Y.pow(2).mod( P );
        BigInteger result = bigPoint_X.pow(3).add( CURVE_PARAMS.getCurve().getB().toBigInteger() /* 7 */ ).subtract( bigPoint_Y.pow(2) );
        Dlog.i("----------------");
        Dlog.e("P : "+P.toString(16));
        Dlog.e("point : "+Numeric.toHexString(point.getEncoded(true)));
        Dlog.e("X : "+ Numeric.toHexString( point_x));
        Dlog.e("Y : "+ Numeric.toHexString( point_y));
        Dlog.e("X : "+bigPoint_X.toString(10));
        Dlog.e("Y : "+bigPoint_Y.toString(10));
        Dlog.e("X : "+Numeric.toHexString(bigPoint_X.toByteArray()));
        Dlog.e("Y : "+Numeric.toHexString(bigPoint_Y.toByteArray()));
//        Dlog.e("x^3 + 7 - y^2");
//        Dlog.e(result.toString(16));
        Dlog.e("(x^3 + 7 - y^2) % p");
        Dlog.e(result.mod( P ).toString(16));
        Dlog.e(result_X.subtract( result_Y ).toString(16));



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mPageIndicatorView = findViewById(R.id.pageIndicatorView);
        mPageIndicatorView.setViewPager( mViewPager );
        mPageIndicatorView.setCount( SectionsPagerAdapter.TOTAL_PAGES ); // specify total count of indicators
        mPageIndicatorView.setSelection(0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mPageIndicatorView.setSelection(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == RESULT_OK ) {
            setResult(resultCode, data);
            super.finish();
        }
    }

    @Override
    public void finish() {
        SharedPreferences database = getSharedPreferences("Customer", MODE_PRIVATE);
        if( !database.contains("account") ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);
            builder
                    .setMessage("종료하시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAffinity();
                        }
                    })
                    .setNegativeButton("아니오", null)
                    .show();
        } else {
            super.finish();
        }
    }
}
