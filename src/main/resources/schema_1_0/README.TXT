Schema design policies in application to XSAMS (proposal).
Edit this note if you have something to add.

* Always put meaningful commit messages, describing the whole set of changes.
	"svn diff" before commit would help in writing such message.

* ID and IDREF:
	
	* Define ID types in typesAttributes.xsd,
	* Always define ID prefix letter (or several letters, if we run out of them) 
		to avoid collisions between different IDs in instance documents
	* Always document the ID and what would be it's use.
	* in documentation of type, it would be good to name places of schema where from that id may be referred
		If specific ID is not planned to be referenced from other parts of schema, document it.
	
	* Never define attributes/elements of type IDREF without specifyng where they should refer!
		Not everyone is as smart as you, so to deny him making stupid things, don't do this.
	* Normally define IDREF type in typesAttributes.xsd, 
		define IDREF type in the same part where ID is defined only if it is used only in that part of schema.
	* Always define ID prefix letter
	* Document where the ID is defined, if it is defined separately
	
	
* Avoid using <attribute ref="attributeName"/> and <element ref="elementName"/>
	Instead define simple type and define attribute as <xs:attribute name="attributeName" type="attributeSpecificType"/>
	This is required to avoid the need to include xsams default namespace name 
		in each of the occurrences of such elements/attributes.
		
* Create at least basic built-in documentation for added types/elements, so people would understand what you meaned.
	That would also make the output of automatic xsd documentation tools meaningful.

