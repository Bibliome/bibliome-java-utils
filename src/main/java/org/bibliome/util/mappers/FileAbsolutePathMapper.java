package org.bibliome.util.mappers;

import java.io.File;

public class FileAbsolutePathMapper<F extends File> implements Mapper<F,String> {
	@Override
	public String map(F x) {
		return x.getAbsolutePath();
	}
}
