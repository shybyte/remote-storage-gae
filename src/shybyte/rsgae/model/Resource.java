package shybyte.rsgae.model;

import java.io.Serializable;
import java.util.Calendar;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Cache
public class Resource implements Serializable{
	private static final long serialVersionUID = 1016697286429182488L;
	private static final String PATH_SEP = "/";
	
	@Id
	String path;
	@Index
	String dir;
	String contentType;
	String content;
	long timestamp; // last modified

	public Resource() {
		// for Objectify
	}

	private Resource(String path, String content, String contentType) {
		this.path = path;
		this.contentType = contentType;
		this.dir = getParentDirectory(path);
		this.content = content;
		this.timestamp = Calendar.getInstance().getTime().getTime();
	}

	public String getPath() {
		return path;
	}

	public String getDir() {
		return dir;
	}

	public String getContentType() {
		return contentType;
	}

	public String getContent() {
		return content;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getName() {
		int searchStart = isDirectory() ? path.length() - 2 : path.length();
		return path.substring(path.lastIndexOf(PATH_SEP, searchStart) + 1);
	}

	public static Resource resource(String path, String content,
			String contentType) {
		assert !isDirectoryPath(path);
		return new Resource(path, content, contentType);
	}

	public static Resource directory(String path) {
		assert isDirectoryPath(path);
		return new Resource(path, null, null);
	}

	public boolean isDirectory() {
		return isDirectoryPath(path);
	}

	public static boolean isDirectoryPath(String path) {
		return path.endsWith(PATH_SEP);
	}

	public static String getParentDirectory(String path) {
		return isDirectoryPath(path) ? path.substring(0,
				path.lastIndexOf(PATH_SEP, path.length() - 2) + 1) : path
				.substring(0, path.lastIndexOf('/') + 1);
	}
}
