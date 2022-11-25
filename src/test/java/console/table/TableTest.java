package console.table;

import console.factory.CellFactory;
import console.factory.TableFactory;
import org.junit.Assert;
import org.junit.Test;
import yankov.jutils.functional.ImmutableList;

public class TableTest {
    @Test
    public void from_SameRowLength_CreateTable() {
        Assert.assertTrue(
                Table.from(
                        asCellList(ImmutableList.fill(3, "")),
                        ImmutableList.from(
                                asCellList(ImmutableList.from("Data 11", "Data 12", "Data 13")),
                                asCellList(ImmutableList.from("Data 21", "Data 22", "Data 23")),
                                asCellList(ImmutableList.from("Data 31", "Data 32", "Data 33"))
                        ),
                        CellFactory::createEmptyStringCell
                ).getRight().isPresent()
        );
    }

    @Test
    public void from_DifferentRowLength_Error() {
        Assert.assertTrue(
                Table.from(
                        asCellList(ImmutableList.fill(3, "")),
                        ImmutableList.from(
                                asCellList(ImmutableList.from("Data 11", "Data 12", "Data 13")),
                                asCellList(ImmutableList.from("Data 21", "Data 22")),
                                asCellList(ImmutableList.from("Data 31", "Data 32", "Data 33"))
                        ),
                        CellFactory::createEmptyStringCell
                ).getLeft().isPresent()
        );
    }

