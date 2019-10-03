package xxx.joker.libs.datalayer.dao;

import xxx.joker.libs.datalayer.design.EntityID;
import xxx.joker.libs.datalayer.design.EntityPK;
import xxx.joker.libs.datalayer.wrapper.FieldWrap;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class DaoMetadata {

    private String fieldName;
    private String fieldTypeName;
    private String collTypeName;
    private boolean id;
    private boolean pk;
    private boolean stringNotNull;

    public DaoMetadata() {

    }
    public DaoMetadata(FieldWrap fieldWrap) {
        this.fieldName = fieldWrap.getFieldName();
        this.fieldTypeName = fieldWrap.getFieldType().getName();
        this.collTypeName = fieldWrap.getCollType() == null ? null : fieldWrap.getCollType().getName();
        this.id = fieldWrap.isEntityID();
        this.pk = fieldWrap.isEntityPK();
        this.stringNotNull = fieldWrap.getFieldType() == String.class && !fieldWrap.isAllowNull();
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldTypeName() {
        return fieldTypeName;
    }

    public void setFieldTypeName(String fieldTypeName) {
        this.fieldTypeName = fieldTypeName;
    }

    public String getCollTypeName() {
        return collTypeName;
    }

    public void setCollTypeName(String collTypeName) {
        this.collTypeName = collTypeName;
    }

    public boolean isId() {
        return id;
    }

    public void setId(boolean id) {
        this.id = id;
    }

    public boolean isPk() {
        return pk;
    }

    public void setPk(boolean pk) {
        this.pk = pk;
    }

    public boolean isStringNotNull() {
        return stringNotNull;
    }

    public void setStringNotNull(boolean stringNotNull) {
        this.stringNotNull = stringNotNull;
    }
}
