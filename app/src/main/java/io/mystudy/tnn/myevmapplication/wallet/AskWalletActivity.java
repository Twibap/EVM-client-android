package io.mystudy.tnn.myevmapplication.wallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import io.mystudy.tnn.myevmapplication.AccountActivity;
import io.mystudy.tnn.myevmapplication.R;

/**
 * 사용자의 지갑 보유 여부를 확인하는 Activity이다.
 * 지갑이 있는 사용자는 계정 주소 입력 화면으로 이동한다.
 * 지갑이 없는 사용자는 지갑 생성 화면으로 이동한다.
 * 각 화면의 결과를 MainActivity로 전달한다.
 */
public class AskWalletActivity extends AppCompatActivity implements View.OnClickListener {

    Button btYesHaveWallet;
    Button btNoHaveWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_wallet);

        viewInit();
    }

    private void viewInit(){
        btYesHaveWallet = findViewById(R.id.btYes);
        btNoHaveWallet = findViewById(R.id.btNo);

        btYesHaveWallet.setOnClickListener(this);
        btNoHaveWallet.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            // 지갑 주소를 입력한다.
            case R.id.btYes:
                intent = new Intent(this, AccountActivity.class);
                break;

            // 지갑을 만든다.
            case R.id.btNo:
                intent = new Intent(this, MnemonicActivity.class);
                break;
        }

        startActivityForResult(intent, 0);
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
