/**
 * Driver class for the Intelligent Bus Driver Guidance System.
 *
 * Enforces the following conditions:
 *   D1 - driverID format and uniqueness (uniqueness enforced in DriverRepository)
 *   D2 - address must follow Street Number|Street Name|City|State|Country
 *   D3 - birthdate must follow DD-MM-YYYY
 *   D4 - licenseType cannot be changed if experienceYears > 10
 *   D5 - driverID and name are immutable (no setters provided)
 */
public class Driver {

    // .................Fields..........................


    private final String driverID;   // D5: final — cannot be changed after construction
    private final String name;       // D5: final — cannot be changed after construction
    private int    experienceYears;
    private String licenseType;      // Allowed values: Light, Medium, Heavy, PublicTransport
    private String address;          // D2: Street Number|Street Name|City|State|Country
    private String birthdate;        // D3: DD-MM-YYYY


    // ...................Constructor........................

    /**
     * Creates a new Driver after validating all fields.
     *
     * @param driverID        Exactly 10 chars; chars 0-1 are digits 2-9;
     *                        chars 2-7 contain ≥ 2 special characters;
     *                        chars 8-9 are uppercase A-Z  (D1)
     * @param name            Driver's full name (immutable after creation)
     * @param experienceYears Years of driving experience (must be ≥ 0)
     * @param licenseType     One of: Light, Medium, Heavy, PublicTransport
     * @param address         Format: Street Number|Street Name|City|State|Country (D2)
     * @param birthdate       Format: DD-MM-YYYY (D3)
     * @throws IncorrectValueForField if any field fails its validation rule
     */

    public Driver(String driverID, String name, int experienceYears,
                  String licenseType, String address, String birthdate) {

        // D1: Validate driverID format
        validateDriverID(driverID);

        //Validate name is not blank
        if (name == null || name.trim().isEmpty()) {
            throw new IncorrectValueForField("Name cannot be null or empty.");
        }

        //Validate experienceYears
        if (experienceYears < 0) {
            throw new IncorrectValueForField("Experience years cannot be negative.");
        }

        //Validate licenseType
        validateLicenseType(licenseType);

        //D2: Validate address format
        validateAddress(address);

        //D3: Validate birthdate format
        validateBirthdate(birthdate);

        // All validations passed — assign fields
        this.driverID        = driverID;
        this.name            = name;
        this.experienceYears = experienceYears;
        this.licenseType     = licenseType;
        this.address         = address;
        this.birthdate       = birthdate;
    }


    // .................ReadFunctions....................


    /** @return the driver's unique ID */
    public String getDriverID() { return driverID; }

    /** @return the driver's name */
    public String getName() { return name; }

    /** @return years of driving experience */
    public int getExperienceYears() { return experienceYears; }

    /** @return the licence type (Light / Medium / Heavy / PublicTransport) */
    public String getLicenseType() { return licenseType; }

    /** @return the driver's address in Street Number|Street Name|City|State|Country format */
    public String getAddress() { return address; }

    /** @return the driver's birthdate in DD-MM-YYYY format */
    public String getBirthdate() { return birthdate; }

    //.............WriteFunctions........................

    /**
     * Updates the driver's experience years.
     *
     * @param experienceYears new value (must be ≥ 0)
     */
    public void setExperienceYears(int experienceYears) {
        if (experienceYears < 0) {
            throw new IncorrectValueForField("Experience years cannot be negative.");
        }
        this.experienceYears = experienceYears;
    }

    /**
     * Updates the driver's licence type.
     *
     * D4 — if experienceYears > 10, the licence type CANNOT be changed.
     *
     * @param licenseType new licence type (Light, Medium, Heavy, PublicTransport)
     */
    public void setLicenseType(String licenseType) {
        // D4: Block licence changes for drivers with more than 10 years experience
        if (this.experienceYears > 10) {
            throw new IncorrectValueForField(
                "D4 Violation: License type cannot be changed for drivers with more than 10 years of experience.");
        }
        validateLicenseType(licenseType);
        this.licenseType = licenseType;
    }

    /**
     * Updates the driver's address.
     *
     * D2 — address must follow Street Number|Street Name|City|State|Country.
     *
     * @param address new address string
     */
    public void setAddress(String address) {
        validateAddress(address);
        this.address = address;
    }

