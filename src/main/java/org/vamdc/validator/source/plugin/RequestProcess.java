package org.vamdc.validator.source.plugin;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vamdc.tapservice.vss2.LogicNode;
import org.vamdc.tapservice.vss2.Query;
import org.vamdc.tapservice.vss2.RestrictExpression;
import org.vamdc.tapservice.vss2.impl.QueryImpl;
import org.vamdc.tapservice.api.RequestInterface;
import org.vamdc.dictionary.Requestable;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.xsams.XSAMSData;
import org.vamdc.xsams.util.XSAMSDataImpl;

/*
 * Here we keep request data
 * */
public class RequestProcess implements RequestInterface {
	private XSAMSDataImpl xsamsroot;
	private ObjectContext context;
	private Query query;
	public boolean Valid;
	private Date reqstart;
	private Logger logger;

	public RequestProcess (XSAMSDataImpl xsamsroot,ObjectContext context,Query queryParser){
		initRequest(xsamsroot,context,queryParser);
	}

	public RequestProcess (String query, Collection<Restrictable> collection){
		initRequest(
				new XSAMSDataImpl(),
				DataContext.createDataContext(),
				new QueryImpl(query,collection));
	}

	private void initRequest(XSAMSDataImpl xsamsroot,ObjectContext context,Query queryParser){
		this.xsamsroot = xsamsroot;
		this.context = context;
		this.query = queryParser;
		this.Valid = false;
		if (query!=null && query.getRestrictsList()!=null)
			this.Valid = query.getRestrictsList().size()>0;

			logger = LoggerFactory.getLogger("org.vamdc.tapservice");
			reqstart = new Date();
	}

	public void finishRequest(){
		//Called before sending data to user, to put time in log
		if (query!=null)
			logger.info("Request query "+query.getQuery()+" finished in "+(new Double(new Date().getTime()-reqstart.getTime()))/1000.0 + "s");
		if (query!=null && query.getRestrictsTree()!=null){
			logger.debug("Tree string:"+query.getRestrictsTree().toString());
			for (RestrictExpression re:query.getRestrictsList()){
				logger.debug("Query param:"+re.getColumnName()+"comp"+re.getOperator()+"val"+re.getValue());
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.vamdc.tapservice.RequestInterface#getXsamsroot()
	 */
	public XSAMSData getXsamsroot() {
		return xsamsroot;
	}
	
	public org.vamdc.xsams.schema.XSAMSData getJaxbXSAMSData(){
		return xsamsroot;
	}

	/* (non-Javadoc)
	 * @see org.vamdc.tapservice.RequestInterface#getCayenneContext()
	 */
	public ObjectContext getCayenneContext() {
		return context;
	}

	/* (non-Javadoc)
	 * @see org.vamdc.tapservice.RequestInterface#getRestricts()
	 */
	public List<RestrictExpression> getRestricts() {
		return query.getRestrictsList();
	}

	/* (non-Javadoc)
	 * @see org.vamdc.tapservice.RequestInterface#getRestrictsTree()
	 */
	public LogicNode getRestrictsTree(){
		return query.getRestrictsTree();
	}

	/* (non-Javadoc)
	 * @see org.vamdc.tapservice.api.RequestInterface#checkRequestable(Requestable)
	 * Also checks if tapservice is configured to force source references
	 */
	public boolean checkBranch(Requestable branch){
		return query.checkSelectBranch(branch);
	}

	/* (non-Javadoc)
	 * @see org.vamdc.tapservice.RequestInterface#isValid()
	 */
	public boolean isValid() {
		return Valid;
	}

	/*
	 * Get user's query
	 */
	public String getQueryString(){
		return query.getQuery();
	}

	/* (non-Javadoc)
	 * @see org.vamdc.tapservice.RequestInterface#getLogger()
	 */
	public Logger getLogger(Class<?> className){
		if (className == null)
			return logger;
		return LoggerFactory.getLogger(className);
	}

	public String toString(){
		return "Request query "+query.toString();
	}

}
