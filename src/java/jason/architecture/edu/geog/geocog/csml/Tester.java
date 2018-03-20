/**
 * Copyright (c) 2009, Benjamin Adams
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of Benjamin Adams nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY Benjamin Adams ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Benjamin Adams BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * @author Ben
 */

package edu.geog.geocog.csml;

import edu.geog.geocog.csml.model.CSMLRegion;
import edu.geog.geocog.csml.model.CSMLQualityDimension;
import edu.geog.geocog.csml.model.CSMLDomain;
import edu.geog.geocog.csml.model.CSMLModel;
import edu.geog.geocog.csml.reasoner.CSMLConceptualSpaceReasoner;
import edu.geog.geocog.csml.model.CSMLPoint;
import java.util.ArrayList;

import edu.geog.geocog.csml.exceptions.InvalidContextTypeException;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
/**		double[][] A = { {2., -3.}, {1., 5.}, {-1., 0.} };
		String[] q = { "x", "y" };
		double[] b = { 12., 20., 0. };
		
		CSMLRegion r = new CSMLRegion("My CSMLRegion", "My CSMLDomain");
		boolean test = r.setRegion(A, q, b);
		CSMLRegion r2 = new CSMLRegion("My CSMLRegion 2", "My CSMLDomain");
		System.out.println(test);
		
		String[] testq = {"y", "x"};
		double[] testp = {2.0, 1.0};
		//test = r.contains(testq, testp);
		System.out.println(test);
		
		CSMLParser p = new CSMLParser(new Logger());
		CSMLModel m = p.parseCSML("colors.csml");
		
		Instance[] iArray = m.getInstances();
		ArrayList<Point> points = new ArrayList<Point>();
		for (int i = 0; i < iArray.length; i++) {
			points.add(iArray[i].getPoint("http://geocog.geog.ucsb.edu/csml/repository/ColorHSV"));
		}
		CSMLConceptualSpaceReasoner rsn = new CSMLConceptualSpaceReasoner(m);
		CSMLRegion colorsR = rsn.convexHull(points, "colors_yeah!");
		System.out.println(); **/
	/*	
		CSMLModel m = new CSMLModel();
		CSMLDomain d = new CSMLDomain("xy");
		CSMLQualityDimension qd1 = new CSMLQualityDimension("x");
		d.addQualityDimension(qd1);
		CSMLQualityDimension qd2 = new CSMLQualityDimension("y");
		d.addQualityDimension(qd2);
		m.addDomain(d);
		
		String[] dims = {"x", "y"};
		*/
		/*
		CSMLRegion r1 = new CSMLRegion("R1", "xy");
		r1.setq(dims);
		double[][] r1A = { { -1.0, 0.0 },
						   { 1.0, 0.0 },
						   { 0.0, -1.0 },
						   { 0.0, 1.0 } };
		double[] r1b = { 0.0, 1.0, 0.0, 1.0 };
		r1.setA(r1A);
		r1.setb(r1b);
	*/
