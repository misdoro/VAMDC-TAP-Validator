package org.vamdc.validator;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public enum Setting {
	PluginClass("pluginclass","org.vamdc.database.plugin.OutputBuilder"),
	PluginIDPrefix("pluginidprefix","DBNAME"),
	PluginLimitStates("pluginmaxstates",1000),
	PluginLimitProcesses("pluginmaxproc",1000),
	ServiceTAPURL("tapurl","http://host.name:8080/tap/"),
	ServiceTAPSuffix("tapsuffix","sync?LANG=VSS1&REQUEST=doQuery&FORMAT=XSAMS&QUERY="),
	ServiceVOSIURL("vosiurl","http://host.name:8080/tap/capabilities"),
	
	RegistryURL("registryURL","http://registry.vamdc.eu/registry-11.12/"),
	GUICapsURLHistory("capabilityURLHistory",""),
	
	HTTP_CONNECT_TIMEOUT("http_conn_timeout",2000),
	HTTP_DATA_TIMEOUT ("http_data_timeout",30000),
	
	OperationMode ("validatorsource",OperationModes.network.toString()),
	
	SchemaFile ("schemafilename",""),
	SchemaLocations ("schemalocations",getSchemaLoc()),
	
	StorageTempPath ("tempfilepath",""),
	
	PrettyPrint("prettyprint",true),
	UseGzip("compress",true),
	
	GUIFileSavePath("guifileSavePath",""),
	GUIFileOpenPath("guifileOpenPath",""),
	GUIQueryHistory("guiqueries","Select * where AtomSymbol='Fe';"),
	
	GUIVOSIHistory("guiVOSIHistory",""),
	
	;
		
	public static String getSchemaLoc(){
		return "http://vamdc.org/xml/xsams/0.2" +
				" " +
				Setting.class.getResource("/schema_0_2/xsams.xsd").toString()+
				" " +
				"http://vamdc.org/xml/xsams/0.3" +
				" " +
				Setting.class.getResource("/schema_0_3/xsams.xsd").toString();
	}
	
	;
	
	private final String preferenceName;
	
	private String value;
	private final String defValue;
	
	Setting(String option, Object defValue){
		this.preferenceName = option;
		this.defValue = defValue.toString();
		this.value=defValue.toString();
	}
	
	public void setValue(String value){ this.value = value; }
	public void setValue(boolean bValue){ this.value = Boolean.valueOf(bValue).toString(); }
	public void setValue(String value, boolean autosave) {
		setValue(value);
		if (autosave){
			Preferences prefs = getPrefs();
			prefs.put(this.preferenceName, value);
			savePrefs(prefs);
		}
			
	}
	public String getValue(){ return value.toString(); }
	public int getInt(){ return Integer.parseInt(getValue()); }
	public boolean getBool(){ return Boolean.parseBoolean(getValue()); }
	
	public static void load(){
		Preferences prefs = getPrefs();
		for (Setting node:Setting.values()){
			node.setValue(prefs.get(node.preferenceName, node.defValue));
			
		}
	}
	
	public static void save(){
		Preferences prefs = getPrefs();
		for (Setting node:Setting.values()){
			prefs.put(node.preferenceName, node.value);
		}
		savePrefs(prefs);
	}

	private static Preferences getPrefs() {
		return Preferences.userNodeForPackage(Setting.class);
	}

	
	private static void savePrefs(Preferences prefs) {
		try {
			prefs.sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public static void reset() {
		for (Setting node:Setting.values()){
			node.setValue(node.defValue);
		}
		save();
	}

}
