/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.geog.geocog.csml.model;

/**
 *
 * @author Sandro
 */
public class CSMLUndefinedPoint extends CSMLPoint {

    private String propertyid;
    private CSMLConcept property;

    //protected CSMLUndefinedPoint(String id, CSMLModel model, String domid, String propid) {
   //     super(id, model, domid);
   //     this.propertyid = propid;
   // }

    protected CSMLUndefinedPoint(String id, CSMLModel model, CSMLDomain domain, CSMLConcept property) {
        super(id, model, domain);
        if (!model.containsCSMLElement(property)) {
            throw new RuntimeException("Doman and point must be in the same model");
        }
        this.property = property;
    }

    @Deprecated
    public void postLoadSetup() {
        super.postLoadSetup();
        property = model.getConcept(propertyid);
    }

    public CSMLConcept getReferedProperty() {
        return property;
    }

    public String getReferedPropertyId() {
        return propertyid;
    }

    @Override
    public String toCSML() {
        String csml = "";
        csml += "	<csml:UndefinedPoint csml:ID=\"" + getId() + "\" csml:domainID=\"" + getDomainId() + "\" csml:property=\"" + property.getId() + "\">\n";
        csml += "	</csml:UndefinedPoint>\n";
        return csml;
    }
}