/*		CSMLPoint pa = new CSMLPoint("pa", "xy");
		pa.setq(dims);
		double[] pva = {0.8, 0.9};
		pa.setv(pva);
		Instance ia = new Instance("ia");
		ia.addPoint(pa);
		m.addInstance(ia);
		
		CSMLPoint pb = new CSMLPoint("pb", "xy");
		pb.setq(dims);
		double[] pvb = {1.0, 0.4};
		pb.setv(pvb);
		Instance ib = new Instance("ib");
		ib.addPoint(pb);
		m.addInstance(ib);
		
		CSMLPoint pc = new CSMLPoint("pc", "xy");
		pc.setq(dims);
		double[] pvc = {0.75, 0.8};
		pc.setv(pvc);
		Instance ic = new Instance("ic");
		ic.addPoint(pc);
		m.addInstance(ic);
		
		CSMLPoint pd = new CSMLPoint("pd", "xy");
		pd.setq(dims);
		double[] pvd = {0.25, 1.0};
		pd.setv(pvd);
		Instance id = new Instance("id");
		id.addPoint(pd);
		m.addInstance(id);
		
		ArrayList<Point>points = new ArrayList<Point>();
		points.add(pa); points.add(pb); points.add(pc); points.add(pd);
		
		CSMLRegion r2 = new CSMLRegion("R2", "xy");
		r2.setq(dims);
		double[][] r2A = { { -1.0, 0.0 },
						   { 1.0, 0.0 },
						   { 0.0, -1.0 },
						   { 0.0, 1.0 } };
		double[] r2b = { -0.5, 1.5, -0.5, 1.5 };
		r2.setA(r2A);
		r2.setb(r2b);
		
		CSMLConceptualSpaceReasoner rsn = new CSMLConceptualSpaceReasoner(m);
		CSMLRegion r1 = rsn.convexHull(points, "R1");
		CSMLRegion dR = rsn.domainRegion("xy");
		ArrayList<Region> voronoiRegions;
		voronoiRegions = rsn.voronoiTessellation(points);
		
		CSMLRegion rInt = rsn.intersection(r1, r2, "RNew");
		
		CSMLPoint p1 = new CSMLPoint("p1", "xy");
		p1.setq(dims);
		double[] pv1 = {0.5, 0.5};
		p1.setv(pv1);
		
		boolean p1In = rInt.contains(p1, d);
		System.out.println(p1In);
		
		DomainTypeContext dContext = new DomainTypeContext("testDomainContext");
		dContext.addWeight("xy", 1.0);
		
		QualityDimensionTypeContext qdContext = new QualityDimensionTypeContext("testQualityDimensionContext", "xy");
		qdContext.addWeight("x", 0.5);
		qdContext.addWeight("y", 0.5);
		
		QualityDimensionTypeContext qdContextXYZ = new QualityDimensionTypeContext("testQualityDimensionContextXYZ", "xyz");
		qdContextXYZ.addWeight("xyz.x", 0.25);
		qdContextXYZ.addWeight("xyz.y", 0.25);
		qdContextXYZ.addWeight("xyz.z", 0.5);
		
		m.addContext(dContext);
		m.addContext(qdContext);
		m.addContext(qdContextXYZ);
		
		ArrayList<Context> qdContexts = new ArrayList<Context>();
		qdContexts.add(qdContext);
		qdContexts.add(qdContextXYZ);
		
		String[] qdContextIds = new String[qdContexts.size()];
		for (int i = 0; i < qdContexts.size(); i++) {
			qdContextIds[i] = qdContexts.get(i).getId();
		}
		
		double distPaPc = rsn.distance(pa, pc, qdContext);
		System.out.println(distPaPc);
		
		try {
			double simIaIb = rsn.semanticSimilarity(ia.getId(), ic.getId(), dContext.getId(), qdContextIds);
			System.out.println(simIaIb);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CSMLDomain dXYZ = new CSMLDomain("xyz");
		CSMLQualityDimension qdXYZ1 = new CSMLQualityDimension("xyz.x");
		dXYZ.addQualityDimension(qdXYZ1);
		CSMLQualityDimension qdXYZ2 = new CSMLQualityDimension("xyz.y");
		dXYZ.addQualityDimension(qdXYZ2);
		CSMLQualityDimension qdXYZ3 = new CSMLQualityDimension("xyz.z");
		dXYZ.addQualityDimension(qdXYZ3);
		m.addDomain(dXYZ);
		
		String[] dimsXYZ = {"xyz.x", "xyz.y", "xyz.z"};
		
		CSMLPoint paXYZ = new CSMLPoint("paXYZ", "xyz");
		paXYZ.setq(dimsXYZ);
		double[] pvaXYZ = {0.5, 0.5, 0.5};
		paXYZ.setv(pvaXYZ);
		Instance iaXYZ = new Instance("iaXYZ");
		iaXYZ.addPoint(paXYZ);
		iaXYZ.addPoint(pa);
		m.addInstance(iaXYZ);

		CSMLPoint pcXYZ = new CSMLPoint("pcXYZ", "xyz");
		pcXYZ.setq(dimsXYZ);
		double[] pvcXYZ = {0.48, 0.46, 0.50};
		pcXYZ.setv(pvcXYZ);
		Instance icXYZ = new Instance("icXYZ");
		icXYZ.addPoint(pcXYZ);
		icXYZ.addPoint(pc);
		m.addInstance(icXYZ);
		
		DomainTypeContext dContextXYZ = new DomainTypeContext("testDomainContextXYZ");
		dContextXYZ.addWeight("xy", 0.0);
		dContextXYZ.addWeight("xyz", 1.0);
		m.addContext(dContextXYZ);
		
		double distPaPcXYZ = rsn.distance(paXYZ, pcXYZ, qdContextXYZ);
		System.out.println(distPaPcXYZ);
		
		try {
			double simIaIc = rsn.semanticSimilarity(iaXYZ.getId(), icXYZ.getId(), dContextXYZ.getId(), qdContextIds);
			System.out.println(simIaIc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(m.toCSML());
*/	/*	System.out.println("teste");
		CSMLModel m = new CSMLModel();
		CSMLDomain d1 = new CSMLDomain("d1",m);
		CSMLQualityDimension qd1 = new CSMLQualityDimension("qd1",m);
		d1.addQualityDimension(qd1);
		CSMLQualityDimension qd2 = new CSMLQualityDimension("qd2",m);
		d1.addQualityDimension(qd2);
		m.addDomain(d1);
		
		ArrayList<CSMLPoint> points = new ArrayList<CSMLPoint>();
		
		CSMLPoint p1 = new CSMLPoint("p1",m, "d1");
		p1.setq(d1.getQualityDimensionIds());
		double[] pv1 = {3.4, 6.0};
		p1.setv(pv1);
		points.add(p1);
		
		CSMLPoint p2 = new CSMLPoint("p2",m, "d1");
		p2.setq(d1.getQualityDimensionIds());
		double[] pv2 = {2.3, 0.4};
		p2.setv(pv2);
		points.add(p2);
		
		CSMLPoint p3 = new CSMLPoint("p3",m, "d1");
		p3.setq(d1.getQualityDimensionIds());
		double[] pv3 = {5.1, 0.2};
		p3.setv(pv3);
		points.add(p3);
		
		CSMLPoint p4 = new CSMLPoint("p4",m, "d1");
		p4.setq(d1.getQualityDimensionIds());
		double[] pv4 = {1.2, 3.0};
		p4.setv(pv4);
		points.add(p4);
		
		CSMLPoint p5 = new CSMLPoint("p5",m, "d1");
		p5.setq(d1.getQualityDimensionIds());
		double[] pv5 = {4.0, 1.2};
		p5.setv(pv5);
		points.add(p5);
		
		CSMLConceptualSpaceReasoner rsn = new CSMLConceptualSpaceReasoner(m);
		CSMLRegion newR = rsn.convexHull(points, "r1");
		
		CSMLPoint p6 = new CSMLPoint("p6",m, "d1");
		p6.setq(d1.getQualityDimensionIds());
		double[] pv6 = {-8.6, -0.5};
		p6.setv(pv6);
		points.add(p6);
		
		CSMLPoint p7 = new CSMLPoint("p7",m, "d1");
		p7.setq(d1.getQualityDimensionIds());
		double[] pv7 = {-1.0, -3.0};
		p7.setv(pv7);
		points.add(p7);
		
		CSMLPoint p8 = new CSMLPoint("p8",m, "d1");
		p8.setq(d1.getQualityDimensionIds());
		double[] pv8 = {-5.4, -1.1};
		p8.setv(pv8);
		points.add(p8);
		
		CSMLPoint p9 = new CSMLPoint("p9",m, "d1");
		p9.setq(d1.getQualityDimensionIds());
		double[] pv9 = {-0.4, -2.6};
		p9.setv(pv9);
		points.add(p9);
		
		CSMLPoint p10 = new CSMLPoint("p10",m, "d1");
		p10.setq(d1.getQualityDimensionIds());
		double[] pv10 = {-0.9, -0.2};
		p10.setv(pv10);
		points.add(p10);
		
		CSMLRegion newR2 = rsn.convexHull(points, "r2");
		
		CSMLRegion intReg = rsn.intersection(newR, newR2, "r3");
		
		// System.out.println(intReg.contains(p1, d1));
		///System.out.println(intReg.contains(p2, d1));
		//System.out.println(intReg.contains(p3, d1));
		//System.out.println(intReg.contains(p4, d1));
		//System.out.println(intReg.contains(p5, d1));
		//System.out.println(intReg.contains(p6, d1));
		//System.out.println(intReg.contains(p7, d1));
		//System.out.println(intReg.contains(p8, d1));
		//System.out.println(intReg.contains(p9, d1));
		//System.out.println(intReg.contains(p10, d1));
		
		//System.out.println(); 

       /*         points = new ArrayList<CSMLPoint>();
                p1 = new CSMLPoint("p1",m, "d1");
                p1.setq(d1.getQualityDimensionIds());
                pv1 = new double[] {0.0, 1.0};
                p1.setv(pv1);
                points.add(p1); 

                p2 = new CSMLPoint("p2",m, "d1");
                p2.setq(d1.getQualityDimensionIds());
                pv2 = new double[] {1.0, 0.0};
                p2.setv(pv2);
                points.add(p2);

                p3 = new CSMLPoint("p3",m, "d1");
                p3.setq(d1.getQualityDimensionIds());
                pv3 = new double[] {2.0, 2.0};
                p3.setv(pv3);
                points.add(p3);

                CSMLRegion region1 = rsn.convexHull(points, "Region1");
                System.out.println(region1);

                points = new ArrayList<CSMLPoint>();
                p4 = new CSMLPoint("p4",m, "d1");
                p4.setq(d1.getQualityDimensionIds());
                pv4 = new double[] {1.0, 1.0};
                p4.setv(pv4);
                points.add(p4);

                p5 = new CSMLPoint("p5",m, "d1");
                p5.setq(d1.getQualityDimensionIds());
                pv5 = new double[] {1.0, 5.0};
                p5.setv(pv5);
                points.add(p5);

                p6 = new CSMLPoint("p6",m, "d1");
                p6.setq(d1.getQualityDimensionIds());
                pv6 = new double[] {5.0, 1.0};
                p6.setv(pv6);
                points.add(p6);

                CSMLRegion region2 = rsn.convexHull(points, "Region2");
                System.out.println(region2);

                CSMLRegion intersectionRegion = rsn.intersection(region1, region2, "intersectionRegion");
                System.out.println(intersectionRegion);*/
                System.out.println("teste");
	}

}
