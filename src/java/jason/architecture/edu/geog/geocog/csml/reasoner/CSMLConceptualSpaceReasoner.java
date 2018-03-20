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
package edu.geog.geocog.csml.reasoner;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import edu.geog.geocog.csml.exceptions.InvalidContextException;
import edu.geog.geocog.csml.exceptions.InvalidContextTypeException;
import edu.geog.geocog.csml.exceptions.InvalidInstanceException;
import edu.geog.geocog.csml.model.CSML;
import edu.geog.geocog.csml.model.CSMLConcept;
import edu.geog.geocog.csml.model.CSMLContext;
import edu.geog.geocog.csml.model.CSMLContrastClass;
import edu.geog.geocog.csml.model.CSMLDomain;
import edu.geog.geocog.csml.model.CSMLDomainTypeContext;
import edu.geog.geocog.csml.model.CSMLInstance;
import edu.geog.geocog.csml.model.CSMLModel;
import edu.geog.geocog.csml.model.CSMLPoint;
import edu.geog.geocog.csml.model.CSMLRegion;
import edu.geog.geocog.csml.model.CSMLQualityDimension;
import edu.geog.geocog.csml.model.CSMLQualityDimensionTypeContext;
import edu.geog.geocog.csml.model.CSMLUndefinedPoint;
import edu.geog.geocog.csml.model.Globals;
import edu.geog.geocog.csml.utils.MathFunc;
import edu.geog.geocog.csml.utils.Pair;

public class CSMLConceptualSpaceReasoner {

    public static final int SIMILARITY_EXPONENTIAL = 0;
    public static final int SIMILARITY_GAUSSIAN = 1;
    public static final int SIMILARITY_LINEAR = 2;
    private CSMLModel model;
    private double c = 1.0; // similarity sensitivity parameter
    private String qHullPath = "./qhull/bin/qhull";
    private String tempDir = "./temp/";
    private String tempF = "./temp/teste";
    private long qHullTimeout = 30000; // 30 sec
    private double roundPrecision = Globals.roundPrecision; // rounding error buffer for identifying if values are equal after qhull, qhalf, etc.
    private String glpsolPath = "/usr/local/bin/glpsol";
    private long glpsolTimeout = 30000; // 30 sec

    public CSMLConceptualSpaceReasoner() {
    }

    public CSMLConceptualSpaceReasoner(CSMLModel model) {
        this.model = model;
    }

    // set the default similarity sensitivity parameter
    public void setC(double c) {
        this.c = c;
    }

    // get the default similarity sensitivity parameter
    public double getC() {
        return c;
    }

    public void setqHullPath(String p) {
        qHullPath = new String(p);
    }

    public void setglpsolPath(String p) {
        glpsolPath = new String(p);
    }

    public boolean readModelFromCSML(URL csml) {
        return true;
    }

    public boolean readModelFromCSML(File csml) {
        return true;
    }

    /**
     * Returns a list of all domains shared by the given list of instances
     */
    public ArrayList<CSMLDomain> sharedDomains(ArrayList<CSMLInstance> instances) {
        ArrayList<CSMLDomain> domains = new ArrayList<CSMLDomain>();
       System.out.println("Entrei aqui depois"); 
                
        if (instances == null || instances.size() == 0) {
               //System.out.println("Entrei aqui tambem"); 
            return domains;
            
        }

        ArrayList<String> domainIds = new ArrayList<String>(Arrays.asList(instances.get(0).getDomainIds()));
        for (int i = domainIds.size() - 1; i >= 0; i--) {
            String dId = domainIds.get(i);
            for (int j = 1; j < instances.size(); j++) {
                CSMLInstance curI = instances.get(j);
                String[] dIds = curI.getDomainIds();
                if (!Arrays.asList(dIds).contains(dId)) {
                    domainIds.remove(i);
                    break;
                }
            }
        }

        for (String domainId : domainIds) {
            CSMLDomain d = model.getDomain(domainId);
            if (d != null) {
                domains.add(d);
            }
        }

        return domains;
    }

    public ArrayList<CSMLDomain> sharedDomains(CSMLInstance i1, CSMLInstance i2) {
        ArrayList<CSMLInstance> iList = new ArrayList<CSMLInstance>();
        System.out.println("Entrei aqui primeiro"); 
        iList.add(i1);
        iList.add(i2);
        return sharedDomains(iList);
    }

    public ArrayList<CSMLDomain> sharedDomains(CSMLConcept concept, CSMLInstance instance) {
        ArrayList<CSMLDomain> domains = new ArrayList<CSMLDomain>();
             //System.out.println("Entrei aqui primeiro"); 
        String[] domainIds = concept.getDomainIds();
        
        for (int i = 0; i < domainIds.length; i++) {
            CSMLDomain d = model.getDomain(domainIds[i]);
            //System.out.println("Entrei aqui primeiro:"+d ); 
            if ((instance.getPoint2(d) != null) && (d != null)) {
                   //  System.out.println("Entrei aqui depois"); 
                domains.add(d);
            }
        }

        return domains;
    }

    public ArrayList<CSMLDomain> sharedDomains(CSMLInstance instance, CSMLConcept concept) {
        return sharedDomains(concept, instance);
    }

    public ArrayList<CSMLDomain> sharedDomains(CSMLConcept c1, CSMLConcept c2) {
        ArrayList<CSMLDomain> domains = new ArrayList<CSMLDomain>();
        String[] domainIds = c1.getDomainIds();
        for (int i = 0; i < domainIds.length; i++) {
            CSMLDomain d = model.getDomain(domainIds[i]);
            if ((c2.getRegion(domainIds[i]) != null) && (d != null)) {
                domains.add(d);
            }
        }
        return domains;
    }

