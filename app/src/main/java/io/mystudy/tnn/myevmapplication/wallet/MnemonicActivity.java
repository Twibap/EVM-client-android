package io.mystudy.tnn.myevmapplication.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;

import io.mystudy.tnn.myevmapplication.Application.Dlog;
import io.mystudy.tnn.myevmapplication.R;

public class MnemonicActivity extends AppCompatActivity implements View.OnClickListener {

    Address address;

    TextView viewAddress;
    TextView viewPrivateKey;

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
        viewPrivateKey = findViewById(R.id.view_key);
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
                // Account 생성 후 주소 저장
                Intent intent = getIntent();
                intent.putExtra("account", address.getStringAddress());

                Toast.makeText(this, "주소가 생성되었습니다.", Toast.LENGTH_SHORT).show();

                setResult(RESULT_OK, intent);
                finish();
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
        viewPrivateKey.setText( words );
        Dlog.e(words);

        // Mnemonic 단어의 hash를 HD 지갑의 seed로 사용한다.
        seed = mnemonic.toSeed(words, null);

//        String[] mnemonic_words = words.split(" ");

//        for (int i = 0; i < mnemonic_views.length; i++) {
//            mnemonic_views[i].setText( mnemonic_words[i] );
//        }

        return seed;
    }
}
