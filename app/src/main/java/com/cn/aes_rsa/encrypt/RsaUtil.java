package com.cn.aes_rsa.encrypt;

import com.cn.aes_rsa.encrypt.binary.Base64;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RsaUtil {
    /**
     * 算法名称
     */
    private static final String ALGORITHM = "RSA";
    private static final String PADDING = "RSA/ECB/PKCS1Padding";

    /**
     * 加密
     *
     * @param data   加密之前的数据
     * @param pubKey 公钥
     * @return 加密之后的数据
     */
    public static String encrypt(String data, String pubKey) throws EncryptException {
        try {
            // base64编码的公钥
            byte[] decoded = Base64.decodeBase64(pubKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(decoded));

            Cipher cipher = Cipher.getInstance(PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            int splitLength = publicKey.getModulus().bitLength() / 8 - 11;
            byte[][] arrays = RsaUtil.splitBytes(data.getBytes(), splitLength);
            StringBuilder stringBuffer = new StringBuilder();
            for (byte[] array : arrays) {
                stringBuffer.append(RsaUtil.bytesToHexString(cipher.doFinal(array)));
            }
            return stringBuffer.toString();
        }catch (Exception e) {
            System.out.println("RSA 加密失败");
            e.printStackTrace();
            throw new EncryptException("-10004",e);
        }
    }

    /**
     * 解密
     *
     * @param data   解密之前的数据
     * @param priKey 秘钥
     * @return 解密之后的数据
     */
    public static String decrypt(String data, String priKey) throws EncryptException {
        try {
            KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(priKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

            Cipher cipher = Cipher.getInstance(PADDING);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            int splitLength = privateKey.getModulus().bitLength() / 8;
            byte[] contentBytes = hexStringToBytes(data);
            byte[][] arrays = RsaUtil.splitBytes(contentBytes, splitLength);
            StringBuilder stringBuffer = new StringBuilder();
            for (byte[] array : arrays) {
                stringBuffer.append(new String(cipher.doFinal(array)));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            System.out.println("RSA 解密失败");
            e.printStackTrace();
            throw new EncryptException("-10004",e);
        }
    }

    /**
     * 根据限定的每组字节长度，将字节数组分组
     *
     * @param bytes       等待分组的字节组
     * @param splitLength 每组长度
     * @return 分组后的字节组
     */
    public static byte[][] splitBytes(byte[] bytes, int splitLength) {
        //bytes与splitLength的余数
        int remainder = bytes.length % splitLength;
        //数据拆分后的组数，余数不为0时加1
        int quotient = remainder != 0 ? bytes.length / splitLength + 1 : bytes.length / splitLength;
        byte[][] arrays = new byte[quotient][];
        byte[] array = null;
        for (int i = 0; i < quotient; i++) {
            //如果是最后一组（quotient-1）,同时余数不等于0，就将最后一组设置为remainder的长度
            if (i == quotient - 1 && remainder != 0) {
                array = new byte[remainder];
                System.arraycopy(bytes, i * splitLength, array, 0, remainder);
            } else {
                array = new byte[splitLength];
                System.arraycopy(bytes, i * splitLength, array, 0, splitLength);
            }
            arrays[i] = array;
        }
        return arrays;
    }

    /**
     * 将字节数组转换成16进制字符串
     *
     * @param bytes 即将转换的数据
     * @return 16进制字符串
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length);
        String temp;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(0xFF & aByte);
            if (temp.length() < 2) {
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    /**
     * 将16进制字符串转换成字节数组
     *
     * @param hex 16进制字符串
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hex) {
        int len = (hex.length() / 2);
        hex = hex.toUpperCase();
        byte[] result = new byte[len];
        char[] chars = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(chars[pos]) << 4 | toByte(chars[pos + 1]));
        }
        return result;
    }

    /**
     * 将char转换为byte
     *
     * @param c char
     * @return byte
     */
    private static byte toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}
