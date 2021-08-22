package com.atusoft.infrastructure;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import lombok.Data;

public class RestApi {

	public enum Method {
		GET, POST, PUT, DELETE
	}
	
	@Data
	public static class ApiEntry {
		
		Method method;
		String path;
		String commandName;
		
		
		public ApiEntry(Method method,String path,String commandName) {
			this.method=method;
			this.path=path;
			this.commandName=commandName;
		}
		//TODO FunctionalInterface 
	}
	
	List<ApiEntry> allEntries;
	
	
	public RestApi(ApiEntry[] entries) {
		this.allEntries=Arrays.asList(entries);
	}
	
	public Iterable<ApiEntry> apiEntries() {
		return new Iterable<ApiEntry>()
		{
			public Iterator<ApiEntry> iterator() {
				return allEntries.iterator();
			}
		};
	}
	
	public Iterator<ApiEntry> getApiEntries() {
		return this.allEntries.iterator();
	}
}
