package io.mystudy.tnn.myevmapplication.task;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import io.mystudy.tnn.myevmapplication.Vending.Bill;
import io.mystudy.tnn.myevmapplication.Vending.OrderRepository;

import static android.support.constraint.Constraints.TAG;

public class VerifyPaymentTask extends AsyncTask<Bill, String, String> {
    @Override
    protected String doInBackground(Bill... bills) {
        OrderRepository repository = OrderRepository.getInstance();
        try {
            return repository.verifyPayment(bills[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
        Log.i(TAG, "onPostExecute: "+s);
    }
}