    @Test
    public void fieldSize_HeaderLabelIsLongest_HeaderLabelLength() {
        ImmutableList<ImmutableList<String>> data = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 21", "Data 22", "Data 23"),
                ImmutableList.from("Data 31", "Data 32", "Data 33")
        );
        Assert.assertEquals(createTable(data).fieldSize(1), 8);
    }

    @Test
    public void fieldSize_DataLabelIsLongest_DataLabelLength() {
        ImmutableList<ImmutableList<String>> data = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 21", "Some long data field", "Data 23"),
                ImmutableList.from("Data 31", "Data 32", "Data 33")
        );
        Assert.assertEquals(createTable(data).fieldSize(1), 20);
    }

    @Test
    public void swapRows_ValidIndexes_TableWithSwappedRows() {
        ImmutableList<ImmutableList<String>> data = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 21", "Data 22", "Data 23"),
                ImmutableList.from("Data 31", "Data 32", "Data 33")
        );

        Table<String> table = createTable(data).swapRows(0, 1);

        ImmutableList<ImmutableList<String>> expectedData = ImmutableList.from(
                ImmutableList.from("Data 21", "Data 22", "Data 23"),
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 31", "Data 32", "Data 33")
        );

        assertData(table, expectedData);
    }

    @Test
    public void swapRows_InvalidIndexes_TableWithoutChange() {
        ImmutableList<ImmutableList<String>> data = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 21", "Data 22", "Data 23"),
                ImmutableList.from("Data 31", "Data 32", "Data 33")
        );

        Table<String> table = createTable(data).swapRows(3, 4);

        ImmutableList<ImmutableList<String>> expectedData = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 21", "Data 22", "Data 23"),
                ImmutableList.from("Data 31", "Data 32", "Data 33")
        );

        assertData(table, expectedData);
    }

    @Test
    public void insertEmptyRow_EmptyData_InsertedRow() {
        Table<String> table = createTable(ImmutableList.from(), 3).insertEmptyRow(0);

        ImmutableList<ImmutableList<String>> expectedData = ImmutableList.from(
                ImmutableList.from("", "", "")
        );

        assertData(table, expectedData);
    }

    @Test
    public void insertEmptyRow_EmptyTable_InsertedRow() {
        Table<String> table = createTable(ImmutableList.from(), 0).insertEmptyRow(0);

        ImmutableList<ImmutableList<String>> expectedData = ImmutableList.from(
                ImmutableList.from("")
        );

        assertData(table, expectedData);
    }

    @Test
    public void insertEmptyRow_NonEmptyData_DataWithInsertedRowAtGivenIndex() {
        ImmutableList<ImmutableList<String>> data = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 21", "Data 22", "Data 23"),
                ImmutableList.from("Data 31", "Data 32", "Data 33"),
                ImmutableList.from("Data 41", "Data 42", "Data 43")
        );

        Table<String> table = createTable(data).insertEmptyRow(2);

        ImmutableList<ImmutableList<String>> expectedData = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 21", "Data 22", "Data 23"),
                ImmutableList.from("", "", ""),
                ImmutableList.from("Data 31", "Data 32", "Data 33"),
                ImmutableList.from("Data 41", "Data 42", "Data 43")
        );

        assertData(table, expectedData);
    }

    @Test
    public void insertEmptyCol_EmptyData_TableWithEmptyColumn() {
        Table<String> table = createTable(ImmutableList.from(), 0).insertEmptyColumn(0);
        Assert.assertEquals(1, table.getColCount());
        Assert.assertEquals(0, table.getRowCount());
    }

    @Test
    public void insertEmptyCol_NonEmptyData_DataWithInsertedColAtGivenIndex() {
        ImmutableList<ImmutableList<String>> data = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13", "Data 14"),
                ImmutableList.from("Data 21", "Data 22", "Data 23", "Data 24"),
                ImmutableList.from("Data 31", "Data 32", "Data 33", "Data 34")
        );

        Table<String> table = createTable(data).insertEmptyColumn(2);

        ImmutableList<ImmutableList<String>> expectedData = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "", "Data 13", "Data 14"),
                ImmutableList.from("Data 21", "Data 22", "", "Data 23", "Data 24"),
                ImmutableList.from("Data 31", "Data 32", "", "Data 33", "Data 34")
        );

        Assert.assertTrue(
                ImmutableList.from("Column 1", "Column 2", "", "Column 3", "Column 4")
                        .eq(table.getHeader().stream().map(Cell::getValue).toList())
        );

        assertData(table, expectedData);
    }

    @Test
    public void deleteRow_ValidIndex_TableWithoutDeletedRow() {
        ImmutableList<ImmutableList<String>> data = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 21", "Data 22", "Data 23"),
                ImmutableList.from("Data 31", "Data 32", "Data 33")
        );

        Table<String> table = createTable(data).deleteRow(1);

        ImmutableList<ImmutableList<String>> expectedData = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 31", "Data 32", "Data 33")
        );

        assertData(table, expectedData);
    }

    @Test
    public void deleteRow_InvalidIndex_TableWithoutChange() {
        ImmutableList<ImmutableList<String>> data = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 21", "Data 22", "Data 23"),
                ImmutableList.from("Data 31", "Data 32", "Data 33")
        );

        Table<String> table = createTable(data).deleteRow(3);

        ImmutableList<ImmutableList<String>> expectedData = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 21", "Data 22", "Data 23"),
                ImmutableList.from("Data 31", "Data 32", "Data 33")
        );

        assertData(table, expectedData);
    }

    @Test
    public void deleteCol_ValidIndex_TableWithoutDeletedColumns() {
        ImmutableList<ImmutableList<String>> data = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 21", "Data 22", "Data 23"),
                ImmutableList.from("Data 31", "Data 32", "Data 33")
        );

        Table<String> table = createTable(data).deleteCol(1);

        ImmutableList<ImmutableList<String>> expectedData = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 13"),
                ImmutableList.from("Data 21", "Data 23"),
                ImmutableList.from("Data 31", "Data 33")
        );

        Assert.assertTrue(
                ImmutableList.from("Column 1", "Column 3")
                        .eq(table.getHeader().stream().map(Cell::getValue).toList())
        );

        assertData(table, expectedData);
    }

    @Test
    public void deleteCol_InvalidIndex_TableWithoutChange() {
        ImmutableList<ImmutableList<String>> data = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 21", "Data 22", "Data 23"),
                ImmutableList.from("Data 31", "Data 32", "Data 33")
        );

        Table<String> table = createTable(data).deleteCol(8);

        ImmutableList<ImmutableList<String>> expectedData = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 21", "Data 22", "Data 23"),
                ImmutableList.from("Data 31", "Data 32", "Data 33")
        );

        Assert.assertTrue(
                ImmutableList.from("Column 1", "Column 2", "Column 3")
                        .eq(table.getHeader().stream().map(Cell::getValue).toList())
        );

        assertData(table, expectedData);
    }

    @Test
    public void withData_NewData_TableWithUpdatedData() {
        ImmutableList<ImmutableList<String>> data = ImmutableList.from(
                ImmutableList.from("Data 11", "Data 12", "Data 13"),
                ImmutableList.from("Data 21", "Data 22", "Data 23"),
                ImmutableList.from("Data 31", "Data 32", "Data 33")
        );

        ImmutableList<ImmutableList<String>> newData = ImmutableList.from(
                ImmutableList.from("", "Data 12", "Data 13"),
                ImmutableList.from("Data 21", "", "Data 23"),
                ImmutableList.from("Data 31", "Data 32", "")
        );

        Table<String> table = createTable(data)
                .withData(newData.stream().map(this::asCellList).toList())
                .getRight()
                .orElse(Table.empty(CellFactory::createEmptyStringCell));

        assertData(table, newData);
    }

    private Table<String> createTable(ImmutableList<ImmutableList<String>> data, int headerSize) {
        ImmutableList<Cell<String>> header = asCellList(
                ImmutableList.fill(headerSize, "Column ")
                        .zipWithIndex()
                        .stream()
                        .map(x -> x._1() + (x._2() + 1))
                        .toList()
        );
        ImmutableList<ImmutableList<Cell<String>>> cellData = data.stream().map(this::asCellList).toList();

        return TableFactory.createStringTable(header, cellData);
    }

    private Table<String> createTable(ImmutableList<ImmutableList<String>> data) {
        return createTable(data, data.get(0).size());
    }

    private ImmutableList<Cell<String>> asCellList(ImmutableList<String> data) {
        return data.stream().map(x -> new Cell<>(x, false, y -> y)).toList();
    }

    private void assertData(Table<String> table, ImmutableList<ImmutableList<String>> expectedData) {
        Assert.assertEquals(expectedData.size(), table.getRowCount());
        boolean check = expectedData
                .zip(table.getData().stream().map(x -> x.stream().map(Cell::getValue).toList()).toList())
                .stream()
                .allMatch(x -> x._1().eq(x._2()));
        Assert.assertTrue(check);
    }
}
