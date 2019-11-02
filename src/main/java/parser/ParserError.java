package parser;

public interface ParserError {

    class InvalidFormat implements ParserError {

        private String ref;
        private String expectedFormat;
        private String message;

        public InvalidFormat(String ref, String expectedFormat, String message) {
            this.ref = ref;
            this.expectedFormat = expectedFormat;
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

        @Override
        public String toString() {
            return "InvalidFormat(" + ref + ", " + expectedFormat + ", " + message + ')';
        }
    }

    class MissingName implements ParserError {

        private String name;

        public MissingName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "MissingName(" + name + ")";
        }
    }

    class MissingCell implements ParserError {

        private String ref;

        public MissingCell(String ref) {
            this.ref = ref;
        }

        @Override
        public String toString() {
            return this.ref;
        }
    }
}
