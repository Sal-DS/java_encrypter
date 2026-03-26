import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Main {

    public static File fileKey;
    public static FileWriter key_fw;
    public static File fileEncrypter;
    public static FileWriter encrypter_fw;
    public static SecretKey key;
    static Map<String, String> contas = new HashMap<>();
    public static Encrypter encrypter;

    public static void main(String[] args) throws Exception {

        // Inicia o gerador de chave AES
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);

        // Cria arquivos / carrega chave
        CriarAquivos(keyGen);

        JFrame frame = new JFrame("Encrypt Login");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1, 10, 10));

        // Campo Email
        JTextField emailField = new JTextField();
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));

        // Campo Senha
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createTitledBorder("Senha"));

        // Botão
        JButton encryptButton = new JButton("Encrypt");

        // Adicionando ao frame
        frame.add(emailField);
        frame.add(passwordField);
        frame.add(encryptButton);

        frame.setVisible(true);

        // Ação do botão
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String email = emailField.getText();
                String senha = new String(passwordField.getPassword());

                // ENCRYPT LOGIC
                Encrypter encrypter = null;

                try {
                    encrypter = new Encrypter(email, senha, key);
                } catch (NoSuchPaddingException |
                         IllegalBlockSizeException |
                         NoSuchAlgorithmException |
                         BadPaddingException |
                         InvalidKeyException ex) {

                    throw new RuntimeException(ex);
                }

                // ESCREVER NO DOCUMENTO
                try {
                    encrypter_fw = new FileWriter("Encrypted.txt", true);
                    encrypter_fw.write(encrypter.Email() + ": " + encrypter.Senha() + "\n");
                    encrypter_fw.close();

                    System.out.println("Conta adicionada com sucesso!");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public static void decryptInformations() {

        fileEncrypter = new File("Encrypted.txt");

        if (fileEncrypter.exists()) {
            try (Scanner filescan = new Scanner(fileEncrypter)) {

                while (filescan.hasNextLine()) {
                    String data = filescan.nextLine();

                    String email = data.split(":")[0];
                    String senha = data.split(":")[1].trim();

                    Decrypter dc = new Decrypter(key);

                    // Mostrar descriptografado
                    System.out.println(dc.getDecrypted(email));
                    System.out.println(dc.getDecrypted(senha));
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void CriarAquivos(KeyGenerator keyGen) throws IOException {

        fileKey = new File("key.txt");

        if (fileKey.exists()) {
            try (Scanner filescan = new Scanner(fileKey)) {

                while (filescan.hasNextLine()) {
                    String data = filescan.nextLine();
                    key = new SecretKeySpec(Base64.getDecoder().decode(data), "AES");
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } else {
            System.out.println("Key file not found");

            key_fw = new FileWriter("key.txt");

            key = keyGen.generateKey();

            key_fw.write(Base64.getEncoder().encodeToString(key.getEncoded()));
            key_fw.close();
        }
    }
}