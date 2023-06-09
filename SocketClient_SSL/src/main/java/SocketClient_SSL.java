import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author William
 */
public class SocketClient_SSL extends javax.swing.JFrame {

    /**
     * Creates new form SocketClient_SSL
     */
    
    static SSLSocketFactory socketFactory;
    static SSLSocket socket;
    static DataInputStream in;
    static DataOutputStream out;
    static String username;
    
    static String tagNewNick = "#NEW_NICK#";
            
    public SocketClient_SSL() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnSend = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtChat = new javax.swing.JTextArea();
        edtText = new javax.swing.JTextField();
        edtUsername = new javax.swing.JTextField();
        btnChangeUsername = new javax.swing.JButton();
        lbUsername = new javax.swing.JLabel();
        txtTitle = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("[SSL] Socket Client");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btnSend.setText("Enviar");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        txtChat.setEditable(false);
        txtChat.setColumns(20);
        txtChat.setRows(5);
        jScrollPane1.setViewportView(txtChat);

        edtText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                edtTextKeyPressed(evt);
            }
        });

        edtUsername.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                edtUsernameKeyPressed(evt);
            }
        });

        btnChangeUsername.setText("Alterar nome de usuário");
        btnChangeUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeUsernameActionPerformed(evt);
            }
        });

        lbUsername.setText("Nome de usuário:");

        txtTitle.setForeground(new java.awt.Color(0, 102, 0));
        txtTitle.setText("Projeto Integrador - Sistemas de Informação");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(edtText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSend))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnChangeUsername)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnChangeUsername)
                    .addComponent(lbUsername)
                    .addComponent(txtTitle))
                .addGap(7, 7, 7)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSend)
                    .addComponent(edtText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        String msg = edtText.getText();
        if (msg.isBlank() || msg.isEmpty()){
            return;
        }
        edtText.setText("");
        try {            
            String crypted = EncryptUtils.encryptData(msg);
            out.writeUTF(crypted);
            out.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnSendActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            socket.close();
            System.out.print("SocketClient_SSL.formWindowClosed()");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_formWindowClosing

    private void btnChangeUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeUsernameActionPerformed
        String msg = edtUsername.getText();
        if (msg.isEmpty() || msg.isBlank() || msg.equals(username)){
            return;
        }
        edtUsername.setText("");
        try {
            String crypted = EncryptUtils.encryptData(tagNewNick + msg);
            out.writeUTF(crypted);
            out.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnChangeUsernameActionPerformed

    private void edtTextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_edtTextKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            btnSendActionPerformed(null);
        }
    }//GEN-LAST:event_edtTextKeyPressed

    private void edtUsernameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_edtUsernameKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            btnChangeUsernameActionPerformed(null);
        }
    }//GEN-LAST:event_edtUsernameKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
            /* Set the Nimbus look and feel */
            //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
            /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
            * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
            */
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(SocketClient_SSL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(SocketClient_SSL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(SocketClient_SSL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(SocketClient_SSL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            //</editor-fold>
            
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new SocketClient_SSL().setVisible(true);
                }
            });
            
            // encrypt //
            EncryptUtils.generateKeys();
            
            try {
                // just in case //
                String[] protocols = new String[]{"TLSv1.3"};
                String[] cipher_suites = new String[]{"TLS_AES_128_GCM_SHA256"};
                // server cert //
                System.setProperty("javax.net.ssl.keyStore", ".\\ssl\\keystore.jks");
                System.setProperty("javax.net.ssl.keyStorePassword", "keystore");
                // client cert //
                System.setProperty("javax.net.ssl.trustStore", ".\\ssl\\clienttruststore.jts");
                System.setProperty("javax.net.ssl.trustStorePassword", "keystore");
                // protocol //
                System.setProperty("jdk.tls.server.protocols", "TLSv1.3");

                socketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
                socket = (SSLSocket)socketFactory.createSocket("127.0.0.1", 8084);

                socket.setEnabledProtocols(protocols);
                socket.setEnabledCipherSuites(cipher_suites);           
                
                socket.startHandshake();

                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                // listenner - thread nao consegui fazer funcionar //
                while(true){
                    String input = in.readUTF();                                         
                    String message = EncryptUtils.decryptData(input);
                    
                    if (message.contains(tagNewNick)){
                        String data = message.substring(message.indexOf(tagNewNick) + tagNewNick.length());
                        edtUsername.setText(data);
                        username = data;
                    }else{                    
                        txtChat.setText(txtChat.getText().trim()+"\n" + message);
                    }                                        
                }
            
        } catch (IOException ex) {
                System.err.println("Error main exec");
        }
        //</editor-fold>
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JButton btnChangeUsername;
    public static javax.swing.JButton btnSend;
    public static javax.swing.JTextField edtText;
    public static javax.swing.JTextField edtUsername;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbUsername;
    public static javax.swing.JTextArea txtChat;
    private javax.swing.JLabel txtTitle;
    // End of variables declaration//GEN-END:variables
}
