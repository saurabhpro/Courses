package week4.miniproject_4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServer {

    private static final String GET_METHOD = "GET";
    private static final String SPACE_SEPARATOR = " ";

    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs     A proxy filesystem to serve files from. See the PCDPFilesystem
     *               class for more detailed documentation of its usage.
     * @param ncores The number of cores that are available to your
     *               multi-threaded file server. Using this argument is entirely
     *               optional. You are free to use this information to change
     *               how you create your threads, or ignore it.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs,
                    final int ncores) throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        final Executor executor = Executors.newFixedThreadPool(ncores);

        while (true) {

            // TODO 1) Use socket.accept to get a Socket object
            final var request = socket.accept();

            final Runnable worker = () -> {
                try {
                    final var firstLine = extractRequestFirstLine(request);

                    if (isGetRequest(firstLine)) {
                        final var filePath = extractRequestFilePath(firstLine);

                        final var fileContent = readFileContent(fs, filePath);
                        if (fileContent.isPresent()) {
                            printSuccessResponse(request, fileContent.get());
                        } else {
                            printNotFoundResponse(request);
                        }
                    }
                } catch (IOException io) {
                    throw new RuntimeException(io);
                }
            };

            executor.execute(worker);
        }
    }

    private static Optional<String> readFileContent(PCDPFilesystem fs, String filePath) {
        final var fileContent = fs.readFile(new PCDPPath(filePath));
        return Optional.ofNullable(fileContent);
    }

    private static boolean isGetRequest(String firstLine) {
        return firstLine.startsWith(GET_METHOD);
    }

    private static String extractRequestFirstLine(Socket request) throws IOException {
        final var br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        final var firstLine = br.readLine();
        assert firstLine != null;
        return firstLine;
    }

    private static String extractRequestFilePath(String firstLine) {
        final var firstLineParts = firstLine.split(SPACE_SEPARATOR);
        assert firstLineParts.length > 1;
        return firstLineParts[1];
    }

    private static void printSuccessResponse(Socket request, String fileContent)
        throws IOException {

        final var response = "HTTP/1.0 200 OK\r\n" +
            "Server: FileServer\r\n" +
            "\r\n" +
            fileContent +
            "\r\n";

        writeResponse(request, response);
    }

    private static void printNotFoundResponse(Socket request)
        throws IOException {

        final var response = "HTTP/1.0 404 Not Found\r\n" +
            "Server: FileServer\r\n" +
            "\r\n";

        writeResponse(request, response);
    }

    private static void writeResponse(Socket socket, String response) throws IOException {
        try (final var out = socket.getOutputStream();
             final var ps = new PrintStream(out)) {

            ps.println(response);
        }
    }
}
