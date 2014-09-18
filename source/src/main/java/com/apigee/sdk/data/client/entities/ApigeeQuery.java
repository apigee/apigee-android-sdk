package com.apigee.sdk.data.client.entities;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by ApigeeCorporation on 8/21/14.
 */
public class ApigeeQuery {

    public enum RelationalOperator {
        kApigeeQueryOperationEquals,
        kApigeeQueryOperationLessThan,
        kApigeeQueryOperationLessThanOrEqualTo,
        kApigeeQueryOperationGreaterThan,
        kApigeeQueryOperationGreaterThanOrEqualTo
    };

    private String urlTerms = "";
    private ArrayList<String> requirements = new ArrayList<String>();

    public ApigeeQuery() {
    }

    public ApigeeQuery(Map<String,Object> dictParams) {
        if( dictParams != null ) {
            Set<String> paramKeys = dictParams.keySet();
            for( String paramKey : paramKeys) {
                if( !paramKey.equalsIgnoreCase("type") ) {
                    Object value = dictParams.get(paramKey);
                    if( value instanceof Number ) {
                        Number valueAsNumber = (Number)value;
                        this.addRequiredOperation(paramKey,RelationalOperator.kApigeeQueryOperationEquals,valueAsNumber.intValue());
                    } else if( value instanceof String ) {
                        String valueAsString = (String)value;
                        this.addRequiredOperation(paramKey,RelationalOperator.kApigeeQueryOperationEquals,valueAsString);
                    } else {
                        // TODO: add log message indicating that the key is not being used to construct the query
                    }
                }
            }
        }
    }

    private static String relationalOperatorToString(RelationalOperator relationalOperator) {
        String relationalOperatorAsString = null;
        switch(relationalOperator) {
            case kApigeeQueryOperationEquals:{
                relationalOperatorAsString = "=";
                break;
            }
            case kApigeeQueryOperationGreaterThan:{
                relationalOperatorAsString = ">";
                break;
            }
            case kApigeeQueryOperationGreaterThanOrEqualTo:{
                relationalOperatorAsString = ">=";
                break;
            }
            case kApigeeQueryOperationLessThan:{
                relationalOperatorAsString = "<";
                break;
            }
            case kApigeeQueryOperationLessThanOrEqualTo:{
                relationalOperatorAsString = "<=";
                break;
            }
        }
        return relationalOperatorAsString;
    }

    public void setConsumer(String consumer) {
        this.addURLTerm("consumer",consumer);
    }

    public void setLastUUID(String UUID) {
        this.addURLTerm("last",UUID);
    }

    public void setTime(Long time) {
        this.addURLTerm("time",time.toString());
    }

    public void setPrev(int prev) {
        this.addURLTerm("prev",Integer.toString(prev));
    }

    public void setNext(int next) {
        this.addURLTerm("next",Integer.toString(next));
    }

    public void setLimit(int limit) {
        this.addURLTerm("limit",Integer.toString(limit));
    }

    public void setPos(String pos) {
        this.addURLTerm("pos",pos);
    }

    public void setUpdate(Boolean update) {
        this.addURLTerm("update",update.toString());
    }

    public void setSynch(Boolean synch) {
        this.addURLTerm("synchronized",synch.toString());
    }

    public void addRequirement(String requirement) {
        if( requirement != null ) {
            this.requirements.add(requirement);
        }
    }

    public void addRequiredOperation(String term, RelationalOperator relationalOperator, int valueInt) {
        if( term != null ) {
            String relationalOperatorAsString = relationalOperatorToString(relationalOperator);
            if( relationalOperatorAsString != null ) {
                String assembledRequirement = term + " " + relationalOperatorAsString + " "  + valueInt;
                this.addRequirement(assembledRequirement);
            }
        }
    }

    public void addRequiredOperation(String term, RelationalOperator relationalOperator, String valueString) {
        if( term != null && valueString != null ) {
            String relationalOperatorAsString = relationalOperatorToString(relationalOperator);
            if( relationalOperatorAsString != null ) {
                if( term.equalsIgnoreCase("cursor") || term.equalsIgnoreCase("limit") ) {
                    this.addURLTerm(term,valueString);
                } else if( term.equalsIgnoreCase("ql") ) {
                    this.addRequirement(valueString);
                } else {
                    String assembledRequirement = term + relationalOperatorAsString + "'" + valueString + "'";
                    this.addRequirement(assembledRequirement);
                }
            }
        }
    }

    public void addURLTerm(String urlTerm, String equals) {
        if( urlTerm != null && equals != null ) {
            if( urlTerm.equalsIgnoreCase("ql") ) {
                this.addRequirement(equals);
            } else {
                if( !urlTerms.isEmpty() ) {
                    urlTerms += "&";
                }
                String escapedURLTerm = Uri.encode(urlTerm);
                String escapedEquals = Uri.encode(equals);
                urlTerms += escapedURLTerm + "=" + escapedEquals;
            }
        }
    }

    public void addRequiredContains(String term, String value) {
        if( term != null && value != null ) {
            String assembledRequirement = term;
            assembledRequirement += " contains ";
            assembledRequirement += "'" + value + "'";
            this.addRequirement(assembledRequirement);
        }
    }

    public void addRequiredIn(String term, int low, int high) {
        if( term != null ) {
            String assembledRequirement = term;
            assembledRequirement += " in ";
            assembledRequirement += Integer.toString(low) + "," + Integer.toString(high);
            this.addRequirement(assembledRequirement);
        }
    }

    public void addRequiredWithin(String term, float latitude, float longitude, float distance) {
        if( term != null ) {
            String assembledRequirement = term;
            assembledRequirement += " within ";
            assembledRequirement += Float.toString(distance) + " of " + Float.toString(latitude) + "," + Float.toString(longitude);
            this.addRequirement(assembledRequirement);
        }
    }

    public String getURLAppend() {
        // assemble a url append for all the requirements
        String assembledURL = "";
        // start with the ql term
        if( this.requirements.size() > 0 ) {
            String qlString = "";
            for( int i = 0; i < this.requirements.size(); i++ ) {
                if( i > 0 && !qlString.isEmpty() ) {
                    qlString += " and ";
                }
                qlString += requirements.get(i);
            }
            String escapedQLString = Uri.encode(qlString);
            assembledURL += "ql=" + escapedQLString;
        }
        if( !this.urlTerms.isEmpty() ) {
            if( assembledURL.isEmpty() ) {
                assembledURL += this.urlTerms;
            } else {
                assembledURL += "&" + this.urlTerms;
            }
        }
        return  assembledURL;
    }
}
