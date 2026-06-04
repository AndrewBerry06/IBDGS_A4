import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Integration test: valid bus records are stored and retrieved using a real JSON file. */
class BusValidStorageIntegrationTests {
    @TempDir Path tempDir;

    @Test
    void testValidBusIsStoredAndRetrieved() throws Exception {
        Path file = tempDir.resolve("buses-integration.json");
        BusRepository repository = new BusRepository(file.toString());
        Bus bus = new Bus("12345678", 45, 80.0, "Diesel");

        repository.add(bus);
        Bus savedBus = repository.retrieve("12345678");

        assertNotNull(savedBus, "Saved bus should be retrievable from the real JSON file.");
        assertEquals("12345678", savedBus.getBusID());
        assertEquals(45, savedBus.getCapacity());
        assertEquals(80.0, savedBus.getFuelLevel());
        assertEquals("Diesel", savedBus.getFuelType());
        assertTrue(Files.readString(file).contains("12345678"),
                "The bus ID should appear in the human-readable JSON file.");
    }
}
