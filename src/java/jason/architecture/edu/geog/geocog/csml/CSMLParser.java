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
package edu.geog.geocog.csml;

import edu.geog.geocog.csml.model.CSMLRegion;
import edu.geog.geocog.csml.model.CSMLInstance;
import edu.geog.geocog.csml.model.CSMLContrastClass;
import edu.geog.geocog.csml.model.CSMLContext;
import edu.geog.geocog.csml.model.CSMLPoint;
import edu.geog.geocog.csml.model.CSMLContrastClassTypeContext;
import edu.geog.geocog.csml.model.CSMLQualityDimension;
import edu.geog.geocog.csml.model.CSMLDomainTypeContext;
import edu.geog.geocog.csml.model.CSMLDomain;
import edu.geog.geocog.csml.model.CSMLInstanceTypeContext;
import edu.geog.geocog.csml.model.CSMLModel;
import edu.geog.geocog.csml.model.CSMLConcept;
import edu.geog.geocog.csml.model.CSMLConceptTypeContext;
import edu.geog.geocog.csml.model.CSML;
import edu.geog.geocog.csml.model.CSMLQualityDimensionTypeContext;
import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import edu.geog.geocog.csml.exceptions.CSMLParseException;
import edu.geog.geocog.csml.model.CSMLUndefinedPoint;
import java.net.URI;

public class CSMLParser {
    //namespace for csml:

    public static final String csmlNS = "http://geocog.geog.ucsb.edu/csml/csml-ns#";
    //legal csml tags
    public static final QName tagCSML = new QName(csmlNS, "CSML");
    public static final QName tagLabel = new QName(csmlNS, "Label");
    public static final QName tagDescription = new QName(csmlNS, "Description");
    public static final QName tagDomain = new QName(csmlNS, "Domain");
    public static final QName tagQualityDimension = new QName(csmlNS, "QualityDimension");
    public static final QName tagScale = new QName(csmlNS, "Scale");
    public static final QName tagRange = new QName(csmlNS, "Range");
    public static final QName tagMin = new QName(csmlNS, "Min");
    public static final QName tagMax = new QName(csmlNS, "Max");
    public static final QName tagUnits = new QName(csmlNS, "Units");
    public static final QName tagConcept = new QName(csmlNS, "Concept");
    public static final QName tagRegion = new QName(csmlNS, "Region");
    public static final QName tagAMatrix = new QName(csmlNS, "AMatrix");
    public static final QName tagQVector = new QName(csmlNS, "qVector");
    public static final QName tagBVector = new QName(csmlNS, "bVector");
    public static final QName tagContrastClass = new QName(csmlNS, "ContrastClass");
    public static final QName tagAVector = new QName(csmlNS, "aVector");
    public static final QName tagCCMin = new QName(csmlNS, "ccMin");
    public static final QName tagCCMax = new QName(csmlNS, "ccMax");
    public static final QName tagInstance = new QName(csmlNS, "Instance");
    public static final QName tagPoint = new QName(csmlNS, "Point");
    public static final QName tagUndefinedPoint = new QName(csmlNS, "UndefinedPoint");
    public static final QName tagPointValues = new QName(csmlNS, "PointValues");
    public static final QName tagContext = new QName(csmlNS, "Context");
    public static final QName tagWeight = new QName(csmlNS, "Weight");
    //legal csml attributes
    public static final QName attrID = new QName(csmlNS, "ID");
    public static final QName attrCID = new QName(csmlNS, "cID");
    public static final QName attrDomainID = new QName(csmlNS, "domainID");
    public static final QName attrPrototypeID = new QName(csmlNS, "prototypeID");
    public static final QName attrPropertyID = new QName(csmlNS, "propertyID");
    public static final QName attrCircular = new QName(csmlNS, "circular");
    public static final QName attrType = new QName(csmlNS, "type");
    //mathML type tags
    public static final QName tagMatrixRow = new QName("", "matrixrow");
    public static final QName tagcn = new QName("", "cn");
    public static final QName tagci = new QName("", "ci");
    private Logger log;

    public CSMLParser(Logger log) {
        this.log = log;
    }

