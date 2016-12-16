package com.chocolatkey;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static final String kurl = "http://www.karaokeparty.com/apps/droid/v1_3/";
    static final String kurl2 = "http://www.karaokeparty.com/static/c/m/";
    static final String session = Long.toHexString(Double.doubleToLongBits(Math.random()));
    static public String spath;
    public static void main(String[] args) {
	    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                "karaokeparty.com snatcher by chocolatkey\n" +
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        JFileChooser savefolderchooser = new JFileChooser();
        savefolderchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        savefolderchooser.showDialog(null, "Choose Folder to save files in");
        if (savefolderchooser.getSelectedFile() == null) {
            System.err.println("Choose a folder!");
            System.exit(1);
        }
        spath = savefolderchooser.getSelectedFile().toString();

        Document kparty = null;
        try {
            kparty = Jsoup.connect(kurl + "menu_search").get();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Maybe they're dead by now? Or maybe your internet is just crappy...");
            System.exit(1);
        }
        Element list = kparty.select("script").last();
        Pattern p = Pattern.compile("(?is)pop = (\\[.+?\\])"); // Regex for the value of the key
        Matcher m = p.matcher(list.html());

        JSONArray json = null;
        while(m.find())
        {
            System.out.println("Songs: " + m.group(1));
            json = new JSONArray(m.group(1));
        }
        Path jsonf = Paths.get(spath + File.separator + "songs.json");
        try {
            Files.write(jsonf, json.toString(2).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            //System.out.println("Couldn't write songs.json. Did you forget to delete a previous copy?");
            //System.exit(1);
        }
        System.out.println("Getting data...");
        for (int i = 0; i < json.length(); i++) {
            JSONObject jt = json.getJSONObject(i);
            String jti = jt.getString("i");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                    (i+1) + "/" + json.length() + ": " + jt.getString("a") + " - " + jt.getString("t") + "\n" +
                    "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            if(new File(spath + File.separator + jti + "_b.mp3").exists() && new File(spath + File.separator + jti + "_i.mp3").exists() && new File(spath + File.separator + jti + "_v.mp3").exists()){
                System.out.println("MP3s already exist, skipping them.");
            } else {
                try {
                    System.out.println("DLing vocals version...");
                    savemp3(jti, "v");
                } catch (Exception e){}
                try {
                    System.out.println("DLing backing version...");
                    savemp3(jti, "b");
                } catch (Exception e){}
                try {
                    System.out.println("DLing instrumental version...");
                    savemp3(jti, "i");
                } catch (Exception e){}
            }
            System.out.println("DLing karaoke data..."); // Ya never know
            //saveif(kurl + "abin" + "_" + jti + "_" + session, spath + File.separator + "abin_" + jti);
            //saveif("http://www.karaokeparty.com/php/flashcom/mbin.php?id=" + jti, spath + File.separator + "mbin_" + jti);//lyrics/notes
            saveif("http://www.karaokeparty.com/php/flashcom/mbin.php?id=" + jti + "&t=v", spath + File.separator + "mbinv_" + jti);//vid link
            System.out.println("Downloading thumbnail..."); // Ya never know
            //saveif("http://www.karaokeparty.com/static/c/sc/" + jti + ".jpg", spath + File.separator + jti + ".jpg");

        }

        System.out.println("Final Check: There should be " + json.length() + " songs with ~5 files each (" + (json.length() * 4) + " files total)");
        for (int i = 0; i < json.length(); i++) {
            System.out.print("Song #" + (i+1) + "...");
            JSONObject jt = json.getJSONObject(i);
            if(new File(spath + File.separator + "abin_" + jt.getString("i")).exists() && new File(spath + File.separator + "mbin_" + jt.getString("i")).exists() && new File(spath + File.separator + "mbinv_" + jt.getString("i")).exists() && new File(spath + File.separator + jt.getString("i") + "_b.mp3").exists() && new File(spath + File.separator + jt.getString("i") + "_i.mp3").exists() && new File(spath + File.separator + jt.getString("i") + "_v.mp3").exists()){
                System.out.println("Good");
            } else {
                System.err.println("Not good (some songs don't have a certain version though, could be it)");
            }
        }
        System.out.println("DONE");
    }

    static void saveif(String dlfile, String pcfile){
        boolean overwrite = false;
        if(!overwrite && (new File(pcfile).exists())){
            return;
        } else {
            savedata(dlfile, pcfile);
        }
    }

    static void savedata(String input, String output){
        URL website = null;
        try {
            website = new URL(input);// Stupid analytics/uid thing
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ReadableByteChannel rbc = null;
        try {
            rbc = Channels.newChannel(website.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(output);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void savemp3(String number, String version){
        URL website = null;
        try {
            website = new URL(kurl2 + number + "_" + version + ".mp3");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ReadableByteChannel rbc = null;
        try {
            rbc = Channels.newChannel(website.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(spath + File.separator + number + "_" + version + ".mp3");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
