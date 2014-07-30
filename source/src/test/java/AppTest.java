import com.apigee.sdk.apm.android.JacksonMarshallingService;
import com.apigee.sdk.apm.android.model.App;
import com.apigee.sdk.apm.android.util.StringUtils;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * Created by ApigeeCorporation on 7/29/14.
 */
public class AppTest {

    static String appConfigSampleDataLocation = "apigeeMobileConfigSample.json";

    static Calendar cal = Calendar.getInstance();
    static JacksonMarshallingService marshallingService = new JacksonMarshallingService();

    @BeforeClass
    public static void setUp() {
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Test
    public void App_SampleDataTest() {
        App app = null;
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(appConfigSampleDataLocation);
            assertNotNull("Sample data input stream is null.",inputStream);
            String jsonString = StringUtils.inputStreamToString(inputStream);
            assertNotNull("Sample data input stream to string method failed.",jsonString);
            app = (App)marshallingService.demarshall(jsonString, App.class);
        } catch (Exception e) {
        }

        assertNotNull("appConfig should not be null.",app);

        cal.setTimeInMillis(1403562108260L);
        Date createdDate = cal.getTime();

        cal.setTimeInMillis(1403562108260L);
        Date lastModifiedDate = cal.getTime();

        assertEquals("createdDate is not equal.",app.getCreatedDate(), createdDate);
        assertEquals("lastModifiedDate is not equal.",app.getLastModifiedDate(), lastModifiedDate);

        assertEquals("instaOpsApplicationId is not equal.",(long)app.getInstaOpsApplicationId(),(long)21493);
        assertEquals("orgName is not equal.",app.getOrgName(),"rwalsh");
        assertEquals("appName is not equal.",app.getAppName(),"sdk.demo");
        assertEquals("fullAppName is not equal.",app.getFullAppName(),"rwalsh_sdkdemo");
        assertEquals("appOwner is not equal.",app.getAppOwner(),"rwalsh@apigee.com");

        assertNull("googleID should be null.",app.getGoogleId());
        assertNull("appleID should be null.",app.getAppleId());
        assertNull("description should be null.",app.getDescription());
        assertEquals("environment is not equal.", app.getEnvironment(), "ug-max-prod");
        assertNull("customUploadUrl should be null.", app.getCustomUploadUrl());

        assertFalse("monitoringDisabled should be false.",app.getMonitoringDisabled());
        //assertFalse("deleted should be false.",appConfig.getDeleted());
        assertFalse("deviceLevelOverrideEnabled should be false.",app.getDeviceLevelOverrideEnabled());
        assertFalse("deviceTypeOverrideEnabled should be false.",app.getDeviceTypeOverrideEnabled());
        assertFalse("abTestingOverrideEnabled should be false.",app.getABTestingOverrideEnabled());

        assertNotNull("defaultAppConfig should not be null.",app.getDefaultAppConfig());
        assertNotNull("deviceTypeAppConfig should not be null.",app.getDeviceTypeAppConfig());
        assertNotNull("deviceLevelAppConfig should not be null.",app.getDeviceLevelAppConfig());
        assertNotNull("abTestingAppConfig should not be null.",app.getABTestingAppConfig());

        assertEquals("abTestingPercentage should be 0.",app.getABTestingPercentage(),new Integer(0));

        assertEquals("appConfigOverrideFilters should have 0 filters.",app.getAppConfigOverrideFilters().size(),0);
        assertEquals("deviceNumberFilters should have 0 filters.",app.getDeviceNumberFilters().size(),0);
        assertEquals("deviceIdFilters should have 0 filters.",app.getDeviceIdFilters().size(),0);
        assertEquals("deviceModelRegexFilters should have 0 filters.",app.getDeviceModelRegexFilters().size(),0);
        assertEquals("devicePlatformRegexFilters should have 0 filters.",app.getDevicePlatformRegexFilters().size(),0);
        assertEquals("networkTypeRegexFilters should have 0 filters.",app.getNetworkTypeRegexFilters().size(),0);
        assertEquals("networkOperatorRegexFilters should have 0 filters.",app.getNetworkOperatorRegexFilters().size(),0);
    }
}
