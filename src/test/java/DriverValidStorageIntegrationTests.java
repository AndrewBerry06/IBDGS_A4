import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Integration test: valid driver records are stored and retrieved using a real JSON file. */
class DriverValidStorageIntegrationTests {
    @TempDir Path tempDir;

    @Test
    void testValidDriverIsStoredAndRetrieved() throws Exception {
        Path file = tempDir.resolve("drivers-integration.json");
        DriverRepository repository = new DriverRepository(file.toString());
        Driver driver = new Driver("35@#abcdAB", "Alice Smith", 5, "Heavy",
                "12|Main St|Melbourne|VIC|Australia", "15-06-1990");

        repository.add(driver);
        Driver savedDriver = repository.retrieve("35@#abcdAB");

        assertNotNull(savedDriver, "Saved driver should be retrievable from the real JSON file.");
        assertEquals("Alice Smith", savedDriver.getName());
        assertEquals("35@#abcdAB", savedDriver.getDriverID());
        assertTrue(Files.readString(file).contains("35@#abcdAB"),
                "The driver ID should appear in the human-readable JSON file.");
    }
}
