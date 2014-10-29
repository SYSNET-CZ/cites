package cz.sysnet.cites;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;
import com.itextpdf.text.pdf.*;



/**
 * Trida obsahuje strukturu a metody pracovniho dokumentu
 * 
 * @author Radim
 *
 */
public class WorkDocument implements Serializable {
	private static final long serialVersionUID = -3807706717887929820L;
	private static final String BEAN_NAME = "WorkDocument";
	private static final Charset CS = Charset.forName("UTF-8");
	
	
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
	
	public WorkDocument(IdentFactory ifact) {
		this.ifact = ifact;
		this.pid = ifact.generateId("");
		this.xfdf = false;
		this.tmpDir = System.getProperty("java.io.tmpdir");
		this.sourceXml = null;
		this.sourceXmlPath = null;
		this.templatePath = null;
		this.outputPath = this.tmpDir + System.getProperty("file.separator") + this.pid.toLowerCase() + ".pdf";
	}
	
	public WorkDocument(String pid, boolean xfdf, IdentFactory ifact) {
		this.ifact = ifact;
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
	
	public WorkDocument(String pid, boolean xfdf, String templatePath, IdentFactory ifact) {
		this.ifact = ifact;
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

	public void setSourceXml(String sourceXml) {
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
		this.templateFileName = FilenameUtils.getName(this.templatePath);
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
		this.sourceXmlPath = this.tmpDir + this.pid.toLowerCase() + ext;
		this.loadXml();
		this.templatePath = null;
		this.outputPath = this.tmpDir + this.pid.toLowerCase() + ".pdf";			
	}
	
	public void generatePid(String prefix) {
		this.pid = this.ifact.generateId(prefix);
	}
	
	public String getSourceXmlFileName() {
		this.sourceXmlFileName = FilenameUtils.getName(this.sourceXmlPath);
		return sourceXmlFileName;
	}

	public void setSourceXmlFileName(String sourceXmlFileName) {
		this.sourceXmlFileName = sourceXmlFileName;
		this.sourceXmlPath = this.tmpDir + System.getProperty("file.separator") + this.sourceXmlFileName;
		if (FilenameUtils.getExtension(this.sourceXmlPath).toLowerCase().equals("xfdf")) this.xfdf = true;		
	}

	public String getTemplateFileName() {
		this.templateFileName = FilenameUtils.getName(this.templatePath);
		return templateFileName;
	}

	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
		this.templatePath = this.tmpDir + System.getProperty("file.separator") + this.templateFileName;
	}

	public String getOutputFileName() {
		this.outputFileName = FilenameUtils.getName(this.outputPath);
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
		this.outputPath = this.tmpDir + System.getProperty("file.separator") + this.outputPath;
	}

	public void loadXml() {
		try {
			if (!this.sourceXmlPath.isEmpty()) this.readFile(this.sourceXmlPath);			
		} 
		catch(IOException e) {
			this.sourceXml = "";
		}
	}
	
	private void writeFile(String file, String content) {
		FileOutputStream xmlo = null;
		try {
			Charset cs = Charset.forName("UTF-8");
			xmlo = new FileOutputStream(file);
			xmlo.write(content.getBytes(cs));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (xmlo != null) {
				try {
					xmlo.flush();
					xmlo.close();
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
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
	
	private boolean isReady() {
		boolean out = false;
		if(!this.outputPath.isEmpty() && !this.sourceXmlPath.isEmpty() && !this.templatePath.isEmpty()) {
			out = true;
		}
		
		return out;
	}
	
	/**
	 * Vytvori ploche pdf z XML dat a sablony a ulozi je do FS
	 * 
	 * @return	vraci cestu k vytvorenemu PDF
	 */
	public String createPdf() {
		String out = null;
		PdfReader form = null;
		XfdfReader data = null;
		XfaForm xfa = null;
		PdfStamper outPdf = null;
		if(this.isReady()) {
			try {
				form = new PdfReader(this.templatePath);
				outPdf = new PdfStamper(form, new FileOutputStream(this.outputPath), '\0', true);
				AcroFields aform = outPdf.getAcroFields();
				if (this.xfdf) {
					this.loadXml();
					data = new XfdfReader(this.sourceXml.getBytes(CS));
					aform.setFields(data);
					aform = this.consolidateDate(aform);
				} else {
					xfa = aform.getXfa();
					xfa.fillXfaForm(new FileInputStream(this.sourceXmlPath));					
				}
				out = this.outputPath;
			} 
			catch (Exception e) {
				e.printStackTrace();
			} 
			finally {
				if (outPdf != null) {
					try {
						outPdf.setFormFlattening(true);
						outPdf.close();
					} catch (Exception e) {
						out = null;
					}
				}

				if (!this.xfdf) this.removeFile(this.sourceXmlPath);
			}
		}
		return out;	
	}


/**
 * Konsoliduje obsah XFDF tak aby polozky typu datum mely spravny format 
 * 
 * @param fields	datova pole
 * @return			vraci aktualizovana datova pole
 */
private AcroFields consolidateDate(AcroFields fields) {
	try {
		Map <String,AcroFields.Item>  fieldMap = fields.getFields();
		Set <String> keys = fieldMap.keySet();
		
		for (Iterator <String> it = keys.iterator(); it.hasNext();) {
			String fieldName = (String) it.next();
			if (fieldName.toLowerCase().contains("date")) {
				String fieldValue = fields.getField(fieldName);
				if(fieldValue.toUpperCase().startsWith("D:")) {
					fields.setField(fieldName, this.changeDateFormat(fieldValue));
					fieldValue = fields.getField(fieldName);
               	}
               }
		}
		return fields;	
	} catch(Exception e) {
		return null;
	}		
}

/**
 * Zmeni format data v XFDF poli na korektni hodnotu. Pouziva se pouze v metode consolidateDate
 * 
 * @param 	dateValue	Textova hodnota data ve formatu ISO
 * @return	vraci textovou hodnotu data ve formatu XFDF "D:20030425095243"
 */
private String changeDateFormat(String dateValue) {
	String changedDate = dateValue;
	try {
		if (dateValue == "") return "";
		if (dateValue.toUpperCase().startsWith("D:")) {
			String year = "";
			String month = "";
			String day = "";
			String hour = "";
			String minute = "";
			String sec = "";	
			int len = dateValue.length();
			
			//System.out.println(len);

			year = dateValue.substring(2,6);
			month = dateValue.substring(6,8);
			day = dateValue.substring(8,10);
			
			if (len > 10) {
				hour = dateValue.substring(10,12);
				minute = dateValue.substring(12,14);
				sec = dateValue.substring(14,16);
			}
			changedDate = day + "." + month + "." + year;

			if (len > 10) {
				changedDate = changedDate + " " + hour + ":" + minute + ":" + sec;
			}
		}
		else {
			changedDate = dateValue;
		}			
	} catch (Exception e) {
		changedDate = dateValue;
	}
	return changedDate;
}
}
