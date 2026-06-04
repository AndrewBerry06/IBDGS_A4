import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for the Intelligent Bus Driver Guidance System.
 * Manages persistent storage of Bus records in a JSON file.
 * Supports the following operations:
 *   Add      - Add a new bus (enforces unique busID — B1)
 *   Retrieve - Retrieve a bus by busID
 *   Update   - Update mutable fields of an existing bus (enforces B2)
 *   Count    - Return the total number of stored buses
 * JSON file structure:
 * [
 *   {
 *     "busID":     "12345678",
 *     "capacity":  40,
 *     "fuelLevel": 75.0,
 *     "fuelType":  "Diesel"
 *   }
 * ]
 */

public class BusRepository {
    // .................Fields..........................
    private final Path filePath; // Path to the JSON storage file

    // ...................Constructor........................

    /**
     * Creates a BusRepository backed by the given JSON file.
     * If the file does not yet exist it is created with an empty array.
     * @param filePath path to the JSON storage file
     * @throws IOException if the file cannot be created or read
     */

    public BusRepository(String filePath) throws IOException {
        this.filePath = Paths.get(filePath);
        initialiseFile();
    }
    // .................CoreFunctions....................

    /**
     * Adds a new bus to the repository.
     * Enforces unique busID — duplicate IDs are rejected (B1).
     *
     * @param bus the Bus to add
     * @throws IncorrectValueForField if the busID already exists
     * @throws IOException            if the file cannot be read or written
     */

    public void add(Bus bus) throws IOException {
        JSONArray buses = readAll();

        // B1: Reject duplicate busIDs
        if (findByID(buses, bus.getBusID()) != null) {
            throw new IncorrectValueForField("B1 Violation: A bus with ID '" + bus.getBusID() + "' already exists.");
        }

        buses.put(toJSON(bus));
        writeAll(buses);
    }

    /**
     * Retrieves a bus by its busID.
     *
     * @param busID the ID to search for
     * @return the matching Bus, or null if not found
     * @throws IOException if the file cannot be read
     */

    public Bus retrieve(String busID) throws IOException {
        JSONArray buses = readAll();
        JSONObject obj = findByID(buses, busID);
        return (obj == null) ? null : fromJSON(obj);
    }

    /**
     * Retrieves all buses as a list.
     *
     * @return list of all stored Bus objects
     * @throws IOException if the file cannot be read
     */

    public List<Bus> retrieveAll() throws IOException {
        JSONArray buses = readAll();
        List<Bus> list = new ArrayList<>();
        for (int i = 0; i < buses.length(); i++) {
            list.add(fromJSON(buses.getJSONObject(i)));
        }
        return list;
    }

    /**
     * Updates mutable fields of an existing bus.
     * busID and fuelType cannot be changed after creation.
     * B2 — capacity can only decrease; enforced automatically via setCapacity().
     * Pass -1 for capacity or fuelLevel to leave that field unchanged.
     * @param busID     ID of the bus to update
     * @param capacity  new capacity value, or -1 to leave unchanged
     * @param fuelLevel new fuel level value, or -1 to leave unchanged
     * @throws IncorrectValueForField if no bus with busID exists, or B2 is violated
     * @throws IOException            if the file cannot be read or written
     */

    public void update(String busID, int capacity, double fuelLevel) throws IOException {
        JSONArray buses = readAll();
        JSONObject obj = findByID(buses, busID);
        // Reject update if bus does not exist
        if (obj == null) {
            throw new IncorrectValueForField("No bus found with ID: " + busID);
        }
        // Reconstruct Bus so setter validation (including B2) is applied automatically
        Bus bus = fromJSON(obj);
        // Apply each change only when a new value is provided
        if (capacity  >= 0) bus.setCapacity(capacity);    // B2 enforced inside setCapacity
        if (fuelLevel >= 0) bus.setFuelLevel(fuelLevel);
        // Overwrite the entry in the array and persist
        replaceInArray(buses, bus);
        writeAll(buses);

    }

    /**
     * Returns the total number of buses stored in the repository.
     * @return count of buses
     * @throws IOException if the file cannot be read
     */
    //Use for intergration testing
   public int count() throws IOException {
         return readAll().length();
   }

    // .........FileHelpers...........

    /**
     * Creates the JSON file with an empty array if it does not already exist.
     */
    private void initialiseFile() throws IOException {
        if (!Files.exists(filePath)) {
            Path parent = filePath.getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.writeString(filePath, "[]");
        }
    }

    /**
     * Reads and parses the full JSON array from the storage file.
     * @return JSONArray of all stored bus objects
     */
    private JSONArray readAll() throws IOException {
        return new JSONArray(Files.readString(filePath));
    }

    /**
     * Writes the full JSONArray back to the file.
     * Uses 2-space indent to keep the file human-readable.
     */

    private void writeAll(JSONArray buses) throws IOException {
        Files.writeString(filePath, buses.toString(2));
    }

    // .........JSONConversion...........

    /**
     * Serialises a Bus object to a JSONObject.
     *
     * @param bus the Bus to serialise
     * @return corresponding JSONObject
     */

    private JSONObject toJSON(Bus bus) {
        JSONObject obj = new JSONObject();
        obj.put("busID",     bus.getBusID());
        obj.put("capacity",  bus.getCapacity());
        obj.put("fuelLevel", bus.getFuelLevel());
        obj.put("fuelType",  bus.getFuelType());
        return obj;
    }

    /**
     * @return corresponding Bus
     */
    private Bus fromJSON(JSONObject obj) {
        return new Bus(
                obj.getString("busID"),
                obj.getInt("capacity"),
                obj.getDouble("fuelLevel"),
                obj.getString("fuelType")
        );
    }

    // .........ArrayHelpers...........

    /**
     * Searches the array for a bus with the given busID.
     *
     * @param buses the array to search
     * @param busID the ID to find
     * @return the matching JSONObject, or null if not found
     */

    private JSONObject findByID(JSONArray buses, String busID) {
        for (int i = 0; i < buses.length(); i++) {
            JSONObject obj = buses.getJSONObject(i);
            if (obj.getString("busID").equals(busID)) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Replaces the entry in the array whose busID matches the given bus.
     *
     * @param buses the array to update
     * @param bus   the Bus with updated values
     */

    private void replaceInArray(JSONArray buses, Bus bus) {
        for (int i = 0; i < buses.length(); i++) {
            if (buses.getJSONObject(i).getString("busID").equals(bus.getBusID())) {
                buses.put(i, toJSON(bus));
                return;
            }
        }
    }
}
