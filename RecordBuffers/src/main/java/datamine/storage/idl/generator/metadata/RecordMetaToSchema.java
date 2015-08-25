package datamine.storage.idl.generator.metadata;

import datamine.operator.UnaryOperatorInterface;
import datamine.storage.api.RecordMetadataInterface;
import datamine.storage.idl.Schema;
import datamine.storage.idl.Table;
import datamine.storage.idl.type.CollectionFieldType;
import datamine.storage.idl.type.GroupFieldType;

import java.util.ArrayList;
import java.util.List;

/**
 * convert a RecordMetadataInterface back to Schema
 * Created by tliu on 7/13/15.
 */

public class RecordMetaToSchema implements
        UnaryOperatorInterface<Class<? extends RecordMetadataInterface>, Schema> {

    @Override
    public Schema apply(Class<? extends RecordMetadataInterface> tClass) {
        List<Table> tableList = new ArrayList<Table>();
        addTableInTableList(tableList, tClass);
        Schema schema = new Schema(tClass.getName(), tableList);
        return schema;
    }

    //Find all the needed table and add them into Schema
    private void addTableInTableList(List<Table> tableList, Class<? extends RecordMetadataInterface> tClass) {
        if (tableAlreadyAdded(tableList, tClass)){
            return;
        }

        RecordMetaToTable converter = new RecordMetaToTable();
        tableList.add(converter.apply(tClass));
        for (RecordMetadataInterface field : tClass.getEnumConstants()) {
            if (field.getField().getType() instanceof GroupFieldType) {
                GroupFieldType type = (GroupFieldType) field.getField().getType();
                addTableInTableList(tableList, type.getTableClass());
            }
            if (field.getField().getType() instanceof CollectionFieldType &&
                    ((CollectionFieldType) field.getField().getType()).getElementType() instanceof GroupFieldType) {
                GroupFieldType type = (GroupFieldType) ((CollectionFieldType) field.getField().getType()).getElementType();
                addTableInTableList(tableList, type.getTableClass());
            }
        }
    }

    // Check if schema already exist in the Schema
    private boolean tableAlreadyAdded(List<Table> tableList,Class<? extends RecordMetadataInterface> tClass ){
        for (Table table: tableList) {
            if (table.getName().toLowerCase().equals(tClass.getEnumConstants()[0].getTableName())) {
                return true;
            }
        }
        return false;
    }

}
