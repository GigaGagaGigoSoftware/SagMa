package de.gigagagagigo.sagma.packet.mapper;

final class BoxedPrimitiveMappers {

	public static Mapper<Boolean> booleanMapper() {
		return new AbstractMapper<>(MappedOutputStream::writeBoolean, MappedInputStream::readBoolean);
	}

	public static Mapper<Byte> byteMapper() {
		return new AbstractMapper<>(MappedOutputStream::writeByte, MappedInputStream::readByte);
	}

	public static Mapper<Short> shortMapper() {
		return new AbstractMapper<>(MappedOutputStream::writeShort, MappedInputStream::readShort);
	}

	public static Mapper<Integer> integerMapper() {
		return new AbstractMapper<>(MappedOutputStream::writeInt, MappedInputStream::readInt);
	}

	public static Mapper<Long> longMapper() {
		return new AbstractMapper<>(MappedOutputStream::writeLong, MappedInputStream::readLong);
	}

	public static Mapper<Character> characterMapper() {
		return new AbstractMapper<>(MappedOutputStream::writeChar, MappedInputStream::readChar);
	}

	public static Mapper<Float> floatMapper() {
		return new AbstractMapper<>(MappedOutputStream::writeFloat, MappedInputStream::readFloat);
	}

	public static Mapper<Double> doubleMapper() {
		return new AbstractMapper<>(MappedOutputStream::writeDouble, MappedInputStream::readDouble);
	}

	// prevent instantiation
	private BoxedPrimitiveMappers() {
		throw new UnsupportedOperationException();
	}

}
