package de.hsmainz.cs.semgis.arqextension.raster.algebra;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.LinkedList;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.CannotEvaluateException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.internal.coverage.BufferedGridCoverage;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;

import io.github.galbiston.geosparql_jena.implementation.datatype.raster.CoverageWrapper;

public class Add extends FunctionBase2 {

	@Override
	public NodeValue exec(NodeValue v1, NodeValue v2) {
		CoverageWrapper wrapper = CoverageWrapper.extract(v1);
		GridCoverage2D raster = wrapper.getXYGeometry();
		CoverageWrapper wrapper2 = CoverageWrapper.extract(v2);
		GridCoverage2D raster2 = wrapper2.getXYGeometry();
		Integer rd1 = 0, rd2 = 0;
		try {
		ParameterBlock pbSubtracted = new ParameterBlock();
		pbSubtracted.addSource(raster.getEnvelope());
		pbSubtracted.addSource(raster2.getEnvelope());
		RenderedOp subtractedImage = JAI.create("add", pbSubtracted);
		 final GridCoverageBuilder builder = new GridCoverageBuilder();
	        builder.setName("SampleCoverage.SST");
	        builder.setEnvelope(raster.getEnvelope());
	        builder.setCoordinateReferenceSystem(raster.getCoordinateReferenceSystem());
	        builder.variable(0).setSampleDimension(dim); getSampleDimension().
	        builder.variable(0).setName(raster.getSampleDimensions()[rd1].getDescription() + "+"
					+ raster2.getSampleDimensions()[rd2].getDescription());
	        builder.variable(0).setSampleRange(30, 220);
	        builder.variable(0).setLinearTransform(0.1, 10);
	        builder.variable(0).addNodataValue("Missing values", 255, Color.GRAY);
	        builder.setRenderedImage(subtractedImage);
	        GridCoverage2D cov=(GridCoverage2D) builder.build();
	        //builder.setValues(SampleCoverage.SST.raster());
	        coverage = builder.getGridCoverage2D();
		/*
		 * final GridGeometry grid = new
		 * GridGeometry(raster.getGridGeometry().getExtent(), PixelInCell.CELL_CENTER,
		 * MathTransforms.identity(2),
		 * raster.getGridGeometry().getCoordinateReferenceSystem());
		 * 
		 * final MathTransform1D toUnits = (MathTransform1D) MathTransforms.linear(0.5,
		 * 100);
		 */
		final SampleDimension sd = new SampleDimension.Builder().setName("t")
				.addQuantitative(
						(raster.getSampleDimensions().get(rd1).getName() + "+"
								+ raster2.getSampleDimensions().get(rd2).getName()).toString(),
						raster.getSampleDimensions().get(0).getMeasurementRange().get(),
						raster.getSampleDimensions().get(0).getTransferFunction().get(),
						raster.getSampleDimensions().get(0).getUnits().get())
				.build();
		
		List<SampleDimension>sds=new LinkedList<SampleDimension>();
		sds.add(sd);
		/*
		 * Create the grid coverage, gets its image and set values directly as short
		 * integers.
		 */
		BufferedGridCoverage coverage = new BufferedGridCoverage(raster2.getGridGeometry(),
				sds, DataBuffer.TYPE_SHORT);
		WritableRaster rasterr = ((BufferedImage) coverage.render(null)).getRaster();
		rasterr.setRect(subtractedImage.getSourceImage(0).getData());
		return CoverageWrapper.createCoverage(coverage, wrapper.getSrsURI(), wrapper.getRasterDatatypeURI())
				.asNodeValue();	
		} catch (CannotEvaluateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}

}