import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Encrypter
{
	private String email;
    private String senha;

    public String Email()
    {
        return email;
    }
    public String Senha()
    {
        return senha;
    }

    public Encrypter(String email, String senha, SecretKey key) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedEmail = cipher.doFinal(email.getBytes());
        byte[] encryptedPwd = cipher.doFinal(senha.getBytes());

        byte[] encodedEmail = Base64.getEncoder().encode(encryptedEmail);
        byte[] encodedPwd = Base64.getEncoder().encode(encryptedPwd);

        try {
            this.email = new String(encodedEmail);
            this.senha = new String(encodedPwd);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
 }
