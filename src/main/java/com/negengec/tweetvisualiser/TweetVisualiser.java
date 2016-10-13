package com.negengec.tweetvisualiser;

import org.geotools.data.*;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nagellette-ws on 11.10.2016.
 */
public class TweetVisualiser {

    private File baseSourceFile;
    private String absoluteFilePathBaseMap = "";
    private SimpleFeatureSource baseFeatureSource;

    public boolean runVisualiser() throws Exception {

        // Get file path for base world map
        // TODO duplicate process for OS check, create a class
        String fileName = "TM_WORLD_BORDERS-0.3.shp";
        System.out.println(System.getProperty("os.name"));
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            absoluteFilePathBaseMap = "." + File.separator + "target" + File.separator + "conf" + File.separator + fileName;
            System.out.println(absoluteFilePathBaseMap);
        } else if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            absoluteFilePathBaseMap = "conf" + File.separator + fileName;
            System.out.println(absoluteFilePathBaseMap);
        } else {
            System.out.println("Your operating system does not supported. Abort.");
            System.exit(0);
        }

        // Create a feature source with Shape file path and define style
        baseSourceFile = new File(absoluteFilePathBaseMap);
        FileDataStore store = FileDataStoreFinder.getDataStore(baseSourceFile);
        baseFeatureSource = store.getFeatureSource();
        Style style = SLD.createSimpleStyle(baseFeatureSource.getSchema());
        Layer baseLayer = new FeatureLayer(baseFeatureSource, style);

        // Create a map context, add base layer to the map
        MapContent map = new MapContent();
        map.setTitle("Tweet coverage");

        // create Postgis connection parameters
        // TODO hardcoded, add to class definition, get values with constractor.
        Map params = new HashMap();
        params.put("dbtype", "postgis");
        params.put("host", "localhost");
        params.put("port", "5432");
        params.put("database", "production");
        params.put("user", "gisdb");
        params.put("passwd", "123456");

        // Create data store, get feature layer twitter_stream
        // TODO query project name and records
        // TODO check refresh rate, if not refreshing find a solution
        DataStore pgDatastore = DataStoreFinder.getDataStore(params);
        FeatureSource postgisSource = pgDatastore.getFeatureSource("twitter_stream");
        System.out.println("bc count: " + postgisSource.getCount(Query.ALL));
        Style stylePostgis = SLD.createSimpleStyle(postgisSource.getSchema());
        Layer layerPostgis = new FeatureLayer(postgisSource, stylePostgis);

        // add layers to the map
        map.addLayer(layerPostgis);
        map.layers().add(baseLayer);

        // starting map frame
        JMapFrame.showMap(map);
        return true;


    }
}
