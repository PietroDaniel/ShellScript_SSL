import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class SecureClient {
    public void startClient() throws SSLSecurityException {
        try {
            KeyStore trustStore = KeyStore.getInstance("JKS");
            FileInputStream trustStoreFile = new FileInputStream("truststore.jks");
            trustStore.load(trustStoreFile, "password".toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket("localhost", 8443);

            BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true);

            out.println("Olá, servidor!");

            String message = in.readLine();
            System.out.println("Servidor: " + message);

            sslSocket.close();
        } catch (SSLHandshakeException e) {
            throw new SSLSecurityException("Erro no handshake SSL: Certificado do servidor inválido ou não confiável", e);
        } catch (FileNotFoundException e) {
            throw new SSLSecurityException("Arquivo TrustStore não encontrado", e);
        } catch (IOException e) {
            throw new SSLSecurityException("Erro de I/O durante a comunicação SSL", e);
        } catch (Exception e) {
            throw new SSLSecurityException("Erro ao configurar o cliente SSL", e);
        }
    }
}
