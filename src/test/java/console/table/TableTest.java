package console.table;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableTest {
    @Test
    public void isValid_SameRowLength_True() {
        List<List<String>> data = new ArrayList<>();
        data.add(List.of("Data 11", "Data 12", "Data 13"));
        data.add(List.of("Data 21", "Data 22", "Data 23"));
        data.add(List.of("Data 31", "Data 32", "Data 33"));
        Assert.assertTrue(createTable(data).isValid());
    }

    @Test
    public void isValid_DifferentRowLength_False() {
        List<List<String>> data = new ArrayList<>();
        data.add(List.of("Data 11", "Data 12", "Data 13"));
        data.add(List.of("Data 21", "Data 22"));
        data.add(List.of("Data 31", "Data 32", "Data 33"));
        Assert.assertFalse(createTable(data).isValid());
    }

    @Test
    public void fieldSize_HeaderLabelIsLongest_HeaderLabelLength() {
        List<List<String>> data = new ArrayList<>();
        data.add(List.of("Data 11", "Data 12", "Data 13"));
        data.add(List.of("Data 21", "Data 22", "Data 23"));
        data.add(List.of("Data 31", "Data 32", "Data 33"));
        Assert.assertEquals(createTable(data).fieldSize(1), 8);
    }

    @Test
    public void fieldSize_DataLabelIsLongest_DataLabelLength() {
        List<List<String>> data = new ArrayList<>();
        data.add(List.of("Data 11", "Data 12", "Data 13"));
        data.add(List.of("Data 21", "Some long data field", "Data 23"));
        data.add(List.of("Data 31", "Data 32", "Data 33"));
        Assert.assertEquals(createTable(data).fieldSize(1), 20);
    }

    @Test
    public void swapRows_ValidIndexes_TableWithSwappedRows() {
        List<List<String>> data = new ArrayList<>();
        data.add(List.of("Data 11", "Data 12", "Data 13"));
        data.add(List.of("Data 21", "Data 22", "Data 23"));
        data.add(List.of("Data 31", "Data 32", "Data 33"));

        Table<String> table = createTable(data);
        table.swapRows(0, 1);

        List<List<String>> expectedData = new ArrayList<>();
        expectedData.add(List.of("Data 21", "Data 22", "Data 23"));
        expectedData.add(List.of("Data 11", "Data 12", "Data 13"));
        expectedData.add(List.of("Data 31", "Data 32", "Data 33"));

        List<List<String>> actualData = table.getDataStream().collect(Collectors.toList());

        for (int i = 0; i < actualData.size(); i++) {
            for (int j = 0; j < actualData.get(i).size(); j++)
            Assert.assertEquals(expectedData.get(i).get(j), actualData.get(i).get(j));
        }
    }

    @Test
    public void swapRows_InvalidIndexes_TableWithoutChange() {
        List<List<String>> data = new ArrayList<>();
        data.add(List.of("Data 11", "Data 12", "Data 13"));
        data.add(List.of("Data 21", "Data 22", "Data 23"));
        data.add(List.of("Data 31", "Data 32", "Data 33"));

        Table<String> table = createTable(data);
        table.swapRows(3, 4);

        List<List<String>> expectedData = new ArrayList<>();
        expectedData.add(List.of("Data 11", "Data 12", "Data 13"));
        expectedData.add(List.of("Data 21", "Data 22", "Data 23"));
        expectedData.add(List.of("Data 31", "Data 32", "Data 33"));

        List<List<String>> actualData = table.getDataStream().collect(Collectors.toList());

        for (int i = 0; i < actualData.size(); i++) {
            for (int j = 0; j < actualData.get(i).size(); j++)
                Assert.assertEquals(expectedData.get(i).get(j), actualData.get(i).get(j));
        }
    }

    @Test
    public void insertEmptyRow_EmptyData_InsertedRow() {
        List<List<String>> data = new ArrayList<>();

        Table<String> table = createTable(data);
        table.insertEmptyRow(0);

        List<List<String>> expectedData = new ArrayList<>();
        expectedData.add(List.of("", "", ""));

        List<List<String>> actualData = table.getDataStream().collect(Collectors.toList());

        Assert.assertEquals(1, actualData.size());

        for (int i = 0; i < actualData.size(); i++) {
            for (int j = 0; j < actualData.get(i).size(); j++)
                Assert.assertEquals(expectedData.get(i).get(j), actualData.get(i).get(j));
        }
    }

    @Test
    public void insertEmptyRow_NonEmptyData_DataWithInsertedRowAtGivenIndex() {
        List<List<String>> data = new ArrayList<>();
        data.add(List.of("Data 11", "Data 12", "Data 13"));
        data.add(List.of("Data 21", "Data 22", "Data 23"));
        data.add(List.of("Data 31", "Data 32", "Data 33"));

        Table<String> table = createTable(data);
        table.insertEmptyRow(2);

        List<List<String>> expectedData = new ArrayList<>();
        expectedData.add(List.of("Data 11", "Data 12", "Data 13"));
        expectedData.add(List.of("Data 21", "Data 22", "Data 23"));

        expectedData.add(List.of("", "", ""));
        expectedData.add(List.of("Data 31", "Data 32", "Data 33"));

        List<List<String>> actualData = table.getDataStream().collect(Collectors.toList());

        Assert.assertEquals(4, actualData.size());

        for (int i = 0; i < actualData.size(); i++) {
            for (int j = 0; j < actualData.get(i).size(); j++)
                Assert.assertEquals(expectedData.get(i).get(j), actualData.get(i).get(j));
        }
    }

    @Test
    public void deleteRow_ValidIndex_TableWithoutDeletedRow() {
        List<List<String>> data = new ArrayList<>();
        data.add(List.of("Data 11", "Data 12", "Data 13"));
        data.add(List.of("Data 21", "Data 22", "Data 23"));
        data.add(List.of("Data 31", "Data 32", "Data 33"));

        Table<String> table = createTable(data);
        table.deleteRow(1);

        List<List<String>> expectedData = new ArrayList<>();
        expectedData.add(List.of("Data 11", "Data 12", "Data 13"));
        expectedData.add(List.of("Data 31", "Data 32", "Data 33"));

        List<List<String>> actualData = table.getDataStream().collect(Collectors.toList());

        Assert.assertEquals(2, actualData.size());

        for (int i = 0; i < actualData.size(); i++) {
            for (int j = 0; j < actualData.get(i).size(); j++)
                Assert.assertEquals(expectedData.get(i).get(j), actualData.get(i).get(j));
        }
    }

    @Test
    public void deleteRow_InvalidIndex_TableWithoutChange() {
        List<List<String>> data = new ArrayList<>();
        data.add(List.of("Data 11", "Data 12", "Data 13"));
        data.add(List.of("Data 21", "Data 22", "Data 23"));
        data.add(List.of("Data 31", "Data 32", "Data 33"));

        Table<String> table = createTable(data);
        table.deleteRow(3);

        List<List<String>> expectedData = new ArrayList<>();
        expectedData.add(List.of("Data 11", "Data 12", "Data 13"));
        expectedData.add(List.of("Data 21", "Data 22", "Data 23"));
        expectedData.add(List.of("Data 31", "Data 32", "Data 33"));

        List<List<String>> actualData = table.getDataStream().collect(Collectors.toList());

        Assert.assertEquals(3, actualData.size());

        for (int i = 0; i < actualData.size(); i++) {
            for (int j = 0; j < actualData.get(i).size(); j++)
                Assert.assertEquals(expectedData.get(i).get(j), actualData.get(i).get(j));
        }
    }

    @Test
    public void deleteCol_ValidIndex_TableWithoutDeletedColumns() {
        List<List<String>> data = new ArrayList<>();
        data.add(List.of("Data 11", "Data 12", "Data 13"));
        data.add(List.of("Data 21", "Data 22", "Data 23"));
        data.add(List.of("Data 31", "Data 32", "Data 33"));

        Table<String> table = createTable(data);
        table.deleteCol(0);

        List<List<String>> expectedData = new ArrayList<>();
        expectedData.add(List.of("Data 12", "Data 13"));
        expectedData.add(List.of("Data 22", "Data 23"));
        expectedData.add(List.of("Data 32", "Data 33"));

        List<String> expectedHeader = List.of("Column 2", "Column 3");

        List<List<String>> actualData = table.getDataStream().collect(Collectors.toList());

        Assert.assertEquals(2, actualData.get(0).size());
        Assert.assertEquals(2, table.getHeader().size());

        for (int i = 0; i < table.getHeader().size(); i++) {
            Assert.assertEquals(expectedHeader.get(i), table.getHeader().get(i));
        }

        for (int i = 0; i < actualData.size(); i++) {
            for (int j = 0; j < actualData.get(i).size(); j++)
                Assert.assertEquals(expectedData.get(i).get(j), actualData.get(i).get(j));
        }
    }

    @Test
    public void deleteCol_InvalidIndex_TableWithoutChange() {
        List<List<String>> data = new ArrayList<>();
        data.add(List.of("Data 11", "Data 12", "Data 13"));
        data.add(List.of("Data 21", "Data 22", "Data 23"));
        data.add(List.of("Data 31", "Data 32", "Data 33"));

        Table<String> table = createTable(data);
        table.deleteCol(8);

        List<List<String>> expectedData = new ArrayList<>();
        expectedData.add(List.of("Data 11", "Data 12", "Data 13"));
        expectedData.add(List.of("Data 21", "Data 22", "Data 23"));
        expectedData.add(List.of("Data 31", "Data 32", "Data 33"));

        List<String> expectedHeader = List.of("Column 1", "Column 2", "Column 3");

        List<List<String>> actualData = table.getDataStream().collect(Collectors.toList());

        Assert.assertEquals(3, actualData.size());
        Assert.assertEquals(3, table.getHeader().size());

        for (int i = 0; i < table.getHeader().size(); i++) {
            Assert.assertEquals(expectedHeader.get(i), table.getHeader().get(i));
        }

        for (int i = 0; i < actualData.size(); i++) {
            for (int j = 0; j < actualData.get(i).size(); j++)
                Assert.assertEquals(expectedData.get(i).get(j), actualData.get(i).get(j));
        }
    }

    @Test
    public void updateData_NewData_TableWithUpdatedData() {
        List<List<String>> data = new ArrayList<>();
        data.add(List.of("Data 11", "Data 12", "Data 13"));
        data.add(List.of("Data 21", "Data 22", "Data 23"));
        data.add(List.of("Data 31", "Data 32", "Data 33"));

        List<List<String>> newData = new ArrayList<>();
        newData.add(List.of("", "Data 12", "Data 13"));
        newData.add(List.of("Data 21", "", "Data 23"));
        newData.add(List.of("Data 31", "Data 32", ""));

        Table<String> table = createTable(data);
        table.updateData(newData);

        List<List<String>> actualData = table.getDataStream().collect(Collectors.toList());

        Assert.assertEquals(3, actualData.size());

        for (int i = 0; i < actualData.size(); i++) {
            for (int j = 0; j < actualData.get(i).size(); j++)
                Assert.assertEquals(newData.get(i).get(j), actualData.get(i).get(j));
        }
    }

    private Table<String> createTable(List<List<String>> data) {
        List<String> header = new ArrayList<>();
        header.add("Column 1");
        header.add("Column 2");
        header.add("Column 3");

        return new Table<>(header, data, x -> x, () -> "", false);
    }
}
