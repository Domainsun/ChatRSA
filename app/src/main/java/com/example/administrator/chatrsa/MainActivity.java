package com.example.administrator.chatrsa;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.chatrsa.databinding.MainBinding;

import java.io.IOException;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MainBinding binding;
    String pubKey,priKey,strEn,strDE,strDEafter,strEn1;
    HashMap<String, Object> map = new HashMap<String, Object>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.main);

        event();

    }

    private void event() {
        binding.button.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {


        /*生成公钥私钥*/
        KeyPair keyPair=RSAUtlis.generateRSAKeyPair(RSAUtlis.DEFAULT_KEY_SIZE);
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        switch (view.getId()){
            case R.id.button:

                if (binding.editText.getText().toString().length()==0) {
                    Toast.makeText(MainActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    strDE=binding.editText.getText().toString();

                    System.out.println("用户输入的数据（未加密）:"+strDE);

                    //公钥加密
                    byte[] encryptBytes= new byte[0];
                    try {
                        encryptBytes = RSAUtlis.encryptByPublicKeyForSpilt(strDE.getBytes(),publicKey.getEncoded());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("加密后的数据:"+encryptBytes);
                    strEn1=String.valueOf(encryptBytes);
                    get(strEn1);


                    //私钥解密
                    byte[] decryptBytes= new byte[0];
                    try {
                        decryptBytes = RSAUtlis.decryptByPrivateKeyForSpilt(encryptBytes,privateKey.getEncoded());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    strDEafter=new String(decryptBytes);
                    System.out.println("解密后数据:"+strDEafter);

                }


                binding.tv.setText("输入数据:"+strDE+"\n"+"加密后:"+strEn1+"\n解密后:"+strDEafter);


                break;
        }
    }


    OkHttpClient okHttpClient = new OkHttpClient();
    public void get(String str) {


        final String url = "http://120.25.192.181/stest1/2.php?data="+str;
        System.out.println(url);


        new Thread(new Runnable() {
            @Override
            public void run() {

                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    response.body().toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ).start();


    }

    }
