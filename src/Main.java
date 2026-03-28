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
import java.util.List;

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
        frame.setSize(500, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1, 10, 10));

        // Campo Email
        JTextField emailField = new JTextField();
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));

        // Campo Senha
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createTitledBorder("Senha"));

        // Encrypt Botão
        JButton encryptButton = new JButton("Encrypt");

        //Decrypt Botão
        JButton decryptButton = new JButton("Decrypt");

        // Adicionando ao frame
        frame.add(emailField);
        frame.add(passwordField);
        frame.add(encryptButton);
        frame.add(decryptButton);

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

        decryptButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Encrypt Login");
                frame.setSize(500, 800);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new GridLayout(4, 1, 10, 10));
                frame.setVisible(true);
                JLabel label = new JLabel();
                label.setText("<html>" + decryptInformations().replace("\n", "<br>") + "</html>");
                frame.add(label);
            }
        });
    }

    public static String decryptInformations() {

        String resultado = "Senhas criptografadas. Verifique sua chave(key.txt) e tente novamente.";
        fileEncrypter = new File("Encrypted.txt");
        HashMap<String, String> contas = new HashMap<>();

        if (fileEncrypter.exists()) {
            try (Scanner filescan = new Scanner(fileEncrypter)) {

                while (filescan.hasNextLine()) {
                    String data = filescan.nextLine();

                    String email = data.split(":")[0];
                    String senha = data.split(":")[1].trim();

                    Decrypter dc = new Decrypter(key);

                    contas.put(dc.getDecrypted(email), dc.getDecrypted(senha));
                    System.out.println(dc.getDecrypted(email));
                    System.out.println(dc.getDecrypted(senha));

                    resultado = contas.entrySet()
                            .stream()
                            .map(entry -> entry.getKey() + ": " + entry.getValue())
                            .collect(java.util.stream.Collectors.joining("\n"));
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        return resultado;
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