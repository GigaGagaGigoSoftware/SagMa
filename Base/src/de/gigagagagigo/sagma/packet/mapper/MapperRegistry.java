package de.gigagagagigo.sagma.packet.mapper;

import java.util.*;

import de.gigagagagigo.sagma.packet.PacketPart;

public final class MapperRegistry {

	private static final Map<Class<?>, Mapper<?>> registry = new HashMap<>();

	static {
		register(PacketPart.class, new PacketPartMapper());
		register(String.class, new StringMapper());
		register(Boolean.class, BoxedPrimitiveMappers.booleanMapper());
		register(Byte.class, BoxedPrimitiveMappers.byteMapper());
		register(Short.class, BoxedPrimitiveMappers.shortMapper());
		register(Integer.class, BoxedPrimitiveMappers.integerMapper());
		register(Long.class, BoxedPrimitiveMappers.longMapper());
		register(Character.class, BoxedPrimitiveMappers.characterMapper());
		register(Float.class, BoxedPrimitiveMappers.floatMapper());
		register(Double.class, BoxedPrimitiveMappers.doubleMapper());
	}

	public static <T> void register(Class<T> theClass, Mapper<T> theMapper) {
		Objects.requireNonNull(theClass, "theClass must not be null.");
		Objects.requireNonNull(theMapper, "theMapper must not be null.");
		registry.put(theClass, theMapper);
	}

	@SuppressWarnings("unchecked")
	public static <T> Mapper<T> getMapper(Class<T> theClass) {
		Objects.requireNonNull(theClass, "theClass must not be null.");
		if (!registry.containsKey(theClass))
			throw new IllegalArgumentException("unknown type: " + theClass.getName());
		return (Mapper<T>) registry.get(theClass);
	}

	// prevent instantiation
	private MapperRegistry() {
		throw new UnsupportedOperationException();
	}

}
