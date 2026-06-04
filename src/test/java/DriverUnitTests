import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Driver class.
 *
 * Coverage:
 *   D1 - driverID format rules          (TC-D-01 to TC-D-03)
 *   D2 - address format                 (TC-D-04 to TC-D-06)
 *   D3 - birthdate format               (TC-D-07 to TC-D-09)
 *   D4 - licenseType update restriction (TC-D-10 to TC-D-12)
 *   D5 - immutable driverID and name    (TC-D-13 to TC-D-15)
 *
 * Each condition has at least three cases covering:
 *   - a normal/valid scenario
 *   - an invalid input
 *   - an edge case
 */
class DriverUnitTests {

    // .................Shared Test Values..........................

    // A driverID satisfying all D1 rules:
    //   '3','5'     -> digits 2-9 (positions 1-2)
    //   '@','#'     -> two special characters in positions 3-8
    //   'a','b','c','d' -> alphanumeric fill for positions 3-8
    //   'A','B'     -> uppercase letters (positions 9-10)
    private static final String VALID_ID      = "35@#abcdAB";
    private static final String VALID_NAME    = "Alice Smith";
    private static final String VALID_ADDRESS = "12|Main St|Melbourne|VIC|Australia";
    private static final String VALID_DOB     = "15-06-1990";

    // Creates valid driver using the above
    private Driver makeValidDriver() {
        return new Driver(VALID_ID, VALID_NAME, 5, "Heavy", VALID_ADDRESS, VALID_DOB);
    }


    // .................D1 - Driver ID Rules..........................


    /**
     * TC-D-01  Normal case: a correctly formatted driverID is accepted.
     * "35@#abcdAB" satisfies all D1 rules correct length, valid first two
     * digits, two special characters in positions 3-8, uppercase last two.
     */
    @Test
    void testDriverID_validFormat_accepted() {
        Driver driver = makeValidDriver();
        assertEquals(VALID_ID, driver.getDriverID(),
            "A correctly formatted driverID should be stored as-is.");
    }

    /**
     * TC-D-02  Invalid input: driverID that is only 8 characters long.
     * D1 requires exactly 10 characters anything shorter must be rejected.
     */
    @Test
    void testDriverID_tooShort_throwsException() {
        // "35@#abAB" is only 8 characters
        assertThrows(IncorrectValueForField.class,
            () -> new Driver("35@#abAB", VALID_NAME, 5, "Heavy", VALID_ADDRESS, VALID_DOB),
            "An 8-character driverID should be rejected.");
    }

    /**
     * TC-D-03  Edge case: driverID with only ONE special character in positions 3-8.
     * D1 requires AT LEAST two special characters in that range one is not enough.
     * "35@aabcdAB" has only '@' as a special character in positions 3-8.
     */
    @Test
    void testDriverID_onlyOneSpecialChar_throwsException() {
        // Positions 3-8: '@','a','a','b','c','d' only one special character
        assertThrows(IncorrectValueForField.class,
            () -> new Driver("35@aabcdAB", VALID_NAME, 5, "Heavy", VALID_ADDRESS, VALID_DOB),
            "A driverID with fewer than two special characters in positions 3-8 should be rejected.");
    }


    // .................D2 - Address Format..........................


    /**
     * TC-D-04  Normal case: a properly pipe-delimited 5-part address is accepted.
     * Format must be: Street Number|Street Name|City|State|Country.
     */
    @Test
    void testAddress_validFormat_accepted() {
        Driver driver = makeValidDriver();
        assertEquals(VALID_ADDRESS, driver.getAddress(),
            "A valid pipe-delimited address should be stored correctly.");
    }

    /**
     * TC-D-05  Invalid input: address with only 4 pipe-delimited parts.
     * D2 requires exactly 5 segments a missing Country segment must be rejected.
     */
    @Test
    void testAddress_missingSegment_throwsException() {
        // Only 4 parts missing the Country segment
        assertThrows(IncorrectValueForField.class,
            () -> new Driver(VALID_ID, VALID_NAME, 5, "Heavy", "12|Main St|Melbourne|VIC", VALID_DOB),
            "An address with fewer than 5 pipe-delimited segments should be rejected.");
    }

    /**
     * TC-D-06  Edge case: address where one segment is blank.
     * Pipe count is correct (5 segments) but StreetName is empty must be rejected.
     * e.g. "12||Melbourne|VIC|Australia" has an empty Street Name.
     */
    @Test
    void testAddress_blankSegment_throwsException() {
        // 5 pipes present but Street Name segment is empty
        assertThrows(IncorrectValueForField.class,
            () -> new Driver(VALID_ID, VALID_NAME, 5, "Heavy", "12||Melbourne|VIC|Australia", VALID_DOB),
            "An address with a blank segment should be rejected.");
    }


    // .................D3 - Birthdate Format..........................


    /**
     * TC-D-07  Normal case: a valid birthdate in DD-MM-YYYY format is accepted.
     */
    @Test
    void testBirthdate_validFormat_accepted() {
        Driver driver = makeValidDriver();
        assertEquals(VALID_DOB, driver.getBirthdate(),
            "A valid DD-MM-YYYY birthdate should be stored correctly.");
    }

