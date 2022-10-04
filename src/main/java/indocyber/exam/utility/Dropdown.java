package indocyber.exam.utility;

import java.util.LinkedList;
import java.util.List;

public class Dropdown {

    private String stringValue;

    private Integer intValue;

    private String text;

    public Dropdown(){}

    public Dropdown(String stringValue, String text) {
        this.stringValue = stringValue;
        this.text = text;
    }

    public Dropdown(Integer intValue, String text) {
        this.intValue = intValue;
        this.text = text;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static List<Dropdown> getRoleDropdown(){
        List<Dropdown> role = new LinkedList<>();
        role.add(new Dropdown("SuperAdmin","Super Admin"));
        role.add(new Dropdown("Admin","Admin"));
        role.add(new Dropdown("Customer","Customer"));
        return role;
    }

    public static List<Dropdown> getRoomType(){
        List<Dropdown> type = new LinkedList<>();
        type.add(new Dropdown("Single","Single"));
        type.add(new Dropdown("Double","Double"));
        type.add(new Dropdown("Triple","Triple"));
        return type;
    }
}
