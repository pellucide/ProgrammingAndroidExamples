package com.oreilly.demo.pa.ch17.dataobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class User {
	public long id;
	public String name;
	public String username;
	public String password;
	public String phone;
	public String authtoken;
	public ArrayList<Change> history = new ArrayList<Change>();
	public ArrayList<Long> friends = new ArrayList<Long>();
	
	public String toString() {
		return toJSON(this).toString();
	}
	
	public void addFriend(long id) {
		for(long fid: friends) {
			if(fid == id) return;
		}
		friends.add(id);
		Change change = new Change();
		change.type = Change.ChangeType.ADD;
		change.who = id;
		addHistory(change);
	}
	
	public void deleteFriend(long id) {
		boolean exists = false;
		Iterator<Long> it = friends.iterator();
		while(it.hasNext()) {
			if(it.next() == id) {
				exists = true;
				it.remove();
				break;
			}
		}
		if(!exists) return;
		Change change = new Change();
		change.type = Change.ChangeType.DELETE;
		change.who = id;
		addHistory(change);
	}
	
	public void addHistory(Change change) {
		history.add(change);
		Collections.sort(history);
	}
	
	public static JSONObject toJSON(User user) {
		return toJSON(user, true, true, true);
	}
	
	public static JSONObject toJSON(User user, boolean showpassword, boolean showfriends, boolean showhistory) {
		if(user == null) return null;
		JSONObject json = new JSONObject();
		try { json.put("id", user.id); } catch (Exception e) {}
		if(user.username != null) {
			try { json.put("username", user.username); } catch (Exception e) {}
		}
		if(user.name != null) {
			try { json.put("name", user.name); } catch (Exception e) {}
		}
		if(showpassword && user.password != null) {
			try { json.put("password", user.password); } catch (Exception e) {}
			try { json.put("authtoken", user.authtoken); } catch (Exception e) {}
		}
		if(user.phone != null) {
			try { json.put("phone", user.phone); } catch (Exception e) {}
		}
		
		if(showfriends && user.friends != null && !user.friends.isEmpty()) {
			JSONArray arr = new JSONArray();
			for(long i: user.friends) {
				arr.put(i);
			}
			try { json.put("friends", arr); } catch (Exception e) {}
		}
		if(showhistory && user.history != null && !user.history.isEmpty()) {
			Collections.sort(user.history);
			JSONArray arr = new JSONArray();
			for(Change change: user.history) {
				arr.put(Change.toJSON(change));
			}
			try { json.put("history", arr); } catch (Exception e) {}
		}
		return json;
	}
	
	public static User fromJSON(JSONObject json) {
		if(json == null) return null;
		User user = new User();
		user.id = json.optLong("id");
		user.name = json.optString("name", null);
		user.username = json.optString("username", null);
		user.phone = json.optString("phone", null);
		user.password = json.optString("password", null);
		user.authtoken = json.optString("authtoken", null);
		JSONArray friends = json.optJSONArray("friends");
		if(friends != null) {
			for(int i=0;i<friends.length();i++) {
				user.friends.add(friends.optLong(i));
			}
		}
		JSONArray history = json.optJSONArray("history");
		if(history != null) {
			for(int i=0;i<history.length();i++) {
				user.history.add(Change.fromJSON(history.optJSONObject(i)));
			}
			Collections.sort(user.history);
		}
		return user;
	}
}
