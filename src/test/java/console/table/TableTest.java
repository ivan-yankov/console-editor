package console.table;

import console.factory.TableFactory;
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

        List<List<Cell<String>>> expectedData = new ArrayList<>();
        expectedData.add(asCellList(List.of("Data 21", "Data 22", "Data 23")));
        expectedData.add(asCellList(List.of("Data 11", "Data 12", "Data 13")));
        expectedData.add(asCellList(List.of("Data 31", "Data 32", "Data 33")));

        List<List<Cell<String>>> actualData = table.getData();

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

        List<List<Cell<String>>> expectedData = new ArrayList<>();
        expectedData.add(asCellList(List.of("Data 11", "Data 12", "Data 13")));
        expectedData.add(asCellList(List.of("Data 21", "Data 22", "Data 23")));
        expectedData.add(asCellList(List.of("Data 31", "Data 32", "Data 33")));

        List<List<Cell<String>>> actualData = table.getData();

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

        List<List<Cell<String>>> expectedData = new ArrayList<>();
        expectedData.add(asCellList(List.of("", "", "")));

        List<List<Cell<String>>> actualData = table.getData();

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

        List<List<Cell<String>>> expectedData = new ArrayList<>();
        expectedData.add(asCellList(List.of("Data 11", "Data 12", "Data 13")));
        expectedData.add(asCellList(List.of("Data 21", "Data 22", "Data 23")));

        expectedData.add(asCellList(List.of("", "", "")));
        expectedData.add(asCellList(List.of("Data 31", "Data 32", "Data 33")));

        List<List<Cell<String>>> actualData = table.getData();

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

        List<List<Cell<String>>> expectedData = new ArrayList<>();
        expectedData.add(asCellList(List.of("Data 11", "Data 12", "Data 13")));
        expectedData.add(asCellList(List.of("Data 31", "Data 32", "Data 33")));

        List<List<Cell<String>>> actualData = table.getData();

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

        List<List<Cell<String>>> expectedData = new ArrayList<>();
        expectedData.add(asCellList(List.of("Data 11", "Data 12", "Data 13")));
        expectedData.add(asCellList(List.of("Data 21", "Data 22", "Data 23")));
        expectedData.add(asCellList(List.of("Data 31", "Data 32", "Data 33")));

        List<List<Cell<String>>> actualData = table.getData();

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

        List<List<Cell<String>>> expectedData = new ArrayList<>();
        expectedData.add(asCellList(List.of("Data 12", "Data 13")));
        expectedData.add(asCellList(List.of("Data 22", "Data 23")));
        expectedData.add(asCellList(List.of("Data 32", "Data 33")));

        List<Cell<String>> expectedHeader = asCellList(List.of("Column 2", "Column 3"));

        List<List<Cell<String>>> actualData = table.getData();

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

        List<List<Cell<String>>> expectedData = new ArrayList<>();
        expectedData.add(asCellList(List.of("Data 11", "Data 12", "Data 13")));
        expectedData.add(asCellList(List.of("Data 21", "Data 22", "Data 23")));
        expectedData.add(asCellList(List.of("Data 31", "Data 32", "Data 33")));

        List<Cell<String>> expectedHeader = asCellList(List.of("Column 1", "Column 2", "Column 3"));

        List<List<Cell<String>>> actualData = table.getData();

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

        List<List<Cell<String>>> newData = new ArrayList<>();
        newData.add(asCellList(List.of("", "Data 12", "Data 13")));
        newData.add(asCellList(List.of("Data 21", "", "Data 23")));
        newData.add(asCellList(List.of("Data 31", "Data 32", "")));

        Table<String> table = createTable(data);
        table.updateData(newData);

        List<List<Cell<String>>> actualData = table.getData();

        Assert.assertEquals(3, actualData.size());

        for (int i = 0; i < actualData.size(); i++) {
            for (int j = 0; j < actualData.get(i).size(); j++)
                Assert.assertEquals(newData.get(i).get(j), actualData.get(i).get(j));
        }
    }

    private Table<String> createTable(List<List<String>> data) {
        List<Cell<String>> header = asCellList(List.of("Column 1", "Column 2", "Column 3"));
        List<List<Cell<String>>> cellData = data.stream().map(this::asCellList).collect(Collectors.toList());

        return new Table<>(header, cellData, TableFactory.emptyStringCell());
    }

    private List<Cell<String>> asCellList(List<String> data) {
        return data.stream().map(x -> new Cell<>(x, false, y -> y)).collect(Collectors.toList());
    }
}
