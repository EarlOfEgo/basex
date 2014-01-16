package org.basex.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Add;
import org.basex.core.cmd.AlterDB;
import org.basex.core.cmd.AlterUser;
import org.basex.core.cmd.Check;
import org.basex.core.cmd.Copy;
import org.basex.core.cmd.CreateBackup;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.CreateEvent;
import org.basex.core.cmd.CreateIndex;
import org.basex.core.cmd.CreateUser;
import org.basex.core.cmd.Cs;
import org.basex.core.cmd.DropBackup;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.DropEvent;
import org.basex.core.cmd.DropIndex;
import org.basex.core.cmd.DropUser;
import org.basex.core.cmd.Find;
import org.basex.core.cmd.Flush;
import org.basex.core.cmd.Grant;
import org.basex.core.cmd.Open;
import org.basex.core.cmd.Optimize;
import org.basex.core.cmd.OptimizeAll;
import org.basex.core.cmd.ShowBackups;
import org.basex.core.cmd.ShowEvents;
import org.basex.core.cmd.ShowSessions;
import org.basex.core.cmd.ShowUsers;
import org.basex.core.cmd.Store;
import org.basex.core.cmd.XQuery;
import org.basex.util.list.StringList;

import android.util.Log;

/**
 * Created by stephan on 6/13/13.
 */
public class BaseXDatabase implements BaseXAndroid{

	private Context basexContext = null;

	private static volatile BaseXDatabase instance = null;

	public static BaseXDatabase getBaseXDatabaseInstance(String data_dir) {
		if (instance == null) {
			synchronized (BaseXDatabase.class) {
				if (instance == null) {
					instance = new BaseXDatabase(data_dir);
					
				}
			}
		}
		return instance;
	}

	private BaseXDatabase(String data_dir) {
		basexContext = new Context(data_dir);
	}

	@Override
	public void openDatabase(String name) throws IOException {
		if(basexContext != null)
			new Open(name).execute(basexContext);
		else
			Log.e("BaseXDatabase", "No context");
	}

	@Override
	public void createDatabase(String name) throws IOException {
		if(basexContext != null)
			new CreateDB(name).execute(basexContext);
		else
			Log.e("BaseXDatabase", "No context");
	}

	@Override
	public void closeDatabase() {
		if(basexContext != null)
			basexContext.closeDB();
		else
			Log.e("BaseXDatabase", "No context");
	}

	@Override
	public void deleteDatabase(String name) {
		if(basexContext != null)
			DropDB.drop(name, basexContext);
		else
			Log.e("BaseXDatabase", "No context");
	}
	
	@Override
	public void addSource(String path, String file_name) throws BaseXException {
		if(basexContext != null)
			new Add(path, file_name).execute(basexContext);
		else
			Log.e("BaseXDatabase", "No context");
	}

	@Override
	public String find(String name) throws BaseXException {
		if(basexContext != null)
		return new Find(name).execute(basexContext);
		else
			Log.e("BaseXDatabase", "No context");
		return "";
	}

	@Override
	public List<String> getDatabases() {
		StringList sl;
		if(basexContext != null)
			sl = basexContext.databases.listDBs();
		else {
			Log.e("BaseXDatabase", "No context");
			return new ArrayList<String>();
		}
		List<String> stringList = new ArrayList<String>();
		for (int i = 0; i < sl.size(); i++) {
			stringList.add(sl.get(i));
		}
		return stringList;
	}

	public boolean isDatabaseOpen() {
		return basexContext.data() != null;
	}

	public Context getBasexContext() {
		return basexContext;
	}

	@Override
	public String executeXQuery(String query) throws IOException {
		if(basexContext != null)
			return new XQuery(query).execute(basexContext);
		else
			Log.e("BaseXDatabase", "No context");
		return "";
	}

	@Override
	public String listDB(String name) throws IOException {
		if(basexContext != null)
			return new org.basex.core.cmd.List(name).execute(basexContext);
		else
			Log.e("BaseXDatabase", "No context");
		return "";
	}

	@Override
	public String listDBs() throws IOException {
		if(basexContext != null)
			return new org.basex.core.cmd.List().execute(basexContext);
		else
			Log.e("BaseXDatabase", "No context");
		return "";
	}

	@Override
	public void createBackup(String name) throws IOException {
		if(basexContext != null && name != null)
			new CreateBackup(name).execute(basexContext);
	}

	@Override
	public void createEvent(String name) throws IOException {
		if(basexContext != null && name != null)
			new CreateEvent(name).execute(basexContext);		
	}

	@Override
	public void createIndex(index index) throws IOException {
		if(basexContext != null && index != null)
			new CreateIndex(index.name()).execute(basexContext);
	}

	@Override
	public void createUser(String username, String password) throws IOException {
		if(basexContext != null && username != null && password != null) {
			new CreateUser(username, password).execute(basexContext);
		}
		
	}

