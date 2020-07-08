package parser;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;

import java.util.function.Function;

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

    public static Either<ParserError, Seq<Double>> numericRangeV2(Workbook workbook, String name) {
        return getArea(workbook, name).fold(
                parseError -> left(parseError),
                areaRef -> {
                    Either<ParserError, Seq<SafeCell>> seqSafe =
                            sequenceRight(List.of(areaRef.getAllReferencedCells())
                                    .map(cellRef -> getSafeCell(workbook, cellRef)));
                    return sequenceRight(seqSafe.get().map(SafeCell::asDouble).toList());
                });
    }

    // version finale de numericRange
    public static Either<ParserError, Seq<Double>> numericRange(final Workbook workbook, final String name) {
        return getArea(workbook, name)
                .flatMap(area -> sequenceRight(List.of(area.getAllReferencedCells()).map(cell -> getSafeCell(workbook, cell))))
                .flatMap(safeCells -> sequenceRight(safeCells.map(SafeCell::asDouble).toList()));
    }

    // On réutilise numericRange pour implémenter numeric
    public static Parser<Double> numericV0() {
        return (workbook, name) ->
                numericRange(workbook, name).flatMap(d -> Match(d.size()).of(
                        Case($(1), right(d.head())),
                        Case($(), left(new ParserError.InvalidFormat(name, "Single Numeric", "0 or more than 1 value")))
                ));
    }

    /**
     * Si on veut réimplémenter numericRange à partir de 0 on voit qu'on peut réutiliser numericRangeV2
     */
    private static Parser<Double> numericFromScratch() {
        return (workbook, name) -> {
            Either<ParserError, AreaReference> area = getArea(workbook, name);

            if (area.isLeft()) return Either.left(area.getLeft());

            Either<ParserError, Seq<Double>> seqSafe =
                    sequenceRight(List.of(area.get().getAllReferencedCells())
                            .map(cellRef -> getSafeCell(workbook, cellRef)).toList()
                            .map(sf -> sf.get().asDouble()).toList());

            if (seqSafe.get().size() !=1) {
                return Either.left(new ParserError.InvalidFormat(name, "Single Numeric", "0 or more than 1 value"));
            }else{
                return Either.right(seqSafe.get().head());
            }
        };
    }


    /**
     * Exemple d'utilisation de flatMap sur Either
     */
    public static Either<String, Integer> properFlatMapOfEither(String age) {
        return Option
                .of(age)
                .map(Either::<String, String>right)
                .getOrElse(Either.left("empty"))
                .flatMap(ParserUtils::eitherWrapper);
    }

    private static Either<String, Integer> eitherWrapper(String value) {
        return Try
                .of(() -> value)
                .map(v -> Either.<String, Integer>right(Integer.valueOf(v)))
                .getOrElse(() -> Either.left(value));
    }

    private static <A, B> Parser<B> flatMap(Function<A, Parser<B>> f) {
        return new Parser<B>() {
            @Override
            public Either<ParserError, B> parse(Workbook workbook, String name) {

                Parser<A> pa = (wb, nm) -> (Either<ParserError, A>) parse(wb, nm);

                return pa.parse(workbook, name).flatMap(a -> Either.right((B)f.apply(a)));
            }
        };
    }

    public static Parser<Seq<Double>> numericRange() {
        return (workbook, name) ->
                getArea(workbook, name)
                        .flatMap(area -> sequenceRight(List.of(area.getAllReferencedCells()).map(cell -> getSafeCell(workbook, cell))))
                        .flatMap(safeCells -> sequenceRight(safeCells.map(SafeCell::asDouble).toList()));
    }

    public static Parser<Double> numeric() {
        return Parser.flatMap(numericRange(), d -> Match(d.size()).of(
                Case($(1), success(d.head())),
                Case($(), fail(name -> new ParserError.InvalidFormat(name, "Single Numeric", "0 or more than 1 value")))
        ));
    }

    // Other building blocks
    // intRange: Parser<Seq<Integer>>
    // int: Parser<Integer>

    // stringRange: Parser<Seq<String>>
    // string: Parser<String>
}
