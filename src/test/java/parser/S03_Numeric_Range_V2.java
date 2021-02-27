package parser;

import io.vavr.API;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import static io.vavr.API.*;
import static libs.ExcelUtils.getArea;
import static libs.ExcelUtils.getSafeCell;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static parser.ParserUtils.numericRange;

/**
 * Dans cette version de numericRange on utilise l'interface {@link ParserError} pour représenter les erreurs possibles.
 */
public class S03_Numeric_Range_V2 extends WithExampleWorkbook {

    @Test
    public void numericRangeV2_with_ParserError() {

        assertEquals(Right(Vector(10.12, 12.34, 8.83, 6.23, 9.18, 12.36, 16.28, 18.25, 20.01)),
                   numericRangeV2(workbook, "OilProd"));

        assertEquals(new ParserError.InvalidFormat("Sheet1!B4", "Numeric", "Cannot get a NUMERIC value from a STRING cell"),
                    numericRangeV2(workbook, "PrimaryProduct").getLeft());

        //final Either<ParserError, AreaReference> foo = getArea(workbook, "foo");
        assertEquals(new ParserError.MissingName("foo"),
                    numericRangeV2(workbook, "foo").getLeft());
    }

    @Test
    public void numericRange_Final_with_ParserError() {
        // Dans numericRangeV2, on utilise l'interface ParserError...
        assertEquals(Right(Vector(10.12, 12.34, 8.83, 6.23, 9.18, 12.36, 16.28, 18.25, 20.01)),
                    numericRange(workbook, "OilProd"));

        assertEquals(new ParserError.InvalidFormat("Sheet1!B4", "Numeric", "Cannot get a NUMERIC value from a STRING cell"),
                    numericRange(workbook, "PrimaryProduct").getLeft());

        assertEquals(new ParserError.MissingName("foo"),
                    numericRange(workbook, "foo").getLeft());
    }

    private static Either<ParserError, Seq<Double>> numericRangeV2_Old(Workbook workbook, String name) {
        return getArea(workbook, name).fold(
                Either::left,
                areaRef -> {
                    Either<ParserError, Seq<SafeCell>> seqSafe =
                            Either.sequenceRight(List(areaRef.getAllReferencedCells())
                                    .map(cellRef -> getSafeCell(workbook, cellRef)));
                    return Either.sequenceRight(seqSafe.get().map(SafeCell::asDouble).toList());
                });
    }

    private static Either<ParserError, Seq<Double>> numericRangeV2(Workbook workbook, String name) {
        return getArea(workbook, name)
                .fold(
                        Either::left,
                        area -> Either.sequenceRight(
                                For(List(area.getAllReferencedCells())
                                                .map(cellRef -> getSafeCell(workbook, cellRef)),
                                        cells -> For(cells.map(SafeCell::asDouble))
                                            .yield()
                                )
                        )
                );
    }

}
