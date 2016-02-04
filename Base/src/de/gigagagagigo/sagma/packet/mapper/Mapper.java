package de.gigagagagigo.sagma.packet.mapper;

import java.io.IOException;

public interface Mapper<T> {
	void write(MappedOutputStream out, T data) throws IOException;
	T read(MappedInputStream in) throws IOException;
}
