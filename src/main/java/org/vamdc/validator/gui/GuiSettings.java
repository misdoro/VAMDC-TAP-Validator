package org.vamdc.validator.gui;

import org.vamdc.validator.Settings;

/**
 * Class to keep application root preferences and to define key names.
 * @author doronin
 */
public class GuiSettings extends Settings{

	public final static String FILE_SAVE_PATH = "guifileSavePath";
	public final static String FILE_OPEN_PATH = "guifileOpenPath";
	public final static String QUERY_HISTORY = "guiqueries";
	
	/**
	 * Add get with default, since GUI defines defaults in place
	 * @param key
	 * @param def
	 * @return
	 */
	public static String get(String key,String def){
		String sup = Settings.get(key); 
		if (sup==null || sup.length() == 0)
			return def;
		return sup;
	}
	
}
