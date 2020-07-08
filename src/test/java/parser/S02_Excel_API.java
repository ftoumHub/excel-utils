package parser;

import io.vavr.API;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.junit.jupiter.api.Test;
import util.SafeCell_V0;

import static io.vavr.API.println;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class S02_Excel_API extends WithExampleWorkbook {

    @Test
    public void identifyCellByName() {
        // On récupère la référence à une plage de cellule à partir d'un nom
        assertEquals("Sheet1!$B$6:$J$6", workbook.getName("OilProd").getRefersToFormula());

        // Si la référence n'existe pas, on se prend une exception
        assertThrows(NullPointerException.class, () -> workbook.getName("???").getRefersToFormula());
    }

    @Test
    public void identifyAreaReferenceCells() {

        String oilProdFormule = workbook.getName("OilProd").getRefersToFormula();

        AreaReference area = new AreaReference(oilProdFormule, workbook.getSpreadsheetVersion());

        List.of(area.getAllReferencedCells()).forEach(API::println);

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
        Cell cell = extractOilProdFirstCell();

        SafeCell_V0 safeCellV0 = new SafeCell_V0(cell);
        println(safeCellV0.asDouble());
        assertEquals(Either.right(10.12), safeCellV0.asDouble());

        println(safeCellV0.asString());
    }

    private Cell extractOilProdFirstCell() {
        AreaReference area = new AreaReference(workbook.getName("OilProd").getRefersToFormula(),
                workbook.getSpreadsheetVersion());

        CellReference cellRef = List.of(area.getAllReferencedCells()).head();

        // on a plusieurs accesseurs qui permettent, à partir d'une cellule de :
        Cell cell = workbook
                .getSheet(cellRef.getSheetName()) // trouver un onglet
                .getRow(cellRef.getRow())         // trouver une colonne
                .getCell(cellRef.getCol());       // trouver une cellule
        return cell;
    }
}
