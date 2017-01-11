/*******************************************************************************
 * Copyright (c) 2014, 2016 Orange.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.orange.basedriver.netatmo.sdt;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onem2m.home.devices.Camera;
import org.onem2m.home.driver.Logger;
import org.onem2m.home.driver.Utils;
import org.onem2m.home.modules.PersonSensor;
import org.onem2m.home.modules.Streaming;
import org.onem2m.sdt.Property;
import org.onem2m.sdt.datapoints.ArrayDataPoint;
import org.onem2m.sdt.datapoints.StringDataPoint;
import org.onem2m.sdt.impl.DataPointException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.orange.basedriver.netatmo.impl.Activator;
import com.orange.basedriver.netatmo.model.DetectedPerson;
import com.orange.basedriver.netatmo.model.WelcomeCamera;

public class SDTWelcomeCameraDevice extends Camera {

	private static final long DEFAULT_DETECTION_THRESHOLD = 75000; // 75 s
	private static final Logger LOGGER = new Logger("Netatmo");

	private final WelcomeCamera camera;
	private List<ServiceRegistration> serviceRegistrations;
	private long detectionThreshold;

	// personSensor
	private Map<String /*id*/, DetectedPerson> detectedPersons = new HashMap<>();
	
	public SDTWelcomeCameraDevice(WelcomeCamera pCamera, int detectionThreshold) {
		super(computeId(pCamera.getName()), 
				pCamera.getId(), Activator.NETATMO_DOMAIN);
		camera = pCamera;
		this.detectionThreshold = (detectionThreshold <= 0) ? DEFAULT_DETECTION_THRESHOLD 
				: (detectionThreshold);

		// sdt properties
		setDeviceManufacturer("Netatmo");
		setDeviceName(pCamera.getName());
		setDeviceModelName(camera.getType());
		setProtocol(Activator.PROTOCOL);
		try {
			setPresentationURL(new URL("https://www.netatmo.com/product/security/welcome"));
		} catch (MalformedURLException ignored) {
		}

		// create properties for vpn
		addProperty(new Property("vpnUrl", camera.getVpnUrl()));

		setPersonSensor(new PersonSensor("personSensor_" + getId(), Activator.NETATMO_DOMAIN,
			new ArrayDataPoint<String>("detectedPersons") {
				@Override
				protected List<String> doGetValue() throws DataPointException {
					List<String> ret = new ArrayList<>();
					synchronized (detectedPersons) {
						for (DetectedPerson dp : detectedPersons.values()) {
							Activator.logger.info("check " + dp.getPersonName() + " date=" 
									+ new Date(dp.getTime()*1000l), SDTWelcomeCameraDevice.class);
							if (recentlyDetected(dp)) {
								ret.add(dp.getPersonName());
							}
						}
					}
					return ret;
				}
			}));
		
		setStreaming(new Streaming("streaming_" + getId(), Activator.NETATMO_DOMAIN, 
			new StringDataPoint("url") {
				@Override
				protected String doGetValue() throws DataPointException {
					return camera.getVpnUrl() + "/live/index_local.m3u8";
				}
			}, 
			new StringDataPoint("login") {
				@Override
				protected String doGetValue() throws DataPointException {
					return "";
				}
			}, 
			new StringDataPoint("password") {
				@Override
				protected String doGetValue() throws DataPointException {
					return "";
				}
			}, 
			new StringDataPoint("format") {
				@Override
				protected String doGetValue() throws DataPointException {
					return "HLS";
				}
			}));
	}

	public void register(BundleContext context) {
		if (serviceRegistrations == null) {
			serviceRegistrations = Utils.register(this, context);
		}
	}

	public void unregister() {
		if (serviceRegistrations != null) {
			for (ServiceRegistration sr : serviceRegistrations) {
				sr.unregister();
			}
		}
		// remove modules and device object from Domain
		for(String moduleName : getModuleNames()) {
			Activator.NETATMO_DOMAIN.removeModule(moduleName);
		}
		Activator.NETATMO_DOMAIN.removeDevice(getName());
	}

	public void notifyPerson(String personId, String personName, Long time) {
		synchronized (detectedPersons) {
			DetectedPerson dp = detectedPersons.get(personId);
			if (dp == null) {
				dp = new DetectedPerson(personId, personName);
				detectedPersons.put(personId, dp);
			}
			dp.setTime(time);
		}
	}
	
	private boolean recentlyDetected(final DetectedPerson dp) {
		long diff = System.currentTimeMillis() - dp.getTime() * 1000;
		boolean ret = diff < detectionThreshold;
		Activator.logger.info(dp.getPersonName() 
			+ (ret ? " detected recently " : "detected too long ago ") + diff);
		LOGGER.info(dp.getPersonName() 
				+ (ret ? " detected recently " : "detected too long ago ") + diff + " " +  detectionThreshold);
		return ret;
	}

	private static String computeId(String pId) {
		return pId.replaceAll("[^a-zA-Z0-9]+", "_").trim();
	}

}