    /**
     * TC-D-08  Invalid input: birthdate provided in YYYY-MM-DD format.
     * D3 only accepts DD-MM-YYYY the ISO format must be rejected.
     */
    @Test
    void testBirthdate_wrongFormat_throwsException() {
        // ISO format YYYY-MM-DD instead of required DD-MM-YYYY
        assertThrows(IncorrectValueForField.class,
            () -> new Driver(VALID_ID, VALID_NAME, 5, "Heavy", VALID_ADDRESS, "1990-06-15"),
            "A birthdate not in DD-MM-YYYY format should be rejected.");
    }

    /**
     * TC-D-09  Edge case: birthdate with an invalid month value of 13.
     * The DD-MM-YYYY pattern matches but month 13 does not exist must be rejected.
     */
    @Test
    void testBirthdate_invalidMonth_throwsException() {
        // Pattern DD-MM-YYYY matches but month value 13 is out of range
        assertThrows(IncorrectValueForField.class,
            () -> new Driver(VALID_ID, VALID_NAME, 5, "Heavy", VALID_ADDRESS, "15-13-1990"),
            "A birthdate with month 13 should be rejected.");
    }


    // .................D4 - License Update Restriction..........................


    /**
     * TC-D-10  Normal case: licence type can be changed when experience is exactly 10 years.
     * D4 blocks changes only when experienceYears is GREATER THAN 10, so 10 is still allowed.
     */
    @Test
    void testLicenseUpdate_tenYearsExperience_allowed() {
        Driver driver = new Driver(VALID_ID, VALID_NAME, 10, "Light", VALID_ADDRESS, VALID_DOB);
        // Exactly 10 years update should be permitted
        assertDoesNotThrow(() -> driver.setLicenseType("Heavy"),
            "A driver with exactly 10 years of experience should be allowed to change licenseType.");
        assertEquals("Heavy", driver.getLicenseType());
    }

    /**
     * TC-D-11  Invalid input: licence change attempted on a driver with 11 years experience.
     * D4 blocks any licence update once experienceYears exceeds 10.
     */
    @Test
    void testLicenseUpdate_elevenYearsExperience_blocked() {
        Driver driver = new Driver(VALID_ID, VALID_NAME, 11, "Heavy", VALID_ADDRESS, VALID_DOB);
        // 11 years exceeds the threshold update must be rejected
        assertThrows(IncorrectValueForField.class,
            () -> driver.setLicenseType("Light"),
            "A driver with more than 10 years of experience should not be able to change licenseType.");
    }

    /**
     * TC-D-12  Edge case: experience raised above 10 after construction, then licence change attempted.
     * D4 must evaluate experienceYears at the time setLicenseType is called, not at construction.
     */
    @Test
    void testLicenseUpdate_experienceIncreasedThenBlocked() {
        Driver driver = new Driver(VALID_ID, VALID_NAME, 5, "Light", VALID_ADDRESS, VALID_DOB);
        // Raise experience above the threshold after construction
        driver.setExperienceYears(15);
        // Licence change must now be blocked
        assertThrows(IncorrectValueForField.class,
            () -> driver.setLicenseType("Heavy"),
            "After experience exceeds 10, licenseType should no longer be changeable.");
    }


    // .................D5 - Immutable Fields..........................


    /**
     * TC-D-13  Normal case: driverID retains its construction value and no setter exists.
     * D5 requires driverID to be immutable confirmed via getter and reflection check.
     */
    @Test
    void testDriverID_isImmutable_noSetterExists() {
        Driver driver = makeValidDriver();
        // Value must match what was passed at construction
        assertEquals(VALID_ID, driver.getDriverID(),
            "driverID must retain the value set at construction.");
        // No setDriverID method should exist on the class
        boolean hasSetterID = java.util.Arrays.stream(Driver.class.getMethods())
            .anyMatch(m -> m.getName().equals("setDriverID"));
        assertFalse(hasSetterID, "Driver must not expose a setDriverID method (D5).");
    }

    /**
     * TC-D-14  Normal case: name retains its construction value and no setter exists.
     * D5 requires name to be immutable confirmed via getter and reflection check.
     */
    @Test
    void testName_isImmutable_noSetterExists() {
        Driver driver = makeValidDriver();
        // Value must match what was passed at construction
        assertEquals(VALID_NAME, driver.getName(),
            "name must retain the value set at construction.");
        // No setName method should exist on the class
        boolean hasSetterName = java.util.Arrays.stream(Driver.class.getMethods())
            .anyMatch(m -> m.getName().equals("setName"));
        assertFalse(hasSetterName, "Driver must not expose a setName method (D5).");
    }

    /**
     * TC-D-15  Edge case: all mutable fields update successfully while driverID and name stay fixed.
     * Confirms D5 is targeted only driverID and name are locked, not other fields.
     */
    @Test
    void testMutableFields_canBeUpdated_whileImmutableFieldsStayFixed() {
        Driver driver = new Driver(VALID_ID, VALID_NAME, 3, "Light", VALID_ADDRESS, VALID_DOB);

        // All four mutable fields should update without throwing any exception
        assertDoesNotThrow(() -> {
            driver.setExperienceYears(8);
            driver.setLicenseType("Medium");
            driver.setAddress("99|Oak Ave|Sydney|NSW|Australia");
            driver.setBirthdate("01-01-1985");
        }, "Mutable fields should be updatable freely.");

        // Immutable fields must remain unchanged after all updates
        assertEquals(VALID_ID,   driver.getDriverID(), "driverID must not change after update operations.");
        assertEquals(VALID_NAME, driver.getName(),     "name must not change after update operations.");
    }
}
