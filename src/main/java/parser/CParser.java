package parser;

import java.util.function.Function;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.apache.poi.ss.usermodel.Workbook;

import io.vavr.control.Either;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.For;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static io.vavr.Patterns.$Tuple2;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

public abstract class CParser<A>  {

    private final CParser<A> _self = this;

    public abstract Either<ParserError, A> parse(Workbook workbook);

    public <B> CParser<B> flatMap(Function<A, CParser<B>> f) {
        return new CParser<B>() {
            @Override
            public Either<ParserError, B> parse(Workbook workbook) {
                return _self.parse(workbook).flatMap(a -> f.apply(a).parse(workbook));
            }
        };
    }

    public <A, B> CParser<B> flatMapF(Function<A, Either<ParserError, B>> f) {
        return flatMap(a -> lift(f.apply((A)a)));
    }

    static <A, B> CParser<B> flatMap(CParser<A> fa, Function<A, CParser<B>> f) {
        return new CParser<B>() {
            @Override
            public Either<ParserError, B> parse(Workbook workbook) {
                return fa.parse(workbook).flatMap(
                        a -> f.apply(a).parse(workbook));
            }
        };
    }

    static <A> CParser<A> success(A a) {
        return new CParser<A>() {
            @Override
            public Either<ParserError, A> parse(Workbook workbook) {
                return right(a);
            }
        };
    }

    static <A> CParser<A> fail(ParserError error) {
        return new CParser<A>() {
            @Override
            public Either<ParserError, A> parse(Workbook workbook) {
                return left(error);
            }
        };
    }

    static <A> CParser<A> lift(Either<ParserError, A> res) {
        return new CParser<A>() {
            @Override
            public Either<ParserError, A> parse(Workbook workbook) {
                return res;
            }
        };
    }

    public static <A, B> CParser<Tuple2<A, B>> product(CParser<A> pa, CParser<B> pb) {
        return new CParser<Tuple2<A, B>>() {
            @Override
            public Either<ParserError, Tuple2<A, B>> parse(Workbook workbook) {

                final Tuple2<Either<ParserError, A>, Either<ParserError, B>> t = Tuple.of(pa.parse(workbook), pb.parse(workbook));

                return Match(t).of(
                        Case($Tuple2($Left($()), $Right($())), () -> left(t._1.getLeft())),
                        Case($Tuple2($Right($()), $Left($())), () -> left(t._2.getLeft())),
                        Case($Tuple2($Right($()), $Right($())), () -> right(Tuple.of(t._1.get(), t._2.get()))));
            }
        };
    }

    public static <A, B> CParser<B> map(CParser<A> fa, Function<A, B> f) {
        return flatMap(fa, a -> success(f.apply(a)));
    }
}
