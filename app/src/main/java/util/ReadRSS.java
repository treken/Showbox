package util;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import model.NewsDTO;

/**
 * Created by Vlade Ilievski on 10/6/2016.
 */

public class ReadRSS {
    static final String KEY_ITEM = "item";
    static final String KEY_TITLE = "title";
    static final String KEY_LINK = "link";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_PUB_DATE = "pubDate";

    //private XmlPullParserFactory xmlPullParserFactory;

    List<NewsDTO> newsItems= new ArrayList<NewsDTO>();


    private String urlString = null;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    NewsDTO curNews = null;

    public ReadRSS(String url){
        this.urlString = url;
    }


    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text=null;

        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals(KEY_ITEM)){
                            newsItems.add(curNews);
                        }

                        else if(name.equals(KEY_TITLE)){
                            curNews.setTitle(text);
                        }

                        else if(name.equals(KEY_LINK)){
                            curNews.setLink(text);
                        }

                        else if(name.equals(KEY_DESCRIPTION)){
                            curNews.setDescription(text);
                        }
                        else if(name.equals(KEY_PUB_DATE)){
                            curNews.setPubDate(text);
                        }

                        else{
                        }
                        break;
                }
                event = myParser.next();
            }
            parsingComplete = false;
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchXML(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream stream = conn.getInputStream();
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);

                    parseXMLAndStoreIt(myparser);
                    stream.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}

//        public static List<NewsDTO> getNewsFromFile (Context ctx){
//
//            // List of StackSites that we will return
//            List<NewsDTO> newsItems;
//            newsItems = new ArrayList<NewsDTO>();
//
//            // temp holder for current StackSite while parsing
//            NewsDTO curNews = null;
//            // temp holder for current text value while parsing
//            String curText = "";
//
//            try {
//                // Get our factory and PullParser
//                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//                XmlPullParser xpp = factory.newPullParser();
//
//                // Open up InputStream and Reader of our file.
//                FileInputStream fis = ctx.openFileInput("feedburner.com/thr/film");
//                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
//
//                // point the parser to our file.
//                xpp.setInput(reader);
//
//                // get initial eventType
//                int eventType = xpp.getEventType();
//
//                // Loop through pull events until we reach END_DOCUMENT
//                while (eventType != XmlPullParser.END_DOCUMENT) {
//                    // Get the current tag
//                    String tagname = xpp.getName();
//
//                    // React to different event types appropriately
//                    switch (eventType) {
//                        case XmlPullParser.START_TAG:
//                            if (tagname.equalsIgnoreCase(KEY_ITEM)) {
//                                // If we are starting a new <site> block we need
//                                //a new StackSite object to represent it
//                                curNews = new NewsDTO();
//                            }
//                            break;
//
//                        case XmlPullParser.TEXT:
//                            //grab the current text so we can use it in END_TAG event
//                            curText = xpp.getText();
//                            break;
//
//                        case XmlPullParser.END_TAG:
//                            if (tagname.equalsIgnoreCase(KEY_ITEM)) {
//
//                                newsItems.add(curNews);
//                            } else if (tagname.equalsIgnoreCase(KEY_TITLE)) {
//
//                                curNews.setTitle(curText);
//                            } else if (tagname.equalsIgnoreCase(KEY_LINK)) {
//
//                                curNews.setLink(curText);
//                            } else if (tagname.equalsIgnoreCase(KEY_DESCRIPTION)) {
//
//                                curNews.setDescription(curText);
//                            } else if (tagname.equalsIgnoreCase(KEY_PUB_DATE)) {
//
//                                curNews.setPubDate(curText);
//                            }
//                            break;
//
//                        default:
//                            break;
//                    }
//
//                    eventType = xpp.next();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return newsItems;
//            // return the populated list.
//        }

//    public void fetchXML(){
//        Thread thread=new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                    URL url=new URL(urlString);
//                    HttpURLConnection connect=(HttpURLConnection)url.openConnection();
//                    connect.setReadTimeout(10000);
//                    connect.setConnectTimeout(15000);
//                    connect.setRequestMethod("GET");
//                    connect.setDoInput(true);
//                    connect.connect();
//
//                    InputStream stream=connect.getInputStream();
//                    xmlPullParserFactory=xmlPullParserFactory.newInstance();
//                    XmlPullParser myParser=xmlPullParserFactory.newPullParser();
//                    myParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
//                    myParser.setInput(stream,null);
//                    getNewsFromFile(myParser);
//                    stream.close();
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });
//    }




