public class MainApp {
    public static void main(String[] args) {
        SecureServer server = new SecureServer();
        SecureClient client = new SecureClient();
        
        
        new Thread(() -> {
            try {
                System.out.println("Iniciando o servidor...");
                server.startServer();
            } catch (SSLSecurityException e) {
                System.err.println("Erro no servidor SSL: " + e.getMessage());
            }
        }).start();

        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        
        new Thread(() -> {
            try {
                System.out.println("Iniciando o cliente...");
                client.startClient();
            } catch (SSLSecurityException e) {
                System.err.println("Erro no cliente SSL: " + e.getMessage());
            }
        }).start();
    }
}
