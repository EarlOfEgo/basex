package org.basex.android;

import java.io.IOException;
import java.util.List;

import org.basex.core.BaseXException;

public interface BaseXAndroid {
	
	public enum index {
		TEXT,
		ATTRIBUTE,
		FULLTEXT
	}
	
	public enum rights {
		NONE,
		READ,
		WRITE,
		CREATE,
		ADMIN
	}
	
	public void alterDatabase(String oldName, String newName) throws IOException;
	public void alterUser(String name, String password) throws IOException;
	
	public void openDatabase(String name) throws IOException;
	public void createDatabase(String name) throws IOException;
	public void openOrCreateDatabase(String name) throws IOException;
	public void copyDatabase(String oldDb, String newDb) throws IOException;
	
	public void createBackup(String name) throws IOException;
	public void createEvent(String name) throws IOException;
	public void createIndex(index index) throws IOException;
	public void createUser(String username, String password) throws IOException;
	public void grant(rights right, String database, String user) throws IOException;
	public void flush() throws IOException;
	
	public void dropBackup(String name) throws IOException;
	public void dropDatabase(String name) throws IOException;
	public void dropEvent(String name) throws IOException;
	public void dropIndex(index index) throws IOException;
	public void dropUser(String name, String database) throws IOException;
	
	public void cs(String query) throws IOException;
	public void store(String path, String input) throws IOException;
	
	public void optimize(boolean all) throws IOException;
	
	public void closeDatabase() throws IOException;
	public void deleteDatabase(String name) throws IOException;
	public void addSource(String path, String file_name) throws BaseXException, IOException;
	public String executeXQuery(String query) throws BaseXException, IOException;
	public String find(String name) throws BaseXException, IOException;
	public List<String> getDatabases() throws IOException;
	public List<String> getUsers() throws IOException;
	public List<String> getEvents() throws IOException;
	public List<String> getSessions() throws IOException;
	public List<String> getBackups() throws IOException;
	public String listDB(String name) throws BaseXException, IOException;
	public String listDBs() throws BaseXException, IOException;

}
