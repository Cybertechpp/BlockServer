package net.blockserver.io.nbt;

import java.io.IOException;

public class StringTag extends NamedTag{
	private String v = null;
	public String getValue(){
		return v;
	}
	public void setValue(String v){
		this.v = v;
	}
	public void write(NBTWriter writer) throws IOException{
		writer.writeString(v, 2);
	}
	public void read(NBTReader reader) throws IOException{
		v = reader.readString(2);
	}
}
