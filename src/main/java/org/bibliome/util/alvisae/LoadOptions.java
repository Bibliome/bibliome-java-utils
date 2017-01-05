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

package org.bibliome.util.alvisae;

import java.util.Collection;
import java.util.HashSet;

public class LoadOptions {
	private boolean loadContents = true;
	private Collection<Integer> docIds;
	private Collection<String> docDescriptions;
	private Collection<String> docExternalIds;
	
	private boolean head = true;
	private Integer taskId;
	private String taskName;
	private Collection<Integer> userIds;
	private Collection<String> userNames;
	
	private boolean loadTextBound = true;
	private boolean loadGroups = true;
	private boolean loadRelations = true;
	private boolean loadUnmatched = true;
	
	private boolean loadDependencies = false;
	private boolean adjudicate = false;
	
	public LoadOptions() {
		super();
	}

	public boolean isLoadContents() {
		return loadContents;
	}

	public Collection<Integer> getDocIds() {
		return docIds;
	}
	
	public boolean hasDocIds() {
		return docIds != null;
	}

	public Collection<String> getDocDescriptions() {
		return docDescriptions;
	}
	
	public boolean hasDocDescriptions() {
		return docDescriptions != null;
	}

	public Collection<String> getDocExternalIds() {
		return docExternalIds;
	}
	
	public boolean hasDocExternalIds() {
		return docExternalIds != null;
	}

	public boolean isHead() {
		return head;
	}

	public Integer getTaskId() {
		return taskId;
	}
	
	public boolean hasTaskId() {
		return taskId != null;
	}

	public String getTaskName() {
		return taskName;
	}
	
	public boolean hasTaskName() {
		return taskName != null;
	}

	public Collection<Integer> getUserIds() {
		return userIds;
	}
	
	public boolean hasUserIds() {
		return userIds != null;
	}

	public Collection<String> getUserNames() {
		return userNames;
	}
	
	public boolean hasUserNames() {
		return userNames != null;
	}

	public boolean isLoadTextBound() {
		return loadTextBound;
	}

	public boolean isLoadGroups() {
		return loadGroups;
	}

	public boolean isLoadRelations() {
		return loadRelations;
	}

	public boolean isLoadUnmatched() {
		return loadUnmatched;
	}

	public boolean isLoadDependencies() {
		return loadDependencies;
	}

	public boolean isAdjudicate() {
		return adjudicate;
	}

	public void setAdjudicate(boolean adjudicate) {
		this.adjudicate = adjudicate;
	}

	public void setLoadDependencies(boolean loadDependencies) {
		this.loadDependencies = loadDependencies;
	}

	public void setLoadUnmatched(boolean loadUnmatched) {
		this.loadUnmatched = loadUnmatched;
	}

	public void setLoadTextBound(boolean loadTextBound) {
		this.loadTextBound = loadTextBound;
	}

	public void setLoadGroups(boolean loadGroups) {
		this.loadGroups = loadGroups;
	}

	public void setLoadRelations(boolean loadRelations) {
		this.loadRelations = loadRelations;
	}

	public void setLoadContents(boolean loadContents) {
		this.loadContents = loadContents;
	}

	public void setDocIds(Collection<Integer> docIds) {
		this.docIds = docIds;
	}
	
	public void addDocId(int id) {
		if (docIds == null)
			docIds = new HashSet<Integer>();
		docIds.add(id);
	}

	public void setDocDescriptions(Collection<String> docDescriptions) {
		this.docDescriptions = docDescriptions;
	}
	
	public void addDocDescription(String desc) {
		if (docDescriptions == null)
			docDescriptions = new HashSet<String>();
		docDescriptions.add(desc);
	}

	public void setDocExternalIds(Collection<String> docExternalIds) {
		this.docExternalIds = docExternalIds;
	}
	
	public void addDocExternalId(String id) {
		if (docExternalIds == null)
			docExternalIds = new HashSet<String>();
		docExternalIds.add(id);
	}

	public void setHead(boolean head) {
		this.head = head;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setUserIds(Collection<Integer> userIds) {
		this.userIds = userIds;
	}
	
	public void addUserId(int userId) {
		if (userIds == null)
			userIds = new HashSet<Integer>();
		userIds.add(userId);
	}

	public void setUserNames(Collection<String> userNames) {
		this.userNames = userNames;
	}
	
	public void clearUserNames() {
		userNames.clear();
	}
	
	public void addUserName(String userName) {
		if (userNames == null)
			userNames = new HashSet<String>();
		userNames.add(userName);
	}
}
