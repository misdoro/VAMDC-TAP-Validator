package org.vamdc.validator;

/**
 * Class keeping track of necessary setting updates between versions
 */
public class MigrateSettings {
	public static final int V1207=12070;
	public static final int V12072=12072;
	public static final int LATESTVERSION=V12072;
	
	public static void call(int version){
		if (version<V1207){
			System.out.println("Migrating settings to version "+V1207+" from "+version);
			MigrateSettings.checkXsamsOne();
			Setting.SettingsVersion.saveValue(V1207);
		}
		if (version<V12072){
			Setting.LicenseVersion.saveValue(0);
			Setting.SettingsVersion.saveValue(V12072);
		}
	}
	
	static void checkXsamsOne() {
		String schemaloc = Setting.SchemaLocations.getValue(); 
		if (!schemaloc.contains("xsams/1.0"))
			Setting.SchemaLocations.saveValue(Setting.getSchemaLoc());
	}

}
