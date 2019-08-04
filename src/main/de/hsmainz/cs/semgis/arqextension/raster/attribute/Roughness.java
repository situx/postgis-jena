package de.hsmainz.cs.semgis.arqextension.raster.attribute;

import javax.media.jai.iterator.RectIterFactory;
import javax.media.jai.iterator.WritableRectIter;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase4;
import org.geotoolkit.coverage.grid.GridCoordinates2D;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.jaitools.tiledimage.DiskMemImage;

public class Roughness extends FunctionBase4{

	@Override
	public NodeValue exec(NodeValue v1, NodeValue v2, NodeValue v3, NodeValue v4) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 public GridCoverage2D execute(GridCoverage2D inputGc) {
	        this.initSurface(inputGc);

	        DiskMemImage outputImage = this.createDiskMemImage(inputGc, RasterPixelType.FLOAT);
	        WritableRectIter writer = RectIterFactory.createWritable(outputImage,
	                outputImage.getBounds());

	        GridCoordinates2D pos = new GridCoordinates2D();

	        int y = bounds.y;
	        writer.startLines();
	        while (!writer.finishedLines()) {

	            int x = bounds.x;
	            writer.startPixels();
	            while (!writer.finishedPixels()) {
	                pos.setLocation(x, y);

	                visitRoughness(writer, pos);

	                writer.nextPixel();
	                x++;
	            }

	            writer.nextLine();
	            y++;
	        }

	        return createGridCoverage("Roughness", outputImage);
	    }

	    private void visitRoughness(WritableRectIter writer, GridCoordinates2D pos) {
	        // +-------+ +-------+
	        // | 0 1 2 | | a b c |
	        // | 3 4 5 |>| d e f |
	        // | 6 7 8 | | g h i |
	        // +-------+ +-------+
	        double[][] mx = getSubMatrix(pos, 3, 3);
	        if (Double.isNaN(mx[1][1]) || SSUtils.compareDouble(srcNoData, mx[1][1])) {
	            writer.setSample(0, NoData);
	            return;
	        }

	        // Roughness is the largest difference between any two cells
	        double roughnessMin = mx[0][0];
	        double roughnessMax = mx[0][0];
	        for (int col = 0; col < 3; col++) {
	            for (int row = 0; row < 3; row++) {
	                roughnessMax = Math.max(roughnessMax, mx[col][row]);
	                roughnessMin = Math.min(roughnessMin, mx[col][row]);
	            }
	        }

	        double roughness = roughnessMax - roughnessMin;

	        writer.setSample(0, roughness);
	        updateStatistics(roughness);
	    }
	}

}
