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

package fr.inra.maiage.bibliome.util.qsync.cli;

public class IntervalBuilder {
	private static final int REASONABLE_INTARVAL = 10;
	
	private int interval = 60;
	private boolean forceInterval = false;
	
	public IntervalBuilder() {
		super();
	}

	public void setInterval(int interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("zero or negative interval");
		}
		this.interval = interval;
	}

	public void setForceInterval() {
		this.forceInterval = true;
	}
	
	public int getInterval() {
		if (interval < REASONABLE_INTARVAL && !forceInterval) {
			throw new IllegalArgumentException("the specified interval is not sensible");
		}
		return interval;
	}
}
