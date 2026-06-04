import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Bus class.
 * Coverage:
 *   B1 - busID format rules              (TC-B-01 to TC-B-03)
 *   B2 - capacity update restriction     (TC-B-04 to TC-B-06)
 *   B3 - driver age restriction          (TC-B-07 to TC-B-09)
 *   B4 - electric bus experience         (TC-B-10 to TC-B-12)
 *   B5 - licence restriction             (TC-B-13 to TC-B-15)
 * Each condition has at least three cases covering:
 *   - a normal/valid scenario
 *   - an invalid input
 *   - an edge case
 */

public class BusUnitTests {

    // .................Shared Test Values..........................

    private static final String VALID_BUS_ID  = "12345678"; // 8 digits — satisfies B1
    private static final int    VALID_CAP     = 40;
    private static final double VALID_FUEL    = 75.0;
    private static final String VALID_FUEL_TYPE = "Diesel";

    // Creates a valid bus using the above constants
    private Bus makeValidBus() {
        return new Bus(VALID_BUS_ID, VALID_CAP, VALID_FUEL, VALID_FUEL_TYPE);
    }

    // .................B1 - Bus ID Rules..........................

    /**
     * TC-B-01  Normal case: a correctly formatted busID is accepted.
     * "12345678" is exactly 8 digits — satisfies all B1 rules.
     */
    @Test
    void testBusID_validFormat_accepted() {
        Bus bus = makeValidBus();
        assertEquals(VALID_BUS_ID, bus.getBusID(),
                "A correctly formatted busID should be stored as-is.");
    }
    /**
     * TC-B-02  Invalid input: busID that is only 6 characters long.
     * B1 requires exactly 8 characters — anything shorter must be rejected.
     */
    @Test
    void testBusID_TooShort_ThrowsException() {
        assertThrows(IncorrectValueForField.class, ()
                        -> new Bus("123456", VALID_CAP, VALID_FUEL, VALID_FUEL_TYPE),
                "A 6-character busID should be rejected.");
    }
    /**
     * TC-B-03  Edge case: busID containing a letter.
     * B1 requires ALL characters to be digits — a single letter must be rejected.
     * "1234567A" has 8 characters but the last is not a digit.
     */
    @Test
    void testBusID_containsLetter_ThrowsException() {
        assertThrows(IncorrectValueForField.class, () ->
                new Bus("1234567A", VALID_CAP, VALID_FUEL, VALID_FUEL_TYPE),
                "A busID containing a non-digit character should be rejected.");
    }

    // .................B2 - Capacity Update Restriction..........................
    /**
     * TC-B-04  Normal case: decreasing capacity is allowed.
     * B2 only blocks increases — a decrease from 40 to 30 must succeed.
     */
    @Test
    void testCapacity_decrease_allowed() {
        Bus bus = makeValidBus();
        assertDoesNotThrow(() -> bus.setCapacity(30),
                "Decreasing capacity should be allowed.");
        assertEquals(30, bus.getCapacity());
    }
    /**
     * TC-B-05  Invalid input: increasing capacity is blocked.
     * B2 states capacity cannot increase — going from 40 to 50 must be rejected.
     */
    @Test
    void testCapacity_increase_ThrowsException() {
        Bus bus = makeValidBus();
        assertThrows(IncorrectValueForField.class, () -> bus.setCapacity(41),
                "Increasing capacity should be rejected (B2).");
    }
    /**
     * TC-B-06  Edge case: setting capacity to the same value.
     * B2 says capacity cannot INCREASE — same value is not an increase,
     * so setting capacity to 40 on a bus already at 40 must be allowed.
     */
    @Test
    void testCapacity_sameValueAllowed() {
        Bus bus = makeValidBus();
        assertDoesNotThrow(() -> bus.setCapacity(40),
                "Setting Capacity to the same value should not be rejected.");
    }

