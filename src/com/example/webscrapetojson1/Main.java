package com.example.webscrapetojson1;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

class ScrapeHyperlinks {
    public static JSONObject jo = new JSONObject();
    public static JSONArray jinfo = new JSONArray(); // FileInfo Array
    public static JSONArray jrunners = new JSONArray(); // Runners Array

    static String year = "2023";
    public static void main(String[] args) {
        // Get current time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        Map<String, String> f = new  LinkedHashMap<String,String>(2);
        f.put("SaveDateTime", currentDateandTime);
        f.put("year", year);

        // adding map to list
        jinfo.add(f);
        // putting File Info to JSONObject
        jo.put("FileInfo", jinfo);
        try {
            // fetch the document over HTTP
            Document doc = Jsoup.connect("https://www.imra.ie/runners/?year=" + year).get();
            Elements runners = doc.getElementsByTag("tr");

            int runnerCount = 0;
            runners.remove(0); // Removes header row
            for ( Element runner : runners ) {
                runnerCount ++;
                Elements link = runner.select("a[href]");
                String linkString = link.attr("href");
                String webId = linkString.replace("/runners/view/id/","");

                // name from link element
                String name = link.text();
                Elements r_info = runner.getAllElements();
                String category = r_info.get(3).getElementsByTag("td").text();
                String raceNo = r_info.get(4).getElementsByTag("td").text();
                String noOfRaces = null;
                try {
                    // Column 5 does not exist in header row
                    if (r_info.get(5) != null) {
                        noOfRaces = r_info.get(5).getElementsByTag("td").text();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("webId: " + webId +name + raceNo + category);
//                System.out.println("name: " + name);
//                System.out.println("Race No.: " + raceNo.text());
//                System.out.println("Category: " + category.text());
                if ( noOfRaces != null) {
                    System.out.println("No. Of Races: " + noOfRaces);
                }
                if ( webId != "") {
                    writeToJson(webId, name, raceNo, category, noOfRaces);
                }
            }

            // FileInfo for JSON file
            System.out.println("No. Of Runners: " + runnerCount);
            // writing JSON to file:"JSONExample.json" in cwd
            PrintWriter pw = new PrintWriter(new FileOutputStream("JSONExample7.json",false));
            pw.write(jo.toJSONString());
            pw.flush();
            pw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeToJson(String webId, String name, String raceNo, String category, String noOfRaces ) throws IOException
    {
        // split name into surname, firstname
        String names[] = name.split(", ");
        String surname = names[0];
        String firstname = names[1];

        Map<String, String> m = new LinkedHashMap<String, String>(7);
        m.put("webId", webId);
        m.put("name", name);
        m.put("firstname", firstname);
        m.put("surname", surname);
        m.put("raceNo", raceNo);
        m.put("category", category);
        m.put("noOfRaces", noOfRaces);
        // adding map to list
        jrunners.add(m);
        // putting Runners to JSONObject
        jo.put("runners", jrunners);


        // writing JSON to file:"JSONExample.json" in cwd
//        PrintWriter pw = new PrintWriter(new FileOutputStream("JSONExample5.json",false));
//        pw.write(jo.toJSONString());
//        pw.flush();
//        pw.close();

//        FileWriter file = new FileWriter("JSONExample6.json");
//        file.write(jo.toJSONString());
//        file.flush();
//        file.close();




    }

}