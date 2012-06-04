package org.vamdc.validator.cli;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.vamdc.validator.Setting;
import org.vamdc.validator.interfaces.DocumentElement;
import org.vamdc.validator.interfaces.DocumentElementsLocator;
import org.vamdc.validator.interfaces.DocumentError;
import org.vamdc.validator.interfaces.XSAMSIOModel;
import org.vamdc.validator.interfaces.DocumentElement.ElementTypes;
import org.vamdc.tapservice.validator.report.*;

/**
 * Write report xml file for this document
 * @author doronin
 *
 */
public class XMLReport {
	private Report report;
	private File outFile;
	public XMLReport(XSAMSIOModel doc, File statusFile, String xsamsFileName){
		outFile = statusFile;

		report = new Report();
		//Fill in report
		report.setNodeCapabilitiesUrl(Setting.ServiceVOSIURL.getValue());
		report.setNodeTapSyncUrl(Setting.ServiceTAPURL.getValue());
		report.setNodeAvailable(doc.getLineCount()>0);
		
		XMLGregorianCalendar cal=null;
		try {
			cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
		} catch (DatatypeConfigurationException e) {
		}
		report.setQueryDate(cal);
		report.setQueryString(doc.getQuery());
		report.setDocumentInfo(getDocInfo(doc));
		report.getDocumentInfo().setDocumentFileName(xsamsFileName);

		//Add all errors
		report.getValidationErrors().addAll(getErrorInfo(doc));
	}

	/**
	 * Fill in error report xml
	 * @param doc
	 * @return
	 */
	private Collection<DocError> getErrorInfo(XSAMSIOModel doc) {
		ArrayList<DocError> errors = new ArrayList<DocError>();
		if (doc!=null && doc.getElementsLocator()!=null)
			for(DocumentError err:doc.getElementsLocator().getErrors()){
				DocError error = new DocError();
				error.setErrorText(err.getMessage());
				DocumentElement element = err.getElement();
				if (element!=null){
					error.setElementName(element.getName());
					error.setStartRow(BigInteger.valueOf(element.getFirstLine()));
					error.setStartCol(BigInteger.valueOf(element.getFirstCol()));
					error.setEndRow(BigInteger.valueOf(element.getLastLine()));
					error.setEndCol(BigInteger.valueOf(element.getLastCol()));
				}
				errors.add(error);
			}
		return errors;
	}


	/**
	 * Map between XML error types and java ElementTypes 
	 */
	private static Map<ElementTypes,BlockType> BlockElementType = new HashMap<ElementTypes,BlockType>(){
		private static final long serialVersionUID = 5347975853574692283L;
		{
			put(ElementTypes.Atom,BlockType.ATOM);
			put(ElementTypes.AtomicState,BlockType.ATOM_STATE);
			put(ElementTypes.Molecule,BlockType.MOLECULE);
			put(ElementTypes.MolecularState,BlockType.MOLECULE_STATE);
			put(ElementTypes.Particle,BlockType.PARTICLE);
			put(ElementTypes.Solid,BlockType.SOLID);
			put(ElementTypes.NonRadiativeTransition,BlockType.NONRADIATIVE);
			put(ElementTypes.RadiativeTransition,BlockType.RADIATIVE);
			put(ElementTypes.CollisionalTransition,BlockType.COLLISION);
			put(ElementTypes.Source,BlockType.SOURCE);
			put(ElementTypes.Function,BlockType.FUNCTION);
			put(ElementTypes.Method,BlockType.METHOD);
		}};
		/**
		 * Generate docInfo from XSAMSIOModel
		 * @param doc
		 * @return
		 */
		private DocInfo getDocInfo(XSAMSIOModel doc){
			DocInfo docInfo = new DocInfo();
			//File info
			docInfo.setSize(BigInteger.valueOf(doc.getSize()));
			docInfo.setRowCount(BigInteger.valueOf(doc.getLineCount()));

			//Blocks count
			DocumentElementsLocator loc = doc.getElementsLocator();
			if (loc!=null){
				for (ElementTypes element:ElementTypes.values()){
					int value = loc.getElements(element).size();
					BlockCountType bct = new BlockCountType();
					bct.setType(BlockElementType.get(element));
					bct.setValue(BigInteger.valueOf(value));	
					docInfo.getBlockCounts().add(bct);
				}
			}

			return docInfo;
		}

		/**
		 * Write XML report to file specified in constructor
		 */
		public int write(){
			try {
				JAXBContext jc = JAXBContext.newInstance(Report.class);
				Marshaller m = jc.createMarshaller();
				m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

				m.marshal(report, outFile);
			} catch (JAXBException e) {
				e.printStackTrace();
			}


			return 0;
		}

}
