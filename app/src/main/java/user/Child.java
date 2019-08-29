package user;

/**
 * Created by victor on 10/9/2017.
 * This class does monitor all the child's
 * related information
 */

public class Child {

    public static final String SEX_MALE = "MALE";
    public static final String SEX_FEMALE = "FEMALE";

    private int age;
    private String sex;
    private User parent;

    public Child () {
        this.age = 0;
        this.sex = null;
        this.parent = null;
    }

    public Child (int age, String sex, User parent){
        this.age = age;
        this.sex = sex;
        this.parent = parent;
    }

    public int getAge(){
        return this.age;
    }

    public String getSex(){
        return this.sex;
    }

    public User getParent(){
        return this.parent;
    }

    public void setAge(int age){
        this.age = age;
    }

    public void setSex(String sex){
        this.sex = sex;
    }

    public void setParent(User parent){
        this.parent = parent;
    }

    public void setSexMale(){
        this.sex = SEX_MALE;
    }

    public void setSexFemale(){
        this.sex = SEX_FEMALE;
    }
}
