package utils;
import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.io.IOException;

public class Excelreader {
    private static Workbook workbook;
    private static Sheet sheet;
    public static void loadExcel(String sheetName) {
        try {

            FileInputStream file = new FileInputStream(
                    "src/test/resources/testdata/LoginTestData.xlsx"
            );

            workbook = WorkbookFactory.create(file);

            sheet = workbook.getSheet(sheetName);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static String getCellData(int row, int column) {

    Cell cell = sheet.getRow(row).getCell(column);

    if (cell == null) {
        return "";
    }

    switch (cell.getCellType()) {

        case STRING:
            return cell.getStringCellValue();

        case NUMERIC:
            return String.valueOf(
                    (long) cell.getNumericCellValue()
            );

        case BOOLEAN:
            return String.valueOf(
                    cell.getBooleanCellValue()
            );

        default:
            return "";
    }
}
    public static int getRowCount() {
        return sheet.getPhysicalNumberOfRows();
    }
}