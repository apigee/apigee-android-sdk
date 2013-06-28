package com.instaops.android;

import com.apigee.sdk.apm.android.ApplicationConfigurationService;
import com.apigee.sdk.apm.android.MockWebManagerClientConfigLoader;

import junit.framework.TestCase;

public class MockWebManagerClientConfigTest extends TestCase {

	ApplicationConfigurationService loader;
	
	protected void setUp() throws Exception {
		super.setUp();
		loader = new MockWebManagerClientConfigLoader();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAddConfigListner() {
		
	}


//	public void testPropertyFiring() throws LoadConfigurationException {
//		
//		
//		loader.loadConfigurations("123");
//		//assertEquals("blah,blah,black,sheep", loader.getConfigurations().get("metrics.urls"));
//		
//		loader.addConfigListner(new PropertyChangeListener() {
//			
//			@Override
//			public void propertyChange(PropertyChangeEvent arg0) {
//				System.out.println("Fired Event :" + arg0.getPropertyName() );
//			}
//		});
//		
//		loader.loadConfigurations("456");
//	}
	
}
