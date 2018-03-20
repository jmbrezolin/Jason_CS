/**
 * Copyright (c) 2009, Benjamin Adams All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of Benjamin Adams nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY Benjamin Adams ''AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL Benjamin Adams BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Ben
 */
package edu.geog.geocog.csml.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

// Conceptual Space Model maintains the current state of the Conceptual Space in memory. i.e., the 
// domains, concepts, contrast classes, instances, and contexts.
public class CSMLModel {

    public static final int MODEL_OK = 0;
    // 5-tuple from Conceptual Space Algebra
    private Hashtable<String, CSMLDomain> domains = new Hashtable<String, CSMLDomain>();
    private Hashtable<String, CSMLConcept> concepts = new Hashtable<String, CSMLConcept>();
    private Hashtable<String, CSMLContrastClass> contrastClasses = new Hashtable<String, CSMLContrastClass>();
    private Hashtable<String, CSMLInstance> instances = new Hashtable<String, CSMLInstance>();
    private Hashtable<String, CSMLContext> contexts = new Hashtable<String, CSMLContext>();
    private HashMap<String, CSMLElement> allElements = new HashMap<String, CSMLElement>();
    private String csmlBase = "";

    public CSMLModel() {
    }

    public void setcsmlBase(String base, boolean refactor) {
        if (refactor) {
            String oldBase = csmlBase;
            // TODO update all elements in model with new Id values
        }
        csmlBase = base;
    }

    public void setcsmlBase(String base) {
        setcsmlBase(base, true);
    }

    public String getcsmlBase() {
        return csmlBase;
    }

    public void addDomain(String domainId, CSMLDomain domain) {
        domains.put(domainId, domain);
        allElements.put(domainId, domain);
    }

    public void addDomain(CSMLDomain domain) {
        addDomain(domain.getId(), domain);
    }

    public void removeDomain(String domainId) {
        domains.remove(domainId);
        allElements.remove(domainId);
    }

    public void removeDomain(CSMLDomain d) {
        domains.remove(d.getId());
        allElements.remove(d.getId());
    }

    public CSMLDomain getDomain(String domainId) {
        return domains.get(domainId);
    }

    public CSMLDomain[] getDomains() {
        return domains.values().toArray(new CSMLDomain[0]);
    }

    public int getNumberOfDomains() {
        return domains.size();
    }

    public CSMLPoint getCSMLPoint(String uid) {
        return (CSMLPoint) allElements.get(uid);
    }

    public void addConcept(String conceptId, CSMLConcept concept) {
        concepts.put(conceptId, concept);
        allElements.put(conceptId, concept);
    }

    public void addConcept(CSMLConcept concept) {
        addConcept(concept.getId(), concept);
    }

    public void removeConcept(String conceptId) {
        concepts.remove(conceptId);
        allElements.remove(conceptId);
    }

    public void removeConcept(CSMLConcept concept) {
        concepts.remove(concept.getId());
        allElements.remove(concept.getId());
    }

    public CSMLConcept getConcept(String conceptId) {
        return concepts.get(conceptId);
    }

    public CSMLConcept[] getConcepts() {
        return concepts.values().toArray(new CSMLConcept[0]);
    }

    public CSMLElement getElement(String elementId) {
        return allElements.get(elementId);
    }

    public int getNumberOfConcepts() {
        return concepts.size();
    }

    public void addContrastClass(String ccId, CSMLContrastClass cc) {
        contrastClasses.put(ccId, cc);
        allElements.put(ccId, cc);
    }

    public void addContrastClass(CSMLContrastClass cc) {
        addContrastClass(cc.getId(), cc);
    }

    public void removeContrastClass(String ccId) {
        contrastClasses.remove(ccId);
        allElements.remove(ccId);
    }

    public void removeContrastClass(CSMLContrastClass cc) {
        contrastClasses.remove(cc.getId());
        allElements.remove(cc.getId());
    }

    public CSMLContrastClass getContrastClass(String ccId) {
        return contrastClasses.get(ccId);
    }

