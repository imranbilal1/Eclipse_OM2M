package org.onem2m.home.devices;

import org.onem2m.home.modules.BinarySwitch;
import org.onem2m.home.modules.Clock;
import org.onem2m.home.modules.EnergyConsumption;
import org.onem2m.home.modules.EnergyGeneration;
import org.onem2m.home.modules.FaultDetection;
import org.onem2m.home.modules.RunMode;
import org.onem2m.home.types.DeviceType;
import org.onem2m.sdt.Domain;
import org.onem2m.sdt.Property;
import org.onem2m.sdt.types.SimpleType;

public class SmartElectricMeter extends GenericDevice {
	
	private BinarySwitch binarySwitch;
	private EnergyConsumption energyConsumption;
	private FaultDetection faultDetection;
	private RunMode runMode;
	private Clock clock;
	private EnergyGeneration generationMeter;
	
	private Property measuringScope;
	
	public SmartElectricMeter(final String id, final String serial, final Domain domain) {
		super(id, serial, DeviceType.deviceSmartElectricMeter, domain);
		
		measuringScope = new Property("propMeasuringScope");
		measuringScope.setType(SimpleType.String);
		measuringScope.setOptional(true);
		measuringScope.setDoc("Measuring scope of the meter (ex. Whole house, room, or device)");
		addProperty(measuringScope);
	}

	public void addModule(BinarySwitch binarySwitch) {
		this.binarySwitch = binarySwitch;
		super.addModule(binarySwitch);
	}

	public void addModule(EnergyConsumption energyConsumption) {
		this.energyConsumption = energyConsumption;
		super.addModule(energyConsumption);
	}

	public void addModule(FaultDetection faultDetection) {
		this.faultDetection = faultDetection;
		super.addModule(faultDetection);
	}

	public void addModule(RunMode runMode) {
		this.runMode = runMode;
		super.addModule(runMode);
	}

	public void addModule(Clock clock) {
		this.clock = clock;
		super.addModule(clock);
	}

	public void addModule(EnergyGeneration generationMeter) {
		this.generationMeter = generationMeter;
		super.addModule(generationMeter);
	}

	public EnergyConsumption getEnergyConsumption() {
		return energyConsumption;
	}

	public FaultDetection getFaultDetection() {
		return faultDetection;
	}

	public BinarySwitch getBinarySwitch() {
		return binarySwitch;
	}

	public RunMode getRunMode() {
		return runMode;
	}

	public Clock getClock() {
		return clock;
	}

	public EnergyGeneration getGenerationMeter() {
		return generationMeter;
	}

}