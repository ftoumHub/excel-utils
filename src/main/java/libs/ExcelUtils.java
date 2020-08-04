package libs;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import io.vavr.control.Either;
import io.vavr.control.Try;
import parser.ParserError;
import parser.SafeCell;

public class ExcelUtils {

    private ExcelUtils() {}

    public static Either<ParserError, AreaReference> getArea(Workbook workbook, String name) {
        return Try.of(() -> new AreaReference(workbook.getName(name).getRefersToFormula(), workbook.getSpreadsheetVersion()))
                .fold(
                        e -> left(new ParserError.MissingName(name)),
                        Either::right
                );
    }

    public static Either<ParserError, SafeCell> getSafeCell(Workbook workbook, CellReference cellRef) {
        Try<SafeCell> safeCellTry = Try.of(() -> new SafeCell(getCell(workbook, cellRef)));
        return safeCellTry.isFailure()
                ? left(new ParserError.MissingCell(cellRef.toString()))
                : right(safeCellTry.get());
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
