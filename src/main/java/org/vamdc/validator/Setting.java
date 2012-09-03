package org.vamdc.validator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public enum Setting {
	PluginClass("pluginclass","org.vamdc.database.plugin.OutputBuilder"),
	PluginIDPrefix("pluginidprefix","DBNAME"),
	PluginLimitStates("pluginmaxstates",1000),
	PluginLimitProcesses("pluginmaxproc",1000),
	ServiceTAPURL("tapurl","http://host.name:8080/tap/"),
	ServiceTAPSuffix("tapsuffix","sync?LANG=VSS2&REQUEST=doQuery&FORMAT=XSAMS&QUERY="),
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

	GUILogConsole("guiLogConsole",false),
	
	GUILogConsoleDim("guiLogConsoleDim",""),
	GUISettingsDim("guiSettingsDim",""),
	GUISearchDim("guiSearchDim",""),
	GUIMainDim("guiMainDim",""),

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
	public void saveValue(Object value){
		if (value!=null){
			setValue(value.toString());
			Preferences prefs = getPrefs();
			prefs.put(this.preferenceName, value.toString());
			savePrefs(prefs);
		};
	}
	public void saveBytes(byte[] bytes){
		if (bytes!=null && bytes.length>0){
			Preferences prefs = getPrefs();
			prefs.putByteArray(preferenceName, bytes);
			savePrefs(prefs);
		}
	}
	public byte[] loadBytes(){
		Preferences prefs = getPrefs();
		return prefs.getByteArray(preferenceName, defValue.getBytes());
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

	public void saveObject(Object object){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			oos.flush();
			saveBytes(bos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Object loadObject(){
		try {
			byte[] bytes = loadBytes();
			if (bytes==null || bytes.length<10)
				return null;
			InputStream input = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(input);
			Object result = ois.readObject();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
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
