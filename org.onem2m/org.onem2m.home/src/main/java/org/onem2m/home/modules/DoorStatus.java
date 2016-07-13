package org.onem2m.home.modules;

import org.onem2m.home.types.DoorState;
import org.onem2m.home.types.ModuleType;
import org.onem2m.sdt.Domain;
import org.onem2m.sdt.Module;
import org.onem2m.sdt.datapoints.BooleanDataPoint;
import org.onem2m.sdt.datapoints.StringDataPoint;
import org.onem2m.sdt.impl.AccessException;
import org.onem2m.sdt.impl.DataPointException;

public class DoorStatus extends Module {
	
	private DoorState doorState;
	
	private StringDataPoint openDuration;
	
	private BooleanDataPoint openAlarm;
	
	
	public DoorStatus(final String name, final Domain domain, DoorState state) {
		super(name, domain, ModuleType.doorStatus.getDefinition());

		this.doorState = state;
		this.doorState.setWritable(false);
		this.doorState.setDoc("\"True\" indicates that door is closed, \"False\"indicates the door is open.");
		addDataPoint(this.doorState);
	}

	public int getDoorState() throws DataPointException, AccessException {
		return doorState.getValue();
	}

	public void setOpenDuration(StringDataPoint dp) {
		this.openDuration = dp;
		this.openDuration.setOptional(true);
		this.openDuration.setWritable(false);
		this.openDuration.setDoc("The time duration the door has been open. The type of openDuration is an ISO 8601 Time encoded string.");
		addDataPoint(openDuration);
	}

	public String getOpenDuration() throws DataPointException, AccessException {
		if (openDuration == null)
			throw new DataPointException("Not implemented");
		return openDuration.getValue();
	}

	public void setOpenAlarm(BooleanDataPoint dp) {
		this.openAlarm = dp;
		this.openAlarm.setOptional(true);
		this.openAlarm.setWritable(true);
		this.openAlarm.setDoc("The state of the door open alarm. True indicates that the open alarm is active. False indicates that open alarm is not active.");
		addDataPoint(openAlarm);
	}

	public boolean getOpenAlarm() throws DataPointException, AccessException {
		if (openAlarm == null)
			throw new DataPointException("Not implemented");
		return openAlarm.getValue();
	}

	public void setOpenAlarm(boolean b) throws DataPointException, AccessException {
		if (openAlarm == null)
			throw new DataPointException("Not implemented");
		this.openAlarm.setValue(b);
	}

}