    /**
     * Returns weighted distance between two points in a domain
     */
    public double distance(CSMLPoint p1, CSMLPoint p2, CSMLQualityDimensionTypeContext context) {
        if (p1 == null || p2 == null) {
            return -1.0;
        }
        if (context == null) {
            context = model.createCSMLQualityDimensionTypeContext("__TEMP", p1.getDomain());
            CSMLDomain dd = model.getDomain(context.getDomainId());
            String[] qdIds = dd.getQualityDimensionIds();
            for (int i = 0; i < qdIds.length; i++) {
                double value = 1.0 / qdIds.length;
                context.addWeight(qdIds[i], value);
            }
        }
        if (!p1.getDomainId().equals(context.getDomainId())) {
            return -1.0;
        }
        if (!p2.getDomainId().equals(context.getDomainId())) {
            return -1.0;
        }

        CSMLDomain d = model.getDomain(context.getDomainId());
        if (d == null) {
            return -1.0;
        }

        double dist = 0.0;

        String[] qdIds = d.getQualityDimensionIds();

        for (int i = 0; i < qdIds.length; i++) {
            CSMLQualityDimension qDim = d.getQualityDimension(qdIds[i]);
            double weight = context.getWeight(qdIds[i]);
            //System.out.println("Value 1:"+p1.getValue(qdIds[i]));
           // System.out.println("Value 2:"+p2.getValue(qdIds[i]));
            double diff = Math.abs(p2.getValue(qdIds[i]) - p1.getValue(qdIds[i]));
            // modify difference if the dimension is circular
            if (qDim.isCircular()) {
                CSML r = qDim.getRange();
                double maxDelta = r.getMagnitude() / 2.0;
                if (diff > maxDelta) {
                    diff -= r.getMagnitude();
                }
            }
            dist += weight * diff * diff;
        }

        return Math.sqrt(dist);
    }

    /**
     * Returns semantic similarity of two instances given domain type and
     * quality dimension type contexts simFunction is one of exponential,
     * gaussian, or linear: SIMILARITY_EXPONENTIAL, SIMILARITY_GAUSSIAN,
     * SIMILARITY_LINEAR
     */
    public double semanticSimilarity(String instanceId1, String instanceId2, String domainTypeContextId,
            String[] qualityDimensionTypeContextIds, int simFunction) throws InvalidContextTypeException,
            InvalidInstanceException, InvalidContextException {
        CSMLInstance i1 = model.getInstance(instanceId1);
        if (i1 == null) {
            throw new InvalidInstanceException();
        }
        CSMLInstance i2 = model.getInstance(instanceId2);
        if (i2 == null) {
            throw new InvalidInstanceException();
        }
        CSMLContext dContext;
        try {
            dContext = model.getContext(domainTypeContextId);
        } catch (Exception e) {
            dContext = null;
        }
        //if (dContext == null) throw new InvalidContextException();
        if (dContext == null) {
            dContext = model.createCSMLDomainTypeContext("__TEMP_DC");
            ArrayList<CSMLDomain> domains = sharedDomains(i1, i2);
            for (CSMLDomain dd : domains) {
                dContext.addWeight(dd.getId(), 1.0 / domains.size());
            }
        }
        if (dContext.getType() != CSMLContext.DOMAIN_TYPE) {
            throw new InvalidContextTypeException();
        }
        ArrayList<CSMLContext> qdContexts = new ArrayList<CSMLContext>();
        if (qualityDimensionTypeContextIds != null) {
            for (int i = 0; i < qualityDimensionTypeContextIds.length; i++) {
                CSMLContext qdContext = model.getContext(qualityDimensionTypeContextIds[i]);
                //           if (qdContext == null) throw new InvalidContextException();
                //           if (qdContext.getType() != CSMLContext.QUALITY_DIMENSION_TYPE) throw new InvalidContextTypeException();
                if (qdContext == null) {
                    qdContexts.add(null);
                }
                if (qdContext.getType() != CSMLContext.QUALITY_DIMENSION_TYPE) {
                    qdContexts.add(null);
                }
                qdContexts.add(qdContext);
            }
        }

        if (i1.equals(i2)) {
            return 0.0;
        }
        ArrayList<CSMLDomain> domains = sharedDomains(i1, i2);

        double dist = 0.0;
        for (int i = 0; i < domains.size(); i++) {
            CSMLDomain d = domains.get(i);
            double dWeight = dContext.getWeight(d.getId());
            if (dWeight <= 0.0) {
                break;  // don't bother calculating distance if zero, negative weights are invalid
            }
            CSMLPoint p1 = i1.getPoint(d.getId());
            CSMLPoint p2 = i2.getPoint(d.getId());
            CSMLQualityDimensionTypeContext qdC = null;
            for (int q = 0; q < qdContexts.size(); q++) {
                CSMLQualityDimensionTypeContext qdCTest = (CSMLQualityDimensionTypeContext) qdContexts.get(q);
                if (qdCTest.getDomainId().equals(d.getId())) {
                    qdC = qdCTest;
                    break;
                }
            }
            if (qdC != null) {
                //double qdNNorm = Math.sqrt(d.numberOfDimensions());
                //double qdDist = distance(p1, p2, qdC);
                //System.out.println("dWeight: "+dWeight);
                //System.out.println("qdNNorm: "+qdNNorm);
                //System.out.println("qdDist : "+qdDist);
                //System.out.println("qdNNorm*qdDist = "+(qdNNorm*qdDist));

                dist += dWeight * Math.sqrt(d.numberOfDimensions()) * distance(p1, p2, qdC);
            } else {
                dist += dWeight * Math.sqrt(d.numberOfDimensions()) * distance(p1, p2, null);
            }
        }

        double similarity = 0.0;
        if (simFunction == SIMILARITY_GAUSSIAN) {
            similarity = Math.exp(-c * dist * dist);
        } else if (simFunction == SIMILARITY_EXPONENTIAL) {
            similarity = Math.exp(-c * dist);
        } else if (simFunction == SIMILARITY_LINEAR) {
            if (dist == 0) {
                similarity = Double.POSITIVE_INFINITY;
            } else {
                similarity = 1.0 / (c * dist);
            }
        }
        return similarity;
    }

