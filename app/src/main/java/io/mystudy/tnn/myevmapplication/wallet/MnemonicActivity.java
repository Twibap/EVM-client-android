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

//        mnemonicUtils = new Mnemonic(this);

        address = new Address();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
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

                address = new Address();
                showDataToUI( address );

                break;
        }
    }

    void showDataToUI(Address address){
        viewAddress.setText(address.getStringAddress());
        viewPrivateKey.setText(address.getStringPrivateKey());
    }

    void getMnemonic(){
//        SecureRandom random = new SecureRandom();
//        MnemonicUtils mnemonicUtils = new MnemonicUtils();
//        byte[] walletSeed = new byte[16];   // 128 bits, 16 bytes

        // Seed 얻기
//        random.nextBytes(walletSeed);

//        String words = mnemonicUtils.generateMnemonic(walletSeed);
//        Dlog.e(words);

//        String[] mnemonic_words = words.split(" ");

//        for (int i = 0; i < mnemonic_views.length; i++) {
//            mnemonic_views[i].setText( mnemonic_words[i] );
//        }
    }
}
