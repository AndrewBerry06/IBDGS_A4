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
 *
 * Manages persistent storage of Driver records JSON file.
 * Supports the following operations:
 *   Add      - Add a new driver (enforces unique driverID)
 *   Retrieve - Retrieve a driver by driverID
 *   Update   - Update mutable fields of an existing driver (enforces D4, D5)
 *   Count    - Return the total number of stored drivers
 *
 * JSON file structure:
 * [
 *   {
 *     "driverID":        "35@#abcdAB",
 *     "name":            "Alice Smith",
 *     "experienceYears": 5,
 *     "licenseType":     "Heavy",
 *     "address":         "12|Main St|Melbourne|VIC|Australia",
 *     "birthdate":       "15-06-1990"
 *   }
 * ]
 */
public class DriverRepository {

    // .................Fields..........................

    private final Path filePath;   // Path to the JSON storage file


    // ...................Constructor........................

    /**
     * Creates a DriverRepository backed by the given JSON file.
     * If the file does not yet exist it is created with an empty array.
     *
     * @param filePath path to the JSON storage file
     * @throws IOException if the file cannot be created or read
     */
    public DriverRepository(String filePath) throws IOException {
        this.filePath = Paths.get(filePath);
        initialiseFile();
    }


    // .................CoreFunctions....................


    /**
     * Adds a new driver to the repository.
     * Enforces unique driverID — duplicate IDs are rejected.
     *
     * @param driver the Driver to add
     * @throws IncorrectValueForField if the driverID already exists
     * @throws IOException            if the file cannot be read or written
     */
    public void add(Driver driver) throws IOException {
        JSONArray drivers = readAll();

        // Reject duplicate driverIDs
        if (findByID(drivers, driver.getDriverID()) != null) {
            throw new IncorrectValueForField(
                "D1 Violation: A driver with ID '" + driver.getDriverID() + "' already exists.");
        }

        drivers.put(toJSON(driver));
        writeAll(drivers);
    }

    /**
     * Retrieves a driver by their driverID.
     *
     * @param driverID the ID to search for
     * @return the matching Driver, or null if not found
     * @throws IOException if the file cannot be read
     */
    public Driver retrieve(String driverID) throws IOException {
        JSONArray drivers = readAll();
        JSONObject obj = findByID(drivers, driverID);
        return (obj == null) ? null : fromJSON(obj);
    }

    /**
     * Retrieves all drivers as a list.
     *
     * @return list of all stored Driver objects
     * @throws IOException if the file cannot be read
     */
    public List<Driver> retrieveAll() throws IOException {
        JSONArray drivers = readAll();
        List<Driver> list = new ArrayList<>();
        for (int i = 0; i < drivers.length(); i++) {
            list.add(fromJSON(drivers.getJSONObject(i)));
        }
        return list;
    }

    /**
     * Updates mutable fields of an existing driver.
     *
     * D5 — driverID and name cannot be changed; they are ignored even if supplied.
     * D4 — licenseType cannot be changed if experienceYears > 10.
     *
     * Pass -1 for experienceYears or null for any String field to leave it unchanged.
     *
     * @param driverID        ID of the driver to update
     * @param experienceYears new experience value, or -1 to leave unchanged
     * @param licenseType     new licence type, or null to leave unchanged
     * @param address         new address, or null to leave unchanged
     * @param birthdate       new birthdate, or null to leave unchanged
     * @throws IncorrectValueForField if no driver with driverID exists, or a value fails validation
     * @throws IOException            if the file cannot be read or written
     */
    public void update(String driverID,
                       int    experienceYears,
                       String licenseType,
                       String address,
                       String birthdate) throws IOException {

        JSONArray drivers = readAll();
        JSONObject obj = findByID(drivers, driverID);

        // Reject update if driver does not exist
        if (obj == null) {
            throw new IncorrectValueForField(
                "No driver found with ID: " + driverID);
        }

        // Reconstruct Driver so setter validation (including D4) is applied automatically
        Driver driver = fromJSON(obj);

        // Apply each change only when a new value is provided
        if (experienceYears >= 0)  driver.setExperienceYears(experienceYears);
        if (licenseType    != null) driver.setLicenseType(licenseType);   // D4 enforced here
        if (address        != null) driver.setAddress(address);
        if (birthdate      != null) driver.setBirthdate(birthdate);

        // Overwrite the entry in the array and persist
        replaceInArray(drivers, driver);
        writeAll(drivers);
    }

    /**
     * Returns the total number of drivers stored in the repository.
     *
     * @return count of drivers
     * @throws IOException if the file cannot be read
     */
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
     *
     * @return JSONArray of all stored driver objects
     */
    private JSONArray readAll() throws IOException {
        return new JSONArray(Files.readString(filePath));
    }

    /**
     * Writes the full JSONArray back to the file.
     * Uses a 2-space indent to keep the file human-readable.
     */
    private void writeAll(JSONArray drivers) throws IOException {
        Files.writeString(filePath, drivers.toString(2));
    }


    // .........JSONConversion...........


    /**
     * Serialises a Driver object to a JSONObject.
     *
     * @param driver the Driver to serialise
     * @return corresponding JSONObject
     */
    private JSONObject toJSON(Driver driver) {
        JSONObject obj = new JSONObject();
        obj.put("driverID",        driver.getDriverID());
        obj.put("name",            driver.getName());
        obj.put("experienceYears", driver.getExperienceYears());
        obj.put("licenseType",     driver.getLicenseType());
        obj.put("address",         driver.getAddress());
        obj.put("birthdate",       driver.getBirthdate());
        return obj;
    }

    /**
     * Deserialises a JSONObject into a Driver object.
     *
     * @param obj the JSONObject to deserialise
     * @return corresponding Driver
     */
    private Driver fromJSON(JSONObject obj) {
        return new Driver(
            obj.getString("driverID"),
            obj.getString("name"),
            obj.getInt("experienceYears"),
            obj.getString("licenseType"),
            obj.getString("address"),
            obj.getString("birthdate")
        );
    }


    // .........ArrayHelpers...........


    /**
     * Searches the array for a driver with the given driverID.
     *
     * @param drivers  the array to search
     * @param driverID the ID to find
     * @return the matching JSONObject, or null if not found
     */
    private JSONObject findByID(JSONArray drivers, String driverID) {
        for (int i = 0; i < drivers.length(); i++) {
            JSONObject obj = drivers.getJSONObject(i);
            if (obj.getString("driverID").equals(driverID)) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Replaces the entry in the array whose driverID matches the given driver.
     *
     * @param drivers the array to update
     * @param driver  the Driver with updated values
     */
    private void replaceInArray(JSONArray drivers, Driver driver) {
        for (int i = 0; i < drivers.length(); i++) {
            if (drivers.getJSONObject(i).getString("driverID").equals(driver.getDriverID())) {
                drivers.put(i, toJSON(driver));
                return;
            }
        }
    }

}
