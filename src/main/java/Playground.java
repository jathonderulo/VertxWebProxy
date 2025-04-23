import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Playground {
    private static final Logger LOG = LoggerFactory.getLogger(Playground.class);

    public static void main(String[] args) {
        LOG.info("Application started.");
        String filePath = "logs.txt";

        try {
            // Simulate some operation
            LOG.debug("Attempting to write to file: {}", filePath);
            // Example operation
            throw new IOException("Simulated exception");
        } catch (IOException e) {
            LOG.error("An error occurred while writing to the file.", e);
        }
    }
}