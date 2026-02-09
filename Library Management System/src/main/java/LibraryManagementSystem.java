import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

//Name: Jackson DeWitt, Course: Software Development 1 (202620-CEN-3024C-23585), Date: 2/7/2026
//Class Name: LibraryManagementSystem (or LMS)
//The main application of the project that takes the patron class and holds, modifies, or deletes patron information
//based on the user's wishes.

public class LibraryManagementSystem {
    private static final Map<String, Patron> patrons = new HashMap<>();
    private static final Scanner scanner = new Scanner(System.in);
    //The main menu for showing the user all options and how to quit
    public static void main(String[] args) {
        System.out.println("=== Library Management System ===\n");
        System.out.println("Note: Press Q (or q) at any time to quit current operation or exit the application\n");
        //To check what the user has put in and whether they would like to quit
        boolean running = true;
        while (running) {
            displayMenu();
            System.out.print("Enter your choice (1-4, or Q to exit): ");
            String choice = scanner.nextLine().trim();

            //Checks for quit command
            if (choice.equalsIgnoreCase("Q")) {
                System.out.println("Exiting Library Management System. Goodbye!");
                running = false;
                continue;
            }
            //Each option tied to each function later in the code
            switch (choice) {
                case "1":
                    addPatronFromFile();
                    break;
                case "2":
                    addPatronManually();
                    break;
                case "3":
                    removePatron();
                    break;
                case "4":
                    listAllPatrons();
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1-4 or Q to exit.\n");
            }
        }
        scanner.close();
    }

    //The printing of the actual menu
    private static void displayMenu() {
        System.out.println("Main Menu:");
        System.out.println("1. Add patrons from text file");
        System.out.println("2. Add patron manually");
        System.out.println("3. Remove patron by ID");
        System.out.println("4. List all patrons");
        System.out.println();
    }


