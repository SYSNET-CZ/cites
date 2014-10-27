/**
 * 
 */
package cz.sysnet.cites;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

/**
 * @author Radim
 *
 */
public class test {

	/**
	 * @param args
	 */
	static PdfFactory pfact;
	static WorkDocument wdoc;
	static IdentFactory ifact;
	
	public static void main(String[] args) throws IllegalStateException, IOException {
		// TODO Auto-generated method stub
		String eol = System.getProperty("line.separator");  
		
		ifact = new IdentFactory();
		wdoc = new WorkDocument(ifact);
		pfact = new PdfFactory(wdoc);
		pfact.setDebug(true);
		
		//String result = restTemplate.
		System.out.println("Ahoj, swěte!");
		
		String url = "http://athos.sysnet.cz/clients/env/cites/v3/cites-admin300.nsf/api/data/collections/name/pdf-forms";
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
        String json = "";
        String line = "";
        String baseurl = request.getURI().toURL().getProtocol() + "://" + request.getURI().toURL().getHost();
      	System.out.println(baseurl);
      	
        while ((line = rd.readLine()) != null) {
        	json += line.trim() + eol;
        	//System.out.println(line);
        }
        json = json.trim();
       
    	System.out.println("-------------------------------------------------------------------------");
    	//System.out.println(json);

        //JSONObject jsonObject = JSONObject.fromObject( json );
        JSONArray jsonArray = JSONArray.fromObject(json);
        JSONObject jsonObject;
        //Object bean;
        PdfTemplate bean = new PdfTemplate();
        for (int i = 0; i < jsonArray.size(); i++ ) {
        	jsonObject = jsonArray.getJSONObject(i);
        	System.out.print("id\t" );
        	System.out.println(jsonObject.get("id"));
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
        
        System.out.println("Ahoj, swěte! " + jsonArray.size());
	}
}
