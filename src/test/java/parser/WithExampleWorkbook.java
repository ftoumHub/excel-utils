package parser;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

public class WithExampleWorkbook {

    static Workbook workbook;

    @BeforeAll
    public static void setup() throws IOException, InvalidFormatException {
        workbook = IO.load("example.xlsx");
    }
}
