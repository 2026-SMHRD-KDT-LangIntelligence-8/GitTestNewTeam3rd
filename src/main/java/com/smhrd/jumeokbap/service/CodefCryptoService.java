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

    private static final String ENCRYPT_TYPE_RSA = "RSA";

    public String encryptPassword(String plainText) {
        try {
            String base64PublicKey = CodefProperties.PUBLIC_KEY;

            if (base64PublicKey == null || base64PublicKey.isBlank()) {
                throw new IllegalStateException("CODEF public key가 설정되지 않았습니다.");
            }

            byte[] bytePublicKey = Base64.getDecoder().decode(base64PublicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPT_TYPE_RSA);
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(bytePublicKey));

            Cipher cipher = Cipher.getInstance(ENCRYPT_TYPE_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] bytePlain = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytePlain);

        } catch (Exception e) {
            throw new RuntimeException("CODEF 비밀번호 RSA 암호화 실패", e);
        }
    }
}