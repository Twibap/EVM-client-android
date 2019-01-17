package io.mystudy.tnn.myevmapplication.wallet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;

import io.mystudy.tnn.myevmapplication.Application.Dlog;
import io.mystudy.tnn.myevmapplication.R;

public class MnemonicActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    Address address;

    TextView viewAddress;
    TextView viewMnemonic;
    Button btCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnemonic);

        initView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fab.callOnClick();
    }

    void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewAddress = findViewById(R.id.view_address);
        viewMnemonic = findViewById(R.id.view_mnemonic_words);
        btCopy = findViewById(R.id.button_copy);
        btCopy.setOnClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && address != null)
            showDataToUI( address );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_mnemonic, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_ok_mnemonic:
                // Mnemonic words 재확인 불가 경고
                new AlertDialog.Builder(this)
                        .setTitle( getResources().getString(R.string.alert_mnemonic_title))
                        .setMessage( getResources().getString(R.string.alert_mnemonic_body))
                        .setCancelable(false)
                        .setPositiveButton("다시 확인하기", null)
                        .setNeutralButton("확인", this)
                        .create().show();
                break;

            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:

                HDwallet wallet = new HDwallet( getMnemonic() );
                address = new Address( wallet.getAddressKey(0) );
                showDataToUI( address );

                break;

            case R.id.button_copy:  // Mnemonic words를 클립보드에 복사한다.
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EVM", viewMnemonic.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    void showDataToUI(Address address){
        viewAddress.setText(address.getStringAddress());
//        viewPrivateKey.setText(address.getStringPrivateKey());
    }

    /**
     * Mnemonic 단어를 생성해 출력한다.
     * 단어를 생성하는데 사용한 Seed 를 반환한다.
     * @return Seed
     */
    byte[] getMnemonic(){
        SecureRandom random = new SecureRandom();
        Mnemonic mnemonic = new Mnemonic(this);

        // Seed 얻기
        byte[] seed = new byte[16];   // 128 bits 16 bytes
        random.nextBytes( seed );

        String words = mnemonic.generateMnemonic( seed );
        Dlog.e(words);

        // Mnemonic 단어의 hash를 HD 지갑의 seed로 사용한다.
        seed = mnemonic.toSeed(words, null);

        viewMnemonic.setText(words);
//        String[] mnemonic_words = words.split(" ");

//        GridLayout gridLayout = findViewById(R.id.layout_words);
//        for (int i = 0; i < gridLayout.getChildCount(); i++) {
//            TextView textview = (TextView) gridLayout.getChildAt(i);
//            textview.setText( mnemonic_words[i] );
//        }

        return seed;
    }

    // Mnemonic words 재확인 불가 경고 후
    @Override
    public void onClick(DialogInterface dialog, int which) {
        // Account 생성 후 주소 전달
        Intent intent = getIntent();
        intent.putExtra("account", address.getStringAddress());

        // 저장
        SharedPreferences sf = getSharedPreferences("Customer", MODE_PRIVATE);
        sf.edit().putString("account", address.getStringAddress()).apply();

        Toast.makeText(this, "주소가 생성되었습니다.", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK, intent);
        finish();
    }
}
