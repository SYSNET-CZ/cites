package cz.sysnet.cites;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class WebService implements Serializable, Service {
	private static final long serialVersionUID = 6458295802861341637L;
	private static final Charset CS = Charset.forName("UTF-8");
	private static final String EOL = System.getProperty("line.separator");
	private static final String TMPDIR = System.getProperty("java.io.tmpdir");

	@Override
	public String downloadFile(String fromLocation, String toLocation,
			String fileName) {
		String out = null;
		InputStream ist = null;
		OutputStream ost = null;

		String targetPath = toLocation;
		if (targetPath.isEmpty())
			targetPath = TMPDIR;
		String targetFileName = fileName;
		if (targetFileName.isEmpty())
			targetFileName = new PidGenerator().generateId("").toLowerCase()
					+ ".dat";
		targetPath += targetFileName;

		if (!fromLocation.isEmpty()) {
			if (!targetPath.isEmpty()) {
				try {
					HttpClient client = HttpClientBuilder.create().build();
					HttpGet request = new HttpGet(fromLocation);
					HttpResponse response = client.execute(request);
					ist = response.getEntity().getContent();
					ost = new FileOutputStream(new File(targetPath));

					int read = 0;
					byte[] bytes = new byte[1024];

					while ((read = ist.read(bytes)) != -1) {
						ost.write(bytes, 0, read);
					}
					out = targetPath;
				} catch (Exception e) {
					e.printStackTrace();
					out = null;
				} finally {
					if (ist != null) {
						try {
							ist.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (ost != null) {
						try {
							ost.flush();
							ost.close();
						} catch (Exception e) {
							e.printStackTrace();
							out = null;
						}
					}
				}
			}
		}
		return out;
	}

	@Override
	public Map<String, Object> downloadDictionary(String fromLocation,
			EN_DICTIONARY dictName) {
		Map<String, Object> out = null;
		HttpClient client = null;
		HttpGet request = null;
		HttpResponse response = null;
		BufferedReader rd = null;

		try {
			client = HttpClientBuilder.create().build();
			request = new HttpGet(fromLocation);
			response = client.execute(request);
			rd = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			String json = "";
			String line = "";
			String baseurl = request.getURI().toURL().getProtocol() + "://"
					+ request.getURI().toURL().getHost();

			while ((line = rd.readLine()) != null) {
				json += line.trim() + EOL;
			}
			json = json.trim();
			JSONArray jsonArray = JSONArray.fromObject(json);
			JSONObject jsonObject = null;

			switch (dictName) {
			case pdf_forms:
				String key;
				String srcUrl;
				String fPath;
				out = new HashMap<String, Object>();
				for (int i = 0; i < jsonArray.size(); i++) {
					jsonObject = jsonArray.getJSONObject(i);
					key = jsonObject.getString("id");
					srcUrl = baseurl + jsonObject.getString("url");
					fPath = TMPDIR + jsonObject.getString("filename");
					PdfTemplate bean = new PdfTemplate(key,
							jsonObject.getString("name"),
							jsonObject.getString("filename"), srcUrl, fPath);
					out.put(key, bean);
				}
				break;
			case persons:
				break;
			case taxons:
				break;
			default:
				break;

			}

		} catch (Exception e) {
			out = null;
			e.printStackTrace();
		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (Exception e) {
					e.printStackTrace();
					out = null;
				}
			}
		}
		return out;
	}

	@Override
	public String downloadText(String fromLocation) {
		String out = null;
		String filePath = null;
		try {
			filePath = this.downloadFile(fromLocation, "", "");
			out = this.readTextFromFile(filePath);

		} catch (Exception e) {

		} finally {
			if (filePath != null)
				this.removeFile(filePath);
		}
		return out;
	}

	@Override
	public boolean writeTextAsFile(String filePath, String content) {
		boolean out = false;
		FileOutputStream xmlo = null;
		try {
			// Charset cs = Charset.forName("UTF-8");
			xmlo = new FileOutputStream(filePath);
			xmlo.write(content.getBytes(CS));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (xmlo != null) {
				try {
					xmlo.flush();
					xmlo.close();
					out = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return out;
	}

	@Override
	public String readTextFromFile(String filePath) {
		String out = null;
		BufferedReader reader = null;
		try {

			reader = new BufferedReader(new FileReader(filePath));
			String line = null;
			StringBuilder stringBuilder = new StringBuilder();
			String ls = System.getProperty("line.separator");

			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

			out = stringBuilder.toString();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return out;
	}

	@Override
	public boolean removeFile(String filePath) {
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
