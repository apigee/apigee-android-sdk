import com.apigee.sdk.apm.android.JacksonMarshallingService;
import com.apigee.sdk.apm.android.model.App;
import com.apigee.sdk.apm.android.model.ApplicationConfigurationModel;
import com.apigee.sdk.apm.android.util.StringUtils;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The ApplicationConfigurationModelTest test case is used to test the validity of the creation of the various ApplicationConfigurationModel objects that are read from the App top level object.
 *
 * @author  ApigeeCorporation
 */
public class ApplicationConfigurationModelTest {

    static String appConfigSampleDataLocation = "apigeeMobileConfigSample.json";

    static App app = null;

    @BeforeClass
    public static void setUpOnce() {
        try {
            InputStream inputStream = ApplicationConfigurationModel.class.getClassLoader().getResourceAsStream(appConfigSampleDataLocation);
            assertNotNull("Sample data input stream is null.",inputStream);
            String jsonString = StringUtils.inputStreamToString(inputStream);
            assertNotNull("Sample data input stream to string method failed.",jsonString);
            app = (App)new JacksonMarshallingService().demarshall(jsonString, App.class);
            assertNotNull("App configuration object is null. Creation failed.",app);
        } catch (Exception e) {
        }
    }

    /**
     * Tests the validity of the App's defaultAppConfig property.
     */
    @Test
    public void test_DefaultLevelConfig() {
        ApplicationConfigurationModel defaultAppConfig = app.getDefaultAppConfig();
        assertNotNull("defaultAppConfig should not be null.",defaultAppConfig);

        assertEquals("appConfigType should be Default.",defaultAppConfig.getAppConfigType(),"Default");
        //assertEquals("appConfigId should be 0.",(long)defaultAppConfig.getAppConfigID(),0);
        //assertNull("description should be null.",defaultAppConfig.getDescription());
        assertNull("lastModifiedDate should be null.", defaultAppConfig.getLastModifiedDate());

        assertTrue("batteryStatusCaptureEnabled should be true.",defaultAppConfig.getBatteryStatusCaptureEnabled());
        assertTrue("deviceIdCaptureEnabled should be true.",defaultAppConfig.getDeviceIdCaptureEnabled());
        assertTrue("deviceModelCaptureEnabled should be true.",defaultAppConfig.getDeviceModelCaptureEnabled());
        assertTrue("enableLogMonitoring should be true.",defaultAppConfig.getEnableLogMonitoring());
        assertTrue("enableUploadWhenMobile should be true.",defaultAppConfig.getEnableUploadWhenMobile());
        assertTrue("imeicaptureEnabled should be true.",defaultAppConfig.getIMEICaptureEnabled());
        assertTrue("monitorAllUrls should be true.",defaultAppConfig.getMonitorAllUrls());
        assertTrue("networkCarrierCaptureEnabled should be true.",defaultAppConfig.getNetworkCarrierCaptureEnabled());
        assertTrue("networkMonitoringEnabled should be true.",defaultAppConfig.getNetworkMonitoringEnabled());
        assertTrue("obfuscateDeviceId should be true.",defaultAppConfig.getObfuscateDeviceId());
        assertTrue("obfuscateIMEI should be true.",defaultAppConfig.getObfuscateIMEI());
        assertTrue("sessionDataCaptureEnabled should be true.",defaultAppConfig.getSessionDataCaptureEnabled());

        assertFalse("cachingEnabled should be false.",defaultAppConfig.getCachingEnabled());
        assertFalse("enableUploadWhenRoaming should be false.",defaultAppConfig.getEnableUploadWhenRoaming());
        assertFalse("locationCaptureEnabled should be false.",defaultAppConfig.getLocationCaptureEnabled());

        assertEquals("agentUploadIntervalInSeconds should be 60.",(long)defaultAppConfig.getAgentUploadIntervalInSeconds(),60L);
        assertEquals("locationCaptureResolution should be 1.",(long)defaultAppConfig.getLocationCaptureResolution(),1L);
        assertEquals("logLevelToMonitor should be 3.",defaultAppConfig.getLogLevelToMonitor(),3);
        assertEquals("samplingRate should be 100.",(long)defaultAppConfig.getSamplingRate(),100L);

        assertEquals("urlRegex should have 0 count.",defaultAppConfig.getUrlRegex().size(),0);
        assertEquals("customConfigParams should have 0 count..",defaultAppConfig.getCustomConfigParameters().size(),0);
    }

