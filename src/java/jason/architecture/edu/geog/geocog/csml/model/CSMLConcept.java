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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

public class CSMLConcept extends CSMLElement {

    // attributes
    private String prototypeId = "";
    //elements
    // note: the key is domainID of region, NOT ID!
    private Hashtable<String, CSMLRegion> regions = new Hashtable<String, CSMLRegion>();

    public CSMLConcept(String id, CSMLModel model) {
        super(id, model);
    }

    public boolean isProperty() {
        return regions.size() == 1;
    }

    public void setPrototypeId(String pid) {
        prototypeId = new String(pid);
    }

    public String getPrototypeId() {
        return prototypeId;
    }

    public void addRegion(CSMLRegion r) {
        regions.put(r.getDomainId(), r);
    }

    public void removeRegion(String domainId) {
        regions.remove(domainId);
    }

    public void removeRegion(CSMLRegion r) {
        regions.remove(r.getDomainId());
    }

    public CSMLRegion getRegionById(String rid) {
        for (Enumeration<CSMLRegion> e = regions.elements(); e.hasMoreElements();) {
            CSMLRegion r = e.nextElement();
            if (r.getId().equals(rid)) {
                return r;
            }
        }
        return null;
    }

    // get CSMLRegion by domainId
    public CSMLRegion getRegion(String domainId) {
        return regions.get(domainId);
    }

    public CSMLRegion[] getRegions() {
        return regions.values().toArray(new CSMLRegion[0]);
    }

    public String[] getDomainIds() {
        return regions.keySet().toArray(new String[0]);
    }

    public boolean hasRegionInDomain(String domainId) {
        return getRegion(domainId) != null;
    }

    /**
     * Returns true if the given instance i is inside of all of the concept's
     * regions for the given domains. If the concept or instance is undefined
     * for any of the domains it will return false.
     *
     * @param instance
     * @return
     */
    public boolean contains(CSMLInstance instance) {
        Iterator<String> itdomains = regions.keySet().iterator();
        while (itdomains.hasNext()) {
            String curDomainId = itdomains.next();

            if (!regions.containsKey(curDomainId)) {
                return false;
            }
            if (!instance.hasPointInDomain(curDomainId)) {
                return false;
            }

            CSMLRegion curRegion = regions.get(curDomainId);
            CSMLPoint curPoint = instance.getPoint(curDomainId);

            if (!curRegion.contains(curPoint, Globals.roundPrecision)) {
                return false;
            }

        }

        return true;
    }

    public String toCSML() {
        String csml = "";
        csml += "<csml:Concept csml:ID=\"" + getId() + "\"";
        if (!prototypeId.equals("")) {
            csml += " csml:prototypeID=\"" + prototypeId + "\"";
        }
        csml += ">\n";
        csml += getLabelCSML();
        csml += getDescriptionCSML();
        Enumeration<String> keys = regions.keys();
        while (keys.hasMoreElements()) {
            csml += regions.get(keys.nextElement()).toCSML();
        }
        csml += "</csml:Concept>\n";
        return csml;
    }
}
