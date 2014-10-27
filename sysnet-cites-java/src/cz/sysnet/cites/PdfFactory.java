package cz.sysnet.cites;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

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
	private static final Charset CS = Charset.forName("UTF-8");
	private static final String EOL = System.getProperty("line.separator");  
	private WorkDocument doc;
	private boolean debug;
	private PdfReader form;
	private XfdfReader data;
	private XfaForm xfa;
	private PdfStamper outPdf;
	private Map<String, PdfTemplate> template;
	
	public PdfFactory() {
		this.doc = new WorkDocument(new IdentFactory());
		this.form = null;
		this.data = null;
		this.xfa = null;
		this.outPdf = null;
		this.printDebug("OS current temporary directory is " + this.doc.getTmpDir());
	}
	
	public PdfFactory(WorkDocument doc) {
		this.doc = doc;
		this.form = null;
		this.data = null;
		this.xfa = null;
		this.outPdf = null;		
	}
	
	public void loadTemplates(String sourceUrl) throws IllegalStateException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(sourceUrl);
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
        String json = "";
        String line = "";
        String baseurl = request.getURI().toURL().getProtocol() + "://" + request.getURI().toURL().getHost();
        while ((line = rd.readLine()) != null) { 
        	json += line.trim() + EOL;
        }
        json = json.trim();
        JSONArray jsonArray = JSONArray.fromObject(json);
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.size(); i++ ) {
        	jsonObject = jsonArray.getJSONObject(i);
        	String key = jsonObject.getString("id");
        	PdfTemplate bean = new PdfTemplate(key, jsonObject.getString("name"), jsonObject.getString("filename"), jsonObject.getString("url"));
        	
        	bean.setId(jsonObject.getString("id"));
        	System.out.print("name\t" );
        	System.out.println(jsonObject.get("name"));
           	bean.setName(jsonObject.getString("name"));
            System.out.print("filename\t" );
        	System.out.println(jsonObject.get("filename"));
           	bean.setFilename(jsonObject.getString("filename"));
        	System.out.print("url\t" );
        	System.out.println(baseurl + jsonObject.get("url"));
           	bean.setUrl(baseurl + jsonObject.getString("url"));
           	System.out.print("filepath\t" );
        	System.out.println(System.getProperty("java.io.tmpdir") + bean.getFilename());
           	bean.setFilepath(System.getProperty("java.io.tmpdir") + bean.getFilename());
           	
           	
           	
        	
        	//bean = JSONObject.toBean( jsonObject );
        	//bean = (PdfTemplate) JSONObject.toBean( jsonObject, PdfTemplate.class );  
        }
        
        
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

	public Map<String, PdfTemplate> getTemplate() {
		return template;
	}

	public void setTemplate(Map<String, PdfTemplate> template) {
		this.template = template;
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

