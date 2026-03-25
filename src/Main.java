import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileWriter;
import java.io.File;                  // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
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

        //Inicia o gerator de chave para o algoritmo AES
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        //Configura para gerar uma chave de 256bits
        keyGen.init(256);
        //Gera uma chave de criptografia que também será usada para descriptgrafia



        //CRIANDO ARQUIVOS
        fileKey = new File("key.txt");
        if (fileKey.exists()) {
            try(Scanner filescan = new Scanner(fileKey)) {
                while (filescan.hasNextLine()) {
                    String data = filescan.nextLine();
                    key = new SecretKeySpec(Base64.getDecoder().decode(data), "AES");
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        else {
            System.out.println("Key file not found");
            key_fw = new FileWriter("key.txt");
            key = keyGen.generateKey();
            key_fw.write(Base64.getEncoder().encodeToString(key.getEncoded()));
            key_fw.close();
        }
        decryptInformations();
        encryptInformations();

    }
    public static void encryptInformations() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException{

        while(true){

            Scanner scan = new Scanner(System.in);
            String email, senha;
            System.out.println("Digite o email a ser criptografado: ");
            email = scan.nextLine();
            System.out.println("Digite a senha deste email criptografado: ");
            senha = scan.nextLine();
            encrypter = new Encrypter(email, senha, key);


            System.out.println("Deseja continuar inserir outra conta para criptografar?: ");
            int escolha = scan.nextInt();
            contas.put(encrypter.Email(), encrypter.Senha());
            if (escolha != 1) {
                break;
            }
        }

        try{
            encrypter_fw = new FileWriter("Encrypted.txt", true);
            for(Map.Entry<String, String> entry : contas.entrySet()){

                encrypter_fw.write(entry.getKey() + ": " + entry.getValue() + "\n");

            }
            encrypter_fw.close();
            System.out.println("Conta adicionada com sucesso!");

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
    public static void decryptInformations(){
        fileEncrypter = new File("Encrypted.txt");
        if (fileEncrypter.exists()) {
            try(Scanner filescan = new Scanner(fileEncrypter)) {
                while (filescan.hasNextLine()) {
                    String data = filescan.nextLine();
                    String email = data.split(":")[0];
                    String senha = data.split(":")[1].trim();

                    Decrypter dc = new Decrypter(key);

                    //MOSTRAR SENHAS DESCRIPTOGRAFADAS
                    System.out.println(dc.getDecrypted(email));
                    System.out.println(dc.getDecrypted(senha));
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}