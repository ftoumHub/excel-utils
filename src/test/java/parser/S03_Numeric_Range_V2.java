package parser;

import org.junit.jupiter.api.Test;

import static io.vavr.API.Right;
import static io.vavr.API.Vector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static parser.ParserUtils.numericRange;
import static parser.ParserUtils.numericRangeV2;

/**
 * Dans cette version de numericRange on utilise l'interface ParserError pour représenter les erreurs possibles.
 */
public class S03_Numeric_Range_V2 extends WithExampleWorkbook {

    @Test
    public void numericRangeV2_with_ParserError() {

        assertEquals(Right(Vector(10.12, 12.34, 8.83, 6.23, 9.18, 12.36, 16.28, 18.25, 20.01)),
                    numericRangeV2(workbook, "OilProd"));

        assertEquals(new ParserError.InvalidFormat("Sheet1!B4", "Numeric", "Cannot get a NUMERIC value from a STRING cell"),
                    numericRangeV2(workbook, "PrimaryProduct").getLeft());

        assertEquals( new ParserError.MissingName("foo"),
                    numericRangeV2(workbook, "foo").getLeft());
    }

    @Test
    public void numericRange_Final_with_ParserError() {
        // Dans numericRangeV2, on utilise l'interface ParserError...
        assertEquals(Right(Vector(10.12, 12.34, 8.83, 6.23, 9.18, 12.36, 16.28, 18.25, 20.01)),
                    numericRange(workbook, "OilProd"));

        assertEquals(new ParserError.InvalidFormat("Sheet1!B4", "Numeric", "Cannot get a NUMERIC value from a STRING cell"),
                    numericRange(workbook, "PrimaryProduct").getLeft());

        assertEquals( new ParserError.MissingName("foo"),
                    numericRange(workbook, "foo").getLeft());
    }

}
