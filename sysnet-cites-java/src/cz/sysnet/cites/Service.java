package cz.sysnet.cites;

import java.util.Map;

/**
 * @author Radim
 *
 */
public interface Service {
	public String downloadFile(String fromLocation, String toLocation, String fileName);
	public Map<String, Object> downloadDictionary(String fromLocation, EN_DICTIONARY dictName);
	public String downloadText(String fromLocation);
	public boolean writeTextAsFile(String filePath, String content);
	public String readTextFromFile(String filePath);
	public boolean removeFile(String filePath);
}
