package org.vamdc.validator.gui.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	
	private static final String registrySuffix = "services/RegistryQueryv1_0";
	
	private SettingField regURL;
	
	private JButton reload;
	
	private Registry registry;
	
	private CapabilitiesField caps;
	
	public RegistryPanel(Collection<SettingControl> fields, CapabilitiesField caps){
		super();
		this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		
		regURL = SettingsPanel.getTextField(Setting.RegistryURL, Type.HTTPURL, fields);
		this.add(regURL);
		
		reload = new JButton(">");
		reload.addActionListener(this);
		this.add(reload);
		
		this.caps = caps;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		caps.load();
		String reg = regURL.getText();
		if (reg!=null && reg.length()>0){
			try {
				this.registry = RegistryFactory.getClient(reg+registrySuffix);
			
				if (registry!=null){
					Collection<String> ivoaIDs=registry.getIVOAIDs(Service.VAMDC_TAP);
					for (String id:ivoaIDs){
						for (VamdcTapService mirror:registry.getMirrors(id)){
							caps.addItem(mirror.CapabilitiesEndpoint.toString());
						}
					}
				}
			} catch (RegistryCommunicationException e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage());
			}
			
		}
	}

}
