import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Integration test: bus updates are persisted and visible through a new repository instance. */
class BusUpdatePersistenceIntegrationTests {
    @TempDir Path tempDir;

    @Test
    void testBusUpdateIsPersisted() throws Exception {
        Path file = tempDir.resolve("buses-integration.json");
        BusRepository repository = new BusRepository(file.toString());
        repository.add(new Bus("12345678", 50, 90.0, "Hybrid"));

        repository.update("12345678", 42, 65.5);

        BusRepository reopenedRepository = new BusRepository(file.toString());
        Bus updatedBus = reopenedRepository.retrieve("12345678");

        assertNotNull(updatedBus);
        assertEquals(42, updatedBus.getCapacity(), "The decreased capacity should be persisted.");
        assertEquals(65.5, updatedBus.getFuelLevel());
        assertEquals("Hybrid", updatedBus.getFuelType(), "Fuel type should remain unchanged.");
    }
}
