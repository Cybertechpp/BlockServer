package net.blockserver.network;

import java.net.DatagramPacket;

public abstract class BasePacketHandler {
	
	public abstract void handlePacket(DatagramPacket packet);

}