package week4.miniproject_4;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.URL;
import java.nio.channels.ClosedByInterruptException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FileServerTest {
    private static final String rootDirName = "static";
    private static final File rootDir = new File(rootDirName);
    private static final Random rand = new Random();
    private static final Map<String, String> files = new HashMap<>();

    static {
        files.put("/static/A.txt", getRandomFileContents(5));
        files.put("/static/B.txt", getRandomFileContents(10));
        files.put("/static/dir1/C.txt", getRandomFileContents(10));
        files.put("/static/dir3/dir4/E.txt", getRandomFileContents(10));
        files.put("/static/ABC.txt", getRandomFileContents(2048));
    }

    private int port;

    private static int getNCores() {
        String ncoresStr = System.getenv("COURSERA_GRADER_NCORES");
        if (ncoresStr == null) {
            ncoresStr = System.getProperty("COURSERA_GRADER_NCORES");
        }

        if (ncoresStr == null) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return Integer.parseInt(ncoresStr);
        }
    }

    private static String getRandomFileContents(final int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }

    private static void deleteRecursively(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : Objects.requireNonNull(f.listFiles())) {
                deleteRecursively(c);
            }
        }

        if (f.exists()) {
            if (!f.delete()) {
                throw new FileNotFoundException("Failed to delete file: " + f);
            }
        }
    }

    private PCDPFilesystem getFilesystem() {
        PCDPFilesystem fs = new PCDPFilesystem();

        for (Map.Entry<String, String> entry : files.entrySet()) {
            PCDPPath path = new PCDPPath(entry.getKey());
            System.out.println(entry);
            fs.addFile(path, entry.getValue());
        }

        return fs;
    }

    private HttpServer launchServer() throws IOException {
        System.err.println("\nLaunching server for " +
                Thread.currentThread().getStackTrace()[2].getMethodName());
        port = ThreadLocalRandom.current().nextInt(3000, 9000);

        final ServerSocket socket = new ServerSocket(port);
        socket.setReuseAddress(true);
        final PCDPFilesystem fs = getFilesystem();

        Runnable runner = () -> {
            try {
                FileServer server = new FileServer();
                server.run(socket, fs, getNCores());
            } catch (SocketException | ClosedByInterruptException s) {
                // Do nothing, assume killed by main thread
            } catch (IOException io) {
                throw new RuntimeException(io);
            }
        };

        Thread thread = new Thread(runner);

        thread.start();

        return new HttpServer(thread, socket);
    }

    private HttpResponse sendHttpRequest(final String path, final boolean print)
            throws IOException {
        assert !path.startsWith("/");

        if (print) {
            System.err.print("Requesting " + path + "... ");
        }

        URL obj = new URL("http://localhost:" + port + "/" + path);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000); // 5 seconds
        con.setReadTimeout(5000); // 5 seconds

        final int responseCode = con.getResponseCode();

        final String responseStr;
        if (responseCode != 404) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            responseStr = response.toString();
        } else {
            responseStr = "";
        }

        if (print) {
            System.err.println("response code is " + responseCode +
                    ", with content length " + responseStr.length());
        }

        return new HttpResponse(responseCode, responseStr);
    }

    @Test
    public void testTermination() throws IOException, InterruptedException {
        final HttpServer server = launchServer();

        // Termination
        server.socket.close();
        server.thread.interrupt();
        server.thread.join();
    }

    @Test
    public void testFileGet() throws IOException, InterruptedException {
        final HttpServer server = launchServer();

        HttpResponse response = sendHttpRequest(rootDirName + "/A.txt", true);
        assertEquals(200, response.code);
        assertEquals(files.get("/static/A.txt"), response.body);

        // Termination
        server.socket.close();
        server.thread.interrupt();
        server.thread.join();
    }

    @Test
    public void testFileGets() throws IOException, InterruptedException {
        final HttpServer server = launchServer();

        HttpResponse response = sendHttpRequest(rootDirName + "/A.txt", true);
        assertEquals(200, response.code);
        assertEquals(files.get("/static/A.txt"), response.body);

        response = sendHttpRequest(rootDirName + "/B.txt", true);
        assertEquals(200, response.code);
        assertEquals(files.get("/static/B.txt"), response.body);

        // Termination
        server.socket.close();
        server.thread.interrupt();
        server.thread.join();
    }

    @Test
    public void testNestedFileGet() throws IOException, InterruptedException {
        final HttpServer server = launchServer();

        HttpResponse response = sendHttpRequest(rootDirName + "/dir1/C.txt", true);
        assertEquals(200, response.code);
        assertEquals(files.get("/static/dir1/C.txt"), response.body);

        // Termination
        server.socket.close();
        server.thread.interrupt();
        server.thread.join();
    }

    @Test
    public void testDoublyNestedFileGet() throws IOException, InterruptedException {
        final HttpServer server = launchServer();

        HttpResponse response = sendHttpRequest(rootDirName + "/dir3/dir4/E.txt", true);
        assertEquals(200, response.code);
        assertEquals(files.get("/static/dir3/dir4/E.txt"), response.body);

        // Termination
        server.socket.close();
        server.thread.interrupt();
        server.thread.join();
    }

    @Test
    public void testLargeFileGet() throws IOException, InterruptedException {
        final HttpServer server = launchServer();

        HttpResponse response = sendHttpRequest(rootDirName + "/ABC.txt", true);
        assertEquals(200, response.code);
        assertEquals(files.get("/static/ABC.txt"), response.body);

        // Termination
        server.socket.close();
        server.thread.interrupt();
        server.thread.join();
    }

    @Test
    public void testMissingFileGet() throws IOException, InterruptedException {
        final HttpServer server = launchServer();

        HttpResponse response = sendHttpRequest(rootDirName + "/missing.txt", true);
        assertEquals(404, response.code);

        // Termination
        server.socket.close();
        server.thread.interrupt();
        server.thread.join();
    }

    @Test
    public void testMissingNestedFileGet() throws IOException, InterruptedException {
        final HttpServer server = launchServer();

        HttpResponse response = sendHttpRequest(rootDirName + "/missingdir/missing.txt", true);
        assertEquals(404, response.code);

        // Termination
        server.socket.close();
        server.thread.interrupt();
        server.thread.join();
    }

    static class HttpResponse {
        public final int code;
        public final String body;

        public HttpResponse(final int setCode, final String setBody) {
            code = setCode;
            body = setBody;
        }
    }

    static class HttpServer {
        public final Thread thread;
        public final ServerSocket socket;

        HttpServer(final Thread setThread, final ServerSocket setSocket) {
            thread = setThread;
            socket = setSocket;
        }
    }
}
