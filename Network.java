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
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author Y
 */
public class Network {
    private StringBuilder sb = new StringBuilder();
    private int fieldsCount;
    private double alpha;
    private int countOfLayers = 2;
    private int[] countOfNeurons;
    private double[][][] weights; //layer, neuron, sinapse
    private double[][] values;
    private double[][] wt;
    private double[] mins;
    private double[] maxes;
    private String[] fieldsNames;
    
    public void configure(String nnw) throws FileNotFoundException, IOException
    {
        File file = new File(nnw);
        BufferedReader in = new BufferedReader(new FileReader( file.getAbsoluteFile()));

        try
        {

        String str;
        while ((str = in.readLine()) != null)
            {
                sb.append(str + "\n");
            }
        configureLayers();

        }
        finally
        {
         in.close();  
        }
    }
    
    public int ProcessFile(String fileName) throws FileNotFoundException, IOException
        {
           File file = new File(fileName);
           double answer;
           int countOfErrors = 0;
           
                    /*StringBuilder filebuilder = new StringBuilder();
                    BufferedReader in = new BufferedReader(new FileReader( file.getAbsoluteFile()));
                    in.mark((int)file.length());
                 String str;
                 while ((str = in.readLine()) != null)
                     {
                         filebuilder.append(str + "\n");
                     }            
                    FileWriter writer = new FileWriter(file.getAbsoluteFile());
                    writer.write(filebuilder.toString());//.toString().replace(".", ","));*/
                    
           Scanner scan = new Scanner(file);         
           for (int layer = 0; layer < countOfLayers; layer++)
            {
                for (int neuron = 0; neuron < countOfNeurons[layer]; neuron++)
                {
                   values[layer][neuron] = 0;
                }
            }       
           
           scan.useLocale(Locale.US);
           System.out.println(scan.nextLine());
           while (scan.hasNext() || scan.hasNextInt())
               {
                   for (int i = 0; i < fieldsCount-1; i++)
                   {
                    values[0][i] = ((scan.nextDouble() - mins[i])/(maxes[i] - mins[i]))*2 - 1;                     
                   }
                   answer = scan.nextDouble();
                   startNet();
                   double networkAnswer = (values[countOfLayers-1][0]*(maxes[fieldsCount-1] - mins[fieldsCount-1])+mins[fieldsCount-1]);
                   networkAnswer = Math.round(networkAnswer);
                   System.out.println("Answer of network: " + networkAnswer + " Correct: " + answer);
                   if (Math.abs(networkAnswer - answer) > 0.05)
                   {
                       countOfErrors++;
                   }                  
               }
           return countOfErrors;
        }
        
    private void configureLayers()
    {
        fieldsCount = (int)getAttribute("AvailableFieldsCount");
        fieldsNames = new String[fieldsCount];
        mins = new double[fieldsCount];
        maxes = new double[fieldsCount];
        for (int i = 0; i < fieldsCount; i++)
        {
            fieldsNames[i] = getStringAttribute("FieldName_"+i);
            mins[i] = getAttribute("MIN_"+i);
            maxes[i] =getAttribute("MAX_"+i);
        }
        alpha = getAttribute("Alpha");
        countOfLayers = 2 + (int)getAttribute("HideLayers"); 
        countOfNeurons = new int[countOfLayers];
        int maxCountOfNeurons = 0;
        for (int i = 1; i < countOfLayers-1; i++)
        {
            countOfNeurons[i] = (int)getAttribute("Layer_" + i);
            if (countOfNeurons[i] > maxCountOfNeurons)
                maxCountOfNeurons = countOfNeurons[i];
        }
        countOfNeurons[0] = fieldsCount - 1;
        countOfNeurons[countOfLayers-1] = 1;
        
        weights = new double[countOfLayers][maxCountOfNeurons][maxCountOfNeurons];
        values = new double[countOfLayers][maxCountOfNeurons];
        wt = new double[countOfLayers][maxCountOfNeurons];
        
        for (int i = 0; i < countOfLayers-1; i++)
        {
            for (int j = 0; j < countOfNeurons[i]; j++)
            {
                for (int k = 0; k < countOfNeurons[i+1]; k++)
                {
                    //System.out.print("W_" + i + "_" + j + "_" + k + " ");
                     weights[i][j][k] = getAttribute("W_" + i + "_" + j + "_" + k);
                     
                }
                
            }
        }
        

        for (int i = 0; i < countOfLayers-1; i++)
        {
                for (int k = 0; k < countOfNeurons[i+1]; k++)
                {
                  wt[i][k] = getAttribute("WT_" + i + "_" + k);
                }      
        }
    }
    
    private double getAttribute(String attr)
    {
        int iAttr, iEndLine;
        iAttr = sb.indexOf(attr+"=") + attr.length() + 1;
        iEndLine = sb.indexOf("\n",iAttr);
        //System.out.println(iAttr +" "+sb.indexOf(attr+"=")+" "+iEndLine + " " + Double.valueOf(sb.substring(iAttr, iEndLine)));
        return Double.valueOf(sb.substring(iAttr, iEndLine));
    }
    
    private String getStringAttribute(String attr)
    {
        int iAttr, iEndLine;
        iAttr = sb.indexOf(attr+"=") + attr.length() + 1;
        iEndLine = sb.indexOf("\n",iAttr);
        //System.out.println(iAttr +" "+sb.indexOf(attr+"=")+" "+iEndLine + " " + (sb.substring(iAttr, iEndLine)));     
        return sb.substring(iAttr, iEndLine);
    }

    
    private void startNet()
    {
        for (int neuron = 0; neuron < countOfNeurons[0];neuron++)
        {
            //activate(0, neuron);
        }
        
        for (int layer = 1; layer < countOfLayers; layer++)
        {
            for (int neuron = 0; neuron < countOfNeurons[layer]; neuron++)
            {
                for (int previousNeuron = 0; previousNeuron < countOfNeurons[layer-1]; previousNeuron++)
                    {
                        values[layer][neuron] += values[layer-1][previousNeuron] * weights[layer-1][previousNeuron][neuron];
                    }
                values[layer][neuron] += wt[layer-1][neuron];
                activate(layer, neuron);
            }
        }
        
    }
    
    private void activate(int layer, int neuron)
    {
        values[layer][neuron] = 1.0 / (1 + Math.exp(-1*alpha*values[layer][neuron]));
    }
}
