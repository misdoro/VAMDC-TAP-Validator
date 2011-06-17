package org.vamdc.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Class to keep application root preferences and to define key names.
 * @author doronin
 */
public class Settings{

	public final static String PluginClass = "pluginclass";
	public final static String PluginIDPrefix = "pluginidprefix";
	public final static String PluginLimitStates = "pluginmaxstates";
	public final static String PluginLimitProcesses = "pluginmaxproc";
	public final static String ServiceTAPURL = "tapurl";
	public final static String ServiceTAPSuffix = "tapsuffix";
	public final static String ServiceVOSIURL = "vosiurl";
	public final static String HTTP_CONNECT_TIMEOUT = "http_conn_timeout";//Http connect timeout
	public final static String HTTP_DATA_TIMEOUT = "http_data_timeout";//Http data timeout
	public final static String ServicePrettyOut = "tapprettyprint";
	public final static String OperationMode = "validatorsource";
	public final static String SchemaFile = "schemafilename";
	public final static String SchemaLocations = "schemalocations";
	
	/**
	 * Default settings
	 */
	public final static Map<String,Object> defaults = new HashMap<String,Object>(){
		private static final long serialVersionUID = -1018276585101724211L;
	{
		put(Settings.PluginClass,"org.vamdc.database.plugin.OutputBuilder");
		put(Settings.PluginIDPrefix,"DBNAME");
		put(Settings.PluginLimitStates,1000);
		put(Settings.PluginLimitProcesses,1000);
		put(Settings.ServiceTAPURL,"http://host.name:8080/tap/");
		put(Settings.ServiceTAPSuffix,"sync?LANG=VSS1&REQUEST=doQuery&FORMAT=XSAMS&QUERY=");
		put(Settings.ServiceVOSIURL,"http://host.name:8080/tap/capabilities");
		put(Settings.ServicePrettyOut,true);
		put(Settings.HTTP_CONNECT_TIMEOUT,2000);
		put(Settings.HTTP_DATA_TIMEOUT,30000);
		put(Settings.OperationMode,OperationModes.network.toString());
		put(Settings.SchemaFile,"");
		put(Settings.SchemaLocations,"http://vamdc.org/xml/xsams/0.2" +
				" " +
				this.getClass().getResource("/schema/xsams.xsd").toString());
		
	}};
	/**
	 * Path for temporary file storage.
	 * If not defined, using system default. If can't create it, use memory storage.
	 */
	public final static String StorageTempPath = "tempfilepath"; 
	
	/**
	 * Possible xsams io controlled operation modes
	 */
	public static enum OperationModes {
		plugin,
		file,
		network
	}
	
	
	/**
	 * Get preferences object
	 * @return stored preferences.
	 */
	public static Preferences getPreferences(){
		return PrefsHolder.stored.prefs;
	}
	
	/**
	 * Get preferences object
	 * @return stored preferences.
	 */
	private static Map<String,Object> getOverride(){
		return PrefsHolder.stored.override;
	}
	
	
	public Settings(){
		prefs = Preferences.userNodeForPackage(this.getClass());
		override = new HashMap<String,Object>();
	}
	
	protected Preferences prefs;
	protected Map<String,Object> override;

	
	private static class PrefsHolder{
		private final static Settings stored=new Settings();
	}
	
	/**
	 * Override preferences option. Used for command-line options input
	 * @param key Option name
	 * @param value Option value
	 */
	public static void override(String key, Object value){
		Settings.getOverride().put(key, value);
	}
	
	/**
	 * Reset all settings to default values
	 */
	public static void reset(){
		Preferences my = Settings.getPreferences();
		for(String key:defaults.keySet()){
			my.put(key, String.valueOf(defaults.get(key)));
		}
		try {
			my.sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
	
	//Getters
	public static String get(String key) {
		Object override = Settings.getOverride().get(key);
		if (override!=null)
			return String.valueOf(override);
		
		Object def = defaults.get(key);
		if (def==null)
			def="";
		return Settings.getPreferences().get(key, String.valueOf(def));
		
	}
	
	public static boolean getBoolean(String key) {
		return Boolean.parseBoolean(Settings.get(key));
	}

	public static double getDouble(String key) {
		return Double.parseDouble(Settings.get(key));
	}

	public static float getFloat(String key) {
		return Float.parseFloat(Settings.get(key));
	}

	public static int getInt(String key) {
		return Integer.parseInt(Settings.get(key));
	}

	public static  long getLong(String key, long def) {
		return Long.parseLong(Settings.get(key));
	}

	//Setters
	public static void put(String key, String value) {
		Settings.getPreferences().put(key, value);
	}


	public static void putBoolean(String key, boolean value) {
		Settings.getPreferences().putBoolean(key, value);
	}


	public static void putDouble(String key, double value) {
		Settings.getPreferences().putDouble(key, value);
	}


	public static void putFloat(String key, float value) {
		Settings.getPreferences().putFloat(key, value);
		
	}


	public static void putInt(String key, int value) {
		Settings.getPreferences().putInt(key, value);
	}


	public static void putLong(String key, long value) {
		Settings.getPreferences().putLong(key, value);
	}

	public static void sync(){
		try {
			Settings.getPreferences().sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
	
}
