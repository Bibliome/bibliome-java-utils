package fr.inra.maiage.bibliome.util.streams;

import java.io.File;
import java.io.FileFilter;

public enum AcceptAllFiles implements FileFilter {
	INSTANCE {
		@Override
		public boolean accept(File arg0) {
			return true;
		}
	};
}