    public CSMLContrastClass[] getContrastClasses() {
        return contrastClasses.values().toArray(new CSMLContrastClass[0]);
    }

    public int getNumberOfContrastClasses() {
        return contrastClasses.size();
    }

    public void addInstance(String instanceId, CSMLInstance instance) {
        instances.put(instanceId, instance);
        allElements.put(instanceId, instance);
    }

    public void addInstance(CSMLInstance instance) {
        addInstance(instance.getId(), instance);
    }

    public void removeInstance(String instanceId) {
        instances.remove(instanceId);
        allElements.remove(instanceId);
    }

    public void removeInstance(CSMLInstance instance) {
        instances.remove(instance.getId());
        allElements.remove(instance.getId());
    }

    public CSMLInstance getInstance(String instanceId) {
        return instances.get(instanceId);
    }

    public CSMLInstance[] getInstances() {
        return instances.values().toArray(new CSMLInstance[0]);
    }

    public CSMLInstance[] getInstancesWithPointsInDomain(String domainId) {
        CSMLDomain d = getDomain(domainId);
        if (d == null) {
            return null;
        }
        ArrayList<CSMLInstance> iList = new ArrayList<CSMLInstance>();
        for (CSMLInstance i : getInstances()) {
            if (i.hasPointInDomain(domainId)) {
                iList.add(i);
            }
        }
        return iList.toArray(new CSMLInstance[0]);
    }

    public int getNumberOfInstances() {
        return instances.size();
    }

    public void addContext(String contextId, CSMLContext context) {
        contexts.put(contextId, context);
        allElements.put(contextId, context);
    }

    public void addContext(CSMLContext context) {
        addContext(context.getId(), context);
    }

    public void removeContext(String contextId) {
        contexts.remove(contextId);
        allElements.remove(contextId);
    }

    public void removeContext(CSMLContext context) {
        contexts.remove(context.getId());
        allElements.remove(context.getId());
    }

    public CSMLContext getContext(String contextId) {
        return contexts.get(contextId);
    }

    public CSMLContext[] getContexts() {
        return contexts.values().toArray(new CSMLContext[0]);
    }

    public CSMLQualityDimensionTypeContext[] getQualityDimensionTypeContexts() {
        CSMLContext[] cArray = getContexts();
        ArrayList<CSMLQualityDimensionTypeContext> qdtcArrayList = new ArrayList<CSMLQualityDimensionTypeContext>();
        for (CSMLContext c : cArray) {
            if (c instanceof CSMLQualityDimensionTypeContext) {
                qdtcArrayList.add((CSMLQualityDimensionTypeContext) c);
            }
        }
        return qdtcArrayList.toArray(new CSMLQualityDimensionTypeContext[0]);
    }

    // get the quality dimension type contexts for the given domain
    public CSMLQualityDimensionTypeContext[] getQualityDimensionTypeContexts(String domainId) {
        CSMLContext[] cArray = getContexts();
        ArrayList<CSMLQualityDimensionTypeContext> qdtcArrayList = new ArrayList<CSMLQualityDimensionTypeContext>();
        for (CSMLContext c : cArray) {
            if (c instanceof CSMLQualityDimensionTypeContext) {
                CSMLQualityDimensionTypeContext qdtc = (CSMLQualityDimensionTypeContext) c;
                if (qdtc.getDomainId().equals(domainId)) {
                    qdtcArrayList.add(qdtc);
                }
            }
        }
        return qdtcArrayList.toArray(new CSMLQualityDimensionTypeContext[0]);
    }

    public CSMLDomainTypeContext[] getDomainTypeContexts() {
        CSMLContext[] cArray = getContexts();
        ArrayList<CSMLDomainTypeContext> dtcArrayList = new ArrayList<CSMLDomainTypeContext>();
        for (CSMLContext c : cArray) {
            if (c instanceof CSMLDomainTypeContext) {
                dtcArrayList.add((CSMLDomainTypeContext) c);
            }
        }
        return dtcArrayList.toArray(new CSMLDomainTypeContext[0]);
    }

