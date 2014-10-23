package cz.sysnet.cites;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import org.apache.commons.io.FilenameUtils;



/**
 * Trida obsahuje strukturu a metody pracovniho dokumentu
 * 
 * @author Radim
 *
 */
public class WorkDocument implements Serializable {
	private static final long serialVersionUID = -3807706717887929820L;
	private static final String BEAN_NAME = "WorkDocument";
	
	private String tmpDir;
	private String sourceXml;
	private String sourceXmlPath;
	private String sourceXmlFileName;
	private String templatePath;
	private String templateFileName;
	private String outputPath;
	private String outputFileName;
	private boolean xfdf;
	private String pid; 
	private IdentFactory ifact;
	
	public WorkDocument() {
		if (ifact.equals(null)) ifact = new IdentFactory();
		this.pid = ifact.generateId("");
		this.xfdf = false;
		this.tmpDir = System.getProperty("java.io.tmpdir");
		this.sourceXml = null;
		this.sourceXmlPath = null;
		this.templatePath = null;
		this.outputPath = this.tmpDir + System.getProperty("file.separator") + this.pid.toLowerCase() + ".pdf";
	}
	
	public WorkDocument(String pid, boolean xfdf) {
		this.pid = pid;
		this.xfdf = xfdf;
		String ext = ".xml";
		if (this.xfdf) ext = ".xfdf";
		this.tmpDir = System.getProperty("java.io.tmpdir");
		this.sourceXmlPath = this.tmpDir + System.getProperty("file.separator") + this.pid.toLowerCase() + ext;
		this.loadXml();
		this.templatePath = null;
		this.outputPath = this.tmpDir + System.getProperty("file.separator") + this.pid.toLowerCase() + ".pdf";		
	}
	
	public WorkDocument(String pid, boolean xfdf, String templatePath) {
		this.pid = pid;
		this.xfdf = xfdf;
		String ext = ".xml";
		if (this.xfdf) ext = ".xfdf";
		this.tmpDir = System.getProperty("java.io.tmpdir");
		this.sourceXmlPath = this.tmpDir + System.getProperty("file.separator") + this.pid.toLowerCase() + ext;
		this.loadXml();
		this.templatePath = templatePath;
		this.outputPath = this.tmpDir + System.getProperty("file.separator") + this.pid.toLowerCase() + ".pdf";
	}
	

	public static String getBeanName() {
		return BEAN_NAME;
	}

	public String getSourceXml() {
		return sourceXml;
	}

	public void setSourceXml(String sourceXml) throws IOException {
		this.sourceXml = sourceXml;
		if (this.sourceXmlPath.isEmpty()) {
			if (this.pid.isEmpty()) this.pid = this.ifact.generateId("");
			String ext = ".xml";
			if (this.xfdf) ext = ".xfdf";
			this.sourceXmlPath = this.tmpDir + System.getProperty("file.separator") + this.pid.toLowerCase() + ext;
		}
		this.writeFile(this.sourceXmlPath, this.sourceXml);		
	}

	public String getSourceXmlPath() {
		return sourceXmlPath;
	}

	public void setSourceXmlPath(String sourceXmlPath) throws IOException {
		this.sourceXmlPath = sourceXmlPath;
		this.sourceXml = this.readFile(this.sourceXmlPath);	
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getTmpDir() {
		return tmpDir;
	}

	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

	public boolean isXfdf() {
		return xfdf;
	}

	public void setXfdf(boolean xfdf) {
		this.xfdf = xfdf;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;	
		String ext = ".xml";
		if (this.xfdf) ext = ".xfdf";
		this.sourceXmlPath = this.tmpDir + System.getProperty("file.separator") + this.pid.toLowerCase() + ext;
		this.loadXml();
		this.templatePath = null;
		this.outputPath = this.tmpDir + System.getProperty("file.separator") + this.pid.toLowerCase() + ".pdf";			
	}
	
	public void generatePid(String prefix) {
		this.pid = this.ifact.generateId(prefix);
	}
	
	public String getSourceXmlFileName() {
		File f = new File(this.sourceXmlPath);
		this.sourceXmlFileName = f.getName();
		return sourceXmlFileName;
	}

	public void setSourceXmlFileName(String sourceXmlFileName) {
		this.sourceXmlFileName = sourceXmlFileName;
		this.sourceXmlPath = this.tmpDir + System.getProperty("file.separator") + this.sourceXmlFileName;
		FilenameUtils.getExtension("/path/to/file/foo.txt");
	}

	public String getTemplateFileName() {
		return templateFileName;
	}

	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public void loadXml() {
		try {
			if (!this.sourceXmlPath.isEmpty()) this.readFile(this.sourceXmlPath);			
		} 
		catch(IOException e) {
			this.sourceXml = "";
		}
	}
	
	private void writeFile(String file, String content) throws IOException {
		Charset cs = Charset.forName("UTF-8");
		FileOutputStream xmlo = new FileOutputStream(file);
		xmlo.write(content.getBytes(cs));
		xmlo.close();
	}
	
	private String readFile( String filePath ) throws IOException {
	    BufferedReader reader = new BufferedReader( new FileReader (filePath));
	    String line = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    String ls = System.getProperty("line.separator");
	    String out;

	    while( ( line = reader.readLine() ) != null ) {
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }
	    
	    out = stringBuilder.toString();
	    reader.close();
	    return out;
	}
	
	public boolean removeFile(String filePath)   {
		boolean out = false;
		try {
			File f = new File(filePath);
			f.delete();		
			out = true;
		} catch (Exception e) {
			out = false;
		}
		return out;
	}
}
