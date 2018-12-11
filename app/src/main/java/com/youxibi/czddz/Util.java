package com.youxibi.czddz;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;

public class Util {

    public static String httpGet(String urlString) throws IOException {
        InputStream stream = null;
        String str = "";

        try {
            stream = downloadUrl(urlString, "GET", null);
            str = read(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    public static String httpPost(String urlString, String params) throws IOException {
        InputStream stream = null;
        String str = "";

        try {
            stream = downloadUrl(urlString, "POST", params);
            str = read(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    private static InputStream downloadUrl(String urlString, String method, String params) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod(method);

        if (params != null) {
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(params.getBytes());
        }
        if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            return downloadUrl(conn.getHeaderField("Location"), method, params);
        }
        InputStream stream = conn.getInputStream();
        return stream;
    }

    /**
     * 封装请求体信息
     */
    public static String getRequestData(Map<String, String> params, String encode) {
        StringBuffer sb = new StringBuffer();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            sb.deleteCharAt(sb.length() - 1);// 删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // 从流中读取数据
    public static String read(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return new String(outStream.toByteArray());
    }

    public static String getRandomString(int type, int length) { //length表示生成字符串的长度
        String base = "0123456789";
        if (type == 1) {
            base = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";
//            base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        }
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String MD5(String s) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(s.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    sb.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    sb.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        Log.i("Util", output.toByteArray().length / 1024 + " K, " + bmp.getWidth() + ", " + bmp.getHeight());
        if (output.toByteArray().length > 32 * 1024) { // 微信分享缩略图大小不能超过32k
            output.reset();
            Bitmap b = Bitmap.createScaledBitmap(bmp, bmp.getWidth() * 4 / 5, bmp.getHeight() * 4 / 5, true);
            b.compress(Bitmap.CompressFormat.PNG, 100, output);
            Log.i("Util", output.toByteArray().length / 1024 + " K");
        }
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
