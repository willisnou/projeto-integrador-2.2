import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

/**
 *
 * @author William
 */

class Connection {
    SSLSocket socket;
    DataInputStream in;
    DataOutputStream out;
    String username;  
    String tagNewNick = "#NEW_NICK#";
    
    public Connection(SSLSocket socket, String user){
        try {
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.username = user;
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Error creatind connection client.");
        }       
    }
    
    public void write(String msg){
        try {
            String output = EncryptUtils.encryptData(msg);
            this.out.writeUTF(output);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Error sendind message to client.");
        }
    }
    
    public void welcomeMessage(String msg){
        try {
            TimeUnit.MILLISECONDS.sleep(500);
            write(msg);
            write(tagNewNick + username);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            System.err.println("Error sendind welcome message to client.");
        }
    }
}

public class SocketServer_SSL {
    
    static SSLServerSocketFactory ssFactory;
    static SSLServerSocket ss;    
    static Map<String, Connection> listUsers = new HashMap<String, Connection>(); 
    static String tagNewNick = "#NEW_NICK#";
    
    //just in case //
    static String[] protocols = new String[]{"TLSv1.3"};
    static String[] cipher_suites = new String[]{"TLS_AES_128_GCM_SHA256"};
    
    // controllers //
    private int count = 0;
    private String defaultName = "USER_";
    
    public SocketServer_SSL(){
        try {
            initializeProperties();
            ssFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            ss = (SSLServerSocket)ssFactory.createServerSocket(8084);
            ss.setNeedClientAuth(true);
            ss.setEnabledCipherSuites(cipher_suites);
            
            System.out.println("----------------------------------------------");
            System.out.println("Listenning (SSL)...");
            System.out.println("----------------------------------------------");
            
            start();
        } catch (IOException ex) {
            System.err.println("Error creating SSL server.");
        }
    }
    
    private void start(){
        while(true){
            try {
                SSLSocket newSocket = (SSLSocket)ss.accept();
                String user = defaultName + String.valueOf(count++);
                Connection client = new Connection(newSocket, user);
                
                listUsers.put(user, client);
                System.out.println("New client just connected: " + user);
                
                Thread thread = new Thread(new SocketListenner(client, user));
                thread.setPriority(10);
                thread.setDaemon(true);
                thread.start();
            } catch (IOException ex) {
                System.err.println("Error starting or accepting new client.");
            }
            
            
        }
    }
    
    private void initializeProperties(){
        // server cert //
        System.setProperty("javax.net.ssl.keyStore", "c:\\ssl\\keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "keystore");
        // client cert //
        System.setProperty("javax.net.ssl.trustStore", "c:\\ssl\\clienttruststore.jts");
        System.setProperty("javax.net.ssl.trustStorePassword", "keystore");            
        // protocol //
        System.setProperty("jdk.tls.server.protocols", "TLSv1.3");        
    }    
    
    private class SocketListenner implements Runnable{
        private Connection client;
        private String user = "";        
        
        public SocketListenner(Connection socket, String user){
            this.client = socket;
            this.user = user;
        }
        
        private void writeToAll(String message){
            for (Map.Entry<String, Connection> entry : listUsers.entrySet()) {                        
                            String current = entry.getKey();
                            Connection client = entry.getValue();
                            System.out.println("Sending message to: " + current + " | " + message);
                            client.write(user + ": " + message);
                        }
        }
        
        private void systemMessage(String message){
            for (Map.Entry<String, Connection> entry : listUsers.entrySet()) {                        
                            String current = entry.getKey();
                            Connection client = entry.getValue();
                            System.out.println("Sending message to: " + current + " | " + message);
                            client.write("Sistema: " + message);
                        }
        }
        
        private void updateUsername(String message){
            String old = this.user;
            
            // atualiza o listenner //
            this.user = message.substring(message.indexOf(tagNewNick) + tagNewNick.length());
            
            // atualiza pro usuário //
            client.write(tagNewNick+this.user);
            
            // atualiza pra demais usuários //            
            systemMessage("Usuário " + old.toUpperCase() + " agora é conhecido como " + this.user.toUpperCase());
            
            // remove a referência antiga e atualiza //
            listUsers.remove(old);
            listUsers.put(this.user, client);
            
            // atualiza a referência interna //            
            client.username = this.user;
            
        } 
        
        private void disconnect(){
            try {
                client.in.close();
                client.out.close();
                client.socket.close();
                listUsers.remove(this.user);
                systemMessage("Usuário " + user + " se desconectou.");
                System.out.println("Client just disconnected: " + user);
            } catch (IOException ex) {
                System.err.println("Error disconnecting user");
            }
        }
        
        @Override
        public void run() {
            try {
                // hellow //
                for (Map.Entry<String, Connection> entry : listUsers.entrySet()) {                        
                    String current = entry.getKey();
                    Connection client = entry.getValue();
                    System.out.println("Sending hello to: " + current);
                    client.welcomeMessage(user + " Entrou!");
                }    
                // loop response //
                while (!ss.isClosed()){                                                        
                    String input = client.in.readUTF();
                    
                    // decrypt //
                    String message = EncryptUtils.decryptData(input);
                    
                    System.out.println("Data received...: " + message);
                    
                    // new nickname //
                    if (message.contains(tagNewNick)){
                        updateUsername(message);
                    }else{
                        writeToAll(message);   
                    }
                }           
                disconnect();
            } catch (Exception ex) {
                if (ex instanceof EOFException){                    
                    disconnect();
                }else if (ex instanceof SocketException){                    
                    disconnect();                    
                }else{
                    disconnect();
                    ex.printStackTrace();
                }
            }
        }
        
    }
    
    
    public static void main(String[] args) {
        // encrypt //
        EncryptUtils.generateKeys();
        
        new SocketServer_SSL();                
    }
}
