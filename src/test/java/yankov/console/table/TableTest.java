package yankov.console.table;

import org.junit.Assert;
import org.junit.Test;
import yankov.console.factory.CellFactory;
import yankov.console.factory.TableFactory;

import java.util.List;

import static yankov.jfp.utils.ListUtils.*;

public class TableTest {
    @Test
    public void from_SameRowLength_CreateTable() {
        Assert.assertTrue(
            Table.from(
                asCellList(listFill(3, "")),
                List.of(
                    asCellList(List.of("Data 11", "Data 12", "Data 13")),
                    asCellList(List.of("Data 21", "Data 22", "Data 23")),
                    asCellList(List.of("Data 31", "Data 32", "Data 33"))
                ),
                CellFactory::createEmptyStringCell
            ).getRight().isPresent()
        );
    }

    @Test
    public void from_DifferentRowLength_Error() {
        Assert.assertTrue(
            Table.from(
                asCellList(listFill(3, "")),
                List.of(
                    asCellList(List.of("Data 11", "Data 12", "Data 13")),
                    asCellList(List.of("Data 21", "Data 22")),
                    asCellList(List.of("Data 31", "Data 32", "Data 33"))
                ),
                CellFactory::createEmptyStringCell
            ).getLeft().isPresent()
        );
    }

    @Test
    public void fieldSize_HeaderLabelIsLongest_HeaderLabelLength() {
        List<List<String>> data = List.of(
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 21", "Data 22", "Data 23"),
            List.of("Data 31", "Data 32", "Data 33")
        );
        Assert.assertEquals(createTable(data).fieldSize(1), 8);
    }

    @Test
    public void fieldSize_DataLabelIsLongest_DataLabelLength() {
        List<List<String>> data = List.of(
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 21", "Some long data field", "Data 23"),
            List.of("Data 31", "Data 32", "Data 33")
        );
        Assert.assertEquals(createTable(data).fieldSize(1), 20);
    }

    @Test
    public void swapRows_ValidIndexes_TableWithSwappedRows() {
        List<List<String>> data = List.of(
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 21", "Data 22", "Data 23"),
            List.of("Data 31", "Data 32", "Data 33")
        );

        Table<String> table = createTable(data).swapRows(0, 1);

        List<List<String>> expectedData = List.of(
            List.of("Data 21", "Data 22", "Data 23"),
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 31", "Data 32", "Data 33")
        );

        assertData(table, expectedData);
    }

    @Test
    public void swapRows_InvalidIndexes_TableWithoutChange() {
        List<List<String>> data = List.of(
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 21", "Data 22", "Data 23"),
            List.of("Data 31", "Data 32", "Data 33")
        );

        Table<String> table = createTable(data).swapRows(3, 4);

        List<List<String>> expectedData = List.of(
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 21", "Data 22", "Data 23"),
            List.of("Data 31", "Data 32", "Data 33")
        );

        assertData(table, expectedData);
    }

    @Test
    public void insertEmptyRow_EmptyData_InsertedRow() {
        Table<String> table = createTable(List.of(), 3).insertEmptyRow(0);

        List<List<String>> expectedData = List.of(
            List.of("", "", "")
        );

        assertData(table, expectedData);
    }

    @Test
    public void insertEmptyRow_EmptyTable_InsertedRow() {
        Table<String> table = createTable(List.of(), 0).insertEmptyRow(0);

        List<List<String>> expectedData = List.of(
            List.of("")
        );

        assertData(table, expectedData);
    }

    @Test
    public void insertEmptyRow_NonEmptyData_DataWithInsertedRowAtGivenIndex() {
        List<List<String>> data = List.of(
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 21", "Data 22", "Data 23"),
            List.of("Data 31", "Data 32", "Data 33"),
            List.of("Data 41", "Data 42", "Data 43")
        );

        Table<String> table = createTable(data).insertEmptyRow(2);

        List<List<String>> expectedData = List.of(
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 21", "Data 22", "Data 23"),
            List.of("", "", ""),
            List.of("Data 31", "Data 32", "Data 33"),
            List.of("Data 41", "Data 42", "Data 43")
        );

        assertData(table, expectedData);
    }

    @Test
    public void insertEmptyCol_EmptyData_TableWithEmptyColumn() {
        Table<String> table = createTable(List.of(), 0).insertEmptyColumn(0);
        Assert.assertEquals(1, table.getColCount());
        Assert.assertEquals(0, table.getRowCount());
    }

