package parser;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import org.apache.poi.ss.usermodel.Workbook;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static io.vavr.control.Either.sequenceRight;
import static libs.ExcelUtils.getArea;
import static libs.ExcelUtils.getSafeCell;
import static parser.Parser.fail;
import static parser.Parser.success;

public class ParserUtils {

    private ParserUtils() {}

    /**
     * Implémentation finale de numericRange en utilisant le flatMap de Either.
     *
     * @param workbook
     * @param name
     * @return
     */
    public static Either<ParserError, Seq<Double>> numericRange(final Workbook workbook, final String name) {
        return getArea(workbook, name)
                .flatMap(area -> sequenceRight(List.of(area.getAllReferencedCells()).map(cell -> getSafeCell(workbook, cell))))
                .flatMap(safeCells -> sequenceRight(safeCells.map(SafeCell::asDouble).toList()));
    }

    // Other building blocks
    // intRange: Parser<Seq<Integer>>
    // int: Parser<Integer>
    public static Either<ParserError, Seq<Integer>> intRange(final Workbook workbook, final String name) {
        return getArea(workbook, name)
                .flatMap(area -> sequenceRight(List.of(area.getAllReferencedCells()).map(cell -> getSafeCell(workbook, cell))))
                .flatMap(safeCells -> sequenceRight(safeCells.map(SafeCell::asInteger).toList()));
    }

    // stringRange: Parser<Seq<String>>
    // string: Parser<String>
    public static Either<ParserError, Seq<String>> stringRange(final Workbook workbook, final String name) {
        return getArea(workbook, name)
                .flatMap(area -> sequenceRight(List.of(area.getAllReferencedCells()).map(cell -> getSafeCell(workbook, cell))))
                .flatMap(safeCells -> sequenceRight(safeCells.map(SafeCell::asString).toList()));
    }

    public static Parser<Seq<Double>> numericRange() {
        return (workbook, name) -> numericRange(workbook, name);
    }

    /**
     * On réutilise numericRange pour implémenter numeric, en faisant simplement un Patter Matching sur la taille
     * de la liste.
     */
    public static Parser<Double> numericV0() {
        return (workbook, name) ->
                numericRange(workbook, name).flatMap(d -> Match(d.size()).of(
                        Case($(1), right(d.head())),
                        Case($(), left(new ParserError.InvalidFormat(name, "Single Numeric", "0 or more than 1 value")))
                ));
    }

    public static Parser<Double> numeric() {
        return Parser.flatMap(numericRange(), d -> Match(d.size()).of(
                Case($(1), success(d.head())),
                Case($(), fail(name -> new ParserError.InvalidFormat(name, "Single Numeric", "0 or more than 1 value")))
        ));
    }


}