    public CSMLInstanceTypeContext[] getInstanceTypeContexts() {
        CSMLContext[] cArray = getContexts();
        ArrayList<CSMLInstanceTypeContext> itcArrayList = new ArrayList<CSMLInstanceTypeContext>();
        for (CSMLContext c : cArray) {
            if (c instanceof CSMLInstanceTypeContext) {
                itcArrayList.add((CSMLInstanceTypeContext) c);
            }
        }
        return itcArrayList.toArray(new CSMLInstanceTypeContext[0]);
    }

    public CSMLConceptTypeContext[] getConceptTypeContexts() {
        CSMLContext[] cArray = getContexts();
        ArrayList<CSMLConceptTypeContext> ctcArrayList = new ArrayList<CSMLConceptTypeContext>();
        for (CSMLContext c : cArray) {
            if (c instanceof CSMLConceptTypeContext) {
                ctcArrayList.add((CSMLConceptTypeContext) c);
            }
        }
        return ctcArrayList.toArray(new CSMLConceptTypeContext[0]);
    }

    public CSMLContrastClassTypeContext[] getContrastClassTypeContexts() {
        CSMLContext[] cArray = getContexts();
        ArrayList<CSMLContrastClassTypeContext> cctcArrayList = new ArrayList<CSMLContrastClassTypeContext>();
        for (CSMLContext c : cArray) {
            if (c instanceof CSMLContrastClassTypeContext) {
                cctcArrayList.add((CSMLContrastClassTypeContext) c);
            }
        }
        return cctcArrayList.toArray(new CSMLContrastClassTypeContext[0]);
    }

    public int getNumberOfContexts() {
        return contexts.size();
    }

    public ArrayList<CSMLContrastClass> contrastClassesOfDomain(String domainId) {
        ArrayList<CSMLContrastClass> ccList = new ArrayList<CSMLContrastClass>();
        for (Enumeration<CSMLContrastClass> e = contrastClasses.elements(); e.hasMoreElements();) {
            CSMLContrastClass cc = e.nextElement();
            if (cc.getDomain().equals(domainId)) {
                ccList.add(cc);
            }
        }
        return ccList;
    }

    /**
     * public ArrayList<Context> getDomainTypeContexts(String domainId) {
     * ArrayList<Context> cList = new ArrayList<Context>(); for
     * (Enumeration<Context> e = contexts.elements(); e.hasMoreElements(); ) {
     * CSMLContext c = e.nextElement(); if (c.getType() ==
     * CSMLContext.DOMAIN_TYPE) { cList.add(c); } } return cList; }
     *
     */
    /**
     * public ArrayList<Context> getQualityDimensionTypeContexts(String
     * domainId) { ArrayList<Context> cList = new ArrayList<Context>(); for
     * (Enumeration<Context> e = contexts.elements(); e.hasMoreElements(); ) {
     * CSMLContext c = e.nextElement(); if (c.getType() ==
     * CSMLContext.QUALITY_DIMENSION_TYPE) { cList.add(c); } } return cList; }
     *
     */
    public ArrayList<CSMLDomain> getIntersectionOfDomains(CSMLInstance i1, CSMLInstance i2) {
        ArrayList<CSMLDomain> shared = new ArrayList<CSMLDomain>();
        String[] domainIds = i1.getDomainIds();
        for (int i = 0; i < domainIds.length; i++) {
            if (i2.hasPointInDomain(domainIds[i])) {
                shared.add(domains.get(domainIds[i]));
            }
        }
        return shared;
    }

    public ArrayList<CSMLDomain> getIntersectionOfDomains(CSMLConcept c1, CSMLConcept c2) {
        ArrayList<CSMLDomain> shared = new ArrayList<CSMLDomain>();
        String[] domainIds = c1.getDomainIds();
        for (int i = 0; i < domainIds.length; i++) {
            if (c2.hasRegionInDomain(domainIds[i])) {
                shared.add(domains.get(domainIds[i]));
            }
        }
        return shared;
    }

