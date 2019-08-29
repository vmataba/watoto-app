package configuration.subscription;

/**
 * Created by victor on 11/8/2017.
 * This class is Basically Modeling **Vifurushi Node** From
 * Firebase Database
 */

public class Bundle {

    /**Class Variables**/
    private String bundle_id;
    private String bundle_name;
    private String bundle_desc;
    private double bundle_price;
    private int num_days;

    //Default Constructor
    public Bundle () {
        this.bundle_id = null;
        this.bundle_name = null;
        this.bundle_desc = null;
        this.bundle_price = 0.0;
        this.num_days = 0;
    }
    //Constructor which initializes name
    public Bundle (String bundle_name){
        this();
        this.bundle_name = bundle_name;
    }
    //Constructor which initializes name and price
    public Bundle(String bundle_name, double bundle_price){
        this(bundle_name);
        this.bundle_price = bundle_price;
    }
    /***
     * Other
     * Setters
     * And
     * Getters
     * ***/
    public void setBundle_id(String bundle_id){
        this.bundle_id = bundle_id;
    }

    public void setBundle_name(String bundle_name){
        this.bundle_name = bundle_name;
    }

    public void setBundle_price (double bundle_price){
        this.bundle_price = bundle_price;
    }

    public void setBundle_desc(String bundle_desc){
        this.bundle_desc = bundle_desc;
    }

    public void setNum_days (int num_days){
        this.num_days = num_days;
    }

    public String getBundle_id(){
        return this.bundle_id;
    }

    public String getBundle_name () {
        return this.bundle_name;
    }

    public String getBundle_desc (){
        return this.bundle_desc;
    }

    public double getBundle_price () {
        return this.bundle_price;
    }

    public int getNum_days () {
        return this.num_days;
    }

}
