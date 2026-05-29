package utils;
import org.testng.annotations.DataProvider;

public class TestDataExtract {

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {

        Excelreader.loadExcel("LoginData");

        int rows = Excelreader.getRowCount();

        Object[][] data =
                new Object[rows - 1][3];

        for (int i = 1; i < rows; i++) {

            data[i - 1][0] =
                    Excelreader.getCellData(i, 0);

            data[i - 1][1] =
                    Excelreader.getCellData(i, 1);

            data[i - 1][2] =
                    Excelreader.getCellData(i, 2);
        }

        return data;
    }
    @DataProvider(name = "noteData")
public Object[][] getNoteData() {

    Excelreader.loadExcel("NoteData");

    int rows = Excelreader.getRowCount();

    Object[][] data = new Object[rows - 1][4];

    for (int i = 1; i < rows; i++) {
        data[i - 1][0] = Excelreader.getCellData(i, 0); // category
        data[i - 1][1] = Excelreader.getCellData(i, 1); // title
        data[i - 1][2] = Excelreader.getCellData(i, 2); // description
        data[i - 1][3] = Excelreader.getCellData(i, 3); // expected
    }

    return data;
}
}