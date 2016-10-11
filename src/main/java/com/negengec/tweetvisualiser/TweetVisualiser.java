package com.negengec.tweetvisualiser;

import org.geotools.map.MapContent;
import org.geotools.swing.JMapFrame;

/**
 * Created by nagellette-ws on 11.10.2016.
 */
public class TweetVisualiser {

    public boolean runVisualiser() throws Exception {
        MapContent map = new MapContent();
        map.setTitle("Tweet coverage");
        JMapFrame.showMap(map);
        return true;
    }
}
