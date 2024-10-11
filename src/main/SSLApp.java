package main;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class SSLApp {

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "servidor".equalsIgnoreCase(args[0])) {
            executarServidor();
        } else {
            executarCliente();
        }
    }

    private static void executarServidor() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (FileInputStream keyStoreStream = new FileInputStream("keystore.jks")) {
            keyStore.load(keyStoreStream, "password".toCharArray());
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, "password".toCharArray());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        try (SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(8443)) {
            System.out.println("Servidor SSL/TLS iniciado...");

            while (true) {
                try (SSLSocket socket = (SSLSocket) serverSocket.accept()) {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    writer.write("Conexão segura estabelecida!");
                    writer.flush();
                }
            }
        }
    }

    private static void executarCliente() throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (FileInputStream trustStoreStream = new FileInputStream("truststore.jks")) {
            trustStore.load(trustStoreStream, "password".toCharArray());
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(trustStore);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        try (SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket("localhost", 8443)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            String resposta = reader.readLine();
            System.out.println("Mensagem do servidor: " + resposta);
        }
    }
}