    private boolean isAbsolute(String uri) {
        try {
            URI u = new URI(uri);
            return u.isAbsolute();
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    public CSMLModel parseCSML(String csml) {
        String csmlBase = "";
        CSMLModel model = new CSMLModel();

        XMLInputFactory2 csmlif = null;
        try {
            csmlif = (XMLInputFactory2) XMLInputFactory2.newInstance();
            //csmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
            //csmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
            //csmlif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
            csmlif.configureForSpeed();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Starting to parse " + csml);
        System.out.println("");
        long starttime = System.currentTimeMillis();
        try {
            XMLStreamReader2 csmlReader = (XMLStreamReader2) csmlif.createXMLStreamReader(csml, new FileInputStream(csml));
            int eventType = csmlReader.getEventType();

            int levelsDeep = 0;
            QName curElement = null;

            boolean inCSMLTag = false;
            boolean inDomainTag = false;
            boolean inQualityDimensionTag = false;
            boolean inRangeTag = false;
            boolean inConceptTag = false;
            boolean inRegionTag = false;
            boolean inAMatrixTag = false;
            boolean inMatrixRowTag = false;
            boolean inQVectorTag = false;
            boolean inBVectorTag = false;
            boolean inContrastClassTag = false;
            boolean inAVectorTag = false;
            boolean inInstanceTag = false;
            boolean inPointTag = false;
            boolean inUndefinedPointTag = false;
            boolean inPointValuesTag = false;
            boolean inContextTag = false;

            CSMLDomain curDomain = null;
            CSMLQualityDimension curQDim = null;
            CSML curRange = null;
            CSMLConcept curConcept = null;
            CSMLRegion curRegion = null;
            ArrayList<ArrayList<Double>> curAMatrix = null;
            ArrayList<Double> curMatrixRow = null;
            ArrayList<String> curQVector = null;
            ArrayList<Double> curBVector = null;
            CSMLContrastClass curCC = null;
            ArrayList<Double> curAVector = null;
            CSMLInstance curInstance = null;
            CSMLPoint curPoint = null;
            CSMLUndefinedPoint curUndefinedPoint = null;
            ArrayList<Double> curPointValues = null;
            CSMLContext curContext = null;

            //int testy = 0;

            if (csmlReader.hasNext()) {
                eventType = csmlReader.next();
            }
            while (csmlReader.hasNext()) {
                switch (eventType) {
                    case XMLEvent.START_ELEMENT:
                        curElement = csmlReader.getName();
                        //System.out.println(curElement);

                        if (curElement.equals(tagCSML)) { // csml:CSML
                            if (!inCSMLTag) {
                                inCSMLTag = true;
                                int xmlbaseIndex = csmlReader.getAttributeIndex("http://www.w3.org/XML/1998/namespace", "base");
                                if (xmlbaseIndex != -1) {
                                    csmlBase = csmlReader.getAttributeValue(xmlbaseIndex);
                                    if (!csmlBase.endsWith("#")) {
                                        csmlBase = csmlBase + "#";
                                    }
                                    model.setcsmlBase(csmlBase);
                                }
                            } else {
                                throw new CSMLParseException(); // cannot have csml tag inside another
                            }
                        } else if (!inCSMLTag) { // all other tags must be inside of a csml:CSML tag
                            throw new CSMLParseException();
                        } else if (curElement.equals(tagDomain)) { // csml:CSMLDomain
                            if (levelsDeep != 1) {
                                throw new CSMLParseException(); // csml:CSMLDomain is level 1
                            }
                            int idIdx = csmlReader.getAttributeIndex(attrID.getNamespaceURI(), attrID.getLocalPart());
                            if (idIdx == -1) {
                                throw new CSMLParseException();  // csml:ID tag required
                            }
                            String id = csmlReader.getAttributeValue(idIdx);
                            if (!isAbsolute(id)) {
                                id = csmlBase + id;
                            }
                            curDomain = model.createCSMLDomain(id);
                            inDomainTag = true;
                        } else if (curElement.equals(tagQualityDimension)) { // csml:CSMLQualityDimension
                            if (levelsDeep != 2 || !inDomainTag) {
                                throw new CSMLParseException(); // csml:CSMLQualityDimension is in CSMLDomain, level 2
                            }
                            int idIdx = csmlReader.getAttributeIndex(attrID.getNamespaceURI(), attrID.getLocalPart());
                            if (idIdx == -1) {
                                throw new CSMLParseException();  // csml:ID tag required
                            }
                            String id = csmlReader.getAttributeValue(idIdx);
                            if (!isAbsolute(id)) {
                                id = csmlBase + id;
                            }
                            curQDim = model.createCSMLQualityDimension(id);
                            int circIdx = csmlReader.getAttributeIndex(attrCircular.getNamespaceURI(), attrCircular.getLocalPart());
                            boolean circular = false;
                            if (circIdx != -1) {
                                circular = csmlReader.getAttributeAsBoolean(circIdx);
                            }
                            curQDim.setCircular(circular);
                            inQualityDimensionTag = true;
                        } else if (curElement.equals(tagScale)) {
                            if (levelsDeep != 3 || !inQualityDimensionTag) {
                                throw new CSMLParseException(); // Scale must be in quality dimension
                            }                        // handled in CHARACTERS
                        } else if (curElement.equals(tagRange)) {
                            if (levelsDeep != 3 || !inQualityDimensionTag) {
                                throw new CSMLParseException();
                            }
                            curRange = new CSML();
                            inRangeTag = true;
                        } else if (curElement.equals(tagMin)) {
                            if (levelsDeep != 4 || !inRangeTag) {
                                throw new CSMLParseException();
                            }
                            curRange.setMin(csmlReader.getElementAsDouble());
                            levelsDeep--;
                        } else if (curElement.equals(tagMax)) {
                            if (levelsDeep != 4 || !inRangeTag) {
                                throw new CSMLParseException();
                            }
                            curRange.setMax(csmlReader.getElementAsDouble());
                            levelsDeep--;
                        } else if (curElement.equals(tagUnits)) {
                            if (levelsDeep != 3 || !inQualityDimensionTag) {
                                throw new CSMLParseException();
                            }
                            // handled in CHARACTERS
                        } else if (curElement.equals(tagConcept)) { // csml:CSMLConcept
                            if (levelsDeep != 1) {
                                throw new CSMLParseException(); // csml:CSMLConcept is level 1
                            }
                            int idIdx = csmlReader.getAttributeIndex(attrID.getNamespaceURI(), attrID.getLocalPart());
                            if (idIdx == -1) {
                                throw new CSMLParseException(); // csml:ID tag required
                            }
                            String id = csmlReader.getAttributeValue(idIdx);
                            if (!isAbsolute(id)) {
                                id = csmlBase + id;
                            }
                            curConcept = model.createCSMLConcept(id);
                            int protoIdx = csmlReader.getAttributeIndex(attrPrototypeID.getNamespaceURI(), attrPrototypeID.getLocalPart());
                            if (protoIdx != -1) {
                                String prototypeID = csmlBase + csmlReader.getAttributeValue(protoIdx);
                                curConcept.setPrototypeId(prototypeID);
                            }
                            inConceptTag = true;
                        } else if (curElement.equals(tagRegion)) { // csml:CSMLRegion
                            if (levelsDeep != 2 || !inConceptTag) {
                                throw new CSMLParseException(); // csml:CSMLRegion is level 2 and must be inside concept tag
                            }
                            int idIdx = csmlReader.getAttributeIndex(attrID.getNamespaceURI(), attrID.getLocalPart());
                            if (idIdx == -1) {
                                throw new CSMLParseException(); // csml:ID tag required
                            }
                            int domainIdx = csmlReader.getAttributeIndex(attrDomainID.getNamespaceURI(), attrDomainID.getLocalPart());
                            if (domainIdx == -1) {
                                throw new CSMLParseException(); // csml:domainID tag required
                            }
                            String id = csmlReader.getAttributeValue(idIdx);
                            if (!isAbsolute(id)) {
                                id = csmlBase + id;
                            }
                            String domainId = csmlReader.getAttributeValue(domainIdx);
                            if (!isAbsolute(domainId)) {
                                domainId = csmlBase + domainId;
                            }
                            curRegion = model.createCSMLRegionLate(id, domainId);
                            inRegionTag = true;
                        } else if (curElement.equals(tagAMatrix)) { // csml:AMatrix
                            if (levelsDeep != 3 || !inRegionTag) {
                                throw new CSMLParseException(); // is only found inside csml:CSMLRegion tag
                            }
                            curAMatrix = new ArrayList<ArrayList<Double>>();
                            inAMatrixTag = true;
                        } else if (curElement.equals(tagMatrixRow)) { // matrixrow
                            if (levelsDeep != 4 || !inAMatrixTag) {
                                throw new CSMLParseException(); // is only found inside csml:AMatrix tag
                            }
                            curMatrixRow = new ArrayList<Double>();
                            inMatrixRowTag = true;
                        } else if (curElement.equals(tagcn)) { // cn
                            if (inMatrixRowTag) {
                                curMatrixRow.add(csmlReader.getElementAsDouble());
                                levelsDeep--;
                            } else if (inBVectorTag) {
                                curBVector.add(csmlReader.getElementAsDouble());
                                levelsDeep--;
                            } else if (inAVectorTag) {
                                curAVector.add(csmlReader.getElementAsDouble());
                                levelsDeep--;
                            } else if (inPointValuesTag) {
                                curPointValues.add(csmlReader.getElementAsDouble());
                                levelsDeep--;
                            } else {
                                throw new CSMLParseException(); // only shows up in the above tags
                            }
                        } else if (curElement.equals(tagQVector)) { // csml:qVector
                            if (!inRegionTag && !inContrastClassTag && !inInstanceTag) {
                                throw new CSMLParseException(); // is only found in these three tags
                            }
                            curQVector = new ArrayList<String>();
                            inQVectorTag = true;
                        } else if (curElement.equals(tagci)) { // ci
                            if (!inQVectorTag) {
                                throw new CSMLParseException(); // only found in qVector tag
                            }                        // handled in CHARACTERS
                        } else if (curElement.equals(tagBVector)) { // csml:bVector
                            if (!inRegionTag) {
                                throw new CSMLParseException(); // only found in CSMLRegion tag
                            }
                            curBVector = new ArrayList<Double>();
                            inBVectorTag = true;
                        } else if (curElement.equals(tagContrastClass)) { // csml:CSMLContrastClass
                            if (levelsDeep != 1) {
                                throw new CSMLParseException(); // csml:CSMLContrastClass is level 1
                            }
                            int idIdx = csmlReader.getAttributeIndex(attrID.getNamespaceURI(), attrID.getLocalPart());
                            if (idIdx == -1) {
                                throw new CSMLParseException(); // csml:ID tag required
                            }
                            int domainIdx = csmlReader.getAttributeIndex(attrDomainID.getNamespaceURI(), attrDomainID.getLocalPart());
                            if (domainIdx == -1) {
                                throw new CSMLParseException(); // csml:domainID tag required
                            }
                            String id = csmlReader.getAttributeValue(idIdx);
                            if (!isAbsolute(id)) {
                                id = csmlBase + id;
                            }
                            String domainId = csmlReader.getAttributeValue(domainIdx);
                            if (!isAbsolute(domainId)) {
                                domainId = csmlBase + domainId;
                            }
                            curCC = model.createCSMLContrastClassLate(id, domainId);
                            inContrastClassTag = true;
                        } else if (curElement.equals(tagAVector)) { // csml:aVector
                            if (!inContrastClassTag) {
                                throw new CSMLParseException(); // only found in Contrast Class tag
                            }
                            curAVector = new ArrayList<Double>();
                            inAVectorTag = true;
                        } else if (curElement.equals(tagCCMin)) { // csml:ccMin
                            if (!inContrastClassTag) {
                                throw new CSMLParseException(); // only found in Contrast Class tag
                            }
                            curCC.setbMin(csmlReader.getElementAsDouble());
                            levelsDeep--;
                        } else if (curElement.equals(tagCCMax)) { // csml:ccMax
                            if (!inContrastClassTag) {
                                throw new CSMLParseException(); // only found in Contrast Class tag
                            }
                            curCC.setbMax(csmlReader.getElementAsDouble());
                            levelsDeep--;
                        } else if (curElement.equals(tagInstance)) { // csml:CSMLInstance
                            if (levelsDeep != 1) {
                                throw new CSMLParseException(); // csml:CSMLInstance is level 1
                            }
                            int idIdx = csmlReader.getAttributeIndex(attrID.getNamespaceURI(), attrID.getLocalPart());
                            if (idIdx == -1) {
                                throw new CSMLParseException(); // csml:ID tag required
                            }
                            String id = csmlReader.getAttributeValue(idIdx);
                            if (!isAbsolute(id)) {
                                id = csmlBase + id;
                            }
                            curInstance = model.createCSMLInstance(id);
                            //System.out.println(curInstance.getId());
                            inInstanceTag = true;
                            //System.out.println(testy);
                            //testy++;
                        } else if (curElement.equals(tagPoint)) { // csml:CSMLPoint
                            if (!inInstanceTag) {
                                throw new CSMLParseException();
                            }
                            int idIdx = csmlReader.getAttributeIndex(attrID.getNamespaceURI(), attrID.getLocalPart());
                            if (idIdx == -1) {
                                throw new CSMLParseException(); // csml:ID tag required
                            }
                            int domainIdx = csmlReader.getAttributeIndex(attrDomainID.getNamespaceURI(), attrDomainID.getLocalPart());
                            if (domainIdx == -1) {
                                throw new CSMLParseException(); // csml:domainID tag required
                            }
                            String id = csmlReader.getAttributeValue(idIdx);
                            if (!isAbsolute(id)) {
                                id = csmlBase + id;
                            }
                            String domainId = csmlReader.getAttributeValue(domainIdx);
                            if (!isAbsolute(domainId)) {
                                domainId = csmlBase + domainId;
                            }
                             
                           // curPoint = model.createCSMLPointLate(id, domainId);
                            //inPointTag = true;
                        } else if (curElement.equals(tagUndefinedPoint)) { // csml:CSMLUndefinedPoint
                            if (!inInstanceTag) {
                                throw new CSMLParseException();
                            }
                            int idIdx = csmlReader.getAttributeIndex(attrID.getNamespaceURI(), attrID.getLocalPart());
                            if (idIdx == -1) {
                                throw new CSMLParseException(); // csml:ID tag required
                            }
                            int domainIdx = csmlReader.getAttributeIndex(attrDomainID.getNamespaceURI(), attrDomainID.getLocalPart());
                            if (domainIdx == -1) {
                                throw new CSMLParseException(); // csml:domainID tag required
                            }
                            int propertyIdx = csmlReader.getAttributeIndex(attrPropertyID.getNamespaceURI(), attrPropertyID.getLocalPart());
                            if (propertyIdx == -1) {
                                throw new CSMLParseException(); // csml:domainID tag required
                            }
                            String id = csmlReader.getAttributeValue(idIdx);
                            if (!isAbsolute(id)) {
                                id = csmlBase + id;
                            }
                            String domainId = csmlReader.getAttributeValue(domainIdx);
                            if (!isAbsolute(domainId)) {
                                domainId = csmlBase + domainId;
                            }
                            String propertyId = csmlReader.getAttributeValue(propertyIdx);
                            if (!isAbsolute(propertyId)) {
                                propertyId = csmlBase + propertyId;
                            }
                          //  curUndefinedPoint = model.createCSMLUndefinedPointLate(id, domainId, propertyId);
                          //  inUndefinedPointTag = true;
                        } else if (curElement.equals(tagPointValues)) { // csml:PointValues
                            if (!inPointTag) {
                                System.out.println("teste:"+tagPointValues);
                                throw new CSMLParseException();
                            }
                            curPointValues = new ArrayList<Double>();
                            inPointValuesTag = true;
                        } else if (curElement.equals(tagContext)) { // csml:CSMLContext
                            if (levelsDeep != 1) {
                                throw new CSMLParseException(); // csml:CSMLContext is level 1
                            }
                            int idIdx = csmlReader.getAttributeIndex(attrID.getNamespaceURI(), attrID.getLocalPart());
                            if (idIdx == -1) {
                                throw new CSMLParseException(); // cmsl:ID tag required
                            }
                            int typeIdx = csmlReader.getAttributeIndex(attrType.getNamespaceURI(), attrType.getLocalPart());
                            String id = csmlReader.getAttributeValue(idIdx);
                            if (!isAbsolute(id)) {
                                id = csmlBase + id;
                            }
                            String type = "qualitydimension";
                            if (typeIdx != -1) {
                                type = csmlReader.getAttributeValue(typeIdx);
                            }
                            if (type.equalsIgnoreCase("qdim") || type.equalsIgnoreCase("qualitydimension")) {
                                int domainIdx = csmlReader.getAttributeIndex(attrDomainID.getNamespaceURI(), attrDomainID.getLocalPart());
                                if (domainIdx == -1) {
                                    throw new CSMLParseException();  // csml:domainId tag required if quality dimension type
                                }
                                String domainId = csmlReader.getAttributeValue(domainIdx);
                                if (!isAbsolute(domainId)) {
                                    domainId = csmlBase + domainId;
                                }
                                curContext = model.createCSMLQualityDimensionTypeContextLate(id, domainId);
                            } else if (type.equalsIgnoreCase("domain")) {
                                curContext = model.createCSMLDomainTypeContext(id);
                            } else if (type.equalsIgnoreCase("instance")) {
                                curContext = model.createCSMLInstanceTypeContext(id);
                            } else if (type.equalsIgnoreCase("concept")) {
                                curContext = model.createCSMLConceptTypeContext(id);
                            } else if (type.equalsIgnoreCase("cc") || type.equalsIgnoreCase("contrastclass")) {
                                curContext = model.createCSMLContrastClassTypeContext(id);
                            }
                            inContextTag = true;
                        } else if (curElement.equals(tagWeight)) { // csml:Weight
                            if (!inContextTag) {
                                throw new CSMLParseException();
                            }
                            int cIdIdx = csmlReader.getAttributeIndex(attrCID.getNamespaceURI(), attrCID.getLocalPart());
                            if (cIdIdx == -1) {
                                throw new CSMLParseException();
                            }
                            String cId = csmlReader.getAttributeValue(cIdIdx);
                            if (!isAbsolute(cId)) {
                                cId = csmlBase + cId;
                            }
                            Double w = csmlReader.getElementAsDouble();
                            levelsDeep--;
                            curContext.addWeight(cId, w);
                        } else if (curElement.equals(tagLabel)) { // csml:Label
                            // handled in CHARACTERS
                        } else if (curElement.equals(tagDescription)) { // csml:Description
                            // handled in CHARACTERS
                        }
                        levelsDeep++;
                        break;
                    case XMLEvent.CHARACTERS:
                        if (curElement == null) {
                            break; // ignore all character text after an end element event
                        }
                        if (curElement.equals(tagLabel)) {
                            String label = new String(csmlReader.getText().trim());
                            if (inQualityDimensionTag) {
                                curQDim.setLabel(label); 	// DOMAIN
                            } else if (inDomainTag) {
                                curDomain.setLabel(label);
                            } else if (inRegionTag) {
                                curRegion.setLabel(label);		// CONCEPT
                            } else if (inConceptTag) {
                                curConcept.setLabel(label);
                            } else if (inContrastClassTag) {
                                curCC.setLabel(label);		// CONTRAST CLASS
                            } else if (inPointTag) {
                                curPoint.setLabel(label);			// INSTANCE
                            } else if (inInstanceTag) {
                                curInstance.setLabel(label);
                            } else if (inContextTag) {
                                curContext.setLabel(label);		// CONTEXT
                            }
                        } else if (curElement.equals(tagDescription)) {
                            String description = new String(csmlReader.getText().trim());
                            if (inQualityDimensionTag) {
                                curQDim.setDescription(description); 	// DOMAIN
                            } else if (inDomainTag) {
                                curDomain.setDescription(description);
                            } else if (inRegionTag) {
                                curRegion.setDescription(description);		// CONCEPT
                            } else if (inConceptTag) {
                                curConcept.setDescription(description);
                            } else if (inContrastClassTag) {
                                curCC.setDescription(description);		// CONTRAST CLASS
                            } else if (inPointTag) {
                                curPoint.setDescription(description);			// INSTANCE
                            } else if (inInstanceTag) {
                                curInstance.setDescription(description);
                            } else if (inContextTag) {
                                curContext.setDescription(description);		// CONTEXT
                            }
                        } else if (curElement.equals(tagScale)) {
                            curQDim.setScale(new String(csmlReader.getText().trim()));
                        } else if (curElement.equals(tagUnits)) {
                            curQDim.setUnits(new String(csmlReader.getText().trim()));
                        } else if (curElement.equals(tagci)) {
                            String id = csmlReader.getText().trim();
                            if (!isAbsolute(id)) {
                                id = csmlBase + id;
                            }
                            curQVector.add(id);
                        }
                        break;
                    case XMLEvent.END_ELEMENT:
                        curElement = csmlReader.getName();
                        if (curElement.equals(tagDomain)) { // add domain to CS model
                            inDomainTag = false;
                        } else if (curElement.equals(tagQualityDimension)) { // add quality dimension to current domain
                            curDomain.addQualityDimension(curQDim);
                            inQualityDimensionTag = false;
                        } else if (curElement.equals(tagRange)) {
                            curQDim.setRange(curRange.getMin(), curRange.getMax());
                            inRangeTag = false;
                        } else if (curElement.equals(tagConcept)) {
                            model.addConcept(curConcept);
                            inConceptTag = false;
                        } else if (curElement.equals(tagRegion)) {
                            if (!curRegion.isOK()) {
                                throw new CSMLParseException();
                            }
                            curConcept.addRegion(curRegion);
                            inRegionTag = false;
                        } else if (curElement.equals(tagAMatrix)) {
                            int numrows = curAMatrix.size();
                            int numcols = 0;
                            if (numrows > 0) {
                                numcols = curAMatrix.get(0).size();
                            }
                            double[][] aMatrix = new double[numrows][numcols];
                            for (int i = 0; i < numrows; i++) {
                                ArrayList<Double> row = curAMatrix.get(i);
                                for (int j = 0; j < numcols; j++) {
                                    aMatrix[i][j] = row.get(j);
                                    curRegion.setA(aMatrix);
                                }
                            }
                            inAMatrixTag = false;
                        } else if (curElement.equals(tagMatrixRow)) {
                            curAMatrix.add(curMatrixRow);
                            inMatrixRowTag = false;
                        } else if (curElement.equals(tagQVector)) {
                            String[] qVector = new String[curQVector.size()];
                            qVector = curQVector.toArray(qVector);
                            if (inRegionTag) {
                                curRegion.setq(qVector);
                            } else if (inContrastClassTag) {
                                curCC.setq(qVector);
                            } else if (inPointTag) {
                                curPoint.setq(qVector);
                            }
                            inQVectorTag = false;
                        } else if (curElement.equals(tagBVector)) {
                            double[] bVector = new double[curBVector.size()];
                            for (int i = 0; i < curBVector.size(); i++) {
                                bVector[i] = curBVector.get(i);
                            }
                            curRegion.setb(bVector);
                            inBVectorTag = false;
                        } else if (curElement.equals(tagContrastClass)) {
                            inContrastClassTag = false;
                        } else if (curElement.equals(tagAVector)) {
                            double[] aVector = new double[curAVector.size()];
                            for (int i = 0; i < curAVector.size(); i++) {
                                aVector[i] = curAVector.get(i);
                            }
                            curCC.seta(aVector);
                            inAVectorTag = false;
                        } else if (curElement.equals(tagInstance)) {
                            //if (model.getInstance(curInstance.getId()) != null) System.out.println(curInstance.getId());
                            inInstanceTag = false;
                        } else if (curElement.equals(tagPoint)) {
                            if (!curPoint.isOK()) {
                                throw new CSMLParseException();
                            }
                            curInstance.addPoint(curPoint);
                            inPointTag = false;

                            System.out.println(curPoint.getDomainId());
                        } else if (curElement.equals(tagUndefinedPoint)) {
                            if (!curUndefinedPoint.isOK()) {
                                throw new CSMLParseException();
                            }
                            curInstance.addPoint(curUndefinedPoint);
                            inUndefinedPointTag = false;

                            System.out.println(curUndefinedPoint.getDomainId());
                        } else if (curElement.equals(tagPointValues)) {
                            double[] pointValues = new double[curPointValues.size()];
                            for (int i = 0; i < curPointValues.size(); i++) {
                                pointValues[i] = curPointValues.get(i);
                            }
                            curPoint.setv(pointValues);
                            inPointValuesTag = false;
                        } else if (curElement.equals(tagContext)) {
                            inContextTag = false;
                        }
                        levelsDeep--;
                        curElement = null;
                        break;
                    case XMLEvent.END_DOCUMENT:
                    //System.out.println("Total of " + elementCount + " occurrences");
                }

                eventType = csmlReader.next();
            }
        } catch (XMLStreamException ex) {
            System.out.println(ex.getMessage());
            if (ex.getNestedException() != null) {
                ex.getNestedException().printStackTrace();
            }
            return null;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        System.out.println(" completed in " + (System.currentTimeMillis() - starttime) + " ms");

        model.postLoadSetupElements();

        //log.writeEventStr("CSML file read successully");
        return model;
    }

    public CSMLModel parseCSML(File csml) {
        return parseCSML(csml.getAbsolutePath());
    }

    public CSMLModel parseCSML(URL csml) {
        CSMLModel model = new CSMLModel();
        // TODO
        return model;
    }
}
