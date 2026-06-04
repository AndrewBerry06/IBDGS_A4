import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Integration test: bus count updates correctly and duplicate IDs do not increase the count. */
class BusCountIntegrationTests {
    @TempDir Path tempDir;

    @Test
    void testBusCountUpdatesCorrectly() throws Exception {
        Path file = tempDir.resolve("buses-integration.json");
        BusRepository repository = new BusRepository(file.toString());

        repository.add(new Bus("12345678", 45, 80.0, "Diesel"));
        repository.add(new Bus("87654321", 35, 55.0, "Electricity"));

        assertEquals(2, repository.count(), "Two valid buses should be counted.");

        Bus duplicateID = new Bus("12345678", 30, 60.0, "Diesel");
        assertThrows(IncorrectValueForField.class, () -> repository.add(duplicateID));
        assertEquals(2, repository.count(), "Count should not increase after a duplicate busID is rejected.");
    }
}
