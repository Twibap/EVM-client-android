package io.mystudy.tnn.myevmapplication.Application.edittext;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;

/**
 * QR Code를 사용해 입력하는 기능을 가진 EditText이다.
 * EditText의 내용이 없으면 QR 입력이 가능하다.
 * EditText의 내용이 있는 경우 Clear Button 으로 삭제한다.
 *
 * TODO
 */
public class QRcodeEditText extends ClearEditText {

    private Drawable cameraDrawable;

    public QRcodeEditText(Context context) {
        super(context);
        init();
    }

    public QRcodeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QRcodeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        Drawable tempDrawable = ContextCompat.getDrawable(getContext(), android.R.drawable.ic_menu_camera);
        cameraDrawable = DrawableCompat.wrap(tempDrawable);
        DrawableCompat.setTintList(cameraDrawable, getHintTextColors());
        cameraDrawable.setBounds(0, 0, cameraDrawable.getIntrinsicWidth(), cameraDrawable.getIntrinsicHeight());

        setQRcodeIconVisible( true );

        addTextChangedListener(this);
    }

    private void setQRcodeIconVisible(boolean visible) {
        cameraDrawable.setVisible(visible, false);
        setCompoundDrawables(null, null, visible ? cameraDrawable: null, null);
    }

    /*
    카메라 버튼을 클릭하면 QR Scanner를 작동시킨다.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int x = (int) event.getX();

        // Camera 버튼이 보이면서, 버튼을 touch 했을 때
        if ( cameraDrawable.isVisible() &&
                x > getWidth() - getPaddingRight() - cameraDrawable.getIntrinsicWidth() ){
            if ( event.getAction() == MotionEvent.ACTION_UP ) {
                new IntentIntegrator((Activity) getContext())
                        .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                        .setBeepEnabled(false)
                        .initiateScan();
            }

            return true;
        } else {
            return super.onTouch(v, event);
        }

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if ( isFocused() ) {
            if (s.length() > 0) {
                setQRcodeIconVisible( false );
                super.onTextChanged(s, start, before, count);   // setClearIconVisible(true)
            } else {
                super.onTextChanged(s, start, before, count);   // setClearIconVisible(false)
                setQRcodeIconVisible( true );
            }
        }

    }

    /*
    Edittext를 클릭했을 때 텍스트 길이가 0이면 QR 입력 버튼을 보여준다.
    텍스트 길이가 0이 아닐 때는 삭제 버튼을 보여준다.
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if ( hasFocus ){
            setQRcodeIconVisible( getText().length() == 0 );
        } else {
            setQRcodeIconVisible( false );
        }

        super.onFocusChange(v, hasFocus);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (cameraDrawable != null){
            if ( text == null || text.length() == 0)
                setQRcodeIconVisible( true );
            else
                setQRcodeIconVisible( false );
        }

        super.setText(text, type);
    }
}
