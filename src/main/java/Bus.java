import org.json.JSONObject;

/**
 * Bus class for the Intelligent Bus Driver Guidance System.
 * Enforces the following conditions:
 *   B1 - busID must be exactly 8 digits and unique (uniqueness enforced in BusRepository)
 *   B2 - capacity can only decrease during updates (enforced in setCapacity)
 *   B3 - drivers older than 50 cannot drive buses with capacity >= 50 (static validator)
 *   B4 - electric buses require >= 5 years driver experience (static validator)
 *   B5 - electric/hybrid buses require Heavy or PublicTransport licence (static validator)
 */

public class Bus {
    // .................Fields..........................
    private final String busID;
    private int capacity;
    private double fuelLevel;
    private String fuelType; // Diesel, Hybrid, Electricity


    // ...................Constructor........................

    /**
     * Creates a new Bus after validating all fields.
     *
     * @param busID     Exactly 8 digit characters (B1)
     * @param capacity  Seating capacity (must be > 0)
     * @param fuelLevel Current fuel level (0.0 to 100.0)
     * @param fuelType  One of: Diesel, Hybrid, Electricity
     * @throws IncorrectValueForField if any field fails its validation rule
     */
    public Bus(String busID, int capacity, double fuelLevel, String fuelType) {
        // B1: Validate busID format (uniqueness is checked in BusRepository)
        validateBusID(busID);

        // Validate capacity is positive
        if (capacity <= 0) {
            throw new IncorrectValueForField("Capacity must be greater than 0.");
        }
        // Validate fuelLevel is within range
        if (fuelLevel < 0.0 || fuelLevel > 100.0) {
            throw new IncorrectValueForField("Fuel level must be between 0.0 and 100.0.");
        }
        // Validate fuelType is an allowed value
        validateFuelType(fuelType);
        // All validations passed — assign fields
        this.busID     = busID;
        this.capacity  = capacity;
        this.fuelLevel = fuelLevel;
        this.fuelType  = fuelType;
    }

    // .................ReadFunctions....................

    /** @return the bus's unique ID */
    public String getBusID()     { return busID; }

    /** @return the seating capacity */
    public int getCapacity()     { return capacity; }

    /** @return current fuel level (0.0 to 100.0) */
    public double getFuelLevel() { return fuelLevel; }

    /** @return the fuel type (Diesel / Hybrid / Electricity) */
    public String getFuelType()  { return fuelType; }


    // .................WriteFunctions....................
    /**
     * Updates the bus capacity.
     * B2 — capacity can only decrease, never increase.
     *
     * @param capacity new capacity value (must be <= current capacity)
     * @throws IncorrectValueForField if new capacity is greater than current
     */
    public void setCapacity(int capacity) {
        // B2: block any increase to capacity
        if (capacity > this.capacity) {
        throw new
                IncorrectValueForField("B2 Violation: Capacity cannot increase during update operations. ");
        }
        if (capacity <= 0) {
            throw new IncorrectValueForField("Capacity must be greater than 0.");
        }
        this.capacity = capacity;
    }
    /**
     * Updates the fuel level.
     *
     * @param fuelLevel new fuel level (must be 0.0 to 100.0)
     */
    public void setFuelLevel(double fuelLevel) {
        if (fuelLevel < 0.0 || fuelLevel > 100.0) {
            throw new IncorrectValueForField("Fuel level must be between 0.0 and 100.");
        }
        this.fuelLevel = fuelLevel;
    }
    /**
     * Updates the fuel type.
     *
     * @param fuelType new fuel type (Diesel, Hybrid, Electricity)
     */
    public void setFuelType(String fuelType) {
        validateFuelType(fuelType);
        this.fuelType = fuelType;
    }

    //.........Error/ConditionValidators...........

    /**
     * Validates the busID against condition B1:
     *   - Exactly 8 characters long
     *   - All characters must be digits (0-8)
     * Note: uniqueness is NOT checked here — that is BusRepository's responsibility.
     *
     * @param busID the ID string to validate
     * @throws IncorrectValueForField if the ID does not meet B1 format requirements
     */

    private void validateBusID(String busID) {
        // Step 1 — null check and length check
        if (busID == null || busID.length() != 8) {
            throw new IncorrectValueForField("B1 Violation: busID must be exactly 8 characters long.");
        }
        // Step 2 — every character must be a digit
        for (int i = 0; i < busID.length(); i++) {
            if (!Character.isDigit(busID.charAt(i))) {
                throw new IncorrectValueForField("B1 Violation: busID must only be digits." + "Found'" + busID.charAt(i) + "' at position" + i + ".");
            }
        }
    }
    /**
     * Validates that the fuel type is one of the three allowed values.
     * @param fuelType the fuel type string to validate
     * @throws IncorrectValueForField if the value is not recognised
     */
    private void validateFuelType(String fuelType) {
        if (fuelType == null) {
            throw new IncorrectValueForField("Fuel Type cannot be null.");
        }

        switch (fuelType) {
            case "Diesel":
            case "Hybrid":
            case "Electricity":
                break;
            default:
                throw new IncorrectValueForField("Invalid fuelType '" + fuelType + "'. " + "Allowed values: Diesel, Hybrid, Electricity.");
        }

    }
    // .........Public Static Validators (B3, B4, B5)...........

    /**
     * B3: Drivers older than 50 cannot drive buses with a capacity of 50 or more.
     * Called externally — e.g. from tests or business logic — since it requires
     * both a driver property (age) and a bus property (capacity).
     * @param DriverAge driver's age in years
     * @param capacity  bus seating capacity
     * @throws IncorrectValueForField if the restriction is violated
     */
    public static void validateDriverAge(int DriverAge, int capacity) {
        if (DriverAge > 50 && capacity >= 50) {
            throw new IncorrectValueForField("B3 Violation: Drivers older than 50 cannot drive buses " +  "with a capacity of 50 or more.");
        }

    }

    /**
     * B4: Only drivers with at least 5 years of experience can drive electric buses.
     * @param experienceYears driver's years of experience
     * @param fuelType        bus fuel type
     * @throws IncorrectValueForField if the restriction is violated
     */

    public static void validateElectricExperience(int experienceYears, String fuelType) {
        if ("Electricity".equals(fuelType) && experienceYears < 5) {
            throw new IncorrectValueForField("B4 Violation: Only drivers with at least 5 years of experience " + "can drive electric buses.");
        }
    }

    /**
     * B5: Only Heavy or PublicTransport licence holders can drive electric or hybrid buses.
     *
     * @param licenseType driver's licence type
     * @param fuelType    bus fuel type
     * @throws IncorrectValueForField if the restriction is violated
     */
    public static void validateLicenceForBusType(String licenseType, String fuelType) {
        boolean isRestrictedBus = "Electricity".equals(fuelType) || "Hybrid".equals(fuelType);
        boolean hasValidLicence = "Heavy".equals(licenseType) || "PublicTransport".equals(licenseType);

        if (isRestrictedBus && !hasValidLicence) {
            throw new IncorrectValueForField("B5 Violation: Only Heavy or PublicTransport licence holders " + "can drive electric or hybrid buses.");
        }
    }
}