    public double semanticSimilarity(String instanceId1, String instanceId2, String domainTypeContextId,
            String[] qualityDimensionTypeContextIds) throws InvalidContextTypeException, InvalidInstanceException,
            InvalidContextException {
        return semanticSimilarity(instanceId1, instanceId2, domainTypeContextId, qualityDimensionTypeContextIds,
                SIMILARITY_EXPONENTIAL);
    }

    public CSMLConcept learnConceptFromExemplars(String conceptId, ArrayList<CSMLInstance> exemplars, ArrayList<CSMLDomain> domains) {
        if (domains == null) {
            domains = sharedDomains(exemplars);
        }
        CSMLConcept concept = model.createCSMLConcept(conceptId);
        for (int i = 0; i < domains.size(); i++) {
            CSMLDomain d = domains.get(i);
            ArrayList<CSMLPoint> points = new ArrayList<CSMLPoint>();
            for (int j = 0; j < exemplars.size(); j++) {
                CSMLInstance ex = exemplars.get(j);
                CSMLPoint p = ex.getPoint(d.getId());
                if (p != null) {
                    points.add(p);
                }
            }
            CSMLRegion newRegion = convexHull(points, conceptId + "." + d.getId() + "_REGION");
            if (newRegion != null) {
                concept.addRegion(newRegion);
            }
        }
        return concept;
    }

    public CSMLConcept learnConceptFromExemplars(String conceptId, ArrayList<CSMLInstance> exemplars) {
        return learnConceptFromExemplars(conceptId, exemplars, null);
    }

    /**
     * Use Voronoi tessellation to get regions based on prototype instances for
     * the given domains. If domains is null, the domains are the intersecting
     * domains for all prototypes
     */
    public ArrayList<CSMLConcept> conceptsFromPrototypeVoronoi(String baseConceptId, ArrayList<CSMLInstance> prototypes,
            ArrayList<CSMLDomain> domains) {
        ArrayList<CSMLConcept> concepts = new ArrayList<CSMLConcept>();
        if (prototypes == null || prototypes.size() < 2) {
            return concepts;
        }

        // find domains that are specified for every prototype instance
        if (domains == null) {
            domains = sharedDomains(prototypes);
        }
        // TODO
        return concepts;
    }

    /**
     * Use Voronoi tessellation to get regions based on prototype instances
     */
    public ArrayList<CSMLConcept> conceptsFromPrototypeVoronoi(String baseConceptId, ArrayList<CSMLInstance> prototypes) {
        return conceptsFromPrototypeVoronoi(baseConceptId, prototypes, null);
    }

    /**
     * Returns a region that fills the entire domain
     */
    public CSMLRegion createWholeDomainRegion(CSMLDomain domain) {
        if (domain == null) {
            return null;
        }

        CSMLRegion dRegion = model.createCSMLRegion(domain.getId() + ".REGION", domain);

        int qdNum = domain.numberOfDimensions();
        String[] qdOrder = domain.getQualityDimensionIds();
        double[][] newA = new double[qdNum * 2][qdNum];
        for (int i = 0; i < newA.length; i++) {
            Arrays.fill(newA[i], 0.0);
        }
        double[] newb = new double[qdNum * 2];

        for (int i = 0; i < qdNum; i++) {
            CSMLQualityDimension qd = domain.getQualityDimension(qdOrder[i]);
            double min = qd.getRange().getMin();
            double max = qd.getRange().getMax();
            newA[i * 2][i] = -1.0;
            newA[i * 2 + 1][i] = 1.0;
            newb[i * 2] = -min;
            newb[i * 2 + 1] = max;
        }
        dRegion.setRegion(newA, qdOrder, newb);

        return dRegion;
    }

