package de.gigagagagigo.sagma.packet.mapper;

import java.io.IOException;
import java.util.Objects;

public class AbstractMapper<T> implements Mapper<T> {

	@FunctionalInterface
	public static interface Writer<T> {
		void write(MappedOutputStream out, T data) throws IOException;
	}

	@FunctionalInterface
	public static interface Reader<T> {
		T read(MappedInputStream in) throws IOException;
	}

	private final Writer<T> writer;
	private final Reader<T> reader;

	public AbstractMapper(Writer<T> writer, Reader<T> reader) {
		this.writer = Objects.requireNonNull(writer, "writer must not be null.");
		this.reader = Objects.requireNonNull(reader, "reader must not be null.");
	}

	@Override
	public void write(MappedOutputStream out, T data) throws IOException {
		out.writeBoolean(data == null);
		if (data != null)
			writer.write(out, data);
	}

	@Override
	public T read(MappedInputStream in) throws IOException {
		boolean isNull = in.readBoolean();
		return isNull ? null : reader.read(in);
	}

}
