package ordenese.rider.model;

/**
 * Created by user on 8/16/2018.
 */

public class Language {
    private String Name, Code;
    private int Language_id;
    private boolean select;

    public void setName(String name) {
        Name = name;
    }

    public void setCode(String code) {
        Code = code;
    }

    public void setLanguage_id(int language_id) {
        Language_id = language_id;
    }

    public String getName() {
        return Name;
    }

    public int getLanguage_id() {
        return Language_id;
    }

    public String getCode() {
        return Code;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }


}
