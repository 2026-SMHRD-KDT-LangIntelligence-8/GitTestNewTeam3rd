package com.smhrd.jumeokbap.service;

import com.smhrd.jumeokbap.config.CodefProperties;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class CodefCryptoService {

    public String encryptPassword(String plainText) {
        try {
            String publicKey = CodefProperties.PUBLIC_KEY;

            X509EncodedKeySpec keySpec =
                    new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception e) {
            throw new RuntimeException("RSA 암호화 실패: " + e.getMessage(), e);
        }

    }
}