    /**
     * Tests the validity of the App's deviceLevelAppConfig property.
     */
    @Test
    public void test_DeviceLevelConfig() {
        ApplicationConfigurationModel deviceLevelAppConfig = app.getDeviceLevelAppConfig();
        assertNotNull("deviceLevelAppConfig should not be null.",deviceLevelAppConfig);

        assertEquals("appConfigType should be Beta.", deviceLevelAppConfig.getAppConfigType(), "Beta");
        //assertEquals("appConfigId should be 0.",(long)deviceLevelAppConfig.getAppConfigID(),0);
        //assertNull("description should be null.",deviceLevelAppConfig.getDescription());
        assertNull("lastModifiedDate should be null.", deviceLevelAppConfig.getLastModifiedDate());

        assertTrue("batteryStatusCaptureEnabled should be true.",deviceLevelAppConfig.getBatteryStatusCaptureEnabled());
        assertTrue("deviceIdCaptureEnabled should be true.",deviceLevelAppConfig.getDeviceIdCaptureEnabled());
        assertTrue("deviceModelCaptureEnabled should be true.",deviceLevelAppConfig.getDeviceModelCaptureEnabled());
        assertTrue("enableLogMonitoring should be true.",deviceLevelAppConfig.getEnableLogMonitoring());
        assertTrue("enableUploadWhenMobile should be true.",deviceLevelAppConfig.getEnableUploadWhenMobile());
        assertTrue("imeicaptureEnabled should be true.",deviceLevelAppConfig.getIMEICaptureEnabled());
        assertTrue("monitorAllUrls should be true.",deviceLevelAppConfig.getMonitorAllUrls());
        assertTrue("networkCarrierCaptureEnabled should be true.",deviceLevelAppConfig.getNetworkCarrierCaptureEnabled());
        assertTrue("networkMonitoringEnabled should be true.",deviceLevelAppConfig.getNetworkMonitoringEnabled());
        assertTrue("obfuscateDeviceId should be true.",deviceLevelAppConfig.getObfuscateDeviceId());
        assertTrue("obfuscateIMEI should be true.",deviceLevelAppConfig.getObfuscateIMEI());
        assertTrue("sessionDataCaptureEnabled should be true.",deviceLevelAppConfig.getSessionDataCaptureEnabled());

        assertFalse("cachingEnabled should be false.",deviceLevelAppConfig.getCachingEnabled());
        assertFalse("enableUploadWhenRoaming should be false.",deviceLevelAppConfig.getEnableUploadWhenRoaming());
        assertFalse("locationCaptureEnabled should be false.",deviceLevelAppConfig.getLocationCaptureEnabled());

        assertEquals("agentUploadIntervalInSeconds should be 60.",(long)deviceLevelAppConfig.getAgentUploadIntervalInSeconds(),60L);
        assertEquals("locationCaptureResolution should be 1.",(long)deviceLevelAppConfig.getLocationCaptureResolution(),1L);
        assertEquals("logLevelToMonitor should be 3.",deviceLevelAppConfig.getLogLevelToMonitor(),3);
        assertEquals("samplingRate should be 100.",(long)deviceLevelAppConfig.getSamplingRate(),100L);

        assertEquals("urlRegex should have 0 count.",deviceLevelAppConfig.getUrlRegex().size(),0);
        assertEquals("customConfigParams should have 0 count..",deviceLevelAppConfig.getCustomConfigParameters().size(),0);
    }

    /**
     * Tests the validity of the App's deviceTypeAppConfig property.
     */
    @Test
    public void test_DeviceTypeConfig() {
        ApplicationConfigurationModel deviceTypeAppConfig = app.getDeviceTypeAppConfig();
        assertNotNull("deviceTypeAppConfig should not be null.",deviceTypeAppConfig);

        assertEquals("appConfigType should be Device.",deviceTypeAppConfig.getAppConfigType(),"Device");
        //assertEquals("appConfigId should be 0.",(long)deviceTypeAppConfig.getAppConfigID(),0);
        //assertNull("description should be null.",deviceTypeAppConfig.getDescription());
        assertNull("lastModifiedDate should be null.", deviceTypeAppConfig.getLastModifiedDate());

        assertTrue("batteryStatusCaptureEnabled should be true.",deviceTypeAppConfig.getBatteryStatusCaptureEnabled());
        assertTrue("deviceIdCaptureEnabled should be true.",deviceTypeAppConfig.getDeviceIdCaptureEnabled());
        assertTrue("deviceModelCaptureEnabled should be true.",deviceTypeAppConfig.getDeviceModelCaptureEnabled());
        assertTrue("enableLogMonitoring should be true.",deviceTypeAppConfig.getEnableLogMonitoring());
        assertTrue("enableUploadWhenMobile should be true.",deviceTypeAppConfig.getEnableUploadWhenMobile());
        assertTrue("imeicaptureEnabled should be true.",deviceTypeAppConfig.getIMEICaptureEnabled());
        assertTrue("monitorAllUrls should be true.",deviceTypeAppConfig.getMonitorAllUrls());
        assertTrue("networkCarrierCaptureEnabled should be true.",deviceTypeAppConfig.getNetworkCarrierCaptureEnabled());
        assertTrue("networkMonitoringEnabled should be true.",deviceTypeAppConfig.getNetworkMonitoringEnabled());
        assertTrue("obfuscateDeviceId should be true.",deviceTypeAppConfig.getObfuscateDeviceId());
        assertTrue("obfuscateIMEI should be true.",deviceTypeAppConfig.getObfuscateIMEI());
        assertTrue("sessionDataCaptureEnabled should be true.",deviceTypeAppConfig.getSessionDataCaptureEnabled());

        assertFalse("cachingEnabled should be false.",deviceTypeAppConfig.getCachingEnabled());
        assertFalse("enableUploadWhenRoaming should be false.",deviceTypeAppConfig.getEnableUploadWhenRoaming());
        assertFalse("locationCaptureEnabled should be false.",deviceTypeAppConfig.getLocationCaptureEnabled());

        assertEquals("agentUploadIntervalInSeconds should be 60.",(long)deviceTypeAppConfig.getAgentUploadIntervalInSeconds(),60L);
        assertEquals("locationCaptureResolution should be 1.",(long)deviceTypeAppConfig.getLocationCaptureResolution(),1L);
        assertEquals("logLevelToMonitor should be 3.",deviceTypeAppConfig.getLogLevelToMonitor(),3);
        assertEquals("samplingRate should be 100.",(long)deviceTypeAppConfig.getSamplingRate(),100L);

        assertEquals("urlRegex should have 0 count.",deviceTypeAppConfig.getUrlRegex().size(),0);
        assertEquals("customConfigParams should have 0 count..",deviceTypeAppConfig.getCustomConfigParameters().size(),0);
    }

