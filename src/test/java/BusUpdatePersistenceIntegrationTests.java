import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/** Integration test: bus updates are persisted and visible through a new repository instance. */
class BusUpdatePersistenceIntegrationTests {
    private static final Path BUS_FILE = Paths.get("data", "buses.json");

    @BeforeEach
    void resetBusFile() throws Exception {
        Files.createDirectories(BUS_FILE.getParent());
        Files.writeString(BUS_FILE, "[]");
    }

    @Test
    void testBusUpdateIsPersisted() throws Exception {
        BusRepository repository = new BusRepository(BUS_FILE.toString());
        repository.add(new Bus("12345678", 50, 90.0, "Hybrid"));

        repository.update("12345678", 42, 65.5);

        BusRepository reopenedRepository = new BusRepository(BUS_FILE.toString());
        Bus updatedBus = reopenedRepository.retrieve("12345678");

        assertNotNull(updatedBus);
        assertEquals(42, updatedBus.getCapacity(), "The decreased capacity should be persisted.");
        assertEquals(65.5, updatedBus.getFuelLevel());
        assertEquals("Hybrid", updatedBus.getFuelType(), "Fuel type should remain unchanged.");
    }
}
