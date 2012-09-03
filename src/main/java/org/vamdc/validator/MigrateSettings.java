package org.vamdc.validator;

/**
 * Class keeping track of necessary setting updates between versions
 */
public class MigrateSettings {
	public static final int V1207=12070;
	
	public static void call(int version){
		if (version<V1207){
			System.out.println("Migrating settings to version "+V1207+" from "+version);
			MigrateSettings.checkXsamsOne();
			Setting.SettingsVersion.saveValue(V1207);
		}
	}
	
	static void checkXsamsOne() {
		String schemaloc = Setting.SchemaLocations.getValue(); 
		if (!schemaloc.contains("xsams/1.0"))
			Setting.SchemaLocations.saveValue(Setting.getSchemaLoc());
	}

}
