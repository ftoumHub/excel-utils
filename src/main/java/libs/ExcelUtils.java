package libs;

import io.vavr.control.Either;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import parser.ParserError;
import parser.SafeCell;

import static io.vavr.API.Try;
import static io.vavr.control.Either.left;

public class ExcelUtils {

    private ExcelUtils() {}

    public static Either<ParserError, AreaReference> getArea(Workbook workbook, String name) {
        return Try(() -> new AreaReference(workbook.getName(name).getRefersToFormula(), workbook.getSpreadsheetVersion()))
                .fold(
                        e -> left(new ParserError.MissingName(name)),
                        Either::right
                );
    }

    public static Either<ParserError, SafeCell> getSafeCell(Workbook workbook, CellReference cellRef) {
        return Try(() -> new SafeCell(getCell(workbook, cellRef)))
                .fold(
                        e -> left(new ParserError.MissingCell(cellRef.toString())),
                        Either::right
                );
    }

    public static Cell getCell(Workbook workbook, CellReference cellRef) {
        // on a plusieurs accesseurs qui permettent, à partir d'une cellule de :
        return workbook
                .getSheet(cellRef.getSheetName())   // trouver un onglet
                .getRow(cellRef.getRow())           // trouver une colonne
                .getCell(cellRef.getCol());         // trouver une cellule
    }

    public static Cell getCellAt(java.util.List<CellReference> cells, int i, Workbook workbook) {
        return workbook.getSheet(cells.get(i).getSheetName())
                .getRow(cells.get(i).getRow())
                .getCell(cells.get(i).getCol());
    }
}
