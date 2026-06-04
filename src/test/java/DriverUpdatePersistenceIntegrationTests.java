import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Integration test: driver updates are persisted and visible through a new repository instance. */
class DriverUpdatePersistenceIntegrationTests {
    @TempDir Path tempDir;

    @Test
    void testDriverUpdateIsPersisted() throws Exception {
        Path file = tempDir.resolve("drivers-integration.json");
        DriverRepository repository = new DriverRepository(file.toString());
        repository.add(new Driver("35@#abcdAB", "Alice Smith", 5, "Heavy",
                "12|Main St|Melbourne|VIC|Australia", "15-06-1990"));

        repository.update("35@#abcdAB", 8, "PublicTransport",
                "99|Oak Ave|Sydney|NSW|Australia", "01-01-1985");

        DriverRepository reopenedRepository = new DriverRepository(file.toString());
        Driver updatedDriver = reopenedRepository.retrieve("35@#abcdAB");

        assertNotNull(updatedDriver);
        assertEquals(8, updatedDriver.getExperienceYears());
        assertEquals("PublicTransport", updatedDriver.getLicenseType());
        assertEquals("99|Oak Ave|Sydney|NSW|Australia", updatedDriver.getAddress());
        assertEquals("01-01-1985", updatedDriver.getBirthdate());
        assertEquals("Alice Smith", updatedDriver.getName(), "Immutable name should remain unchanged.");
    }
}
