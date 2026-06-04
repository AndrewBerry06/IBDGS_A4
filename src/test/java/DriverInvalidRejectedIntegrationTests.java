import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/** Integration test: invalid driver data is rejected and not written to the real data JSON file. */
class DriverInvalidRejectedIntegrationTests {
    private static final Path DRIVER_FILE = Paths.get("data", "drivers.json");

    @BeforeEach
    void resetDriverFile() throws Exception {
        Files.createDirectories(DRIVER_FILE.getParent());
        Files.writeString(DRIVER_FILE, "[]");
    }

    @Test
    void testInvalidDriverIsRejectedAndNotStored() throws Exception {
        DriverRepository repository = new DriverRepository(DRIVER_FILE.toString());

        assertThrows(IncorrectValueForField.class, () -> new Driver("35@#abAB", "Invalid Driver", 3,
                "Heavy", "12|Main St|Melbourne|VIC|Australia", "15-06-1990"));

        assertEquals(0, repository.count(), "Count should stay zero when invalid driver data is rejected.");
        assertEquals("[]", Files.readString(DRIVER_FILE).trim(), "data/drivers.json should remain empty.");
    }
}
