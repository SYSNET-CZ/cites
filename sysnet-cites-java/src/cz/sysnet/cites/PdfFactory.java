package cz.sysnet.cites;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;

/**
 * Tato trida obsahuje metody pro praci s PDF dokumenty. Vytvoreni plocheho PDF, atd...   
 * 
 * @author Radim
 *
 */
public class PdfFactory implements Serializable {
	private static final String BEAN_NAME = "PdfFactory";
	private static final long serialVersionUID = 6567844958570848594L;
	private static Charset CS = Charset.forName("UTF-8");
	private WorkDocument doc;
	private boolean debug;
	private PdfReader form;
	private XfdfReader data;
	private XfaForm xfa;
	private PdfStamper outPdf;
	
	public PdfFactory() {
		this.doc = new WorkDocument();
		this.form = null;
		this.data = null;
		this.xfa = null;
		this.outPdf = null;
		this.printDebug("OS current temporary directory is " + this.doc.getTmpDir());
	}
	
	public void setSourcePath(String sourcePath) throws IOException {
		this.doc.setSourceXmlPath(sourcePath);
	}

	public void setTemplatePath(String templatePath) {
		this.doc.setTemplatePath(templatePath);
	}

	public void setXfdf(boolean xfdf) {
		this.doc.setXfdf(xfdf);
	}
	
	public void setPid(String pid) {
		this.doc.setPid(pid);
	}
	
	
	
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setTmpDir(String tmpDir) {
		this.doc.setTmpDir(tmpDir);
	}

	public PdfReader getForm() {
		return form;
	}

	public void setForm(PdfReader form) {
		this.form = form;
	}

	public XfdfReader getData() {
		return data;
	}

	public void setData(XfdfReader data) {
		this.data = data;
	}

	public XfaForm getXfa() {
		return xfa;
	}

	public void setXfa(XfaForm xfa) {
		this.xfa = xfa;
	}

	public PdfStamper getOutPdf() {
		return outPdf;
	}

	public void setOutPdf(PdfStamper out) {
		this.outPdf = out;
	}
	

	public WorkDocument getDoc() {
		return doc;
	}

	public void setDoc(WorkDocument doc) {
		this.doc = doc;
	}

	public static String getBeanName() {
		return BEAN_NAME;
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
			
			this.printDebug("consolidateDate: start");
			
			for (Iterator <String> it = keys.iterator(); it.hasNext();) {
				String fieldName = (String) it.next();
				if (fieldName.toLowerCase().contains("date")) {
					this.printDebug("consolidateDate: fieldName = " + fieldName);
					String fieldValue = fields.getField(fieldName);
					this.printDebug("consolidateDate: fieldValue = " + fieldValue);
					if(fieldValue.toUpperCase().startsWith("D:")) {
						fields.setField(fieldName, this.changeDateFormat(fieldValue));
						fieldValue = fields.getField(fieldName);
						this.printDebug("consolidateDate: fieldValue = : " + fieldValue);
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
	
	private void printDebug(String msg) {
		if (this.debug) System.out.println(BEAN_NAME + ": " + msg);
	}
	
	/**
	 * Vytvori z XML nebo XFDF ploche PDF podle formulare formId
	 * 
	 * @return	Vraci cestu k ulozenemu PDF nebo null, pokud to neklaplo
	 */
	public String create1FlatPdf() throws IOException, DocumentException, FileNotFoundException {
		String outpath = this.doc.getOutputPath();		
		this.outPdf = null;
		
		try {
			this.form = new PdfReader(this.doc.getTemplatePath());	
			this.outPdf = new PdfStamper(this.form, new FileOutputStream(outpath), '\0', true);
			AcroFields aform = this.outPdf.getAcroFields();
			if (this.doc.isXfdf()) {
				this.doc.loadXml();
				this.data = new XfdfReader(this.doc.getSourceXml().getBytes(CS));
				aform.setFields(this.data);
				aform = this.consolidateDate(aform);
			} else {
				this.xfa = aform.getXfa();
				xfa.fillXfaForm(new FileInputStream(this.doc.getSourceXmlPath()));
				this.doc.removeFile(this.doc.getSourceXmlPath());
			}
		} catch(Exception e) {
			outpath = null;
		} finally {
			try {
				this.outPdf.setFormFlattening(true);
				this.outPdf.close();
				
			} catch (Exception e) {
				outpath = null;
			}
		}
		return outpath;		
	}
}