    public ArrayList<CSMLDomain> getUnionOfDomains(CSMLInstance i1, CSMLInstance i2) {
        ArrayList<CSMLDomain> shared = new ArrayList<CSMLDomain>();
        // TODO

        return shared;
    }

    public ArrayList<CSMLDomain> getUnionOfDomains(CSMLConcept c1, CSMLConcept c2) {
        ArrayList<CSMLDomain> shared = new ArrayList<CSMLDomain>();
        // TODO

        return shared;
    }

    // returns the first domain containing the specified quality dimension (no guarantee of order of domains checked)
    public CSMLDomain getDomainOfQDim(String qualityDimensionId) {
        for (Enumeration<CSMLDomain> e = domains.elements(); e.hasMoreElements();) {
            CSMLDomain d = e.nextElement();
            if (d.getQualityDimension(qualityDimensionId) != null) {
                return d;
            }
        }
        return null;
    }

    // returns all domains containing the specified quality dimension
    public ArrayList<CSMLDomain> getDomainsOfQDim(String qualityDimensionId) {
        ArrayList<CSMLDomain> dList = new ArrayList<CSMLDomain>();
        for (Enumeration<CSMLDomain> e = domains.elements(); e.hasMoreElements();) {
            CSMLDomain d = e.nextElement();
            if (d.getQualityDimension(qualityDimensionId) != null) {
                dList.add(d);
            }
        }
        return dList;
    }

    public int compileModel() {
        return MODEL_OK;
    }

    public String toCSML() {
        String csml = "";
        csml += "<csml:CSML ";
        if (!csmlBase.equals("")) {
            csml += "xml:base=\"" + csmlBase + "\"\n";
            csml += "           ";
        }
        csml += "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n";
        csml += "           xmlns:csml=\"http://geocog.geog.ucsb.edu/csml/csml-ns#\"\n";
        Enumeration<String> keys;
        keys = domains.keys();
        while (keys.hasMoreElements()) {
            csml += domains.get(keys.nextElement()).toCSML();
        }
        keys = concepts.keys();
        while (keys.hasMoreElements()) {
            csml += concepts.get(keys.nextElement()).toCSML();
        }
        keys = instances.keys();
        while (keys.hasMoreElements()) {
            csml += instances.get(keys.nextElement()).toCSML();
        }
        keys = contrastClasses.keys();
        while (keys.hasMoreElements()) {
            csml += contrastClasses.get(keys.nextElement()).toCSML();
        }
        keys = contexts.keys();
        while (keys.hasMoreElements()) {
            csml += contexts.get(keys.nextElement()).toCSML();
        }
        csml += "</csml:CSML>\n";
        return csml;
    }

    public boolean containsURI(String uri) {
        if (allElements.containsKey(uri)) {
            return true;
        }
        CSMLDomain[] dArray = getDomains();
        for (int i = 0; i < dArray.length; i++) {
            if (dArray[i].getQualityDimension(uri) != null) {
                return true;
            }
        }
        CSMLConcept[] cArray = getConcepts();
        for (int i = 0; i < cArray.length; i++) {
            if (cArray[i].getRegionById(uri) != null) {
                return true;
            }
        }
        CSMLInstance[] iArray = getInstances();
        for (int i = 0; i < iArray.length; i++) {
            if (iArray[i].getPointById(uri) != null) {
                return true;
            }
        }
        return false;
    }

