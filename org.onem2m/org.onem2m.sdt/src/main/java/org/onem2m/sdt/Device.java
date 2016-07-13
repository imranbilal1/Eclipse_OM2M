package org.onem2m.sdt;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.onem2m.sdt.utils.Logger;

public class Device extends Element {
	
	private Map<String, Module> modules;
	
	private Map<String, Property> properties;
	
	private Map<String, SubDevice> devices;

	private String definition;

	public Device(final String id, final Domain domain, final String definition) {
		super(definition + "__" + id);
		if (domain.getDevice(definition + "__" + id) != null)
			throw new IllegalArgumentException("Already a device with name " 
					+ definition + "/" + id + " in domain " + domain);
		this.definition = definition;
		modules = new HashMap<String, Module>();
		properties = new HashMap<String, Property>();
		devices = new HashMap<String, SubDevice>();
		domain.addDevice(this);
	}

	public String getId() {
		return getName();
	}

	public String getPid() {
		return getName().replaceAll("\\.", "_");
	}
	
	public String getDefinition() {
		return definition;
	}

	public Collection<String> getModuleNames() {
		return modules.keySet();
	}

	public Collection<Module> getModules() {
		return modules.values();
	}

	public Module getModule(final String name) {
		return modules.get(name);
	}

	public void addModule(final Module module) {
		if (modules.get(module.getName()) != null)
			throw new IllegalArgumentException("Already a module with name " + module.getName() 
					+ " on device " + getName());
		module.setOwner(this);
		this.modules.put(module.getName(), module);
	}

	public void removeModule(final String name) {
		this.modules.remove(name);
	}

	public Collection<String> getPropertyNames() {
		return properties.keySet();
	}

	public Collection<Property> getProperties() {
		return properties.values();
	}

	public Property getProperty(final String name) {
		return properties.get(name);
	}

	public void addProperty(final Property property) {
		this.properties.put(property.getName(), property);
	}

	public void removeProperty(final String name) {
		this.properties.remove(name);
	}

	public Collection<String> getSubDeviceNames() {
		return devices.keySet();
	}

	public Collection<SubDevice> getSubDevices() {
		return devices.values();
	}

	public SubDevice getSubDevice(final String name) {
		return devices.get(name);
	}
	
	public void addSubDevice(final SubDevice device) {
		if (devices.get(device.getName()) != null)
			throw new IllegalArgumentException("Already a subdevice with name " + device.getName() 
					+ " on device " + getName());
		this.devices.put(device.getName(), device);
	}

	public void removeSubDevice(final String name) {
		this.devices.remove(name);
	}

	@Override
	protected String prettyPrint(String t1) {
		String t2 = t1 + "\t";
		StringBuffer ret = new StringBuffer(t1).append("<Device id=\"")
				.append(getId()).append("\">");
		if (getDoc() != null) ret.append("\n").append(t2).append(getDoc());
		prettyPrint(ret, properties.values(), "Properties", t2);
		prettyPrint(ret, modules.values(), "Modules", t2);
		prettyPrint(ret, devices.values(), "SubDevices", t2);
		return ret.append("\n").append(t1).append("</Device>").toString();
	}
	
    protected void finalize() throws Throwable {
    	Logger.info("finalize " + this);
    	modules.clear();
    	devices.clear();
    	properties.clear();
    }
	
}