    /**
     * Updates the driver's birthdate.
     *
     * D3 — birthdate must follow DD-MM-YYYY.
     *
     * @param birthdate new birthdate string
     */
    public void setBirthdate(String birthdate) {
        validateBirthdate(birthdate);
        this.birthdate = birthdate;
    }


    //.........Error/ConditionValidators...........

    /**
     * Validates the driverID against condition D1:
     *   - Exactly 10 characters long
     *   - Characters 0-1 (first two) are digits between '2' and '9'
     *   - Characters 2-7 (positions 3-8, 0-indexed 2-7) contain at least 2 special characters
     *   - Characters 8-9 (last two) are uppercase letters A-Z
     *
     * A "special character" is any character that is NOT a letter or digit.
     *
     * @param driverID the ID string to validate
     * @throws IncorrectValueForField if the ID does not meet D1 requirements
     */
    private void validateDriverID(String driverID) {
        if (driverID == null || driverID.length() != 10) {
            throw new IncorrectValueForField(
                "D1 Violation: driverID must be exactly 10 characters long.");
        }

        // Step 1 — First two characters must be digits between 2 and 9
        for (int i = 0; i < 2; i++) {
            char c = driverID.charAt(i);
            if (c < '2' || c > '9') {
                throw new IncorrectValueForField(
                    "D1 Violation: The first two characters of driverID must be digits between 2 and 9. " +
                    "Found '" + c + "' at position " + i + ".");
            }
        }

        // Step 2 — Characters at positions 2-7 must contain at least 2 special characters
        int specialCount = 0;
        for (int i = 2; i <= 7; i++) {
            char c = driverID.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                specialCount++;
            }
        }
        if (specialCount < 2) {
            throw new IncorrectValueForField(
                "D1 Violation: Characters 3-8 of driverID must contain at least 2 special characters. " +
                "Found " + specialCount + ".");
        }

        // Step 3 — Last two characters must be uppercase A-Z
        for (int i = 8; i < 10; i++) {
            char c = driverID.charAt(i);
            if (c < 'A' || c > 'Z') {
                throw new IncorrectValueForField(
                    "D1 Violation: The last two characters of driverID must be uppercase letters (A-Z). " +
                    "Found '" + c + "' at position " + i + ".");
            }
        }
    }

    /**
     * Validates that the address follows the format: Street Number|Street Name|City|State|Country
     * 4 pipe '|' separators, splitting into 5 non-empty segments.
     *
     * @param address the address string to validate
     * @throws IncorrectValueForField if the format is invalid (D2)
     */
    private void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IncorrectValueForField(
                "D2 Violation: Address cannot be null or empty.");
        }

        // Split on '|' and require exactly 5 parts
        String[] parts = address.split("\\|", -1);
        if (parts.length != 5) {
            throw new IncorrectValueForField(
                "D2 Violation: Address must follow the format " +
                "'Street Number|Street Name|City|State|Country'. " +
                "Found " + parts.length + " segment(s) instead of 5.");
        }

        // Each segment must be non-empty
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].trim().isEmpty()) {
                throw new IncorrectValueForField(
                    "D2 Violation: Address segment at position " + (i + 1) + " is empty.");
            }
        }
    }

    /**
     * Validates that the birthdate follows the format: DD-MM-YYYY
     *
     * @param birthdate the birthdate string to validate
     * @throws IncorrectValueForField if the format is invalid (D3)
     */
    private void validateBirthdate(String birthdate) {
        if (birthdate == null || birthdate.trim().isEmpty()) {
            throw new IncorrectValueForField(
                "D3 Violation: Birthdate cannot be null or empty.");
        }

        if (!birthdate.matches("\\d{2}-\\d{2}-\\d{4}")) {
            throw new IncorrectValueForField(
                "D3 Violation: Birthdate must follow the format DD-MM-YYYY. Received: '" + birthdate + "'.");
        }
    }

    /**
     * Validates that the licence type is one of the four allowed values.
     *
     * @param licenseType the licence type string to validate
     * @throws IncorrectValueForField if the value is not recognised
     */
    private void validateLicenseType(String licenseType) {
        if (licenseType == null) {
            throw new IncorrectValueForField("License type cannot be null.");
        }
        switch (licenseType) {
            case "Light":
            case "Medium":
            case "Heavy":
            case "PublicTransport":
                break; // Valid
            default:
                throw new IncorrectValueForField(
                    "Invalid licenseType '" + licenseType + "'. " +
                    "Allowed values: Light, Medium, Heavy, PublicTransport.");
        }
    }

}
