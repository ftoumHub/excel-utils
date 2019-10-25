package parser;

//import fr.maif.util.societaire.ReferenceSocietaire;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.apache.poi.ss.usermodel.Cell;

public class SafeCell {

    private Cell cell;
    private String reference;

    public SafeCell(Cell cell) {
        this.cell = cell;
        this.reference = String.format("%s!%s", cell.getSheet().getSheetName(), cell.getAddress());
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

    /**public Either<ParserError, ReferenceSocietaire> asRefSoc() {
        Try refSocTry = Try.of(() -> ReferenceSocietaire.ofRefSoc(this.cell.getStringCellValue()));

        if (refSocTry.isFailure()) {
            // Match sur l'instance d'erreur (RefError ou autre)
            // match sur les erreurs possibles provenant de ReferenceSocietaire
            refSocTry.toEither().getLeft();
        }

        return refSocTry.isFailure()
                ? Either.left(new ParserError.InvalidFormat(
                        this.reference, "Numeric", refSocTry.getCause().getMessage()))
                : Either.right((ReferenceSocietaire) refSocTry.get());
    }*/
}
