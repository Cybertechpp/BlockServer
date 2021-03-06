package org.blockserver.net.bridge;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;

import org.blockserver.Server;
import org.blockserver.utils.Callable;

public class NonBlockUDPSocket extends Thread{
	private UDPBridge udp;
	private SocketAddress addr;
	private DatagramSocket socket;
	private final ArrayList<DatagramPacket> receivedPacketQueue = new ArrayList<>();
	private boolean running = true; // I forget to set this to default true every time and go into strange bugs!
	public NonBlockUDPSocket(UDPBridge udp, SocketAddress address){
		this.udp = udp;
		addr = address;
		start();
	}
	@Override
	public void run(){
		setName("UDPSocket");
		udp.getServer().getLogger().info("Binding UDP socket on %s...", addr.toString());
		try{
			socket = new DatagramSocket(null);
			socket.setBroadcast(true);
			socket.setSendBufferSize(1024 * 1024 * 8); // from PocketMine
			socket.setReceiveBufferSize(1024 * 1024); // from PocketMine
			try{
				socket.bind(addr);
			}
			catch(BindException e){
				getServer().getLogger().fatal("Unable to bind to %s!", addr.toString());
				throw new RuntimeException(e);
			}
			socket.setSoTimeout(0);
			getServer().addShutdownFunction(new Callable(this, "_stop"));
			while(running){
				byte[] buffer = new byte[1024 * 1024];
				DatagramPacket pk = new DatagramPacket(buffer, buffer.length);
				socket.receive(pk);
				pk.setData(Arrays.copyOf(buffer, pk.getLength()));
				synchronized(receivedPacketQueue){
					receivedPacketQueue.add(pk);
				}
			}
			socket.close();
		}
		catch(IOException | NoSuchMethodException e){
			e.printStackTrace();
		}
	}
	public DatagramPacket receive(){
		if(receivedPacketQueue.isEmpty()){
			return null;
		}
		synchronized(receivedPacketQueue){
			return receivedPacketQueue.remove(0);
		}
	}
	public boolean send(byte[] buffer, SocketAddress addr){
		try{
			socket.send(new DatagramPacket(buffer, buffer.length, addr));
			return true;
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	public Server getServer(){
		return udp.getServer();
	}
	public void _stop(){
		stop(false);
	}
	public void stop(boolean join){
		running = false;
		if(join){
			try{
				join();
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
}
