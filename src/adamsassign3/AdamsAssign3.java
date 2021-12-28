/* **********************************************
Kyle Adams
CSCI 3020 Section W1
Fall 2019
Assignment 3
This program converts a csv file to an XML file.
************************************************/
package adamsassign3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdamsAssign3 {

    public static void main(String[] args) {

        System.out.println("---Welcome to Kyle Adams CSV to XML Converter---");
        fileInput();

    }

    /* **************************************************************
    * Prompts the user for the name of the csv file to be converted.
    ****************************************************************/
    public static void fileInput() {

        String fn = "";
        ArrayList<String> data = new ArrayList<>();
        Scanner input = new Scanner(System.in);

        do {
            System.out.print("\nEnter the input filename: ");
            fn = input.next();

            try {
                File inputFile = new File(fn);
                Scanner scanner = new Scanner(inputFile);

                while (scanner.hasNextLine()) {

                    String line = scanner.nextLine();
                    data.add(line);
                }

                scanner.close();
                Collections.sort(data);
                separatorChoice(data);
                break;

            } catch (FileNotFoundException ex) {
                System.out.println("WARNING: Cannot open " + fn);
            }
        } while (true);
    }

    /* **************************************************************
    * Prompts the user for the type of separated value the file
    * contains, and validates that the number the user selects is 
    * within the range of choices.
    ****************************************************************/
    public static void separatorChoice(ArrayList<String> data) {

        Scanner input = new Scanner(System.in);

        int separator = 0;

        System.out.println("\nChoose the number that corresponds to the separated value.\n");
        System.out.println("1) Commas");
        System.out.println("2) Colons");
        System.out.println("3) Tabs");
        System.out.println("4) Pipes");
        System.out.print("\nNumber: ");
        separator = input.nextInt();

        while (separator < 1 || separator > 4) {
            System.out.println("\nInvalid Number. Try again.");
            System.out.print("\nNumber: ");
            separator = input.nextInt();
        }

        XMLOutput(data, separator);
    }

    /* **************************************************************
    * Prompts the user for the name of the XML output file. The .xml
    * extension will automatically be apended to the end of the
    * filename if the user did not include it, and if the filename is
    * invalid the user will be required to re-enter a valid filename. 
    ****************************************************************/
    public static void XMLOutput(ArrayList<String> data, int separator) {

        Scanner input = new Scanner(System.in);
        String outputFileName = "";

        System.out.print("\nEnter the output filename: ");
        outputFileName = input.next();

        while (!outputFileName.endsWith(".xml")) {
            outputFileName = outputFileName + ".xml";
        }

        while (outputFileName.equals(".xml")) {

            System.out.print("WARNING: " + outputFileName + " is not a valid XML filename");
            System.out.print("\nEnter the output filename: ");
            outputFileName = input.next();
        }

        printData(data, outputFileName, separator);
    }

    /* **************************************************************
    * Outputs the data from the CSV file to the XML file. The data
    * will be split based on the users choice from the 
    * separatorChoice() function. A check will be performed to see if
    * there is more than one city for each country. If there is, the 
    * cities that share the same country will be included under that 
    * country. Otherwise, Information for the next Country will be 
    * printed.
    ****************************************************************/
    public static void printData(ArrayList<String> data, String outputFileName, int separator) {

        try {
            PrintWriter pw = new PrintWriter(outputFileName);

            pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            pw.println("<!-- Processed by Kyle Adams's converter -->");
            pw.println("<cou:countries"
                    + "\n\txmlns:cou=\"http://library.littletown.tn/country\""
                    + "\n\txmlns:cit=\"http://library.littletown.tn/city\">");

            for (int i = 0; i < data.size(); i++) {

                String countries = data.get(i);
                String[] countryElement = countries.split("");
                
                switch (separator) {

                    case 1:
                        countryElement = countries.split(",");
                        break;
                    case 2:
                        countryElement = countries.split(":");
                        break;
                    case 3:
                        countryElement = countries.split("\\t");
                        break;
                    case 4:
                        countryElement = countries.split("\\|");
                        break;
                }

                double countryPopulation = Double.parseDouble(countryElement[3]);
                double cityPopulation = Double.parseDouble(countryElement[6]);
                DecimalFormat formatter = new DecimalFormat("#,###");

                pw.println("    <cou:country>");
                pw.println("        <cou:name>" + countryElement[0] + "</cou:name>");
                pw.println("        <cou:region>" + countryElement[1] + "</cou:region>");
                pw.println("        <cou:code>" + countryElement[2] + "</cou:code>");
                pw.println("        <cou:population>" + formatter.format(countryPopulation) + "</cou:population>");
                pw.println("        <cit:cities>");
                pw.println("            <cit:city>");
                pw.println("                <cit:name>" + countryElement[4] + "</cit:name>");
                pw.println("                <cit:district>" + countryElement[5].replace("&", "&amp;") + "</cit:district>");
                pw.println("                <cit:population>" + formatter.format(cityPopulation) + "</cit:population>");
                pw.println("            </cit:city>");

                while (i < data.size() - 1) {

                    i++;

                    if (data.get(i).contains(countryElement[0])) {

                        countries = data.get(i);

                        switch (separator) {

                            case 1:
                                countryElement = countries.split(",");
                                break;
                            case 2:
                                countryElement = countries.split(":");
                                break;
                            case 3:
                                countryElement = countries.split("\\t");
                                break;
                            case 4:
                                countryElement = countries.split("\\|");
                                break;
                        }
                        
                        cityPopulation = Double.parseDouble(countryElement[6]);
                        formatter = new DecimalFormat("#,###");

                        pw.println("            <cit:city>");
                        pw.println("                <cit:name>" + countryElement[4] + "</cit:name>");
                        pw.println("                <cit:district>" + countryElement[5].replace("&", "&amp;") + "</cit:district>");
                        pw.println("                <cit:population>" + formatter.format(cityPopulation) + "</cit:population>");
                        pw.println("            </cit:city>");

                    } else {
                        i--;
                        break;
                    }
                }

                pw.println("        </cit:cities>");
                pw.println("    </cou:country>");
            }

            pw.println("</cou:countries>");
            pw.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(AdamsAssign3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
