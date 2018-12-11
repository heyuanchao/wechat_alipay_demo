package com.youxibi.czddz.alipay;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.youxibi.czddz.R;
import com.youxibi.czddz.Util;

import java.io.IOException;
import java.util.Map;

public class RequestAliPay extends AsyncTask<String, Void, String> {

    private Activity activity;

    public RequestAliPay(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return Util.httpGet(params[0]);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(final String orderInfo) {
        if (orderInfo == null) {
            Toast.makeText(activity, R.string.connection_error, Toast.LENGTH_SHORT).show();
            return;
        }
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                PayResult payResult = new PayResult(result);
                /**
                 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                 */
                // String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                final String resultStatus = payResult.getResultStatus();
                Log.i("RequestAliPay", "result status: " + resultStatus);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 判断resultStatus 为9000则代表支付成功
                        if (TextUtils.equals(resultStatus, "9000")) {
                            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                            Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}
