import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Integration test: invalid driver data is rejected and not written to the JSON file. */
class DriverInvalidRejectedIntegrationTests {
    @TempDir Path tempDir;

    @Test
    void testInvalidDriverIsRejectedAndNotStored() throws Exception {
        Path file = tempDir.resolve("drivers-integration.json");
        DriverRepository repository = new DriverRepository(file.toString());

        assertThrows(IncorrectValueForField.class, () -> new Driver("35@#abAB", "Invalid Driver", 3,
                "Heavy", "12|Main St|Melbourne|VIC|Australia", "15-06-1990"));

        assertEquals(0, repository.count(), "Count should stay zero when invalid driver data is rejected.");
        assertEquals("[]", Files.readString(file).trim(), "The real JSON file should remain empty.");
    }
}