    // .................B3 - Driver Age Restriction..........................
    /**
     * TC-B-07  Normal case: driver aged 30 driving a bus with capacity 60.
     * Driver is under 50 years old — no restriction applies.
     */
    @Test
    void testDriverAge_youngDriver_largeBus_allowed(){
        assertDoesNotThrow(() -> Bus.validateDriverAge(30, 60),
                "A driver aged 30 should be allowed to drive any bus.");
    }
    /**
     * TC-B-08  Invalid input: driver aged 51 driving a bus with capacity 50.
     * B3 blocks drivers older than 50 from buses with capacity >= 50.
     */
    @Test
    void testDriverAge_oldDriver_largeBus_ThrowsException() {
        assertThrows(IncorrectValueForField.class, () -> Bus.validateDriverAge(51, 50),
                "A driver older than 50 should not drive a bus with capacity >= 50 (B3).");
    }
    /**
     * TC-B-09  Edge case: driver aged EXACTLY 50 driving a bus with capacity 50.
     * B3 says "older than 50" — exactly 50 is not older than 50 so it must be allowed.
     */
    @Test
    void testDriverAge_middleAgedDriver_largeBus_allowed() {
        assertDoesNotThrow(() -> Bus.validateDriverAge(50, 50),
                "A driver aged exactly 50 should still be allowed (B3 only blocks > 50).");
    }

    // .................B4 - Electric Bus Experience Restriction..........................

    /**
     * TC-B-10  Normal case: driver with exactly 5 years experience driving electric bus.
     * B4 requires AT LEAST 5 years — exactly 5 is the minimum and must be accepted.
     */
    @Test
    void testElectricExperience_fiveYears_allowed() {
        assertDoesNotThrow(() -> Bus.validateElectricExperience(5, "Electricity"),
                "A driver with exactly 5 years experience should be allowed to drive electric buses.");
    }

    /**
     * TC-B-11  Invalid input: driver with 4 years experience driving electric bus.
     * B4 requires at least 5 years — 4 years is one short and must be rejected.
     */
    @Test
    void testElectricExperience_fourYears_ThrowsException() {
        assertThrows(IncorrectValueForField.class,
                () -> Bus.validateElectricExperience(4, "Electricity"),
                "A driver with fewer than 5 years experience should not drive electric buses (B4).");
    }
    /**
     * TC-B-12  Edge case: driver with 4 years experience driving a Diesel bus.
     * B4 only applies to electric buses — a Diesel bus has no experience restriction.
     */
    @Test
    void testElectricExperience_dieselBus_noRestriction() {
        assertDoesNotThrow(() -> Bus.validateElectricExperience(4, "Diesel"),
                "Experience restriction should not apply to non-electric buses.");
    }

    // .................B5 - Licence Restriction..........................

    /**
     * TC-B-13  Normal case: Heavy licence driving a Hybrid bus.
     * B5 requires Heavy or PublicTransport for electric/hybrid — Heavy qualifies.
     */
    @Test
    void testLicence_heavyLicence_hybridBus_allowed() {
        assertDoesNotThrow(() -> Bus.validateLicenceForBusType("Heavy", "Hybrid"),
                "A Heavy licence should be allowed on a Hybrid bus.");
    }
    /**
     * TC-B-14  Invalid input: Light licence driving an electric bus.
     * B5 blocks Light and Medium Licences from electricty and hybrid buses.
     */

    @Test
    void testLicence_lightLicence_electricBus_throwsException() {
        assertThrows(IncorrectValueForField.class,
                () -> Bus.validateLicenceForBusType("Light", "Electricity"),
                "A Light licence should not be allowed on an electric bus (B5).");
    }

    /**
     *  TC-B-15 Valid input: Light Licence is Allowed for driving a diesel bus.
     *  B5 Requires a Heavy or PublicTransport licence ONLY for elctricity/Hybrid busses - Light is acceptable.
     */

    @Test
    void testLicence_lightLicence_DiselBus_allowed() {
        assertDoesNotThrow(() -> Bus.validateLicenceForBusType("Light", "Diesel"),
                "A Low licence should be allowed on an electric bus (B5).");
    }
}
