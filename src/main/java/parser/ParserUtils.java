package parser;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

import java.util.function.Function;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

public class ParserUtils {

    // On retourne une liste de Double
    public static List<Double> numericRangeV0(Workbook workbook, String name) {
        String formula = workbook.getName(name).getRefersToFormula();
        AreaReference area = new AreaReference(formula, workbook.getSpreadsheetVersion());

        return List.of(area.getAllReferencedCells())
                                .map(cellRef ->  workbook.getSheet(cellRef.getSheetName())
                                                .getRow(cellRef.getRow())
                                                .getCell(cellRef.getCol()))
                                .map(Cell::getNumericCellValue);
    }

    // En utilisant SafeCell, on renvoi une Either<ParserErrorX, Double> pour chaque cellule
    public static Either<ParserErrorClass, Seq<Double>> numericRangeV1(Workbook workbook, String name) {
        String formula = workbook.getName(name).getRefersToFormula();
        AreaReference area = new AreaReference(formula, workbook.getSpreadsheetVersion());

        List<SafeCell> safeCells = List.of(area.getAllReferencedCells())
                .map(cellRef ->  workbook.getSheet(cellRef.getSheetName())
                                        .getRow(cellRef.getRow())
                                        .getCell(cellRef.getCol()))
                .map(SafeCell::new);
        // Ici, on se retrouve avec une List<Either<ParserErrorClass, Double>>
        List<Either<ParserErrorClass, Double>> doubles = safeCells.map(SafeCell::asDoubleV1);
        // On transforme cette liste en Either<ParserErrorClass, Seq<Double>> avec Either.sequenceRight
        return Either.sequenceRight(doubles);
    }

    public static Either<ParserError, Seq<Double>> numericRangeV2(Workbook workbook, String name) {
        return getArea(workbook, name).fold(
                    parseError -> left(parseError),
                    areaRef -> {
                        Either<ParserError, Seq<SafeCell>> seqSafe =
                                Either.sequenceRight(List.of(areaRef.getAllReferencedCells())
                                        .map(cellRef -> getSafeCell(workbook, cellRef)));
                        return Either.sequenceRight(seqSafe.get().map(SafeCell::asDouble).toList());
                    });
    }

    static Either<ParserError, AreaReference> getArea(Workbook workbook, String name) {
        return Try.of(() -> new AreaReference(workbook.getName(name).getRefersToFormula(),
                                              workbook.getSpreadsheetVersion()))
                .fold(e -> left(new ParserError.MissingName(name)), areaRef -> right(areaRef));
    }

    static Either<ParserError, SafeCell> getSafeCell(Workbook workbook, CellReference cellRef) {
        Try safeCellTry = Try.of(() -> new SafeCell(
                workbook.getSheet(cellRef.getSheetName())
                        .getRow(cellRef.getRow())
                        .getCell(cellRef.getCol())));
        return safeCellTry.isFailure()
                ? left(new ParserError.MissingCell(cellRef.toString()))
                : right((SafeCell)safeCellTry.get());
    }

    /**
     * Si on veut réimplémenter numericRange à partir de 0 on voit qu'on peut réutiliser numericRangeV2
     */
    public static Parser<Double> numericFromScratch() {
        return new Parser<Double>() {
            @Override
            public Either<ParserError, Double> parse(Workbook workbook, String name) {
                Either<ParserError, AreaReference> area = getArea(workbook, name);

                if (area.isLeft()) return Either.left(area.getLeft());

                Either<ParserError, Seq<Double>> seqSafe =
                        Either.sequenceRight(List.of(area.get().getAllReferencedCells())
                                .map(cellRef -> getSafeCell(workbook, cellRef)).toList()
                                .map(sf -> sf.get().asDouble()).toList());

                if (seqSafe.get().size() !=1){
                    return Either.left(new ParserError.InvalidFormat(name, "Single Numeric", "0 or more than 1 value"));
                }else{
                    return Either.right(seqSafe.get().head());
                }
            }
        };
    }

    // On réutilise numericRangeV2 pour implémenter numeric
    public static Parser<Double> numericBabyStep() {
        return new Parser<Double>() {
            @Override
            public Either<ParserError, Double> parse(Workbook workbook, String name) {
                Either<ParserError, Seq<Double>> erreurOuListe = getArea(workbook, name).fold(
                        parseError -> left(parseError),
                        areaRef -> {
                            Either<ParserError, Seq<SafeCell>> seqSafe =
                                    Either.sequenceRight(List.of(areaRef.getAllReferencedCells())
                                            .map(cellRef -> getSafeCell(workbook, cellRef)));
                            return Either.sequenceRight(seqSafe.get().map(SafeCell::asDouble).toList());
                        });
                return erreurOuListe.flatMap(d -> Match(d.size()).of(
                        Case($(1), right(d.head())),
                        Case($(), left(new ParserError.InvalidFormat(name, "Single Numeric", "0 or more than 1 value")))
                ));
            }
        };
    }

    // On réutilise numericRangeV2 pour implémenter numeric
    public static Parser<Double> numeric() {
        return (workbook, name) ->
                numericRangeV2(workbook, name).flatMap(d -> Match(d.size()).of(
                        Case($(1), right(d.head())),
                        Case($(), left(new ParserError.InvalidFormat(name, "Single Numeric", "0 or more than 1 value")))
                ));
    }

    static <A, B> Parser<B> flatMap(Parser<A> fa, Function<A, Parser<B>> f) {
        return new Parser<B>() {
            @Override
            public Either<ParserError, B> parse(Workbook workbook, String name) {
                return fa.parse(workbook, name).flatMap(a -> f.apply(a).parse(workbook, name));
            }
        };
    }

    static <A, B> Parser<B> flatMap(Function<A, Parser<B>> f) {
        return new Parser<B>() {
            @Override
            public Either<ParserError, B> parse(Workbook workbook, String name) {

                Parser<A> pa = (wb, nm) -> (Either<ParserError, A>) parse(wb, nm);

                return pa.parse(workbook, name).flatMap(a -> Either.right((B)f.apply(a)));
            }
        };
    }

    static <A> Parser<A> success(A a) {
        return new Parser<A>() {
            @Override
            public Either<ParserError, A> parse(Workbook workbook, String name) {
                return right(a);
            }
        };
    }

    static <A> Parser<A> fail(Function<String, ParserError> error) {
        return new Parser<A>() {
            @Override
            public Either<ParserError, A> parse(Workbook workbook, String name) {
                return left(error.apply(name));
            }
        };
    }

    static <A> Parser<A> lift(Either<ParserError, A> res) {
        return new Parser<A>() {
            @Override
            public Either<ParserError, A> parse(Workbook workbook, String name) {
                return res;
            }
        };
    }
}
