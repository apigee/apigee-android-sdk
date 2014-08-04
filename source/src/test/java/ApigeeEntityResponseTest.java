import com.apigee.sdk.apm.android.JacksonMarshallingService;
import com.apigee.sdk.apm.android.model.ApplicationConfigurationModel;
import com.apigee.sdk.apm.android.util.StringUtils;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.response.ApiResponse;
import com.fasterxml.jackson.databind.JsonNode;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by ApigeeCorporation on 8/1/14.
 */
public class ApigeeEntityResponseTest {

    static String apigeeEntityResponseJSONLocation = "apigeeEntityRequest.json";
    static ApiResponse apiResponse = null;

    @BeforeClass
    public static void setUpOnce() {
        try {
            InputStream inputStream = ApplicationConfigurationModel.class.getClassLoader().getResourceAsStream(apigeeEntityResponseJSONLocation);
            assertNotNull("Sample data input stream is null.",inputStream);
            String jsonString = StringUtils.inputStreamToString(inputStream);
            assertNotNull("Sample data input stream to string method failed.",jsonString);
            apiResponse = (ApiResponse)new JacksonMarshallingService().demarshall(jsonString, ApiResponse.class);
            assertNotNull("ApiResponse is null. Creation failed.",apiResponse);
        } catch (Exception e) {
        }
    }

    @Test
    public void test_sampleTopLevelProperties() {
        assertNotNull("apiResponse should not be null.",apiResponse);
        assertEquals("timestamp should be 1405539157157.", apiResponse.getTimestamp(), 1405539157157L);
        assertEquals("entityCount should be 5.", apiResponse.getEntityCount(), 5);
        assertEquals("params size should be 0.", apiResponse.getParams().size(), 0);
        assertEquals("action should be get",apiResponse.getAction(),"get");
        assertEquals("organization should be rwalsh",apiResponse.getProperties().get("organization").textValue(),"rwalsh");
        assertEquals("application uuid should be c42bdc10-fb24-11e3-8452-25d3fc2d5ac5",apiResponse.getApplication(), UUID.fromString("c42bdc10-fb24-11e3-8452-25d3fc2d5ac5"));
        assertEquals("applicationName should be sdk.demo",apiResponse.getProperties().get("applicationName").textValue(),"sdk.demo");
        assertEquals("duration should be 55",apiResponse.getProperties().get("duration").intValue(),55);
        assertEquals("count should be 5",apiResponse.getProperties().get("count").intValue(),5);
        assertEquals("path should be /publicevents",apiResponse.getPath(),"/publicevents");
        assertEquals("uri should be https://api.usergrid.com/rwalsh/sdk.demo/publicevents",apiResponse.getUri(),"https://api.usergrid.com/rwalsh/sdk.demo/publicevents");
    }

    @Test
    public void test_sampleFirstEntityData() {
        Entity firstEntity = apiResponse.getFirstEntity();
        assertNotNull("firstEntity should not be null.",firstEntity);
        assertEquals("firstEntity and entities first object should be equal.", firstEntity, apiResponse.getEntities().get(0));
        assertEquals("uuid should be fa015eaa-fe1c-11e3-b94b-63b29addea01",firstEntity.getUuid(),UUID.fromString("fa015eaa-fe1c-11e3-b94b-63b29addea01"));
        assertEquals("type should be publicevent", firstEntity.getType(), "publicevent");
        assertEquals("eventName should be public event 1",firstEntity.getStringProperty("eventName"),"public event 1");

        JsonNode locationNode = firstEntity.getProperties().get("location");
        assertNotNull("locationNode should not be null.",locationNode);
        assertEquals("latitude should be 33.748995",locationNode.get("latitude").asText(),"33.748995");
        assertEquals("latitude should be -84.387982",locationNode.get("longitude").asText(),"-84.387982");
    }
}
