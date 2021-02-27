package parser;

import io.vavr.API;
import io.vavr.control.Either;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.AreaReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.SafeCell_V0;

import static io.vavr.API.*;
import static libs.ExcelUtils.getCell;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class S02_Excel_API extends WithExampleWorkbook {

    @DisplayName("getName(\"???\") avec une référence de cellule eronnée lance une exception")
    @Test
    public void identifyCellByName() {
        // On récupère la référence à une plage de cellule à partir d'un nom
        assertEquals("Sheet1!$B$6:$J$6", workbook.getName("OilProd").getRefersToFormula());

        // Si la référence n'existe pas, on se prend une exception
        // getName est donc une méthode "partielle"
        assertThrows(NullPointerException.class, () -> workbook.getName("???").getRefersToFormula());
    }

    @DisplayName("Identifier une plage de cellule nommée")
    @Test
    public void identifyAreaReferenceCells() {
        String oilProdFormule = workbook.getName("OilProd").getRefersToFormula();

        AreaReference area = new AreaReference(oilProdFormule, workbook.getSpreadsheetVersion());

        List(area.getAllReferencedCells())
                .forEach(API::println);

        // On a bien une plage de 9 cellules
        assertEquals(area.getAllReferencedCells().length, 9);
    }

    @Test
    public void getNumericCellValue() {
        Cell cell = extractOilProdFirstCell();

        // Une fois, qu'on a la cellule, on peut récupérer sa valeur
        assertEquals(cell.getNumericCellValue(), 10.12, 0);

        assertEquals(Double.valueOf(cell.getNumericCellValue()), Double.valueOf(10.12));
    }

    @Test
    public void getNumericValueWithSafeCell() {
        SafeCell_V0 safeCellV0 = new SafeCell_V0(extractOilProdFirstCell());

        printf("Valeur de la première cellule : %s\n", safeCellV0.asDouble());
        assertEquals(Either.right(10.12), safeCellV0.asDouble());

        println(safeCellV0.asString());
        assertEquals("Cannot get a STRING value from a NUMERIC cell", safeCellV0.asString().getLeft().getMessage());
    }

    private Cell extractOilProdFirstCell() {
        AreaReference area = new AreaReference(
                workbook.getName("OilProd").getRefersToFormula(),
                workbook.getSpreadsheetVersion());

        return getCell(workbook, List(area.getAllReferencedCells()).head());
    }
}
