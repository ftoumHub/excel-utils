package parser;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.junit.jupiter.api.Test;
import util.ParserErrorClass;
import util.SafeCell_V0;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Dans cette version de numericRange on utilise une classe pour représenter les erreurs possibles.
 */
public class S03_Numeric_Range_V1 extends WithExampleWorkbook {

    /**
     * On construit une classe {@link ParserErrorClass} qui va représenter un type d'erreur possible.
     * <p>
     * On utilise également {@link SafeCell} qui est un wrapper "safe"
     */
    @Test
    public void numRangeV1_WithSafeCell() {
        // On utilise maintenant SafeCell pour retourner un Either<ParserErrorClass, Double> pour chaque cellule.
        Either<ParserErrorClass, Seq<Double>> oilProd = numericRangeV1(workbook, "OilProd");

        assertThat(oilProd.get().toJavaList())
                .containsExactly(10.12, 12.34, 8.83, 6.23, 9.18, 12.36, 16.28, 18.25, 20.01);
    }

    @Test
    public void cellErrorHandling() {
        Either<ParserErrorClass, Seq<Double>> primaryProdEither = numericRangeV1(workbook, "PrimaryProduct");

        assertEquals(primaryProdEither.getLeft().getMessage(), "Cannot get a NUMERIC value from a STRING cell");
    }

    @Test
    public void stillNotTotalFunction() {
        // numericRangeV1 n'est pas une fonction totale,
        // on peut de se prendre une exception si on recherche une plage de cellule inexistante
        assertThrows(NullPointerException.class, () -> numericRangeV1(workbook, "foo"));
    }


    // En utilisant SafeCell, on renvoi une Either<ParserErrorX, Double> pour chaque cellule
    private static Either<ParserErrorClass, Seq<Double>> numericRangeV1(Workbook workbook, String name) {
        String formula = workbook.getName(name).getRefersToFormula();
        AreaReference area = new AreaReference(formula, workbook.getSpreadsheetVersion());

        // Problème, ici, on se retrouve avec une List<Either<ParserErrorClass, Double>>
        // On transforme cette liste en Either<ParserErrorClass, Seq<Double>> avec Either.sequenceRight
        return Either.sequenceRight(List.of(area.getAllReferencedCells())
                .map(cellRef ->  workbook.getSheet(cellRef.getSheetName())
                        .getRow(cellRef.getRow())
                        .getCell(cellRef.getCol()))
                .map(SafeCell_V0::new)
                .map(SafeCell_V0::asDouble));
    }
}
