//Name: Jackson DeWitt, Course: Software Development 1 (202620-CEN-3024C-23585), Date: 2/7/2026
//Class Name: Patron
//Given that this class is tied to the LMS main application, this is where all the information of the patrons
//added, deleted, printed or just generally used gets stored for the main application. As you can see below,
//all necessary information is held in different variables (mostly string, but double for the overdue amounts because
//of the potential decimal numbers). I typically try to separate the classes used in main applications to make it easier
//to read.

public class Patron {
    private final String id;
    private final String name;
    private final String address;
    private final double overdueAmount;

    public Patron(String id, String name, String address, double overdueAmount) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.overdueAmount = overdueAmount;
    }

    //This is for the numeric ID number for each patron
    public String id() {
        return id;
    }

    //For their name
    public String name() {
        return name;
    }

    //For their address
    public String address() {
        return address;
    }

    //For their overdue amount (held in double for stated reasons)
    public double overdueAmount() {
        return overdueAmount;
    }

    //The formatting to make it easier for the user to read once printed using the listing function and
    //described in the design document
    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Address: %s | Overdue: $%.2f",
                id, name, address, overdueAmount);
    }
}