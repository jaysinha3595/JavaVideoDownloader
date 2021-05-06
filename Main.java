package com.company;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        // url
        //String link = 

        // selenium initialization
        System.setProperty("webdriver.chrome.driver", "/home/jaysinha/Downloads/chromedriver_linux64/chromedriver");

        // m3u8
        StringBuilder _m3u8 = new StringBuilder();
        String line;
        URL url = new URL(link);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while ((line=br.readLine())!=null){
            _m3u8.append(line);
        }
        br.close();
        System.out.println(_m3u8);

        // get .ts files using regex
        ArrayList<String> _ts = new ArrayList<>();
        Pattern pattern = Pattern.compile(",(.*?)#");
        Matcher matcherObj = pattern.matcher(_m3u8);
        while (matcherObj.find()){
            _ts.add(matcherObj.group(1));
        }
        System.out.println(_ts);

        // Download .ts files - Selenium
        WebDriver webDriver = new ChromeDriver();
        webDriver.manage().window().maximize();

        for(String i:_ts){
            webDriver.navigate().to(link.replace("index.m3u8", i));
            //Thread.sleep(100);
        }
        //webDriver.close(); //closes before downloading
        System.out.println(_ts.size()+" files downloaded.");
        System.out.println("Merging binaries in 1 file.");

        // file streams
        FileInputStream fin = null;
        FileOutputStream fout = null;
        int i;
        int count = 0;
        try {
            fout = new FileOutputStream("video1.ts");
            for(String k:_ts){
                System.out.println("Converting "+ (++count) +" of "+ _ts.size()+" files.");
                fin = new FileInputStream("/home/jaysinha/Downloads/"+k);
                do{
                    i = fin.read();
                    if(i!=-1) fout.write(i);
                }while (i!=-1);
            }
        }catch (IOException e){
            System.out.println(e);
        }finally {
            try {
                if(fin!=null){
                    fin.close();
                }
            }catch (IOException e){
                System.out.println(e);
            }

            try {
                if(fout!=null){
                    fout.close();
                }
            }catch (IOException e){
                System.out.println(e);
            }
        }
        System.out.println("File converted");

        // Delete transport files
        System.out.println("Deleting transport files");
        for(String k:_ts){
            File file = new File("/home/jaysinha/Downloads/"+k);
            file.delete();
        }
        System.out.println("Transport files deleted.\nAll Done");
    }
}