    private static void addPatronFromFile() {
        System.out.println("\n--- Add Patrons from File ---");
        System.out.println("(Press Q at any time to quit and return to main menu)");
        System.out.print("Enter the file path: ");
        String filePath = scanner.nextLine().trim();

        if (filePath.equalsIgnoreCase("Q")) {
            System.out.println("Operation cancelled. Returning to main menu.\n");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int addedCount = 0;
            int errorCount = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    Patron patron = parsePatron(line);
                    if (patrons.containsKey(patron.id())) {
                        System.out.println("Error: Duplicate ID " + patron.id() + " - Skipping");
                        errorCount++;
                    } else {
                        patrons.put(patron.id(), patron);
                        addedCount++;
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Error parsing line: " + line + " - " + e.getMessage());
                    errorCount++;
                }
            }

            System.out.println("\nFile import completed:");
            System.out.println("  Successfully added: " + addedCount + " patron(s)");
            System.out.println("  Errors: " + errorCount + " line(s)");
            System.out.println("  Total patrons in system: " + patrons.size() + "\n");

            //Displays all patrons after file import
            listAllPatrons();

        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found at path: " + filePath + "\n");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage() + "\n");
        }
    }


    private static void addPatronManually() {
        System.out.println("\n--- Manual Patron Entry ---");
        System.out.println("(Press Q at any time to quit and return to main menu)\n");

        //For manual ID entry
        String id;
        while (true) {
            System.out.print("Enter 7-digit patron ID: ");
            id = scanner.nextLine().trim();

            if (id.equalsIgnoreCase("Q")) {
                System.out.println("Operation cancelled. Returning to main menu.\n");
                return;
            }

            if (!Pattern.matches("\\d{7}", id)) {
                System.out.println("Error: ID must be exactly 7 digits (e.g., 1234567).");
                //As required
                continue;
            }

            if (patrons.containsKey(id)) {
                System.out.println("Error: ID " + id + " already exists in system.");
                continue;
            }

            break;
        }

        //For manual name entry
        String name;
        while (true) {
            System.out.print("Enter name: ");
            name = scanner.nextLine().trim();

            if (name.equalsIgnoreCase("Q")) {
                System.out.println("Operation cancelled. Returning to main menu.\n");
                //Just in case the user changes their mind
                return;
            }

            if (name.isEmpty()) {
                System.out.println("Error: Name cannot be empty.");
                //Obvious error for catching non-names
                continue;
            }

            break;
        }

        //For manual address entry (note that addresses can have a certain amount of information, up to the discretion
        //of the user to put in things like zip codes and such)
        String address;
        while (true) {
            System.out.print("Enter address: ");
            address = scanner.nextLine().trim();
            if (address.equalsIgnoreCase("Q")) {
                System.out.println("Operation cancelled. Returning to main menu.\n");
                return;
            }
            if (address.isEmpty()) {
                System.out.println("Error: Address cannot be empty.");
                continue;
            }
            break;
        }


        double overdueAmount;
        while (true) {
            System.out.print("Enter overdue amount (0.00 - 250.00): $");
            String amountInput = scanner.nextLine().trim();

            if (amountInput.equalsIgnoreCase("Q")) {
                System.out.println("Operation cancelled. Returning to main menu.\n");
                return;
            }

            try {
                overdueAmount = Double.parseDouble(amountInput);
                if (overdueAmount < 0 || overdueAmount > 250) {
                    System.out.println("Error: Amount must be between $0.00 and $250.00");
                    continue;
                    //A catch to make sure it fits the guidelines of the appropriate amount
                    //of overdue fees per the design document
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number (e.g., 40.54)");
                //Obvious catch to make sure an actual number was entered and not gibberish
            }
        }

        Patron patron = new Patron(id, name, address, overdueAmount);
        patrons.put(id, patron);

        System.out.println("\nPatron added successfully!");
        System.out.println("ID: " + id + ", Name: " + name + ", Overdue: $" + String.format("%.2f", overdueAmount));
        System.out.println("Total patrons in system: " + patrons.size() + "\n");


        System.out.println("All patrons in system:");
        listAllPatrons();
    }


    private static void removePatron() {
        System.out.println("\n--- Remove Patron ---");
        System.out.println("(Press Q at any time to quit and return to main menu)");
        System.out.print("Enter the 7-digit ID of patron to remove: ");
        String id = scanner.nextLine().trim();

        if (id.equalsIgnoreCase("Q")) {
            System.out.println("Operation cancelled. Returning to main menu.\n");
            return;
        }

        if (!Pattern.matches("\\d{7}", id)) {
            System.out.println("Error: ID must be exactly 7 digits.\n");
            return;
        }

        if (patrons.containsKey(id)) {
            Patron removedPatron = patrons.remove(id);
            System.out.println("\nPatron removed successfully:");
            System.out.println("  " + removedPatron);
            System.out.println("Total patrons remaining: " + patrons.size() + "\n");

            // Display all remaining patrons after removal (Test 5 requirement)
            System.out.println("All remaining patrons:");
            listAllPatrons();
        } else {
            System.out.println("Error: No patron found with ID " + id + "\n");
        }
    }

    //For listing all patrons in local memory
    private static void listAllPatrons() {
        System.out.println("\n--- All Patrons ---");

        if (patrons.isEmpty()) {
            System.out.println("No patrons in the system.\n");
            return;
        }

        System.out.println("Total patrons: " + patrons.size());
        // Java 8 compatible line drawing
        for (int i = 0; i < 80; i++) {
            System.out.print("=");
        }
        System.out.println();

        int count = 1;
        for (Patron patron : patrons.values()) {
            System.out.println(count + ". " + patron);
            count++;
        }

        // Java 8 compatible line drawing
        for (int i = 0; i < 80; i++) {
            System.out.print("=");
        }
        System.out.println();

        // Calculate statistics
        double totalFines = 0.0;
        for (Patron patron : patrons.values()) {
            totalFines += patron.overdueAmount();
        }
        double averageFine = totalFines / patrons.size();

        System.out.println("Summary:");
        System.out.printf("  Total overdue fines: $%.2f%n", totalFines);
        System.out.printf("  Average fine per patron: $%.2f%n", averageFine);
        System.out.println();
    }

    //For formatting patrons put in through a txt file, as was written in the design document. Patron information
    //MUST be written in the appropriate format as listed in the document, or the example given below:
    //Example format: 1245789-Sarah Jones-1136 Gorden Ave. Orlando, FL 32822-40.54
    private static Patron parsePatron(String line) {
        String[] parts = line.split("-", 4);

        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid format. Expected: ID-Name-Address-Amount");
        }

        String id = parts[0].trim();
        String name = parts[1].trim();
        String address = parts[2].trim();
        String amountStr = parts[3].trim();

        // Validate ID
        if (!Pattern.matches("\\d{7}", id)) {
            throw new IllegalArgumentException("ID must be exactly 7 digits");
        }

        // Validate name
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        // Validate amount
        double overdueAmount;
        try {
            overdueAmount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format. Use numbers like 40.54");
        }

        if (overdueAmount < 0 || overdueAmount > 250) {
            throw new IllegalArgumentException("Amount must be between 0.00 and 250.00");
        }

        return new Patron(id, name, address, overdueAmount);
    }
}