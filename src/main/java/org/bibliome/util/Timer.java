package org.bibliome.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Timers are used to time processes.
 * @author rbossy
 *
 * @param <T>
 */
public class Timer<T extends Enum<T>> {
	private final String name;
	private final T category;
	private final Timer<T> parent;
	private long time = 0;
	private long start = -1;
	private int startCount = 0;
	private final Map<String,Timer<T>> children = new HashMap<String,Timer<T>>();

	private Timer(String name, T category, Timer<T> parent) {
		super();
		if (name == null)
			throw new NullPointerException();
		if (category == null)
			throw new NullPointerException();
		this.name = name;
		this.category = category;
		this.parent = parent;
	}
	
	/**
	 * Creates a timer with the specified name for the specified category.
	 * @param name
	 * @param category
	 */
	public Timer(String name, T category) {
		this(name, category, null);
	}
	
	/**
	 * Creates a new child timer with the specified name and for the specified category.
	 * @param name
	 * @param category
	 * @throws IllegalArgumentException if there is already a child timer with the specified name
	 */
	public Timer<T> newChild(String name, T category) {
		if (children.containsKey(name))
			throw new IllegalArgumentException();
		Timer<T> result = new Timer<T>(name, category, this);
		children.put(name, result);
		return result;
	}

	/**
	 * Returns either if this timer is stopped.
	 */
	public boolean isStopped() {
		return start < 0;
	}
	
	/**
	 * Returns this timer name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns this timer category.
	 */
	public T getCategory() {
		return category;
	}
	
	/**
	 * Returns this timer children timers.
	 */
	public Collection<Timer<T>> getChildren() {
		return Collections.unmodifiableCollection(children.values());
	}

	/**
	 * Starts this timer.
	 * @throws IllegalStateException if this timer is already started, or if the parent of this timer is not started.
	 */
	public void start() {
		if (!isStopped())
			throw new IllegalStateException("timer " + name + " is already started");
		if ((parent != null) && parent.isStopped())
			throw new IllegalStateException("could not start timer " + name + ", parent timer " + parent.name + " is stopped");
		start = System.nanoTime();
		startCount++;
	}

	/**
	 * Stops this timer.
	 * @throws IllegalStateException if this timer is not started, or if one of its children is not stopped.
	 */
	public void stop() {
		if (isStopped())
			throw new IllegalStateException();
		for (Timer<T> t : children.values())
			if (!t.isStopped())
				throw new IllegalStateException("could not stop timer " + name + ", child timer " + t.name + " is still started");
		time += System.nanoTime() - start;
		start = -1;
	}

	/**
	 * Returns the timer following the specified path relative to this timer.
	 * The path is a dot separated list of names.
	 * @param path
	 * @return the timer following the specified path relative to this timer, if the specified path does not lead to a timer.
	 */
	public Timer<T> getTimer(String path) {
		Timer<T> timer = this;
		while (true) {
			int dot = path.indexOf('.');
			if (dot < 0)
				return timer.children.get(path);
			timer = timer.children.get(path.substring(0, dot));
			if (timer == null)
				return null;
			path = path.substring(dot + 1);
		}
	}

	/**
	 * Returns the child timer with the specified name.
	 * @param name
	 * @return the child timer with the specified name, null if there is no child timer with the specified name
	 */
	public Timer<T> getChild(String name) {
		return children.get(name);
	}

	/**
	 * If this timer is started, stop it but does not record the time.
	 */
	public void abort() {
		if (isStopped())
			return;
		startCount--;
		start = -1;
	}

	/**
	 * Returns the total time recorded by this timer.
	 * If this timer is started the current recording is not included in the result.
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Returns the number of times this timer has been started.
	 */
	public int getStartCount() {
		return startCount;
	}
	
	/**
	 * Returns a dot separated path to this timer from the root timer.
	 * The root timer is the ancestor of this timer that has no parent timer.
	 */
	public String getPath() {
		List<String> path = new ArrayList<String>();
		for (Timer<T> t = this; t != null; t = t.parent)
			path.add(t.name);
		Collections.reverse(path);
		return Strings.join(path, '.');
	}
	
	private void allTimers(Collection<Timer<T>> timers) {
		timers.add(this);
		for (Timer<T> t : children.values())
			t.allTimers(timers);
	}

	/**
	 * Returns all descendant timers including this one.
	 */
	public Collection<Timer<T>> allTimers() {
		Collection<Timer<T>> result = new ArrayList<Timer<T>>();
		allTimers(result);
		return result;
	}

	/**
	 * Returns the sum of the time recorded by all this timer children.
	 */
	public long getChildrenTime() {
		long result = 0;
		for (Timer<T> t : children.values())
			result += t.getTime();
		return result;
	}
}
