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

    public static Either<ParserError, AreaReference> getArea(Workbook workbook, String name) {
        return Try.of(() -> new AreaReference(workbook.getName(name).getRefersToFormula(),
                workbook.getSpreadsheetVersion()))
                .fold(e -> left(new ParserError.MissingName(name)), areaRef -> right(areaRef));
    }

    public static Either<ParserError, SafeCell> getSafeCell(Workbook workbook, CellReference cellRef) {
        Try safeCellTry = Try.of(() -> new SafeCell(
                workbook.getSheet(cellRef.getSheetName())
                        .getRow(cellRef.getRow())
                        .getCell(cellRef.getCol())));
        return safeCellTry.isFailure()
                ? left(new ParserError.MissingCell(cellRef.toString()))
                : right((SafeCell)safeCellTry.get());
    }

    public static Cell getCell(Workbook workbook, CellReference cellRef) {
        return workbook
                .getSheet(cellRef.getSheetName())
                .getRow(cellRef.getRow())
                .getCell(cellRef.getCol());
    }

    public static Cell getCellAt(java.util.List<CellReference> cells, int i, Workbook workbook) {
        return workbook.getSheet(cells.get(i).getSheetName())
                .getRow(cells.get(i).getRow())
                .getCell(cells.get(i).getCol());
    }
}