    @Test
    public void insertEmptyCol_NonEmptyData_DataWithInsertedColAtGivenIndex() {
        List<List<String>> data = List.of(
            List.of("Data 11", "Data 12", "Data 13", "Data 14"),
            List.of("Data 21", "Data 22", "Data 23", "Data 24"),
            List.of("Data 31", "Data 32", "Data 33", "Data 34")
        );

        Table<String> table = createTable(data).insertEmptyColumn(2);

        List<List<String>> expectedData = List.of(
            List.of("Data 11", "Data 12", "", "Data 13", "Data 14"),
            List.of("Data 21", "Data 22", "", "Data 23", "Data 24"),
            List.of("Data 31", "Data 32", "", "Data 33", "Data 34")
        );

        Assert.assertTrue(
            listEquals(
                List.of("Column 1", "Column 2", "", "Column 3", "Column 4"),
                table.getHeader().stream().map(Cell::getValue).toList()
            )
        );

        assertData(table, expectedData);
    }

    @Test
    public void deleteRow_ValidIndex_TableWithoutDeletedRow() {
        List<List<String>> data = List.of(
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 21", "Data 22", "Data 23"),
            List.of("Data 31", "Data 32", "Data 33")
        );

        Table<String> table = createTable(data).deleteRow(1);

        List<List<String>> expectedData = List.of(
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 31", "Data 32", "Data 33")
        );

        assertData(table, expectedData);
    }

    @Test
    public void deleteRow_InvalidIndex_TableWithoutChange() {
        List<List<String>> data = List.of(
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 21", "Data 22", "Data 23"),
            List.of("Data 31", "Data 32", "Data 33")
        );

        Table<String> table = createTable(data).deleteRow(3);

        List<List<String>> expectedData = List.of(
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 21", "Data 22", "Data 23"),
            List.of("Data 31", "Data 32", "Data 33")
        );

        assertData(table, expectedData);
    }

    @Test
    public void deleteCol_ValidIndex_TableWithoutDeletedColumns() {
        List<List<String>> data = List.of(
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 21", "Data 22", "Data 23"),
            List.of("Data 31", "Data 32", "Data 33")
        );

        Table<String> table = createTable(data).deleteCol(1);

        List<List<String>> expectedData = List.of(
            List.of("Data 11", "Data 13"),
            List.of("Data 21", "Data 23"),
            List.of("Data 31", "Data 33")
        );

        Assert.assertTrue(
            listEquals(
                List.of("Column 1", "Column 3"),
                table.getHeader().stream().map(Cell::getValue).toList()
            )
        );

        assertData(table, expectedData);
    }

    @Test
    public void deleteCol_InvalidIndex_TableWithoutChange() {
        List<List<String>> data = List.of(
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 21", "Data 22", "Data 23"),
            List.of("Data 31", "Data 32", "Data 33")
        );

        Table<String> table = createTable(data).deleteCol(8);

        List<List<String>> expectedData = List.of(
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 21", "Data 22", "Data 23"),
            List.of("Data 31", "Data 32", "Data 33")
        );

        Assert.assertTrue(
            listEquals(
                List.of("Column 1", "Column 2", "Column 3"),
                table.getHeader().stream().map(Cell::getValue).toList()
            )
        );

        assertData(table, expectedData);
    }

    @Test
    public void withData_NewData_TableWithUpdatedData() {
        List<List<String>> data = List.of(
            List.of("Data 11", "Data 12", "Data 13"),
            List.of("Data 21", "Data 22", "Data 23"),
            List.of("Data 31", "Data 32", "Data 33")
        );

        List<List<String>> newData = List.of(
            List.of("", "Data 12", "Data 13"),
            List.of("Data 21", "", "Data 23"),
            List.of("Data 31", "Data 32", "")
        );

        Table<String> table = createTable(data)
            .withData(newData.stream().map(this::asCellList).toList())
            .getRight()
            .orElse(Table.empty(CellFactory::createEmptyStringCell));

        assertData(table, newData);
    }

    private Table<String> createTable(List<List<String>> data, int headerSize) {
        List<Cell<String>> header = asCellList(
                zipWithIndex(listFill(headerSize, "Column "))
                .stream()
                .map(x -> x._1() + (x._2() + 1))
                .toList()
        );
        List<List<Cell<String>>> cellData = data.stream().map(this::asCellList).toList();

        return TableFactory.createStringTable(header, cellData);
    }

    private Table<String> createTable(List<List<String>> data) {
        return createTable(data, data.get(0).size());
    }

    private List<Cell<String>> asCellList(List<String> data) {
        return data.stream().map(x -> new Cell<>(x, false, y -> y)).toList();
    }

    private void assertData(Table<String> table, List<List<String>> expectedData) {
        Assert.assertEquals(expectedData.size(), table.getRowCount());
        boolean check = zip(
            expectedData,
            table.getData().stream()
                .map(x -> x.stream().map(Cell::getValue).toList()).toList()
        ).stream().allMatch(x -> listEquals(x._1(), x._2()));
        Assert.assertTrue(check);
    }
}
