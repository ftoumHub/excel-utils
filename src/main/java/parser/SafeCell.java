package parser;

import io.vavr.control.Either;
import io.vavr.control.Try;
import org.apache.poi.ss.usermodel.Cell;

public class SafeCell {

    private Cell cell;
    private String reference;

    public SafeCell() {}

    public SafeCell(Cell cell) {
        this.cell = cell;
        this.reference = String.format("%s!%s", cell.getSheet().getSheetName(), cell.getAddress());
    }

    public Cell getCell() {
        return cell;
    }

    public String getReference() {
        return reference;
    }

    public Either<ParserError, Double> asDouble() {
        return Try.of(() -> this.cell.getNumericCellValue()).fold(
                e -> Either.left(new ParserError.InvalidFormat(this.reference, "Numeric", e.getMessage())),
                doubleTry -> Either.right(doubleTry)
        );
    }
}
