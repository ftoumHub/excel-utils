package parser;

import io.vavr.API;
import io.vavr.collection.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.junit.jupiter.api.Test;

import static io.vavr.API.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Création d'une première version de la function numericRange.
 */
public class S03_Numeric_Range_V0 extends WithExampleWorkbook {

    /**
     * On retourne une liste de Double, ou une exception si on est pas sur une plage de valeurs numériques
     */
    private static List<Double> numericRangeV0(Workbook workbook, String name) {
        String formula = workbook.getName(name).getRefersToFormula();
        AreaReference area = new AreaReference(formula, workbook.getSpreadsheetVersion());

        return List(area.getAllReferencedCells())
                .map(cellRef -> workbook.getSheet(cellRef.getSheetName())
                        .getRow(cellRef.getRow())
                        .getCell(cellRef.getCol()))
                .map(Cell::getNumericCellValue);
    }

    @Test
    public void numericRangeV0_works_but_is_not_safe() {
        // On peut mainteant facilement extraire une liste de valeur depuis un fichier excel
        List<Double> oilProdAsDouble = numericRangeV0(workbook, "OilProd");

        assertThat(oilProdAsDouble)
                .containsExactly(10.12, 12.34, 8.83, 6.23, 9.18, 12.36, 16.28, 18.25, 20.01);
    }

    @Test
    public void numericRangeV0_fails_if_range_is_not_numeric() {
        // Si on essai de lire une valeur numérique dans une liste de String avec getNumericCell, on se prend une exception.
        // La fonction "numericRangeV0" n'est donc pas une fonction "totale".
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> numericRangeV0(workbook, "PrimaryProduct"));

        assertTrue(thrown.getMessage().contains("Cannot get a NUMERIC value from a STRING cell"));
    }

}
