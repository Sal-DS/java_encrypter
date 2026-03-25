import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Decrypter {

    private final Cipher cipher;
    private SecretKey key;

    public Decrypter(SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        cipher =  Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        this.key = key;
    }
    public String getDecrypted(String info) throws IllegalBlockSizeException, BadPaddingException {
        byte[] encoded = Base64.getDecoder().decode(info);
        byte[] decoded = cipher.doFinal(encoded);
        return new String(decoded);
    }
}