	@Override
	public void grant(rights right, String database, String user)
			throws IOException {
		if(basexContext != null && right != null && user != null) {
			if(database != null)
				new Grant(right.name(), user, database).execute(basexContext);
			else
				new Grant(right.name(), user).execute(basexContext);
		}
	}

	@Override
	public void cs(String query) throws IOException {
		if(basexContext != null && query != null) {
			new Cs(query).execute(basexContext);
		}
	}

	@Override
	public void store(String path, String input) throws IOException {
		if(basexContext != null && path != null) {
			if(input != null)
				new Store(path).execute(basexContext);
			else
				new Store(path, input).execute(basexContext);
		}
	}

	@Override
	public void openOrCreateDatabase(String name) throws IOException {
		if(basexContext != null && name != null) {
			new Check(name).execute(basexContext);
		}
	}

	@Override
	public void copyDatabase(String oldDb, String newDb) throws IOException {
		if(basexContext != null && oldDb != null && newDb != null)
			new Copy(oldDb, newDb).execute(basexContext);
	}

	@Override
	public void flush() throws IOException {
		if(basexContext != null) 
			new Flush().execute(basexContext);
	}

	@Override
	public void alterDatabase(String oldName, String newName)
			throws IOException {
		if(basexContext != null && oldName != null && newName != null)
			new AlterDB(oldName, newName).execute(basexContext);
	}

	@Override
	public void alterUser(String name, String password) throws IOException {
		if(basexContext != null && name != null && password != null)
			new AlterUser(name, password).execute(basexContext);		
	}

	@Override
	public List<String> getUsers() throws IOException {
		List<String> ret = new ArrayList<String>();
		String users = new ShowUsers().execute(basexContext);
		String userLists[] = users.split("\n");
		Pattern MY_PATTERN = Pattern.compile("^(\\w+)\\s\\s.*");

		for (int i = 2; i < userLists.length; i++) {
			Matcher m = MY_PATTERN.matcher(userLists[i]);
			if(m.find())
				ret.add("" + m.group(1));
		}
		
		return ret;
	}

	@Override
	public List<String> getEvents() throws IOException {
		List<String> ret = new ArrayList<String>();
		String events = new ShowEvents().execute(basexContext);
		String eventLists[] = events.split("\n");
		Pattern MY_PATTERN = Pattern.compile("^-\\s(\\w*)$");

		for (int i = 2; i < eventLists.length; i++) {
			Matcher m = MY_PATTERN.matcher(eventLists[i]);
			if(m.find())
				ret.add("" + m.group(1));
		}
		return ret;
	}

	@Override
	public List<String> getSessions() throws IOException {
		List<String> ret = new ArrayList<String>();
		String sessions = new ShowSessions().execute(basexContext);
		String sessionList[] = sessions.split("\n");
		Pattern MY_PATTERN = Pattern.compile("^-\\s(\\w*)$");

		for (int i = 2; i < sessionList.length; i++) {
			Matcher m = MY_PATTERN.matcher(sessionList[i]);
			if(m.find())
				ret.add("" + m.group(1));
		}
		return ret;
	}

	@Override
	public void optimize(boolean all) throws IOException {
		if(basexContext != null)
			if(!all)
				new Optimize().execute(basexContext);
			else
				new OptimizeAll().execute(basexContext);
	}

	@Override
	public void dropBackup(String name) throws IOException {
		if(basexContext != null && name != null) {
			new DropBackup(name).execute(basexContext);
		}
		
	}

	@Override
	public void dropDatabase(String name) throws IOException {
		if(basexContext != null && name != null) {
			new DropDB(name).execute(basexContext);
		}
	}

	@Override
	public void dropEvent(String name) throws IOException {
		if(basexContext != null && name != null) {
			new DropEvent(name).execute(basexContext);
		}
	}

	@Override
	public void dropIndex(index index) throws IOException {
		if(basexContext != null && index != null) {
			new DropIndex(index.name()).execute(basexContext);
		}
	}

	@Override
	public void dropUser(String name, String database) throws IOException {
		if(basexContext != null && name != null) {
			if(database != null)
				new DropUser(name, database).execute(basexContext);
			else
				new DropUser(name).execute(basexContext);
		}
	}

	@Override
	public List<String> getBackups() throws IOException {
		List<String> ret = new ArrayList<String>();
		String backups = new ShowBackups().execute(basexContext);
		String backupLists[] = backups.split("\n");
		Pattern MY_PATTERN = Pattern.compile("^([\\w|-]+)\\s+\\d+");

		for (int i = 2; i < backupLists.length; i++) {
			Matcher m = MY_PATTERN.matcher(backupLists[i]);
			if(m.find())
				ret.add("" + m.group(1));
		}
		
		return ret;
	}	
}