    public ArrayList<CSMLRegion> voronoiTessellation(ArrayList<CSMLPoint> prototypes) {
        if (prototypes == null) {
            return null;
        }
        ArrayList<CSMLRegion> newRegions = new ArrayList<CSMLRegion>();
        if (prototypes.size() == 0) {
            return newRegions;
        }

        String domainId = prototypes.get(0).getDomainId();
        CSMLDomain domain = model.getDomain(domainId);
        if (domain == null) {
            return null;
        }
        // check that prototype points are consistent with domain
        for (int i = 1; i < prototypes.size(); i++) {
            CSMLPoint p = prototypes.get(i);
            if (!p.getDomainId().equals(domainId)) {
                return null;
            } else if (!p.domainOK()) {
                return null;
            }
        }

        String[] qOrder = domain.getQualityDimensionIds();

        if (prototypes.size() == 1) {
            newRegions.add(createWholeDomainRegion(domain));
            return newRegions;
        }

        // generate input for qvoronoi
        String qVoronoiInput = "";
        qVoronoiInput += domain.numberOfDimensions() + "\n";
        qVoronoiInput += prototypes.size() + "\n";
        for (int i = 0; i < prototypes.size(); i++) {
            CSMLPoint p = prototypes.get(i);
            for (int j = 0; j < domain.numberOfDimensions(); j++) {
                double val = p.getValue(qOrder[j]);
                qVoronoiInput += "  " + val;
            }
            qVoronoiInput += "\n";
        }

        String rand;
        String qVoronoiOutputFN;
        File f;

        do {  // find unique random filename
            rand = Long.toHexString(new Random().nextLong());
            qVoronoiOutputFN = new String(tempDir + rand + ".tmp");
            f = new File(qVoronoiOutputFN);
        } while (f.exists());

        try {
            // run qhull
            String[] qVoronoiCmd = {qHullPath, "v", "Qbb", "Qx", "Fs", "Fi", "Fo", "Fv", "TO", qVoronoiOutputFN};
            Process p = Runtime.getRuntime().exec(qVoronoiCmd);

            PrintStream ps = new PrintStream(new BufferedOutputStream(p.getOutputStream()));
            // write H-polytope to qvoronoi
            //System.out.println(qVoronoiInput);
            ps.print(qVoronoiInput);
            ps.close();
            
            

            // Set a timer to interrupt the process if it does not return within the timeout period
            Timer timer = new Timer();
            timer.schedule(new InterruptScheduler(Thread.currentThread()), qHullTimeout);
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                // Stop the process from running
                p.destroy();
                return null;
            } finally {
                // Stop the timer
                timer.cancel();
            }

            BufferedReader qVoronoiResultFile = new BufferedReader(new FileReader(qVoronoiOutputFN));
            ArrayList<String> qHullResult = new ArrayList<String>();
            String inString;
            while ((inString = qVoronoiResultFile.readLine()) != null) {
                qHullResult.add(inString);
                //System.out.println(inString);
            }
            qVoronoiResultFile.close();

            if (qHullResult.size() < 3) {
                return null;
            }

            int numRegions = Integer.parseInt(qHullResult.get(0).trim().split("\\s+")[5]);
            if (numRegions != prototypes.size()) {
                // TODO
            }
            int numClosedHalfplanes = Integer.parseInt(qHullResult.get(2));
            //System.out.println("numClosed:  "+numClosedHalfplanes);
            int numOpenHalfplanes = Integer.parseInt(qHullResult.get(numClosedHalfplanes + 3));
            //System.out.println("numOpen: "+ numOpenHalfplanes);

            String[] newQ = new String[qOrder.length];
            for (int i = 0; i < qOrder.length; i++) {
                newQ[i] = new String(qOrder[i]);
            }

            for (int i = 0; i < numRegions; i++) {
                // number of regions should equal number of prototypes
                CSMLRegion newRegion = model.createCSMLRegion(prototypes.get(i).getId() + ".VORONOI_REGION", domain);

                ArrayList<double[]> tempA = new ArrayList<double[]>();
                ArrayList<Double> tempb = new ArrayList<Double>();

                for (int j = 3; j < numClosedHalfplanes + 3; j++) {
                    String[] resultLine = qHullResult.get(j).trim().split("\\s+");
                    if (Integer.parseInt(resultLine[1]) == i) {
                        double[] aRow = new double[newQ.length];
                        double bVal;
                        for (int n = 3; n < resultLine.length - 1; n++) {
                            aRow[n - 3] = Double.parseDouble(resultLine[n]);
                        }
                        bVal = -Double.parseDouble(resultLine[resultLine.length - 1]);
                        tempA.add(aRow);
                        tempb.add(bVal);
                    } else if (Integer.parseInt(resultLine[2]) == i) {
                        double[] aRow = new double[newQ.length];
                        double bVal;
                        for (int n = 3; n < resultLine.length - 1; n++) {
                            aRow[n - 3] = -Double.parseDouble(resultLine[n]);
                        }
                        bVal = Double.parseDouble(resultLine[resultLine.length - 1]);
                        tempA.add(aRow);
                        tempb.add(bVal);
                    }
                }

                for (int j = 4 + numClosedHalfplanes; j < numClosedHalfplanes + 4 + numOpenHalfplanes; j++) {
                    String[] resultLine = qHullResult.get(j).trim().split("\\s+");
                    if (Integer.parseInt(resultLine[1]) == i) {
                        double[] aRow = new double[newQ.length];
                        double bVal;
                        for (int n = 3; n < resultLine.length - 1; n++) {
                            aRow[n - 3] = Double.parseDouble(resultLine[n]);
                        }
                        bVal = -Double.parseDouble(resultLine[resultLine.length - 1]);
                        tempA.add(aRow);
                        tempb.add(bVal);
                    } else if (Integer.parseInt(resultLine[2]) == i) {
                        double[] aRow = new double[newQ.length];
                        double bVal;
                        for (int n = 3; n < resultLine.length - 1; n++) {
                            aRow[n - 3] = -Double.parseDouble(resultLine[n]);
                        }
                        bVal = Double.parseDouble(resultLine[resultLine.length - 1]);
                        tempA.add(aRow);
                        tempb.add(bVal);
                    }
                }

                double[][] newA = new double[tempA.size()][newQ.length];
                double[] newB = new double[tempb.size()];
                for (int j = 0; j < newA.length; j++) {
                    newA[j] = tempA.get(j);
                    newB[j] = tempb.get(j);
                }

                newRegion.setq(newQ);
                newRegion.setA(newA);
                newRegion.setb(newB);

                newRegions.add(newRegion);
            }
        } catch (IOException e) {
            // TODO
            return null;
        }

        // delete qvoronoi temp file
        if (f.exists()) {
            f.delete();
        }

