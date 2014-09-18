import com.apigee.sdk.AppIdentification;
import com.apigee.sdk.data.client.ApigeeDataClient;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * The AppIdentificationTest test case is used to validate the various ways of creating the AppIdentification object.
 *
 * @author  ApigeeCorporation
 */
public class AppIdentificationTest {

    private final static String ORG_ID = "testOrgID";
    private final static String APP_ID = "testAppID";
    private final static UUID ORG_UUID = UUID.fromString("4c735c7a-fb24-11e3-9064-b71444e51454");
    private final static UUID APP_UUID = UUID.fromString("c42bdc10-fb24-11e3-8452-25d3fc2d5ac5");

    @Test public void test_CreationWithIDs() {
        AppIdentification appIdentification = new AppIdentification(ORG_ID,APP_ID);

        assertNotNull("appIdentification creation failed.", appIdentification);
        assertNotNull("uniqueIdentifier should not be null.", appIdentification.getUniqueIdentifier());

        assertNull("organizationUUID should be null.",appIdentification.getOrganizationUUID());
        assertNull("applicationUUID should be null.",appIdentification.getApplicationUUID());

        assertEquals("organizationID is not equal.",ORG_ID,appIdentification.getOrganizationId());
        assertEquals("appID is not equal.",APP_ID,appIdentification.getApplicationId());
        assertEquals("baseURL is not equal.", ApigeeDataClient.PUBLIC_API_URL,appIdentification.getBaseURL());
    }

    @Test public void test_CreationWithUUIDs() {
        AppIdentification appIdentification = new AppIdentification(ORG_UUID,APP_UUID);

        assertNotNull("appIdentification creation failed.", appIdentification);
        assertNotNull("uniqueIdentifier should not be null.", appIdentification.getUniqueIdentifier());

        assertNull("organizationID should be null.",appIdentification.getOrganizationId());
        assertNull("appID should be null.",appIdentification.getApplicationId());

        assertEquals("organizationUUID is not equal.",ORG_UUID,appIdentification.getOrganizationUUID());
        assertEquals("applicationUUID is not equal.",APP_UUID,appIdentification.getApplicationUUID());
        assertEquals("baseURL is not equal.", ApigeeDataClient.PUBLIC_API_URL,appIdentification.getBaseURL());
    }
}