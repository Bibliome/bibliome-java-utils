package org.bibliome.util.clio;

import java.io.File;

import org.bibliome.util.Strings;

/**
 * Standard converters automatically registered in a CLIOParser.
 * @author rbossy
 *
 */
enum StandardConverters implements CLIOConverter {
	/**
	 * Converter to int and Integer.
	 */
	INTEGER {
		@Override
		public Object convert(String arg) throws CLIOConversionException {
			try {
				return Integer.parseInt(arg);
			}
			catch (NumberFormatException nfe) {
				throw new CLIOConversionException(nfe);
			}
		}

		@Override
		public Class<?>[] targetTypes() {
			return new Class<?>[] { Integer.class, Integer.TYPE };
		}
	},
	
	/**
	 * Converter to long and Long.
	 */
	LONG {
		@Override
		public Object convert(String arg) throws CLIOConversionException {
			try {
				return Long.parseLong(arg);
			}
			catch (NumberFormatException nfe) {
				throw new CLIOConversionException(nfe);
			}
		}

		@Override
		public Class<?>[] targetTypes() {
			return new Class<?>[] { Long.class, Long.TYPE };
		}
	},
	
	/**
	 * Converter to char and Character.
	 */
	CHAR {
		@Override
		public Object convert(String arg) throws CLIOConversionException {
			try {
				return arg.charAt(0);
			}
			catch (IndexOutOfBoundsException ioobe) {
				throw new CLIOConversionException(ioobe);
			}
		}

		@Override
		public Class<?>[] targetTypes() {
			return new Class<?>[] { Character.class, Character.TYPE };
		}
	},
	
	/**
	 * Converts to byte and Byte.
	 */
	BYTE {
		@Override
		public Object convert(String arg) throws CLIOConversionException {
			try {
				return Byte.parseByte(arg);
			}
			catch (NumberFormatException nfe) {
				throw new CLIOConversionException(nfe);
			}
		}

		@Override
		public Class<?>[] targetTypes() {
			return new Class<?>[] { Byte.class, Byte.TYPE };
		}
	},
	
	/**
	 * Converts to short and Short.
	 */
	SHORT {
		@Override
		public Object convert(String arg) throws CLIOConversionException {
			try {
				return Short.parseShort(arg);
			}
			catch (NumberFormatException nfe) {
				throw new CLIOConversionException(nfe);
			}
		}

		@Override
		public Class<?>[] targetTypes() {
			return new Class<?>[] { Short.class, Short.TYPE };
		}
	},
	
	/**
	 * Converts to float and Float.
	 */
	FLOAT {
		@Override
		public Object convert(String arg) throws CLIOConversionException {
			try {
				return Float.parseFloat(arg);
			}
			catch (NumberFormatException nfe) {
				throw new CLIOConversionException(nfe);
			}
		}

		@Override
		public Class<?>[] targetTypes() {
			return new Class<?>[] { Float.class, Float.TYPE };
		}
	},
	
	/**
	 * Converts to double and Double.
	 */
	DOUBLE {
		@Override
		public Object convert(String arg) throws CLIOConversionException {
			try {
				return Double.parseDouble(arg);
			}
			catch (NumberFormatException nfe) {
				throw new CLIOConversionException(nfe);
			}
		}

		@Override
		public Class<?>[] targetTypes() {
			return new Class<?>[] { Double.class, Double.TYPE };
		}
	},
	
	/**
	 * Converts to boolean and Boolean.
	 */
	BOOLEAN {
		@Override
		public Object convert(String arg) throws CLIOConversionException {
			try {
				return Strings.getBoolean(arg);
			}
			catch (IllegalArgumentException iae) {
				throw new CLIOConversionException(iae);
			}
		}

		@Override
		public Class<?>[] targetTypes() {
			return new Class<?>[] { Boolean.class, Boolean.TYPE };
		}
	},
	
	/**
	 * Convrets to File.
	 */
	FILE {
		@Override
		public Object convert(String arg) {
			return new File(arg);
		}

		@Override
		public Class<?>[] targetTypes() {
			return new Class<?>[] { File.class };
		}
	},
	
	/**
	 * Converts to String.
	 */
	STRING {
		@Override
		public Object convert(String arg) {
			return arg;
		}

		@Override
		public Class<?>[] targetTypes() {
			return new Class<?>[] { String.class };
		}
	};
}
