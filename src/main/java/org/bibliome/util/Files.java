/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.bibliome.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Miscellanous file utilities.
 * 
 * @author rbossy
 */
public class Files {

    /**
     * Returns the last modification time of a file or zero if f is null.
     * @param f the file
     * @return the last modification time of a file or zero if f is null
     */
    public static final long timestamp(File f) {
        if (f == null)
            return 0L;
        return f.lastModified();
    }

    /**
     * Returns the last modification times of the specified files.
     * @param f the files
     * @return the last modification times of the specified files
     */
    public static final long[] timestamps(File... f) {
        if (f == null)
            return null;
        long[] result = new long[f.length];
        for (int i = 0; i < f.length; i++)
            result[i] = timestamp(f[i]);
        return result;
    }

    /**
     * Returns the last modification time of the files in the specified file mapping.
     * @param f the file mapping
     * @return the last modification time of the files in the specified file mapping
     */
    public static final <F extends File> Map<String,Long> timestamps(Map<String,F> f) {
        if (f == null)
            return null;
        Map<String,Long> result = new HashMap<String,Long>();
        for (Map.Entry<String,F> e : f.entrySet())
            result.put(e.getKey(), timestamp(e.getValue()));
        return result;
    }

    /**
     * Returns either if a file has been changed since the specified time.
     * @param timestamp
     * @param f
     * @return either if a file has been changed since the specified time
     */
    public static final <F extends File> boolean hasChanged(long timestamp, F f) {
        return timestamp(f) != timestamp;
    }

    /**
     * Returns either if at least one file has been changed since the specified time.
     * @param timestamps
     * @param f
     * @return either if at least one file has been changed since the specified time
     */
    public static final <F extends File> boolean hasChanged(long[] timestamps, F[] f) {
        if (timestamps == null)
            return true;
        if (timestamps.length != f.length)
            return true;
        for (int i = 0; i < timestamps.length; i++)
            if (hasChanged(timestamps[i], f[i]))
                return true;
        return false;
    }

    /**
     * Returns either if at least one file has been changed since the specified time.
     * @param timestamps
     * @param f
     * @return either if at least one file has been changed since the specified time
     */
    public static final <F extends File> boolean hasChanged(Map<String,Long> timestamps, Map<String,F> f) {
        if (timestamps == null)
            return true;
        if (timestamps.size() != f.size())
            return true;
        for (Map.Entry<String,Long> e : timestamps.entrySet()) {
            String k = e.getKey();
            if (!f.containsKey(k))
                return true;
            if (hasChanged(e.getValue(), f.get(k)))
                return true;
        }
        return false;
    }

    /**
     * Copy the specified stream into the specified file.
     * @param source
     * @param dest
     * @param buffer
     * @param close
     * @throws IOException
     */
    public static final void copy(InputStream source, File dest, byte[] buffer, boolean close) throws IOException {
    	OutputStream os = new FileOutputStream(dest);
    	try {
        	int len;
    		while ((len = source.read(buffer)) >= 0)
    			os.write(buffer, 0, len);
    	}
    	finally {
    		os.close();
    		if (close)
    			source.close();
    	}
    }
    
    /**
     * Copy the specified stream into the specified file.
     * @param source
     * @param dest
     * @param bufferSize
     * @param close
     * @throws IOException
     */
    public static final void copy(InputStream source, File dest, int bufferSize, boolean close) throws IOException {
    	copy(source, dest, new byte[bufferSize], close);
    }
    
    /**
     * Copies the contents of the specified source file to the specified destination path.
     * @param source
     * @param dest
     * @param buffer
     * @throws IOException
     */
    public static final void copy(File source, File dest, byte[] buffer) throws IOException {
    	if (dest.exists() && dest.isDirectory())
    		dest = new File(dest, source.getName());
    	copy(new FileInputStream(source), dest, buffer, true);
    }

    /**
     * Copies the contents of the specified source file to the specified destination path.
     * @param source
     * @param dest
     * @throws IOException
     */
    public static final void copy(File source, File dest, int bufferSize) throws IOException {
    	copy(source, dest, new byte[bufferSize]);
    }
    
    /**
     * Copies the contents of the specified source file to the specified destination path.
     * @param source
     * @param dest
     * @throws IOException
     */
    public static final void copy(File source, File dest) throws IOException {
    	copy(source, dest, 1024);
    }

    /**
     * Stores the specified file into the specified zip stream.
     * @param zip
     * @param relativeTo
     * @param file
     * @param buffer
     * @throws IOException
     */
    public static final void zip(ZipOutputStream zip, File relativeTo, File file, byte[] buffer) throws IOException {
    	if (!file.exists())
    		return;
    	if (!file.canRead())
    		return;
    	if (file.isDirectory()) {
    		for (File child : file.listFiles())
    			zip(zip, relativeTo, child, buffer);
    		return;
    	}
    	String entryName = relativeTo == null ? file.getPath() : relativeTo.toURI().relativize(file.toURI()).getPath();
    	zip.putNextEntry(new ZipEntry(entryName));
    	InputStream in = new FileInputStream(file);
    	int len;
    	while ((len = in.read(buffer)) >= 0)
    		zip.write(buffer, 0, len);
    	zip.closeEntry();
    	in.close();
    }
    
    /**
     * Stores the specified file into the specified zip archive file.
     * @param zipFile
     * @param relativeTo
     * @param file
     * @param buffer
     * @throws IOException
     */
    public static final void zip(File zipFile, File relativeTo, File file, byte[] buffer) throws IOException {
    	ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFile));
    	zip(zip, relativeTo, file, buffer);
    	zip.close();
    }
    
    /**
     * Stores the specified file into the specified zip archive file.
     * @param zipFile
     * @param relativeTo
     * @param file
     * @throws IOException
     */
    public static final void zip(File zipFile, File relativeTo, File file, int bufferSize) throws IOException {
    	zip(zipFile, relativeTo, file, new byte[bufferSize]);
    }
    
    /**
     * Stores the specified file into the specified zip archive file.
     * @param zipFile
     * @param relativeTo
     * @param file
     * @throws IOException
     */
    public static final void zip(File zipFile, File relativeTo, File file) throws IOException {
    	zip(zipFile, relativeTo, file, 1024);
    }
    
    public static final boolean recDelete(File file) {
    	if (file.isDirectory()) {
    		for (File f : file.listFiles()) {
    			if (!recDelete(f)) {
    				throw new RuntimeException("could not delete: " + f);
    			}
    		}
    	}
    	return file.delete();
    }
    
    public static final boolean deleteDir(File file) {
    	if (file.isDirectory()) {
    		for (File f : file.listFiles()) {
    			if (!deleteDir(f)) {
    				return false;
    			}
    		}
    	}
    	return file.delete();
    }
}
