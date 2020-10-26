package com.cn.aes_rsa.encrypt;

import com.cn.aes_rsa.encrypt.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author LinDingQiang
 * @time 2020/9/4 14:54
 * @email dingqiang.l@verifone.cn
 */
public class SHA256withRSA {

    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    /**
     * 还原公钥
     *
     * @param publicKey 公钥串
     * @return PublicKey
     */
    public static PublicKey restorePublicKey(String publicKey) throws EncryptException {
        byte[] prikeyByte;
        PublicKey pubTypeKey = null;
        try {
            prikeyByte = Base64.decodeBase64(publicKey.getBytes(StandardCharsets.UTF_8));
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(prikeyByte);
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);

            pubTypeKey = factory.generatePublic(x509EncodedKeySpec);
            return pubTypeKey;
        } catch (Exception e) {
            e.printStackTrace();
            throw new EncryptException("-10000", e);
        }
    }

    /**
     * 还原私钥
     *
     * @param privateKey 私钥串
     * @return PrivateKey
     */
    public static PrivateKey restorePrivateKey(String privateKey) throws EncryptException {
        byte[] prikeyByte;
        PrivateKey priTypeKey;
        try {
            prikeyByte = Base64.decodeBase64(privateKey.getBytes(StandardCharsets.UTF_8));
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(prikeyByte);
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            priTypeKey = factory.generatePrivate(pkcs8EncodedKeySpec);
            return priTypeKey;
        } catch (Exception e) {
            e.printStackTrace();
            throw new EncryptException("-10001", e);
        }
    }


    /**
     * 签名
     *
     * @param privateKey 私钥
     * @param plainText  明文
     * @return 签名后的签名串
     */
    public static String sign(String privateKey, String plainText) throws EncryptException {
        try {
            PrivateKey restorePrivateKey = restorePrivateKey(privateKey);
            Signature sign = Signature.getInstance(SIGNATURE_ALGORITHM);
            sign.initSign(restorePrivateKey);
            sign.update(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] signByte = sign.sign();
            return Base64.encodeBase64String(signByte);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EncryptException("-10003", e);
        }
    }

    /**
     * 验签
     *
     * @param publicKey  公钥
     * @param plainText  明文
     * @param signedText 签名
     */
    public static boolean verifySign(String publicKey, String plainText, String signedText) throws EncryptException{
        try {
            PublicKey publicTypeKey = restorePublicKey(publicKey);
            Signature verifySign = Signature.getInstance(SIGNATURE_ALGORITHM);
            verifySign.initVerify(publicTypeKey);
            verifySign.update(plainText.getBytes(StandardCharsets.UTF_8));
            return verifySign.verify(Base64.decodeBase64(signedText.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new EncryptException("-10002", e);
        }
    }

}
