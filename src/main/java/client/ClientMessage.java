package client;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientMessage implements Serializable {
	
	public String messageType;
	public boolean isMessageTrue;
	public String parameters[] = new String[10];
	private final ArrayList<Object> sendingObjects = new ArrayList<Object>();

	public ClientMessage(String type) {
		for (int i = 0; i < 10; i++) {
			parameters[i] = new String();
		}
		this.messageType = type;
	}

	public void setSendingObjects(ArrayList<Object> finalResult) {
		sendingObjects.addAll(finalResult);
	}

	public ArrayList<Object> getSendingObjects() {
		return sendingObjects;
	}
}
