import com.apigee.sdk.apm.android.JacksonMarshallingService;
import com.apigee.sdk.apm.android.model.ApplicationConfigurationModel;
import com.apigee.sdk.apm.android.util.StringUtils;
import com.apigee.sdk.data.client.entities.User;
import com.apigee.sdk.data.client.response.ApiResponse;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.util.UUID;

import static org.junit.Assert.*;


/**
 * The ApigeeAuthTokenTest test case is used to validate the creation and validity of a token requests response.  This comes into play when, for example, you login a user with the ApigeeDataClient.
 *
 * @author  ApigeeCorporation
 */
public class ApigeeAuthTokenTest {

    static String tokenSampleDataLocation = "token.json";
    static ApiResponse apiResponse = null;

    @BeforeClass
    public static void setUpOnce() {
        try {
            InputStream inputStream = ApplicationConfigurationModel.class.getClassLoader().getResourceAsStream(tokenSampleDataLocation);
            assertNotNull("Sample data input stream is null.", inputStream);
            String jsonString = StringUtils.inputStreamToString(inputStream);
            assertNotNull("Sample data input stream to string method failed.",jsonString);
            apiResponse = (ApiResponse)new JacksonMarshallingService().demarshall(jsonString, ApiResponse.class);
            assertNotNull("Token ApiResponse object is null. Creation failed.",apiResponse);
        } catch (Exception e) {
        }
    }

    /**
     * Tests the validity of the top level access_token and expires_in properties.
     */
    @Test
    public void test_AuthProperties() {
        assertEquals("access_token is not equal.",apiResponse.getAccessToken(),"YWMt7J72Zg0fEeShX-l2bfgeBwAAAUdktenuCX-b_zZ_TvaOMAfcnBKgOFHJJ9U");
        assertEquals("expires_in is not equal.",apiResponse.getProperties().get("expires_in").asLong(),604800L);
    }

    /**
     * Tests the validity of the ApigeeUser created through the use of the sample data.
     */
    @Test
    public void test_UserProperties() {
        User user = apiResponse.getUser();
        assertNotNull("User from apiResponse is null.",user);

        assertEquals("Created is " + user.getLongProperty("created") + " expected \"1403564520222\".",user.getLongProperty("created"),1403564520222L);
        assertEquals("Modified is " + user.getLongProperty("modified") + " expected \"1403564520222\".", user.getLongProperty("modified"), 1403564520222L);

        assertTrue("Activated is false and should be true.", user.isActivated());
        assertFalse("Disabled is false and should be false.", user.isDisabled());

        assertEquals("UUID is " + user.getUuid().toString() + "expected \"61fa03f4-fb2a-11e3-acca-39529b0acff6\".",user.getUuid(), UUID.fromString("61fa03f4-fb2a-11e3-acca-39529b0acff6"));
        assertEquals("Type is " + user.getType() + "expected \"user\".",user.getType(),"user");
        assertEquals("Name is " + user.getName() + "expected \"Test User\".",user.getName(),"Test User");
        assertEquals("Email is " + user.getEmail() + "expected \"rwalsh@apigee.com\".",user.getEmail(),"rwalsh@apigee.com");
        assertEquals("Username is " + user.getUsername() + "expected \"testuser\".",user.getUsername(),"testuser");
        assertEquals("Picture is " + user.getPicture() + "expected \"http://www.gravatar.com/avatar/e466d447df831ddce35fbc50763fb03a\".",user.getPicture(),"http://www.gravatar.com/avatar/e466d447df831ddce35fbc50763fb03a");
    }

}
