import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.filechooser.FileSystemView;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author William
 */
public final class EncryptUtils {
    private static final String ALGORITHM = "RSA";
    private static final String defaultPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator;
    private static final String privateFile = defaultPath + "privateKey.dat";
    private static final String publicFile = defaultPath + "publicKey.dat";
    private static final Path PRIVATEPATH = Paths.get(privateFile);
    private static final Path PUBLICPATH = Paths.get(publicFile);
    private static PublicKey publicKey = null;
    private static PrivateKey privateKey = null;    
    
    public static void generateKeys(){
        try {            
            if(Files.exists(PRIVATEPATH) && Files.exists(PUBLICPATH)){
                return;
            }
            
            KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
            generator.initialize(2048);
            
            KeyPair keys = generator.generateKeyPair();
            PrivateKey privateKey = keys.getPrivate();
            PublicKey publicKey = keys.getPublic();            
                        
            // salva em arquivo //
            if (!Files.exists(PUBLICPATH)){
                Files.createFile(PUBLICPATH);
                Files.write(PUBLICPATH, Base64.getEncoder().encode(publicKey.getEncoded()));
            }            
            if (!Files.exists(PRIVATEPATH)){
                Files.createFile(PRIVATEPATH);
                Files.write(PRIVATEPATH, Base64.getEncoder().encode(privateKey.getEncoded()));
            }                                                
        } catch (NoSuchAlgorithmException | IOException ex) {
            System.err.println("generageKeys: " + ex);
        }       
    }
    
    public static PublicKey loadPublicKey(){
        try {
            if(!Files.exists(PUBLICPATH)){
                return null;
            }
            if (publicKey != null){
                return publicKey;
            }
            
            byte[] fileData = Base64.getDecoder().decode(Files.readAllBytes(PUBLICPATH));                        
            
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(fileData);
            PublicKey key = factory.generatePublic(keySpec);
                    
            // def pra evitar io //
            publicKey = key;
            
            return key;
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.err.println("loading public key: " + ex);
            return null;
        }
    }
    
    public static PrivateKey loadPrivateKey(){
        try {
            if(!Files.exists(PRIVATEPATH)){
                return null;
            }
            if (privateKey != null){
                return privateKey;
            }
            
            byte[] fileData = Base64.getDecoder().decode(Files.readAllBytes(PRIVATEPATH));
            
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(fileData);
            PrivateKey key = factory.generatePrivate(keySpec);
            
            // def pra evitar io //
            privateKey = key;
            
            return key;            
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException   ex) {
            System.err.println("loading private key: " + ex);
            return null;
        }        
    }
    
     public static String encryptData(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        Cipher cipher = null;
        String encryptedString = "";
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, loadPublicKey());
            byte[] encryptedText = cipher.doFinal(data.getBytes());
            encryptedString = Base64.getEncoder().encodeToString(encryptedText);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("encrypting: " + ex);
        }

        return encryptedString;
    }

    public static String decryptData(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        String decryptedString = "";
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, loadPrivateKey());
            byte[] encryptedText = Base64.getDecoder().decode(data.getBytes());
            decryptedString = new String(cipher.doFinal(encryptedText));

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("decrypting: " + ex);
        }
        return decryptedString;
    }
}

/*
        //Uso: 
        
        String message = "Hello, World!";        
        Utils.generateKeys();                
        System.out.println("Original: " + message);
        String ciphed = Utils.encryptData(message);
        System.out.println("Encriptado: " + ciphed);                        
        System.out.println("Decriptado: " + Utils.decryptData(ciphed));
*/