        return newRegions;
    }

    /**
     * Return convex region from convex hull of input points
     */
    public CSMLRegion convexHull(ArrayList<CSMLPoint> points, String newRegionId) {
        CSMLRegion newRegion = null;
         
        if (points == null || points.size() == 0) {
            return null;
        }

        // test that all points are from same domain
        String domainId = new String(points.get(0).getDomainId());
        for (int i = 0; i < points.size(); i++) {
            CSMLPoint p = points.get(i);
            if (!p.getDomainId().equals(domainId)) {
                return null;
            }
        }
        
         
         
        CSMLDomain domain = model.getDomain(domainId);

        // number of points must be at least # of dimensions in domain + 1
        if (points.size() < domain.numberOfDimensions() + 1) {
             
            return null;
        }
        
        
        String[] qDims = domain.getQualityDimensionIds();
        double[][] pointValues = new double[points.size()][qDims.length];
        double[] midpoint = new double[qDims.length];
        
        for (int i = 0; i < points.size(); i++) {
            for (int j = 0; j < qDims.length; j++) {
                pointValues[i][j] = points.get(i).getValue(qDims[j]);
                
            }
        }

        // if # dimensions == 1
        if (qDims.length == 1) {
            double[] sorted = new double[pointValues.length];
            for (int i = 0; i < pointValues.length; i++) {
                sorted[i] = pointValues[i][0];
            }
            Arrays.sort(sorted);
            double min = sorted[0];
            double max = sorted[sorted.length - 1];
            double sum = 0.0;
            for (int i = 0; i < sorted.length; i++) {
                sum += sorted[i];
            }
            midpoint[0] = sum / sorted.length;

            newRegion = model.createCSMLRegion(newRegionId, domain);
            newRegion.setq(qDims);
            double[][] aMatrix = {{1.0}, {-1.0}};
            newRegion.setA(aMatrix);
            double[] bVector = {max, -min};
            newRegion.setb(bVector);
            newRegion.setCentroid(midpoint);
        } else {  // if # dimensions > 1 call qhull program
            String rand;
            String qHullOutput;
            File f;

            do {  // find unique random filename
                rand = Long.toHexString(new Random().nextLong());
                qHullOutput = new String(tempDir + rand + ".tmp");
                f = new File(qHullOutput);
            } while (f.exists());

            try {
                // run qhull
                String[] qHull = {qHullPath, "FV", "n", "TO", qHullOutput};
                Process p = Runtime.getRuntime().exec(qHull);
                

                PrintStream ps = new PrintStream(new BufferedOutputStream(p.getOutputStream()));
                ps.println(qDims.length);
                ps.println(points.size());
                for (int i = 0; i < points.size(); i++) {
                    for (int j = 0; j < qDims.length; j++) {
                        ps.print(pointValues[i][j]);
                        if (j < qDims.length - 1) {
                            ps.print(" ");
                        }
                        
                    }
                    ps.println();
                    //System.out.println(ps.);
                    
                }
                ps.close();

                // Set a timer to interrupt the process if it does not return within the timeout period
                Timer timer = new Timer();
                timer.schedule(new InterruptScheduler(Thread.currentThread()), qHullTimeout);
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    // Stop the process from running
                    p.destroy();
                    return null;
                } finally {
                    // Stop the timer
                    timer.cancel();
                }
                
             
              
                
                BufferedReader qHullResultFile = new BufferedReader(new FileReader(qHullOutput));

                String inString;              
                ArrayList<String> qHullResult = new ArrayList<String>();
                while ((inString = qHullResultFile.readLine()) != null) {
                    qHullResult.add(inString);  
                }
                qHullResultFile.close();                

                if (qHullResult.size() < 5) {
                    
                    return null;
                }
                

                
                String[] midpointStrs = qHullResult.get(1).trim().split("\\s+");  
                //System.out.println("Achou!"+midpointStrs[0]);
                
                for (int i = 0; i < midpoint.length; i++) {
                    midpoint[i] = Double.parseDouble(midpointStrs[i]);
                }
                int cols = Integer.parseInt(qHullResult.get(3).trim());
                int halfplaneNum = Integer.parseInt(qHullResult.get(2).trim());
                double[][] aMatrix = new double[halfplaneNum][cols - 1];
                double[] bVector = new double[halfplaneNum];
                
                for (int i = 4; i < halfplaneNum + 4; i++) {
                    String[] rowVals = qHullResult.get(i).trim().split("\\s+");
                   // System.out.println("Achou2!"+rowVals[0]);
                    for (int j = 0; j < rowVals.length - 1; j++) {
                        aMatrix[i - 4][j] = Double.parseDouble(rowVals[j]);
                    }
                    bVector[i - 4] = -Double.parseDouble(rowVals[rowVals.length - 1]);
                }
                newRegion = model.createCSMLRegion(newRegionId, domain);
                newRegion.setq(qDims);
                newRegion.setA(aMatrix);
                newRegion.setb(bVector);
                newRegion.setCentroid(midpoint);
            } catch (IOException e) {
                return null;
            }

            // delete the qhull temp file
            if (f.exists()) {
                f.delete();
            }
        }

        return newRegion;
    }

    private double[][] makeArray2D(ArrayList<ArrayList<Double>> doubles) {
        if (doubles == null) {
            return null;
        }
        if (doubles.size() == 0) {
            return new double[0][0];
        }
        int numCols = doubles.get(0).size();
        double[][] returnArray = new double[doubles.size()][numCols];
        for (int i = 0; i < doubles.size(); i++) {
            for (int j = 0; j < numCols; j++) {
                returnArray[i][j] = doubles.get(i).get(j);
            }
        }
        return returnArray;
    }

    private double[] makeArray1D(ArrayList<Double> doubles) {
        if (doubles == null) {
            return null;
        }
        double[] returnArray = new double[doubles.size()];
        for (int i = 0; i < doubles.size(); i++) {
            returnArray[i] = doubles.get(i);
        }
        return returnArray;
    }

    /**
     * Returns true if any point on hull of r1 is inside r2 or vice versa
     */
    public boolean intersects(CSMLRegion r1, CSMLRegion r2) {
        CSMLPoint[] r1VPoints = r1.getVPolytope();
        if (r1VPoints == null);
        //TODO
        //TODO
        return false;
    }

    /**
     * Returns the intersection of two regions
     */
    public CSMLRegion intersection(CSMLRegion r1, CSMLRegion r2) {
        return intersection(r1, r2, r1.getId() + "_" + r2.getId() + ".INTERSECTION");
    }

    /**
     * Returns the intersection of two regions
     */
    public CSMLRegion intersection(CSMLRegion r1, CSMLRegion r2, String newRegionId) {
        String domainId = r1.getDomainId();
        if (!r2.getDomainId().equals(domainId)) {
            return null;
        }
        CSMLDomain domain = model.getDomain(domainId);
        if (domain == null) {
            return null;
        }
        if (!r1.isOK() || !r1.domainOK() || !r2.isOK() || !r2.domainOK()) {
            return null;
        }

        if (contains(r1, r2)) {
            // TODO return copy? of region r2
            return r2;
        } else if (contains(r2, r1)) {
            // TODO return copy? of region r1
            return r1;
        }

        CSMLRegion newRegion = model.createCSMLRegion(newRegionId, domain);

        ArrayList<ArrayList<Double>> newAMatrix = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> newBVector = new ArrayList<Double>();

        String[] qVector = r1.getq();
        double[][] r1AMatrix = r1.getA();
        double[] r1bVector = r1.getb();
        double[][] r2AMatrix = r2.getAOrdered(qVector);
        double[] r2bVector = r2.getb();

        // add AMatrix rows from region 1
        for (int m = 0; m < r1AMatrix.length; m++) {
            ArrayList<Double> newRow = new ArrayList<Double>();
            for (int n = 0; n < r1AMatrix[m].length; n++) {
                newRow.add(r1AMatrix[m][n]);
            }
            newAMatrix.add(newRow);
        }

        // add AMatrix rows from region 2
        for (int m = 0; m < r2AMatrix.length; m++) {
            ArrayList<Double> newRow = new ArrayList<Double>();
            for (int n = 0; n < r2AMatrix[m].length; n++) {
                newRow.add(r2AMatrix[m][n]);
            }
            newAMatrix.add(newRow);
        }

        // add bVector values from region 1
        for (int m = 0; m < r1bVector.length; m++) {
            newBVector.add(r1bVector[m]);
        }

        for (int m = 0; m < r2bVector.length; m++) {
            newBVector.add(r2bVector[m]);
        }

        // eliminate redundant inequalities using linear programming
        boolean[] redundant = new boolean[newAMatrix.size()];
        for (int i = 0; i < redundant.length; i++) {
            redundant[i] = false;
        }
        for (int m = newAMatrix.size() - 1; m >= 0; m--) {
            File glpsolInF;
            String rand;
            String glpsolInFN;
            do {  // find unique random filename
                rand = Long.toHexString(new Random().nextLong());
                glpsolInFN = new String(tempDir + rand + ".mod");
                glpsolInF = new File(glpsolInFN);
            } while (glpsolInF.exists());

            try {
                PrintWriter glpsolIn = new PrintWriter(new BufferedWriter(new FileWriter(glpsolInF)));

                Double[] sTx = newAMatrix.get(m).toArray(new Double[0]);
                double t = newBVector.get(m);
                for (int i = 0; i < qVector.length; i++) {
                    glpsolIn.println("var q" + i + ";");
                }

                glpsolIn.print("maximize sTx: ");
                for (int i = 0; i < sTx.length; i++) {
                    glpsolIn.print(sTx[i] + "*q" + i);
                    if (i < sTx.length - 1) {
                        glpsolIn.print(" + ");
                    } else {
                        glpsolIn.println(";");
                    }
                }

                for (int i = 0; i < newAMatrix.size(); i++) {
                    if (!redundant[i] && i != m) {
                        glpsolIn.print("row" + i + " : ");
                        ArrayList<Double> row = newAMatrix.get(i);
                        for (int j = 0; j < row.size(); j++) {
                            glpsolIn.print(row.get(j) + "*q" + j);
                            if (j < row.size() - 1) {
                                glpsolIn.print(" + ");
                            } else {
                                glpsolIn.println(" <= " + newBVector.get(i) + ";");
                            }
                        }
                    }
                }
                glpsolIn.print("rowt : ");
                for (int i = 0; i < sTx.length; i++) {
                    glpsolIn.print(sTx[i] + "*q" + i);
                    if (i < sTx.length - 1) {
                        glpsolIn.print(" + ");
                    } else {
                        glpsolIn.println(" <= " + (t + 1) + ";");
                    }
                }
                glpsolIn.println("end;");
                glpsolIn.close();

                // run glpsol
                File glpsolOutF;
                String glpsolOutFN;
                do {  // find unique random filename
                    rand = Long.toHexString(new Random().nextLong());
                    glpsolOutFN = new String(tempDir + rand + ".sol");
                    glpsolOutF = new File(glpsolOutFN);
                } while (glpsolOutF.exists());
                String[] glpsol = {glpsolPath, "-m", glpsolInF.getAbsolutePath(), "-w", glpsolOutF.getAbsolutePath()};
                Process p = Runtime.getRuntime().exec(glpsol);
                p.getInputStream().close();

                // Set a timer to interrupt the process if it does not return within the timeout period
                Timer timer = new Timer();
                timer.schedule(new InterruptScheduler(Thread.currentThread()), glpsolTimeout);
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    // Stop the process from running
                    p.destroy();
                    return null;
                } finally {
                    // Stop the timer
                    timer.cancel();
                }

                BufferedReader glpsolResultFile = new BufferedReader(new FileReader(glpsolOutF));
                String inString = glpsolResultFile.readLine(); // get first line
                inString = glpsolResultFile.readLine(); // get second line - the one we want
                if (inString != null) {
                    String[] lineTwo = inString.trim().split("\\s+");
                    // test if there is any solution to the linear program
                    //   if not then there is no intersection of regions
                    if (lineTwo[0].equals("1")) {
                        return null;
                    }
                    double objVal = Double.parseDouble(lineTwo[2]);
                    redundant[m] = objVal <= t;
                }
                glpsolResultFile.close();

                if (glpsolInF.exists()) {
                    glpsolInF.delete();
                }
                if (glpsolOutF.exists()) {
                    glpsolOutF.delete();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // remove redundant inequalities from linear programming
        for (int m = newAMatrix.size() - 1; m >= 0; m--) {
            if (redundant[m]) {
                newAMatrix.remove(m);
                newBVector.remove(m);
            }
        }

        String[] newQVector = new String[domain.numberOfDimensions()];

        for (int i = 0; i < newQVector.length; i++) {
            newQVector[i] = new String(qVector[i]);
        }

        newRegion.setq(newQVector);
        double[][] newA = makeArray2D(newAMatrix);
        double[] newB = makeArray1D(newBVector);
        newRegion.setA(newA);
        newRegion.setb(newB);

        return newRegion;
    }

    /**
     * Property-concept combination. No context weights are required.
     */
    public CSMLConcept combine(CSMLConcept property, CSMLConcept concept) {
        if (!property.isProperty()) {
            return null;
        }
        CSMLConcept newConcept = model.createCSMLConcept(property.getId() + concept.getId());
        String propDomain = property.getDomainIds()[0];
        if (!concept.hasRegionInDomain(propDomain)) {
            // TODO
        }
        // TODO
        return newConcept;
    }

    /**
     * Contrast class-concept combination
     */
    public CSMLConcept combine(CSMLContrastClass cc, CSMLConcept c) {
       // CSMLConcept newConcept = new CSMLConcept(new String(cc.getId() + c.getId()));
        // TODO

        //return newConcept;
        return null;
    }

    private boolean equalWithinPrecision(double x, double y) {
        if (Math.abs(x - y) < roundPrecision) {
            return true;
        } else {
            return false;
        }
    }

    public boolean samePoint(CSMLPoint p1, CSMLPoint p2) {
        if (p1 == p2) {
            return true;
        }
        if (p1 == null || p2 == null) {
            return false;
        }
        if (p1.equals(p2)) {
            return true;
        }
        String domainId = p1.getDomainId();
        if (domainId == null) {
            return false;
        }
        CSMLDomain d = model.getDomain(domainId);
        if (d == null) {
            return false;
        }
        if (!p1.getDomainId().equals(p2.getDomainId())) {
            return false;
        }

        String[] qdIds = d.getQualityDimensionIds();
        for (int i = 0; i < qdIds.length; i++) {
            if (!equalWithinPrecision(p1.getValue(qdIds[i]), p2.getValue(qdIds[i]))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the two instances have the same points for each of the
     * given domains
     */
    public boolean sameInstance(CSMLInstance i1, CSMLInstance i2, ArrayList<String> domainIds) {
        for (int i = 0; i < domainIds.size(); i++) {
            CSMLPoint p1 = i1.getPoint(domainIds.get(i));
            CSMLPoint p2 = i2.getPoint(domainIds.get(i));
            if (!samePoint(p1, p2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the two instances have the same points for all shared
     * domains
     */
    public boolean sameInstance(CSMLInstance i1, CSMLInstance i2) {
        ArrayList<CSMLDomain> domains = sharedDomains(i1, i2);
        ArrayList<String> domainIds = new ArrayList<String>();
        for (int i = 0; i < domains.size(); i++) {
            domainIds.add(domains.get(i).getId());
        }
        return sameInstance(i1, i2, domainIds);
    }

    /**
     * Returns true if region r contains point p
     */
    public boolean contains(CSMLRegion r, CSMLPoint p) {
        if (r == null || p == null) {
            return false;
        }
        String domainId = r.getDomainId();
        // point and region must be in same domain
        if (domainId == null || !r.getDomainId().equals(p.getDomainId())) {
            return false;
        }
        CSMLDomain d = model.getDomain(domainId);
        // domain must exist in model
        if (d == null) {
            return false;
        }
        // region and point must be consistent with domain
        if (!r.domainOK() || !p.domainOK()) {
            return false;
        }

        return r.contains(p, roundPrecision);
    }

    /**
     * Returns true if region r1 contains region r2 (i.e. r2's convex hull
     * points) completely
     */
    public boolean contains(CSMLRegion r1, CSMLRegion r2) {
        CSMLPoint[] hullPoints = r2.getVPolytope();
        if (hullPoints == null) {
            hullPoints = vPolytope(r2);
        }
        if (hullPoints == null) {
            return false;
        }
        for (int i = 0; i < hullPoints.length; i++) {
            if (!contains(r1, hullPoints[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if concept c contains instance i for the given domains
     */
    public boolean contains(CSMLConcept concept, CSMLInstance instance) {
        return concept.contains(instance);
    }

    /**
     * Returns true if concept c contains instance i for all shared domains
     */
//    public boolean contains(CSMLConcept concept, CSMLInstance instance) {
//        return contains(concept, instance, sharedDomains(concept, instance));
//    }

    /**
     * Gets the V-polytope representation of a region as a set of points. Also
     * sets the vPolytope in the region data structure, so that it does not need
     * to be calculated again
     */
    public CSMLPoint[] vPolytope(CSMLRegion r) {
        CSMLPoint[] points = null;

        if (r.getCentroidAsPoint() != null) {
            String qHullInput = r.qHullFormat();

            String rand;
            String qHullOutputFN;
            File f;

            do {  // find unique random filename
                rand = Long.toHexString(new Random().nextLong());
                qHullOutputFN = new String(tempDir + rand + ".tmp");
                f = new File(qHullOutputFN);
            } while (f.exists());

            try {
                // run qhull
                String[] qHull = {qHullPath, "H", "Fp", "TO", qHullOutputFN};
                Process p = Runtime.getRuntime().exec(qHull);

                PrintStream ps = new PrintStream(new BufferedOutputStream(p.getOutputStream()));
                // write H-polytope to qhull
                ps.print(qHullInput);
                ps.close();

                // Set a timer to interrupt the process if it does not return within the timeout period
                Timer timer = new Timer();
                timer.schedule(new InterruptScheduler(Thread.currentThread()), qHullTimeout);
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    // Stop the process from running
                    p.destroy();
                    return null;
                } finally {
                    // Stop the timer
                    timer.cancel();
                }

                BufferedReader qHullResultFile = new BufferedReader(new FileReader(qHullOutputFN));
                ArrayList<String> qHullResult = new ArrayList<String>();
                String inString;
                while ((inString = qHullResultFile.readLine()) != null) {
                    qHullResult.add(inString);
                }
                qHullResultFile.close();

                if (qHullResult.size() < 3) {
                    return null;
                }
                int qLength = Integer.parseInt(qHullResult.get(0));
                if (qLength != r.getq().length) {
                    return null;
                }

                int numPoints = Integer.parseInt(qHullResult.get(1));
                points = new CSMLPoint[numPoints];
                for (int i = 0; i < numPoints; i++) {
                    String row = qHullResult.get(i + 2);
                    String[] rowVals = row.trim().split("\\s+");
                    points[i] = model.createCSMLPoint(r.getId() + ".VPOLYTOPE_" + i, r.getDomain());
                    String[] pointQ = new String[qLength];
                    for (int j = 0; j < qLength; j++) {
                        pointQ[j] = new String(r.getq()[j]);
                    }
                    double[] pointV = new double[qLength];
                    for (int j = 0; j < rowVals.length; j++) {
                        pointV[j] = Double.parseDouble(rowVals[j]);
                    }
                    points[i].setPoint(pointQ, pointV);
                }
            } catch (IOException e) {
                // TODO
                return null;
            }

            // delete qhull temp file
            if (f.exists()) {
                f.delete();
            }
        }

        r.setVPolytope(points);
        return points;
    }

    /**
     * Calculates v polytope for region r and stores the result in the r object.
     * Returns true if successful.
     */
    public boolean setVPolytope(CSMLRegion r) {
        CSMLPoint[] pts = vPolytope(r);
        if (pts == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean standardizeInstancesInDomain(String domainId) {
        CSMLDomain d = model.getDomain(domainId);
        String[] qIds = d.getQualityDimensionIds();
        CSMLInstance[] instances = model.getInstances();

        ArrayList<ArrayList<Pair<String, Double>>> pointValues = new ArrayList<ArrayList<Pair<String, Double>>>();

        // create a table column for each quality dimension
        for (String qId : qIds) {
            pointValues.add(new ArrayList<Pair<String, Double>>());
        }

        for (CSMLInstance i : instances) {
            if (i.hasPointInDomain(domainId)) {
                for (int q = 0; q < qIds.length; q++) {
                    // add the quality dimension value for the point
                    pointValues.get(q).add(new Pair<String, Double>(i.getId(), i.getPoint(domainId).getValue(qIds[q])));
                }
            }
        }
        for (int q = 0; q < qIds.length; q++) {
            ArrayList<Pair<String, Double>> row = pointValues.get(q);
            ArrayList<Double> rowVals = new ArrayList<Double>();
            for (int i = 0; i < row.size(); i++) {
                rowVals.add(row.get(i).getSecond());
            }
            Pair<Double, Double> meanSD = MathFunc.meanStddev(rowVals);
            for (int i = 0; i < row.size(); i++) {
                double score = MathFunc.standardScore(rowVals, i, meanSD);
                CSMLInstance inst = model.getInstance(row.get(i).getFirst());
                CSMLPoint p = inst.getPoint(domainId);
                // update the value of the point with the standard score value
                p.updateValue(qIds[q], score);
            }
        }

        return true;
    }

    public boolean standardizeInstancesInAllDomains() {
        CSMLDomain[] domains = model.getDomains();
        for (CSMLDomain d : domains) {
            standardizeInstancesInDomain(d.getId());
        }
        return true;
    }

    private class InterruptScheduler extends TimerTask {

        Thread target = null;

        public InterruptScheduler(Thread target) {
            this.target = target;
        }

        @Override
        public void run() {
            target.interrupt();
        }
    }
}
