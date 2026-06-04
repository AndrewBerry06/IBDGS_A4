import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/** Integration test: valid bus records are stored and retrieved using the real data JSON file. */
class BusValidStorageIntegrationTests {
    private static final Path BUS_FILE = Paths.get("data", "buses.json");

    @BeforeEach
    void resetBusFile() throws Exception {
        Files.createDirectories(BUS_FILE.getParent());
        Files.writeString(BUS_FILE, "[]");
    }

    @Test
    void testValidBusIsStoredAndRetrieved() throws Exception {
        BusRepository repository = new BusRepository(BUS_FILE.toString());
        Bus bus = new Bus("12345678", 45, 80.0, "Diesel");

        repository.add(bus);
        Bus savedBus = repository.retrieve("12345678");

        assertNotNull(savedBus, "Saved bus should be retrievable from the JSON file.");
        assertEquals("12345678", savedBus.getBusID());
        assertEquals(45, savedBus.getCapacity());
        assertEquals(80.0, savedBus.getFuelLevel());
        assertEquals("Diesel", savedBus.getFuelType());
        assertTrue(Files.readString(BUS_FILE).contains("12345678"),
                "The bus ID should appear in data/buses.json.");
    }
}
