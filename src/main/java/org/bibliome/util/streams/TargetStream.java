package org.bibliome.util.streams;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;

import org.bibliome.util.Checkable;

public interface TargetStream extends Checkable {
	String getName();
	OutputStream getOutputStream() throws IOException;
	PrintStream getPrintStream() throws IOException;
	Writer getWriter() throws IOException;
	BufferedWriter getBufferedWriter() throws IOException;
}
