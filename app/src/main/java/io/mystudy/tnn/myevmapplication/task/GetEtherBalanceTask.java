package io.mystudy.tnn.myevmapplication.task;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import io.mystudy.tnn.myevmapplication.Application.Confidential;
import io.mystudy.tnn.myevmapplication.Application.Dlog;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetEtherBalanceTask extends AsyncTask<String, Boolean, String> {

    private String ETH_ACCOUNT;
    private String ETH_NODE;
    private String ETH_JSON_RPC_MSG;

    private Handler mBalanceHandler;

    public enum NetworkType {
        MAIN, ROPSTEN   // , KOVAN, RINKEBY
    }

    public GetEtherBalanceTask(NetworkType networkType, Handler balanceHandler){
        switch (networkType){
            case MAIN:
                ETH_NODE = "https://mainnet.infura.io/v3/"+Confidential.Infura_Project_ID;
                break;
            case ROPSTEN:
            default:
                ETH_NODE = "https://ropsten.infura.io/v3/"+Confidential.Infura_Project_ID;
                break;
        }

        mBalanceHandler = balanceHandler;
    }

    @Override
    protected void onPreExecute() {
        mBalanceHandler.sendEmptyMessage(1);    // send true for show loading message;
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String[] eth_accounts) {
        ETH_ACCOUNT = eth_accounts[0];
        ETH_JSON_RPC_MSG = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\": [\""+ETH_ACCOUNT+"\", \"latest\"],\"id\":1}";

        RequestBody body = FormBody
                .create(MediaType.parse("application/json"), ETH_JSON_RPC_MSG);

        Request request = new Request.Builder()
                .url( ETH_NODE )
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();

        onProgressUpdate(true);

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Web3j web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/v3/"+Confidential.Infura_Project_ID));
//        try {
//            BigInteger weiBalance = web3j.ethGetBalance(ETH_ACCOUNT, DefaultBlockParameterName.LATEST).send().getBalance();
//            BigDecimal ethBalance = Convert.fromWei(weiBalance.toString(), Convert.Unit.ETHER);
//            return ethBalance.toString(); // 15.69739122142653826
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null){
            Dlog.e("GetEtherBalanceTask Response : "+ s);
            // {"jsonrpc":"2.0","id":1,"result":"0xd9d85625c576c714"}

            onProgressUpdate(false);

            try {
                JSONObject object = new JSONObject(s);
                String resultResponse = object.getString("result");
                BigInteger weiBalance = Numeric.toBigInt(resultResponse);
                BigDecimal ethBalance = Convert.fromWei(weiBalance.toString(), Convert.Unit.ETHER);

                String balance = ethBalance.toString();
                Bundle bundle = new Bundle();
                bundle.putString("balance", balance);

                Message msg = new Message();
                msg.what = 0;
                msg.setData(bundle);

                mBalanceHandler.sendMessage(msg);    // send false stop showing loading message
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            super.onPostExecute(s);
        }
    }
}
