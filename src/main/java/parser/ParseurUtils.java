package parser;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.For;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static io.vavr.Patterns.$Tuple2;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

import java.util.function.Function;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Try;

public class ParseurUtils {

    public static Parseur<Seq<Double>> numericRange(String name) {

        Function<Seq<SafeCell>, Either<ParserError, Seq<Double>>> _ =
                safeCells -> Either.sequenceRight(safeCells.map(sf -> sf.asDouble()).toList());

        return range(name).flatMapF(safeCells -> _.apply((Seq<SafeCell>)safeCells));
    }

    static Parseur<Double> numeric(String name) {
        return flatMap(numericRange(name), d -> Match(d.asJava().size()).of(
                Case($(1), success(d.head())),
                Case($(), fail(new ParserError.InvalidFormat(name, "Single Numeric", "0 or more than 1 value")))
        ));
        /**return numericRange(name).flatMap(d -> Match(d.asJava().size()).of(
                Case($(1), success(d.head())),
                Case($(), fail(new ParserError.InvalidFormat(name, "Single Numeric", "0 or more than 1 value")))
        ));*/
    }

    static Parseur<Seq<SafeCell>> range(String name) {
        return new Parseur<Seq<SafeCell>>() {
            @Override
            public Either<ParserError, Seq<SafeCell>> parse(Workbook workbook) {
                return getArea(workbook, name)
                        .fold(
                            error -> left(error),
                            areaRef -> Either.sequenceRight(List.of(areaRef.getAllReferencedCells())
                                                .map(cellRef -> getSafeCell(workbook, cellRef)).toList()));
            }
        };
    }

    static Either<ParserError, AreaReference> getArea(Workbook workbook, String name) {
        return Try.of(() -> new AreaReference(workbook.getName(name).getRefersToFormula(),
                workbook.getSpreadsheetVersion()))
                .map(areaRef -> Either.<ParserError, AreaReference>right(areaRef))
                .getOrElse(() -> left(new ParserError.MissingName(name)));
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

    static <A, B> Parseur<B> flatMap(Parseur<A> fa, Function<A, Parseur<B>> f) {
        return new Parseur<B>() {
            @Override
            public Either<ParserError, B> parse(Workbook workbook) {
                return fa.parse(workbook).flatMap(
                        a -> f.apply(a).parse(workbook));
            }
        };
    }

    static <A> Parseur<A> success(A a) {
        return new Parseur<A>() {
            @Override
            public Either<ParserError, A> parse(Workbook workbook) {
                return right(a);
            }
        };
    }

    static <A> Parseur<A> fail(ParserError error) {
        return new Parseur<A>() {
            @Override
            public Either<ParserError, A> parse(Workbook workbook) {
                return left(error);
            }
        };
    }

    static <A> Parseur<A> lift(Either<ParserError, A> res) {
        return new Parseur<A>() {
            @Override
            public Either<ParserError, A> parse(Workbook workbook) {
                return res;
            }
        };
    }

    static Tuple product_ko(Workbook workbook) {

        Iterator<Tuple2<Seq<Double>, Seq<Double>>> t = For(
                numericRange("OilProd").parse(workbook),
                numericRange("GasProd").parse(workbook)
        ).yield(
                (oilParseur, gasParseur) -> Tuple.of(oilParseur, gasParseur)
        );

        return t.get();
    }

    public static <A, B> Parseur<Tuple2> product(Parseur<A> pa, Parseur<B> pb) {
        return new Parseur<Tuple2>() {

            @Override public Either<ParserError, Tuple2> parse(Workbook workbook) {

                Either<ParserError, A> firstParseur = pa.parse(workbook);
                Either<ParserError, B> secondParseur = pb.parse(workbook);

                return Match(new Tuple2<>(firstParseur, secondParseur)).of(
                        Case($Tuple2($Left($()), $Right($())), () -> Either.left(firstParseur.getLeft())),
                        Case($Tuple2($Right($()), $Left($())), () -> Either.left(secondParseur.getLeft())),
                        Case($Tuple2($Right($()), $Right($())), () -> Either.right(Tuple.of(firstParseur.get(), secondParseur.get()))));
            }
        };
    }

    public static Parseur<Production> production() {

        final Parseur<Tuple2> product = product(numericRange("OilProd"), numericRange("GasProd"));

        return new Parseur<Production>() {
            @Override
            public Either<ParserError, Production> parse(Workbook workbook) {
                Either<ParserError, Tuple2> parse = product.parse(workbook);

                if (parse.isLeft()) return left(parse.getLeft());

                java.util.List<Double> oilProd = ((io.vavr.collection.Vector<Double>) parse.get()._1).asJava();
                java.util.List<Double> gasProd = ((io.vavr.collection.Vector<Double>) parse.get()._2).asJava();

                return right(new Production(oilProd, gasProd));
            }
        };
    }

}
