import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Integration test: invalid bus data is rejected and not written to the JSON file. */
class BusInvalidRejectedIntegrationTests {
    @TempDir Path tempDir;

    @Test
    void testInvalidBusIsRejectedAndNotStored() throws Exception {
        Path file = tempDir.resolve("buses-integration.json");
        BusRepository repository = new BusRepository(file.toString());

        assertThrows(IncorrectValueForField.class, () -> new Bus("1234ABCD", 40, 70.0, "Diesel"));

        assertEquals(0, repository.count(), "Count should stay zero when invalid bus data is rejected.");
        assertEquals("[]", Files.readString(file).trim(), "The real JSON file should remain empty.");
    }
}
