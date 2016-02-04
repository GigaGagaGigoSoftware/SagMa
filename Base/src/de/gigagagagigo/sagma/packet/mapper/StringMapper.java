package de.gigagagagigo.sagma.packet.mapper;

import java.io.IOException;

class StringMapper implements Mapper<String> {

	@Override
	public void write(MappedOutputStream out, String string) throws IOException {
		if (string == null) {
			out.writeInt(-1);
		} else {
			byte[] data = string.getBytes("UTF-8");
			out.writeInt(data.length);
			for (int i = 0; i < data.length; i++)
				out.writeByte(data[i]);
		}
	}

	@Override
	public String read(MappedInputStream in) throws IOException {
		int length = in.readInt();
		if (length < 0) {
			return null;
		} else {
			byte[] data = new byte[length];
			for (int i = 0; i < length; i++)
				data[i] = in.readByte();
			return new String(data, "UTF-8");
		}
	}

}
