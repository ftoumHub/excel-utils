package parser;

public class ParserErrorClass {

    private String ref;
    private String expectedFormat;
    private String message;

    public ParserErrorClass(String ref, String expectedFormat, String message) {
        this.ref = ref;
        this.expectedFormat = expectedFormat;
        this.message = message;
    }

    public String getRef() {
        return ref;
    }

    public String getExpectedFormat() {
        return expectedFormat;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
