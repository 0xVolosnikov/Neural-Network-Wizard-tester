/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetworkwizardtester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Y
 */
public class NeuralNetworkWizardTester {
    private static String nnwText;
    private static Network net = new Network();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 2) 
            System.out.println("Too few params! \n Syntax: *.nnw [command] ");
        else
        {
            String fileName = args[0];

            try {
                net.configure(fileName);
                switch(args[1])
                {
                    case "h":
                    case "help":
                        System.out.println("     Help for NNWT:");
                        System.out.println("'file [fileName]' - tests network on the set from the file");
                        break;
                    case "file":
                        System.out.println("Number of errors: " + net.ProcessFile(args[2]));
                        break;
                    case "console":
                        break;
                }
            } catch (FileNotFoundException ex) {
                System.out.println("File not found: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("Error with file: " + ex.getMessage());
            }
        }
    }
    
}
