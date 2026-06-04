import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/** Integration test: driver count updates correctly and duplicate IDs do not increase the count. */
class DriverCountIntegrationTests {
    private static final Path DRIVER_FILE = Paths.get("data", "drivers.json");

    @BeforeEach
    void resetDriverFile() throws Exception {
        Files.createDirectories(DRIVER_FILE.getParent());
        Files.writeString(DRIVER_FILE, "[]");
    }

    @Test
    void testDriverCountUpdatesCorrectly() throws Exception {
        DriverRepository repository = new DriverRepository(DRIVER_FILE.toString());

        repository.add(new Driver("35@#abcdAB", "Alice Smith", 5, "Heavy",
                "12|Main St|Melbourne|VIC|Australia", "15-06-1990"));
        repository.add(new Driver("46$%efghCD", "Ben Carter", 4, "Medium",
                "21|King St|Melbourne|VIC|Australia", "20-03-1992"));

        assertEquals(2, repository.count(), "Two valid drivers should be counted.");

        Driver duplicateID = new Driver("35@#abcdAB", "Different Name", 3, "Heavy",
                "55|Queen St|Melbourne|VIC|Australia", "10-10-1995");
        assertThrows(IncorrectValueForField.class, () -> repository.add(duplicateID));
        assertEquals(2, repository.count(), "Count should not increase after a duplicate driverID is rejected.");
        assertTrue(Files.readString(DRIVER_FILE).contains("35@#abcdAB"));
    }
}
