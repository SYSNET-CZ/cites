package cz.sysnet.cites;


import java.io.BufferedReader;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
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
	private static final String EOL = System.getProperty("line.separator");  
	private WorkDocument doc;
	private boolean debug;
	private PdfReader form;
	private XfdfReader data;
	private XfaForm xfa;
	private PdfStamper outPdf;
	private Map<String, PdfTemplate> template;
	
	
	
	public PdfFactory(WorkDocument doc) {
		this.doc = doc;
		this.template = new HashMap<String, PdfTemplate>();		
		this.printDebug("OS current temporary directory is " + this.doc.getTmpDir());
	}
	
	public PdfFactory() {
		this(new WorkDocument(new IdentFactory()));
	}

	public void loadTemplates(String sourceUrl) {
		HttpClient client = null;
        HttpGet request = null;
        HttpResponse response = null;
        BufferedReader rd = null;
        this.template.clear();
        
        try {
    		client = HttpClientBuilder.create().build();
            request = new HttpGet(sourceUrl);
            response = client.execute(request);
            rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
            String json = "";
            String line = "";
            String baseurl = request.getURI().toURL().getProtocol() + "://" + request.getURI().toURL().getHost();
            
            while ((line = rd.readLine()) != null) { 
            	json += line.trim() + EOL;
            }
            json = json.trim();
            JSONArray jsonArray = JSONArray.fromObject(json);
            JSONObject jsonObject = null;
            String key = null;
            String srcUrl = null;
            String fPath = null;
            for (int i = 0; i < jsonArray.size(); i++ ) {
            	jsonObject = jsonArray.getJSONObject(i);
            	key = jsonObject.getString("id");
            	srcUrl = baseurl + jsonObject.getString("url");
            	fPath = System.getProperty("java.io.tmpdir") + jsonObject.getString("filename");
            	PdfTemplate bean = new PdfTemplate(key, jsonObject.getString("name"), jsonObject.getString("filename"), srcUrl, fPath);        	
               	this.template.put(key, bean);           	  
            }        
        } 
        catch (Exception e) {
        	e.printStackTrace();
        } 
        finally {
        	if (rd != null) {
            	try { rd.close(); } catch (Exception e) { e.printStackTrace(); }
        	}
        }
	}
	
	public void storeTemplateFiles() {
		if (!this.template.isEmpty()) {
			PdfTemplate value;
			for(Map.Entry<String, PdfTemplate> tEntry:this.template.entrySet()) {
				value = tEntry.getValue();
				value.storeFile();
			}
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
	

	private void printDebug(String msg) {
		if (this.debug) System.out.println(BEAN_NAME + ": " + msg);
	}
	
	/**
	 * Vytvori z XML nebo XFDF ploche PDF podle formulare a ulozi do FS
	 * 
	 * @param pid			jednoznacny identifikator zdrojoveho dokumentu
	 * @param xfdf			XFDF nebo XFA?
	 * @param templateId	identifikator sablony formulare
	 * @return				vraci cestu k vytvorenemu PDF
	 */
	public String create1FlatPdf(String pid, boolean xfdf, String templateId) {
		String out = null;
		try {
			this.doc.setXfdf(xfdf);
			this.doc.setPid(pid);
			String templatePath = this.template.get(templateId).getFilepath();
			this.doc.setTemplatePath(templatePath);
			out = this.doc.createPdf();
				
		} catch (Exception e) {
			out = null;
		}
		return out;		
	}
}

