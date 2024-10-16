import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class SecureServer {
    public void startServer() throws SSLSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            FileInputStream keyStoreFile = new FileInputStream("keystore.jks");
            keyStore.load(keyStoreFile, "password".toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "password".toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(8443);

            System.out.println("Servidor SSL/TLS iniciado...");

            while (true) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }

        } catch (FileNotFoundException e) {
            throw new SSLSecurityException("Arquivo KeyStore não encontrado", e);
        } catch (SSLHandshakeException e) {
            throw new SSLSecurityException("Erro no handshake SSL: Certificado inválido ou não confiável", e);
        } catch (IOException e) {
            throw new SSLSecurityException("Erro de I/O durante a comunicação SSL", e);
        } catch (Exception e) {
            throw new SSLSecurityException("Erro ao configurar o servidor SSL", e);
        }
    }

    private void handleClient(SSLSocket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Conexão segura estabelecida com sucesso!");

            String message = in.readLine();
            System.out.println("Cliente: " + message);

            socket.close();
        } catch (IOException e) {
            System.err.println("Erro ao se comunicar com o cliente: " + e.getMessage());
        }
    }
}
