package org.vamdc.validator.interfaces;

import java.util.List;

public interface DocumentElementsLocator {
	public List<DocumentElement> getElements(DocumentElement.ElementTypes type);
	public List<DocumentError> getErrors();
}
