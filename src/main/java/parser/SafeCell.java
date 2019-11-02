package parser;

import io.vavr.control.Either;
import io.vavr.control.Try;
import org.apache.poi.ss.usermodel.Cell;

public class SafeCell {

    private Cell cell;
    private String reference;

    public SafeCell() {
    }

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

    public Either<ParserErrorClass, Double> asDoubleV0() {
        try {
            return Either.right(this.cell.getNumericCellValue());
        } catch (IllegalStateException e){
            return Either.left(new ParserErrorClass(this.reference, "Numeric", e.getMessage()));
        }
    }

    public Either<ParserErrorClass, Double> asDoubleV1() {
        Try doubleTry = Try.of(() -> this.cell.getNumericCellValue());

        return doubleTry.isFailure()
                ? Either.left(new ParserErrorClass(this.reference, "Numeric", doubleTry.getCause().getMessage()))
                : Either.right((Double) doubleTry.get());
    }

    public Either<ParserErrorClass, String> asString() {
        Try doubleTry = Try.of(() -> this.cell.getStringCellValue());

        return doubleTry.isFailure()
                ? Either.left(new ParserErrorClass(this.reference, "String", doubleTry.getCause().getMessage()))
                : Either.right((String) doubleTry.get());
    }

    // Accesseur "Safe"
    public Either<ParserError, Double> asDouble() {
        Try doubleTry = Try.of(() -> this.cell.getNumericCellValue());

        return doubleTry.isFailure()
                ? Either.left(new ParserError.InvalidFormat(
                        this.reference, "Numeric", doubleTry.getCause().getMessage()))
                : Either.right((Double) doubleTry.get());
    }
}
