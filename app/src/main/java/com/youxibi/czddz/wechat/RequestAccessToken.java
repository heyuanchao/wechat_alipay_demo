package com.youxibi.czddz.wechat;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.youxibi.czddz.R;
import com.youxibi.czddz.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RequestAccessToken extends AsyncTask<String, Void, String> {

    private Activity activity;

    public RequestAccessToken(Activity activity) {
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
    protected void onPostExecute(String result) {
        if (result == null) {
            Toast.makeText(activity, R.string.connection_error, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(result);

            String access_token = jsonObject.optString("access_token");
            int expires_in = jsonObject.optInt("expires_in");
            String refresh_token = jsonObject.optString("refresh_token");
            String openid = jsonObject.optString("openid");
            String scope = jsonObject.optString("scope");
            String unionid = jsonObject.optString("unionid");

            String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid;
            new RequestUserInfo(activity).execute(url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
