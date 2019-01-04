package io.mystudy.tnn.myevmapplication.task;

import android.os.AsyncTask;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import io.mystudy.tnn.myevmapplication.Application.Dlog;
import io.mystudy.tnn.myevmapplication.Vending.OrderRepository;
import io.mystudy.tnn.myevmapplication.wallet.history.AccountInfoAdapter;

public class GetOrderHistoryTask extends AsyncTask<String, Void, Void> {

    private AccountInfoAdapter mAccountInfoAdapter;

    public GetOrderHistoryTask(AccountInfoAdapter list) {
        mAccountInfoAdapter = list;
    }

    @Override
    protected void onPreExecute() {
        mAccountInfoAdapter.setStateMessage(OrderRepository.STATUS.LOOKING_ORDER);
        mAccountInfoAdapter.showProgressBar(true);
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... execParams) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        OrderRepository repository = OrderRepository.getInstance();
        ArrayList result;
        if ( execParams.length == 1 ){
            String address = execParams[0];
            result = repository.getHistory(address);

        } else if ( execParams.length == 2 ){
            String address = execParams[0];
            String skipCount = execParams[1];
            result = repository.getHistory(address, skipCount);

        } else {
            throw new InvalidParameterException();
        }

        if (result != null)
            Dlog.i("History "+result.size()+" received");
        else
            Dlog.i("No history received");

        mAccountInfoAdapter.addAll( result );
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mAccountInfoAdapter.showProgressBar(false);
        mAccountInfoAdapter.setStateMessage(OrderRepository.getInstance().getStatus());
        super.onPostExecute(aVoid);
    }

    public void execute(String address, int skipCount) {
        String count = Integer.toString( skipCount );
        this.execute(address, count);
    }
}
