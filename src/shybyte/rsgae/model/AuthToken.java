package shybyte.rsgae.model;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class AuthToken {
	public static Pattern isValidPublicPath = Pattern.compile("^/[^/]+/public.*[^/]$");
	
	@Id
	String id;
	String username;
	List<String> scopes;
	long timestamp;

	public AuthToken() {
		// for Objectify
	}

	private AuthToken(String id, String username, List<String> scopes) {
		this.id = id;
		this.username = username;
		this.scopes = scopes;
		this.timestamp = Calendar.getInstance().getTime().getTime();
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public List<String> getScopes() {
		return scopes;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	public boolean isValidForPath(String path,String httpMethod) {
		String pathBase = "/"+username+"/";
		String publicPathBase = pathBase+"public/";
		for(String scope: scopes ) {
			String scopePath = scope.substring(0,scope.lastIndexOf(":")-1); 
			if (path.startsWith(pathBase+scopePath) || path.startsWith(publicPathBase+scopePath)) {
				return matches(httpMethod, scope);
			}
		}
		return false;
	}
	
	public static boolean isValidPublicPathRequest(String path,String httpMethod) {
		return httpMethod.equals("GET") && (isValidPublicPath.matcher(path).matches());
	}

	private boolean matches(String httpMethod, String scope) {
		if (httpMethod.equals("DELETE") || httpMethod.equals("PUT")) {
			return scope.endsWith(":rw");
		} else {
			return true;
		}
	}
	
	public static AuthToken generateAuthToken(String username,List<String> scopes) {
		return new AuthToken(randomID(),username,scopes);
	}
	
	private static String randomID() {
		return UUID.randomUUID().toString();
	}
	
	

}
