package cz.sysnet.cites;

import java.io.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

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
	
	public PdfTemplate(PdfTemplate source) {
		this.filename = source.getFilename();
		this.filepath = source.getFilepath();
		this.id = source.getId();
		this.name = source.getName();
		this.url = source.getUrl();
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
	
	public void storeFile() {
        InputStream ist = null;
        OutputStream ost = null;

		if (!this.url.isEmpty()) {
			if (!this.filepath.isEmpty()) {
				try {
					HttpClient client = HttpClientBuilder.create().build();
			        HttpGet request = new HttpGet(this.url);
			        HttpResponse response = client.execute(request);
			        ist = response.getEntity().getContent();
			        ost = new FileOutputStream(new File(this.filepath));
			        
			        int read = 0;
					byte[] bytes = new byte[1024];
			 
					while ((read = ist.read(bytes)) != -1) {
						ost.write(bytes, 0, read);
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
				} 
				finally {
					if (ist != null) { 
						try { ist.close(); } catch (Exception e) { e.printStackTrace();}
					}
					if (ost != null) {
						try {
							ost.flush();
							ost.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
