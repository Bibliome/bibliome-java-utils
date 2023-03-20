package fr.inra.maiage.bibliome.util.marshall;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DataBuffer {
	public static final int DEFAULT_BUFFER_SIZE = 1024;
	
	private final FileChannel channel;
	private final ByteBuffer byteBuffer;
	private long nextRefillPosition;
	
	public DataBuffer(FileChannel channel, ByteBuffer byteBuffer, long nextRefillPosition) {
		super();
		this.channel = channel;
		this.byteBuffer = byteBuffer;
		this.nextRefillPosition = nextRefillPosition;
		byteBuffer.clear();
		refill();
	}
	
	public DataBuffer(FileChannel channel, int bufSz, long nextRefillPosition) {
		this(channel, ByteBuffer.allocate(bufSz), nextRefillPosition);
	}
	
	public DataBuffer(FileChannel channel, long nextRefillPosition) {
		this(channel, DEFAULT_BUFFER_SIZE, nextRefillPosition);
	}

	public DataBuffer(FileChannel channel, ByteBuffer byteBuffer) {
		this(channel, byteBuffer, 0);
	}

	public DataBuffer(FileChannel channel, int bufSz) {
		this(channel, bufSz, 0);
	}

	private void ensureRemaining(int nBytes) {
		if (byteBuffer.remaining() < nBytes) {
			byteBuffer.compact();
			refill();
		}
	}
	
	private void refill() {
		try {
			nextRefillPosition += channel.read(byteBuffer, nextRefillPosition);
			byteBuffer.rewind();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public byte getByte() {
		ensureRemaining(1);
		return byteBuffer.get();
	}
	
	public char getChar() {
		ensureRemaining(2);
		return byteBuffer.getChar();
	}
	
	public short getShort() {
		ensureRemaining(2);
		return byteBuffer.getShort();
	}
	
	public int getInt() {
		ensureRemaining(4);
		return byteBuffer.getInt();
	}
	
	public long getLong() {
		ensureRemaining(8);
		return byteBuffer.getLong();
	}
	
	public float getFloat() {
		ensureRemaining(4);
		return byteBuffer.getFloat();
	}
	
	public double getDouble() {
		ensureRemaining(8);
		return byteBuffer.getDouble();
	}
}
