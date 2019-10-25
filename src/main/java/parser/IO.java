package parser;

import static io.vavr.API.println;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class IO {

    public static Workbook load(String fileName) throws IOException, InvalidFormatException {
        println(String.format("Loading %s", fileName));

        return WorkbookFactory.create(new FileInputStream(new File(fileName)));
    }
}
