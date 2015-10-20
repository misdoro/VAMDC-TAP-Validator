package org.vamdc.validator.source.plugin;

import java.util.Collection;
import java.util.Date;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vamdc.tapservice.vss2.LogicNode;
import org.vamdc.tapservice.vss2.Query;
import org.vamdc.tapservice.vss2.RestrictExpression;
import org.vamdc.tapservice.vss2.VSSParser;
import org.vamdc.tapservice.api.RequestInterface;
import org.vamdc.validator.Setting;
import org.vamdc.dictionary.Requestable;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.xsams.XSAMSManager;
import org.vamdc.xsams.schema.XSAMSData;
import org.vamdc.xsams.util.XSAMSManagerImpl;

/**
 * Request process implementation, similar to java vamdc-tap webservice
 *
 */
public class RequestProcess implements RequestInterface {
	private XSAMSManager xsamsroot;
	private ObjectContext context;
	private Query query;
	public boolean valid;
	private Date reqstart;
	private Logger logger;

	public RequestProcess (XSAMSManager xsamsroot,ObjectContext context,Query queryParser){
		initRequest(xsamsroot,context,queryParser);
	}

	
	public RequestProcess (String query, Collection<Restrictable> collection){
		initRequest(
				new XSAMSManagerImpl(),
				initCayenneContext(),
				VSSParser.parse(query,collection));
	}


	private ObjectContext initCayenneContext() {
		String cayenneSuffix=Setting.PluginCayenneSuffix.getValue();
		String cayenneXML;
		
		if (cayenneSuffix!=null && cayenneSuffix.length()>0){
			cayenneXML="cayenne-"+cayenneSuffix+".xml";
		}else{
			cayenneXML="cayenne-DBNode.xml";
		}
		
		ServerRuntime cayenneRuntime = new ServerRuntime(cayenneXML);
        ObjectContext context = cayenneRuntime.getContext();
		return context;
	}

	private void initRequest(XSAMSManager xsamsroot,ObjectContext context,Query queryParser){
		this.xsamsroot = xsamsroot;
		this.context = context;
		this.query = queryParser;
		this.valid = false;
		if (query != null && query.getRestrictsList() != null)
			this.valid = (query.getRestrictsList().size() > 0) 
			|| query.getQuery().trim().toLowerCase().startsWith("select species");

			logger = LoggerFactory.getLogger("org.vamdc.tapservice");
			reqstart = new Date();
	}

	public void finishRequest(){
		//Called before sending data to user, to put time in log
		if (query!=null)
			logger.info("Request query "+query.getQuery()+" finished in "+(new Date().getTime()-reqstart.getTime())/1000.0 + "s");
		if (query!=null && query.getRestrictsTree()!=null){
			logger.debug("Tree string:"+query.getRestrictsTree().toString());
			for (RestrictExpression re:query.getRestrictsList()){
				logger.debug("Query param:"+re.getColumnName()+"comp"+re.getOperator()+"val"+re.getValue());
			}
		}
	}

	@Override
	public XSAMSManager getXsamsManager() {
		return xsamsroot;
	}
	
	public XSAMSData getJaxbXSAMSData(){
		return (XSAMSData) xsamsroot;
	}

	@Override
	public ObjectContext getCayenneContext() {
		return context;
	}

	
	@Override
	public Collection<RestrictExpression> getQueryKeywords() {
		return query.getRestrictsList();
	}

	@Override
	public LogicNode getQueryTree(){
		return query.getRestrictsTree();
	}

	@Override
	public boolean checkBranch(Requestable branch){
		return query.checkSelectBranch(branch);
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public String getQueryString(){
		return query.getQuery();
	}


	public String toString(){
		return "Request query "+query.toString();
	}

	@Override
	public Query getQuery() {
		return query;
	}

	@Override
	public void setLastModified(Date date) {
	}

}
