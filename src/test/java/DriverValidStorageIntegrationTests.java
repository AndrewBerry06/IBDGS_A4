import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/** Integration test: valid driver records are stored and retrieved using the real data JSON file. */
class DriverValidStorageIntegrationTests {
    private static final Path DRIVER_FILE = Paths.get("data", "drivers.json");

    @BeforeEach
    void resetDriverFile() throws Exception {
        Files.createDirectories(DRIVER_FILE.getParent());
        Files.writeString(DRIVER_FILE, "[]");
    }

    @Test
    void testValidDriverIsStoredAndRetrieved() throws Exception {
        DriverRepository repository = new DriverRepository(DRIVER_FILE.toString());
        Driver driver = new Driver("35@#abcdAB", "Alice Smith", 5, "Heavy",
                "12|Main St|Melbourne|VIC|Australia", "15-06-1990");

        repository.add(driver);
        Driver savedDriver = repository.retrieve("35@#abcdAB");

        assertNotNull(savedDriver, "Saved driver should be retrievable from the JSON file.");
        assertEquals("Alice Smith", savedDriver.getName());
        assertEquals("35@#abcdAB", savedDriver.getDriverID());
        assertTrue(Files.readString(DRIVER_FILE).contains("35@#abcdAB"),
                "The driver ID should appear in data/drivers.json.");
    }
}
