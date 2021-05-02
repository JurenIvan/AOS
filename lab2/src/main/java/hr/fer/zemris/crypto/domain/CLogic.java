package hr.fer.zemris.crypto.domain;

import hr.fer.zemris.crypto.domain.enums.EncodingMode;
import hr.fer.zemris.crypto.domain.enums.HashAlgorithms;
import hr.fer.zemris.crypto.domain.enums.SimetricAlgorithm;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

import static hr.fer.zemris.crypto.domain.Utils.getPrivateKey;
import static hr.fer.zemris.crypto.domain.Utils.getPublicKey;
import static hr.fer.zemris.crypto.domain.enums.EncodingMode.CTR;
import static hr.fer.zemris.crypto.domain.enums.EncodingMode.ECB;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.*;

public class CLogic {


    public static String encrypt(String inTextArea, String keyTextField, String iv, SimetricAlgorithm simetricAlgorithm, EncodingMode encodingMode) {
        byte[] encryptionKeyBytes = keyTextField.getBytes(UTF_8);
        try {
            Cipher cipher = getInstance(simetricAlgorithm.getCode() + "/" + encodingMode.name() + "/" + (encodingMode == CTR ? "NoPadding" : "PKCS5Padding"));
            SecretKey secretKey = new SecretKeySpec(encryptionKeyBytes, simetricAlgorithm.getCode());
            if (encodingMode != ECB) {
                IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(UTF_8));
                cipher.init(ENCRYPT_MODE, secretKey, ivParameterSpec);
            } else {
                cipher.init(ENCRYPT_MODE, secretKey);
            }

            byte[] encryptedMessageBytes = cipher.doFinal(inTextArea.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : encryptedMessageBytes) {
                sb.append(String.format("%02X ", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String decrypt(String inTextArea, String keyTextField, String iv, SimetricAlgorithm simetricAlgorithm, EncodingMode encodingMode) {
        byte[] encryptionKeyBytes = keyTextField.getBytes(UTF_8);

        try {
            Cipher cipher = getInstance(simetricAlgorithm.getCode() + "/" + encodingMode.name() + "/" + (encodingMode == CTR ? "NoPadding" : "PKCS5Padding"));
            SecretKey secretKey = new SecretKeySpec(encryptionKeyBytes, simetricAlgorithm.getCode());
            if (encodingMode != ECB) {
                IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(UTF_8));
                cipher.init(DECRYPT_MODE, secretKey, ivParameterSpec);
            } else {
                cipher.init(DECRYPT_MODE, secretKey);
            }

            byte[] encryptedMessageBytes = cipher.doFinal(Utils.convertHex(inTextArea));

            return new String(encryptedMessageBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static byte[] encrypt(String data, String publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
            return cipher.doFinal(data.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] encrypt(String data, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static String decrypt(byte[] data, Key privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(data));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String decrypt(String data, String base64PrivateKey) {
        return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
    }

    public static String digest(String inData, HashAlgorithms algorithm) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm.getCode());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        byte[] result = md.digest(inData.getBytes(UTF_8));
        return Utils.bytesToHex(result);
    }
}