    /**
     * Tests the validity of the App's abTestingAppConfig property.
     */
    @Test
    public void test_ABTestingConfig() {
        ApplicationConfigurationModel abTestingAppConfig = app.getABTestingAppConfig();
        assertNotNull("abTestingAppConfig should not be null.",abTestingAppConfig);

        assertEquals("appConfigType should be A/B.",abTestingAppConfig.getAppConfigType(),"A/B");
        //assertEquals("appConfigId should be 0.",(long)abTestingAppConfig.getAppConfigID(),0);
        //assertNull("description should be null.",abTestingAppConfig.getDescription());
        assertNull("lastModifiedDate should be null.", abTestingAppConfig.getLastModifiedDate());

        assertTrue("batteryStatusCaptureEnabled should be true.",abTestingAppConfig.getBatteryStatusCaptureEnabled());
        assertTrue("deviceIdCaptureEnabled should be true.",abTestingAppConfig.getDeviceIdCaptureEnabled());
        assertTrue("deviceModelCaptureEnabled should be true.",abTestingAppConfig.getDeviceModelCaptureEnabled());
        assertTrue("enableLogMonitoring should be true.",abTestingAppConfig.getEnableLogMonitoring());
        assertTrue("enableUploadWhenMobile should be true.",abTestingAppConfig.getEnableUploadWhenMobile());
        assertTrue("imeicaptureEnabled should be true.",abTestingAppConfig.getIMEICaptureEnabled());
        assertTrue("monitorAllUrls should be true.",abTestingAppConfig.getMonitorAllUrls());
        assertTrue("networkCarrierCaptureEnabled should be true.",abTestingAppConfig.getNetworkCarrierCaptureEnabled());
        assertTrue("networkMonitoringEnabled should be true.",abTestingAppConfig.getNetworkMonitoringEnabled());
        assertTrue("obfuscateDeviceId should be true.",abTestingAppConfig.getObfuscateDeviceId());
        assertTrue("obfuscateIMEI should be true.",abTestingAppConfig.getObfuscateIMEI());
        assertTrue("sessionDataCaptureEnabled should be true.",abTestingAppConfig.getSessionDataCaptureEnabled());

        assertFalse("cachingEnabled should be false.",abTestingAppConfig.getCachingEnabled());
        assertFalse("enableUploadWhenRoaming should be false.",abTestingAppConfig.getEnableUploadWhenRoaming());
        assertFalse("locationCaptureEnabled should be false.",abTestingAppConfig.getLocationCaptureEnabled());

        assertEquals("agentUploadIntervalInSeconds should be 60.",(long)abTestingAppConfig.getAgentUploadIntervalInSeconds(),60L);
        assertEquals("locationCaptureResolution should be 1.",(long)abTestingAppConfig.getLocationCaptureResolution(),1L);
        assertEquals("logLevelToMonitor should be 3.",abTestingAppConfig.getLogLevelToMonitor(),3);
        assertEquals("samplingRate should be 100.",(long)abTestingAppConfig.getSamplingRate(),100L);

        assertEquals("urlRegex should have 0 count.",abTestingAppConfig.getUrlRegex().size(),0);
        assertEquals("customConfigParams should have 0 count..",abTestingAppConfig.getCustomConfigParameters().size(),0);
    }
}