    public boolean containsId(String id) {
        return containsURI(id);
    }

//    public void refactorQDimId(String origId, String newId) {
//        for (Enumeration<CSMLDomain> e = domains.elements(); e.hasMoreElements();) {
//            CSMLDomain d = e.nextElement();
//            CSMLQualityDimension qDim = d.getQualityDimension(origId);
//            if (qDim != null) {
//                d.removeQualityDimension(qDim);
//                qDim.setId(newId);
//                d.addQualityDimension(qDim);
//            }
//        }
//
//        // refactor quality dimension-type contexts
//        for (Enumeration<CSMLContext> e = contexts.elements(); e.hasMoreElements();) {
//            CSMLContext c = e.nextElement();
//            if (c instanceof CSMLQualityDimensionTypeContext) {
//                CSMLQualityDimensionTypeContext dc = (CSMLQualityDimensionTypeContext) c;
//                Double w = dc.getWeight(origId);
//                if (w != null) {
//                    dc.removeWeight(origId);
//                    dc.addWeight(newId, w);
//                }
//            }
//        }
//    }
//    public void refactorDomainId(String origId, String newId) {
//        CSMLDomain d = getDomain(origId);
//        if (d != null) {
//            removeDomain(d);
//            d.setId(newId);
//            addDomain(d);
//        }
//
//        // refactor concepts
//        for (Enumeration<CSMLConcept> e = concepts.elements(); e.hasMoreElements();) {
//            CSMLConcept c = e.nextElement();
//            CSMLRegion r = c.getRegion(origId);
//            if (r != null) {
//                c.removeRegion(r);
//                r.setDomainId(newId);
//                c.addRegion(r);
//            }
//        }
//
//        // refactor instances
//        for (Enumeration<CSMLInstance> e = instances.elements(); e.hasMoreElements();) {
//            CSMLInstance i = e.nextElement();
//            CSMLPoint p = i.getPoint(origId);
//            if (p != null) {
//                i.removePoint(p);
//                p.setDomainId(newId);
//                i.addPoint(p);
//            }
//        }
//
//        // refactor domain-type contexts
//        for (Enumeration<CSMLContext> e = contexts.elements(); e.hasMoreElements();) {
//            CSMLContext c = e.nextElement();
//            if (c instanceof CSMLDomainTypeContext) {
//                CSMLDomainTypeContext dc = (CSMLDomainTypeContext) c;
//                Double w = dc.getWeight(origId);
//                if (w != null) {
//                    dc.removeWeight(origId);
//                    dc.addWeight(newId, w);
//                }
//            }
//        }
//    }
    public void refactorInstanceId(String origId, String newId) {
        CSMLInstance i = getInstance(origId);
        if (i != null) {
            removeInstance(i);
            i.setId(newId);
            addInstance(i);
        }

        // refactor prototypes of concepts
        for (Enumeration<CSMLConcept> e = concepts.elements(); e.hasMoreElements();) {
            CSMLConcept c = e.nextElement();
            if (c.getPrototypeId().equals(origId)) {
                c.setPrototypeId(newId);
            }
        }

        // refactor instance-type contexts
        for (Enumeration<CSMLContext> e = contexts.elements(); e.hasMoreElements();) {
            CSMLContext c = e.nextElement();
            if (c instanceof CSMLInstanceTypeContext) {
                CSMLInstanceTypeContext ic = (CSMLInstanceTypeContext) c;
                Double w = ic.getWeight(origId);
                if (w != null) {
                    ic.removeWeight(origId);
                    ic.addWeight(newId, w);
                }
            }
        }
    }

    /**
     * Do not test for point containment. Search for direct link between regions
     * and points
     *
     * @return
     */
//    public Collection<CSMLRegion> locateConceptsHavingPoint(String uid) {
//        CSMLPoint point = locatePoint(uid);
//        
//        point.
//    
//    }
    public boolean containsCSMLElement(CSMLElement elem) {
        return allElements.containsKey(elem.getId());
    }

    public CSMLConcept createCSMLConcept(String id) {
        CSMLConcept entity = new CSMLConcept(id, this);
        concepts.put(id, entity);
        allElements.put(id, entity);
        return entity;
    }

    public CSMLConceptTypeContext createCSMLConceptTypeContext(String id) {
        CSMLConceptTypeContext entity = new CSMLConceptTypeContext(id, this);
        contexts.put(id, entity);
        allElements.put(id, entity);
        return entity;
    }

    public CSMLContrastClassTypeContext createCSMLContrastClassTypeContext(String id) {
        CSMLContrastClassTypeContext entity = new CSMLContrastClassTypeContext(id, this);
        contexts.put(id, entity);
        allElements.put(id, entity);
        return entity;
    }

