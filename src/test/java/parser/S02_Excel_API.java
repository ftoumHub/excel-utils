package parser;

import java.io.IOException;

import io.vavr.control.Either;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Workbook;

import static io.vavr.API.println;
import static org.junit.Assert.assertEquals;

import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.vavr.collection.List;

public class S02_Excel_API {

    private static Workbook workbook;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void setup() throws IOException, InvalidFormatException {
        workbook = IO.load("example.xlsx");
    }

    @Test
    public void identifyCellByName() {
        // On récupère la référence à une plage de cellule à partir d'un nom
        Name oilProduction = workbook.getName("OilProd");

        assertEquals(oilProduction.getRefersToFormula(), "Sheet1!$B$6:$J$6");
    }

    @Test
    public void identifyAreaReferenceCells() {

        String oilProdFormule = workbook.getName("OilProd").getRefersToFormula();

        AreaReference area = new AreaReference(oilProdFormule, workbook.getSpreadsheetVersion());

        List.of(area.getAllReferencedCells()).forEach(c -> println(c));

        assertEquals(area.getAllReferencedCells().length, 9);
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

    @Test
    public void getNumericValue() {
        Cell cell = extractOilProdFirstCell();

        // Une fois, qu'on a la cellule, on peut récupérer sa valeur
        assertEquals(cell.getNumericCellValue(), 10.12, 0);

        assertEquals(Double.valueOf(cell.getNumericCellValue()), Double.valueOf(10.12));
    }

    @Test
    public void getNumericValueWithSafeCell() {
        Cell cell = extractOilProdFirstCell();

        SafeCell safeCell = new SafeCell(cell);
        println(safeCell.asDouble());
        assertEquals(Either.right(10.12), safeCell.asDoubleV1());

        println(safeCell.asString());
    }
}
