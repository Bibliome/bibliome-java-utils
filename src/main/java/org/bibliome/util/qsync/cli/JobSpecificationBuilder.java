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

package org.bibliome.util.qsync.cli;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.bibliome.util.qsync.JobPool;
import org.bibliome.util.qsync.JobSpecification;
import org.ggf.drmaa.DrmaaException;

public interface JobSpecificationBuilder {
	void parseOpt(String opt, Iterator<String> argIt) throws IOException;
	List<JobSpecification> getJobSpecification(JobPool jp) throws IOException, DrmaaException;
}
