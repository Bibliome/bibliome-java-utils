package org.bibliome.util.marshall;

/**
 * Marshall reference implementation.
 * @author rbossy
 *
 * @param <T>
 */
public class MReferenceImpl<T> implements MReference<T> {
	private final Unmarshaller<T> unmarshaller;
	private final int position;
	private T value;
	
	/**
	 * Creates a new marshall reference at the specified position using the specified unmarshaller for dereferencing.
	 * Dereferencing is lazy.
	 * @param position
	 * @param unmarshaller
	 */
	public MReferenceImpl(int position, Unmarshaller<T> unmarshaller) {
		super();
		this.position = position;
		this.unmarshaller = unmarshaller;
	}

	@Override
	public T get() {
		if (value == null)
			value = unmarshaller.read(position);
		return value;
	}

	@Override
	public int getPosition() {
		return position;
	}
}
