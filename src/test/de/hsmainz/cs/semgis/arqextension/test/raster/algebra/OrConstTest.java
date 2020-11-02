package de.hsmainz.cs.semgis.arqextension.test.raster.algebra;

import static org.junit.Assert.assertEquals;

import org.apache.jena.sparql.expr.NodeValue;
import org.junit.jupiter.api.Test;

import de.hsmainz.cs.semgis.arqextension.raster.algebra.OrConst;
import de.hsmainz.cs.semgis.arqextension.test.util.SampleRasters;
import io.github.galbiston.geosparql_jena.implementation.datatype.raster.HexWKBRastDatatype;

public class OrConstTest extends SampleRasters {

	public static final String combinedRasterLiteral="";
	
	@Test
	public void testOrConst() {
		NodeValue covLiteral = NodeValue.makeNode(wkbString1, HexWKBRastDatatype.INSTANCE);
        NodeValue covLiteral2 = NodeValue.makeInteger(10);
        NodeValue bandnum = NodeValue.makeInteger(0);
        OrConst instance=new OrConst();
        NodeValue expResult = NodeValue.makeNode(wkbString1, HexWKBRastDatatype.INSTANCE);
        NodeValue result = instance.exec(covLiteral,bandnum,covLiteral2);
        assertEquals(expResult, result);
	}

}