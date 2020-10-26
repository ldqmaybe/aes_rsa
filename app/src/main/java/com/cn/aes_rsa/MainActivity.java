package com.cn.aes_rsa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cn.aes_rsa.encrypt.AesUtil;
import com.cn.aes_rsa.encrypt.Encrypt;
import com.cn.aes_rsa.encrypt.EncryptException;
import com.cn.aes_rsa.encrypt.RsaUtil;
import com.cn.aes_rsa.encrypt.SHA256withRSA;
import com.cn.aes_rsa.encrypt.http.HttpManager;
import com.cn.aes_rsa.encrypt.http.NetWorkCallBack;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MainActivity extends AppCompatActivity {
    String encrpytData;
    EditText etInput;
    TextView tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etInput = findViewById(R.id.et_input);
        tvShow = findViewById(R.id.tv_show);
        findViewById(R.id.btn_encrypt).setOnClickListener(v -> {
            try {
                encrpytData = getEncrpytData();
                tvShow.setText(encrpytData);
            } catch (EncryptException e) {
                e.printStackTrace();
            }

        });
        findViewById(R.id.btn_decrypt).setOnClickListener(v -> {
            try {
                showDecrpytData(tvShow.getText().toString());
            } catch (EncryptException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.btn_server).setOnClickListener(v -> {
            try {
                HttpManager.getInstance().postJson(getEncrpytData(), new NetWorkCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> {
                            try {
                                showDecrpytData(response);
                            } catch (EncryptException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    public void onError(String e) {

                    }
                });
            } catch (EncryptException e) {
                e.printStackTrace();
            }
        });
    }


    private void showDecrpytData(String response) throws EncryptException {
        JsonObject root = JsonParser.parseString(response).getAsJsonObject();

        String encryptKey = root.get("encryptKey").getAsString();
        String decryptKey = RsaUtil.decrypt(encryptKey, Encrypt.PRIVATE_KEY);
        System.out.println("解密后的密钥：" + decryptKey);

        String busData = root.get("busData").getAsString();
        String decryptBusData = AesUtil.decrypt(busData, decryptKey);

        JsonObject respDataJsonObject = JsonParser.parseString(decryptBusData).getAsJsonObject();
        System.out.println("解密后的数据：" + respDataJsonObject.toString());

        String decryptPlaitText = respDataJsonObject.get("plainText").getAsString();
        String decryptSignInfo = respDataJsonObject.get("signInfo").getAsString();
        etInput.setText(decryptPlaitText);
    }

    private String getEncrpytData() throws EncryptException {
        JsonObject busDataJson = new JsonObject();
        String plainText = etInput.getText().toString();
        String signInfo = SHA256withRSA.sign(Encrypt.PRIVATE_KEY, plainText);
        busDataJson.addProperty("plainText", plainText);
        busDataJson.addProperty("signInfo", signInfo);

        String aesKey = AesUtil.getAESSecureKey();
        String encryptedAesKey = RsaUtil.encrypt(aesKey, Encrypt.PUBLIC_KEY);
        String encrypt = AesUtil.encrypt(busDataJson.toString(), aesKey);

        JsonObject root = new JsonObject();
        root.addProperty("encryptKey", encryptedAesKey);
        root.addProperty("busData", encrypt);
        return root.toString();
    }
}