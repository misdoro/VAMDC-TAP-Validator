package org.vamdc.validator.gui.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.vamdc.registry.client.Registry;
import org.vamdc.registry.client.Registry.Service;
import org.vamdc.registry.client.RegistryCommunicationException;
import org.vamdc.registry.client.RegistryFactory;
import org.vamdc.registry.client.VamdcTapService;
import org.vamdc.validator.Setting;
import org.vamdc.validator.gui.settings.FieldVerifier.Type;

public class RegistryPanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = 8296380326849668408L;
	
	public static final String registrySuffix = "services/RegistryQueryv1_0";
	
	private SettingField regURL;
	
	private JButton reload;
		
	private CapabilitiesField capabilitiesField;
	
	public RegistryPanel(Collection<SettingControl> fields, CapabilitiesField caps){
		super();
		this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		
		regURL = SettingsPanel.getTextField(Setting.RegistryURL, Type.HTTPURL, fields);
		this.add(regURL);
		
		reload = new JButton(">");
		reload.addActionListener(this);
		this.add(reload);
		
		this.capabilitiesField = caps;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try{
			URL registryURL = getRegistryURL(regURL.getText());
			Registry reg = RegistryFactory.getClient(registryURL);
			if (reg!=null){
				capabilitiesField.load();
				loadFromRegistry(reg);
			}
		} catch (MalformedURLException ex) {
			JOptionPane.showMessageDialog(this, "Malformed registry URL"+ex.getMessage());
			ex.printStackTrace();
		} catch (RegistryCommunicationException e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage());
			e1.printStackTrace();
		}
		
	}

	private void loadFromRegistry(Registry reg) {
		if (reg!=null){
			for (String id:reg.getIVOAIDs(Service.VAMDC_TAP)){
				for (VamdcTapService mirror:reg.getMirrors(id)){
					if (mirror.CapabilitiesEndpoint!=null)
						capabilitiesField.addItem(mirror.CapabilitiesEndpoint.toString());
				}
			}
		}
	}

	public static URL getRegistryURL(String regBase) throws MalformedURLException {
		if (regBase!=null && regBase.length()>0){
			if (!regBase.endsWith("/"))
				regBase+="/";
			return new URL(regBase+registrySuffix);
		}
		return null;
	}

}
