package shybyte.rsgae.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class AuthToken {
	public static Pattern isValidPublicPath = Pattern
			.compile("^/[^/]+/public.*[^/]$");

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

	public List<Scope> getScopes() {
		List<Scope> scopes = new ArrayList<Scope>();
		for (String scope : this.scopes) {
			scopes.add(Scope.apply(scope));
		}
		return scopes;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public boolean isValidForPath(String path, String httpMethod) {
		String pathBase = "/" + username + "/";
		String publicPathBase = pathBase + "public/";
		for (Scope scope : getScopes()) {
			if ((path.startsWith(pathBase + scope.path()) || path
					.startsWith(publicPathBase + scope.path()))
					&& scope.allowsHttpMethod(httpMethod)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isValidPublicPathRequest(String path,
			String httpMethod) {
		return httpMethod.equals("GET")
				&& (isValidPublicPath.matcher(path).matches());
	}

	public static final AuthToken generateAuthToken(String username,
			List<String> scopes) {
		return new AuthToken(randomID(), username, scopes);
	}
	
	public static final AuthToken generateAuthToken(String username) {
		return new AuthToken(randomID(), username, null);
	}

	private static String randomID() {
		return UUID.randomUUID().toString();
	}

}
