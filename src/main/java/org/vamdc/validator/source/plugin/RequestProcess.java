package org.vamdc.validator.source.plugin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vamdc.tapservice.VSS1Parser.LogicNode;
import org.vamdc.tapservice.VSS1Parser.QueryParser;
import org.vamdc.tapservice.VSS1Parser.RestrictExpression;
import org.vamdc.tapservice.api.RequestInterface;
import org.vamdc.tapservice.api.Dictionary.Requestable;
import org.vamdc.xsams.XSAMSData;
import org.vamdc.xsams.util.XSAMSDataImpl;

/*
 * Here we keep request data
 * */
public class RequestProcess implements RequestInterface {
	private XSAMSDataImpl xsamsroot;
	private ObjectContext context;
	private QueryParser query;
	public boolean Valid;
	private Date reqstart;
	private Logger logger;

	public RequestProcess (XSAMSDataImpl xsamsroot,ObjectContext context,QueryParser queryParser){
		initRequest(xsamsroot,context,queryParser);
	}

	public RequestProcess (String query, Map<String,Integer> restrictables){
		initRequest(
				new XSAMSDataImpl(),
				DataContext.createDataContext(),
				new QueryParser(query,restrictables));
	}

	private void initRequest(XSAMSDataImpl xsamsroot,ObjectContext context,QueryParser queryParser){
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
				logger.debug("Query param:"+re.getColumnName()+"comp"+re.getOperator()+"val"+re.getSingleRestrict());
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
	
	public boolean checkReturnable(String columnName){
		return query.checkColumn(columnName);
	}

	/* (non-Javadoc)
	 * @see org.vamdc.tapservice.api.RequestInterface#checkRequestable(Requestable)
	 * Also checks if tapservice is configured to force source references
	 */
	public boolean checkBranch(Requestable columnIndex){
		//ToDo: probably reorganize this block to use some tree structures 
		if (columnIndex==Requestable.Sources)
			return true;
		if (
				(columnIndex == Requestable.Collisions
						||columnIndex == Requestable.RadiativeTransitions
						||columnIndex == Requestable.NonRadiativeTransitions
				)&& query.checkColumn(Requestable.Processes.name().toUpperCase())
				)
			return true;
		if (columnIndex == Requestable.Species && query.checkColumn(Requestable.States.name().toUpperCase()))
			return true;
		return query.checkColumn(columnIndex.name().toUpperCase());
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