    public CSMLDomainTypeContext createCSMLDomainTypeContext(String id) {
        CSMLDomainTypeContext entity = new CSMLDomainTypeContext(id, this);
        contexts.put(id, entity);
        allElements.put(id, entity);
        return entity;
    }

    public CSMLInstanceTypeContext createCSMLInstanceTypeContext(String id) {
        CSMLInstanceTypeContext entity = new CSMLInstanceTypeContext(id, this);
        contexts.put(id, entity);
        allElements.put(id, entity);
        return entity;
    }

    public CSMLQualityDimensionTypeContext createCSMLQualityDimensionTypeContextLate(String id, String domainid) {
        CSMLQualityDimensionTypeContext entity = new CSMLQualityDimensionTypeContext(id, this, domainid);
        contexts.put(id, entity);
        allElements.put(id, entity);
        return entity;
    }

    public CSMLQualityDimensionTypeContext createCSMLQualityDimensionTypeContext(String id, CSMLDomain domain) {
        CSMLQualityDimensionTypeContext entity = new CSMLQualityDimensionTypeContext(id, this, domain);
        contexts.put(id, entity);
        allElements.put(id, entity);
        return entity;
    }

    public CSMLContrastClass createCSMLContrastClassLate(String id, String domainid) {
        CSMLContrastClass entity = new CSMLContrastClass(id, this, domainid);
        contrastClasses.put(id, entity);
        allElements.put(id, entity);
        return entity;
    }

    public CSMLContrastClass createCSMLContrastClass(String id, CSMLDomain domain) {
        CSMLContrastClass entity = new CSMLContrastClass(id, this, domain);
        contrastClasses.put(id, entity);
        allElements.put(id, entity);
        return entity;
    }

    public CSMLDomain createCSMLDomain(String id) {
        CSMLDomain entity = new CSMLDomain(id, this);
        domains.put(id, entity);
        allElements.put(id, entity);
        return entity;
    }

    public CSMLInstance createCSMLInstance(String id) {
        CSMLInstance entity = new CSMLInstance(id, this);
        instances.put(id, entity);
        allElements.put(id, entity);
        return entity;
    }

    public CSMLPoint createCSMLPoint(String id, CSMLDomain dom) {
        CSMLPoint entity = new CSMLPoint(id, this, dom);
        allElements.put(id, entity);
        return entity;
    }
    
    //Foi comentado

    //@Deprecated
  //  public CSMLPoint createCSMLPointLate(String id, String domainid) {
  //      CSMLPoint entity = new CSMLPoint(id, this, domainid);
  //      allElements.put(id, entity);
  //      return entity;
   // }

    public CSMLQualityDimension createCSMLQualityDimension(String id) {
        CSMLQualityDimension entity = new CSMLQualityDimension(id, this);
        allElements.put(id, entity);
        return entity;
    }

    @Deprecated
    public CSMLRegion createCSMLRegionLate(String id, String domainid) {
        CSMLRegion entity = new CSMLRegion(id, this, domainid);
        allElements.put(id, entity);
        return entity;
    }

    public CSMLRegion createCSMLRegion(String id, CSMLDomain domain) {
        CSMLRegion entity = new CSMLRegion(id, this, domain);
        allElements.put(id, entity);
        return entity;
    }

      //Foi comentado
    
    @Deprecated
//    public CSMLUndefinedPoint createCSMLUndefinedPointLate(String id, String domainid, String propertyid) {
//        CSMLUndefinedPoint entity = new CSMLUndefinedPoint(id, this, domainid, propertyid);
 //       allElements.put(id, entity);
//        return entity;
//    }

    public CSMLUndefinedPoint createCSMLUndefinedPoint(String id, CSMLDomain domain, CSMLConcept property) {
        CSMLUndefinedPoint entity = new CSMLUndefinedPoint(id, this, domain, property);
        allElements.put(id, entity);
        return entity;
    }

    @Deprecated
    public void postLoadSetupElements() {
        Collection<CSMLElement> elements = allElements.values();
        for (CSMLElement el : elements) {
            el.postLoadSetup();
        }
    }
}
