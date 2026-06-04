import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/** Integration test: bus count updates correctly and duplicate IDs do not increase the count. */
class BusCountIntegrationTests {
    private static final Path BUS_FILE = Paths.get("data", "buses.json");

    @BeforeEach
    void resetBusFile() throws Exception {
        Files.createDirectories(BUS_FILE.getParent());
        Files.writeString(BUS_FILE, "[]");
    }

    @Test
    void testBusCountUpdatesCorrectly() throws Exception {
        BusRepository repository = new BusRepository(BUS_FILE.toString());

        repository.add(new Bus("12345678", 45, 80.0, "Diesel"));
        repository.add(new Bus("87654321", 35, 55.0, "Electricity"));

        assertEquals(2, repository.count(), "Two valid buses should be counted.");

        Bus duplicateID = new Bus("12345678", 30, 60.0, "Diesel");
        assertThrows(IncorrectValueForField.class, () -> repository.add(duplicateID));
        assertEquals(2, repository.count(), "Count should not increase after a duplicate busID is rejected.");
        assertTrue(Files.readString(BUS_FILE).contains("12345678"));
    }
}
