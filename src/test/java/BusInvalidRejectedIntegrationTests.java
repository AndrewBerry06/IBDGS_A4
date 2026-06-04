import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/** Integration test: invalid bus data is rejected and not written to the real data JSON file. */
class BusInvalidRejectedIntegrationTests {
    private static final Path BUS_FILE = Paths.get("data", "buses.json");

    @BeforeEach
    void resetBusFile() throws Exception {
        Files.createDirectories(BUS_FILE.getParent());
        Files.writeString(BUS_FILE, "[]");
    }

    @Test
    void testInvalidBusIsRejectedAndNotStored() throws Exception {
        BusRepository repository = new BusRepository(BUS_FILE.toString());

        assertThrows(IncorrectValueForField.class, () -> new Bus("1234ABCD", 40, 70.0, "Diesel"));

        assertEquals(0, repository.count(), "Count should stay zero when invalid bus data is rejected.");
        assertEquals("[]", Files.readString(BUS_FILE).trim(), "data/buses.json should remain empty.");
    }
}
