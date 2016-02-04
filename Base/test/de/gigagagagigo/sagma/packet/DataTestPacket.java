package de.gigagagagigo.sagma.packet;

public class DataTestPacket implements Packet {
	public boolean theBoolean;
	public byte theByte;
	public short theShort;
	public int theInt;
	public long theLong;
	public char theChar;
	public float theFloat;
	public double theDouble;
	public Boolean noBoxedBoolean;
	public Boolean someBoxedBoolean;
	public String noString;
	public String someString;

	@Override
	public String toString() {
		return "[DataTestPacket]{ boolean: " + theBoolean
			+ ", byte: " + theByte
			+ ", short: " + theShort
			+ ", int: " + theInt
			+ ", long: " + theLong
			+ ", char: " + theChar
			+ ", float: " + theFloat
			+ ", double: " + theDouble
			+ ", no boxed boolean: " + noBoxedBoolean
			+ ", some boxed boolean: " + someBoxedBoolean
			+ ", no string: " + noString
			+ ", some string: " + someString
			+ " }";
	}

	// BEGIN GENERATED CODE

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((noBoxedBoolean == null) ? 0 : noBoxedBoolean.hashCode());
		result = prime * result + ((noString == null) ? 0 : noString.hashCode());
		result = prime * result + ((someBoxedBoolean == null) ? 0 : someBoxedBoolean.hashCode());
		result = prime * result + ((someString == null) ? 0 : someString.hashCode());
		result = prime * result + (theBoolean ? 1231 : 1237);
		result = prime * result + theByte;
		result = prime * result + theChar;
		long temp = Double.doubleToLongBits(theDouble);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Float.floatToIntBits(theFloat);
		result = prime * result + theInt;
		result = prime * result + (int) (theLong ^ (theLong >>> 32));
		result = prime * result + theShort;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataTestPacket other = (DataTestPacket) obj;
		if (noBoxedBoolean == null) {
			if (other.noBoxedBoolean != null)
				return false;
		} else if (!noBoxedBoolean.equals(other.noBoxedBoolean))
			return false;
		if (noString == null) {
			if (other.noString != null)
				return false;
		} else if (!noString.equals(other.noString))
			return false;
		if (someBoxedBoolean == null) {
			if (other.someBoxedBoolean != null)
				return false;
		} else if (!someBoxedBoolean.equals(other.someBoxedBoolean))
			return false;
		if (someString == null) {
			if (other.someString != null)
				return false;
		} else if (!someString.equals(other.someString))
			return false;
		if (theBoolean != other.theBoolean)
			return false;
		if (theByte != other.theByte)
			return false;
		if (theChar != other.theChar)
			return false;
		if (Double.doubleToLongBits(theDouble) != Double.doubleToLongBits(other.theDouble))
			return false;
		if (Float.floatToIntBits(theFloat) != Float.floatToIntBits(other.theFloat))
			return false;
		if (theInt != other.theInt)
			return false;
		if (theLong != other.theLong)
			return false;
		if (theShort != other.theShort)
			return false;
		return true;
	}

	// END GENERATED CODE
}
