package parser;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WithExampleWorkbook {

    static Workbook workbook;

    @BeforeAll
    public static void setup() throws IOException, InvalidFormatException {
        ClassLoader classLoader = WithExampleWorkbook.class.getClassLoader();
        File file = new File(classLoader.getResource("test-exemple.xlsx").getFile());
        workbook = WorkbookFactory.create(file);
    }

    @Test
    public void testReadFileWithClassLoader(){
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File(classLoader.getResource("test-exemple.xlsx").getFile());
        assertTrue(file.exists());
    }
}
