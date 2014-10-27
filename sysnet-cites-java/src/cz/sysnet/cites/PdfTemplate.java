package cz.sysnet.cites;

import java.io.Serializable;

public class PdfTemplate implements Serializable {
	private static final String BEAN_NAME = "PdfTemplate";
	private static final long serialVersionUID = 2847557199910466817L;
	private String id;
	private String name;
	private String filename;
	private String url;
	private String filepath;
	
	public PdfTemplate() {
		
	}
	
	public PdfTemplate(String id, String name, String filename, String url) {
		this.id = id;
		this.name = name;
		this.filename = filename;
		this.url = url;
	}
	
	public PdfTemplate(String id, String name, String filename, String url, String filepath) {
		this.id = id;
		this.name = name;
		this.filename = filename;
		this.url = url;
		this.filepath = filepath;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public static String getBeanName() {
		return BEAN_NAME;
	}
	
}
