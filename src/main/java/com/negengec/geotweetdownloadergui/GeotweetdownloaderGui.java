package com.negengec.geotweetdownloadergui;

import com.negengec.geotweetdownloader.CheckInitialDbState;
import com.negengec.geotweetdownloader.DbOperations;
import com.negengec.geotweetdownloader.SettingsReader;
import com.negengec.geotweetdownloader.SettingsWriter;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by nagellette-ws on 04.10.2016.
 */
public class GeotweetdownloaderGui {

    private JTextField consumerKey;
    private JTextField consumerSecret;
    private JTextField accessTokken;
    private JTextField accessTokkenSecret;
    private JTextField dbHostUrl;
    private JTextField dbHostPort;
    private JTextField dbName;
    private JTextField dbUser;
    private JTextField dbUserPassword;
    private JPanel panel1;
    private JTextField projectName;
    private JTextField lat1;
    private JTextField lon1;
    private JTextField lat2;
    private JTextField lon2;
    private JTextField tweetUser;
    private JTextField tweet;
    private JTextField tweetLat;
    private JTextField tweetLon;
    private JButton updateSettings;
    private JButton btnRun;

    public static void main(String[] args) {
        SettingsReader readInitialSettings = new SettingsReader();
        readInitialSettings.readSettings();

        JFrame frame = new JFrame("Geo Tweet Downloader");
        final GeotweetdownloaderGui functionStarter = new GeotweetdownloaderGui();
        frame.setContentPane(functionStarter.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        functionStarter.consumerKey.setText(readInitialSettings.getConsumerKey());
        functionStarter.consumerSecret.setText(readInitialSettings.getConsumerSecret());
        functionStarter.accessTokken.setText(readInitialSettings.getAccessTokken());
        functionStarter.accessTokkenSecret.setText(readInitialSettings.getAccessTokkenSecret());
        functionStarter.dbHostUrl.setText(readInitialSettings.getDbHostUrl());
        functionStarter.dbHostPort.setText(readInitialSettings.getDbHostPort());
        functionStarter.dbName.setText(readInitialSettings.getDbName());
        functionStarter.dbUser.setText(readInitialSettings.getDbUser());
        functionStarter.dbUserPassword.setText(readInitialSettings.getDbUserPassword());
        functionStarter.lat1.setText("39.9");
        functionStarter.lon1.setText("25.07");
        functionStarter.lat2.setText("42.05");
        functionStarter.lon2.setText("44.6");


        functionStarter.updateSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    SettingsWriter settingWriter = new SettingsWriter();
                    settingWriter.writeSettings(functionStarter.consumerKey.getText(), functionStarter.consumerSecret.getText(), functionStarter.accessTokken.getText(),
                            functionStarter.accessTokkenSecret.getText(), functionStarter.dbHostUrl.getText(), functionStarter.dbHostPort.getText(), functionStarter.dbName.getText(),
                            functionStarter.dbUser.getText(), functionStarter.dbUserPassword.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        functionStarter.btnRun.addActionListener(new ActionListener() {
            private PrintStream out;

            public void actionPerformed(ActionEvent arg0) {

                CheckInitialDbState checkState = new CheckInitialDbState();
                if (!checkState.checkInitialState(functionStarter.dbHostUrl.getText(), functionStarter.dbHostPort.getText(), functionStarter.dbName.getText(),
                        functionStarter.dbUser.getText(), functionStarter.dbUserPassword.getText())) {
                    checkState.createInitialState(functionStarter.dbHostUrl.getText(), functionStarter.dbHostPort.getText(), functionStarter.dbName.getText(),
                            functionStarter.dbUser.getText(), functionStarter.dbUserPassword.getText());
                }

                final SettingsReader settingFileOpen = new SettingsReader();
                settingFileOpen.readSettings();
                final DbOperations dbOperations = new DbOperations();

                ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true).setOAuthConsumerKey(settingFileOpen.getConsumerKey())
                        .setOAuthConsumerSecret(settingFileOpen.getConsumerSecret())
                        .setOAuthAccessToken(settingFileOpen.getAccessTokken())
                        .setOAuthAccessTokenSecret(settingFileOpen.getAccessTokkenSecret());

                TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
                try {
                    out = new PrintStream(new FileOutputStream("error_log.txt"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                StatusListener listener = new StatusListener() {
                    public void onStatus(Status status) {

                        if (status.getGeoLocation() != null) {
                            System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText()
                                    + "location:" + status.getGeoLocation());
                            System.setErr(out);

                            String tweetText = status.getText();
                            if (tweetText.contains("'")) {
                                String tweetText1 = tweetText.replaceAll("'", "''");
                                tweetText = tweetText1;
                            }

                            functionStarter.tweetUser.setText(status.getUser().getScreenName());
                            functionStarter.tweet.setText(tweetText);
                            Double tempLatDouble = status.getGeoLocation().getLatitude();
                            Double tempLonDouble = status.getGeoLocation().getLongitude();
                            functionStarter.tweetLat.setText(tempLatDouble.toString());
                            functionStarter.tweetLon.setText(tempLonDouble.toString());

                            String sql = "INSERT INTO twitter_stream"
                                    + " (TWITTERUSER, TWEET, PROJECTNAME, LAT, LON) VALUES ('"
                                    + status.getUser().getScreenName() + "' , '" + tweetText + "' , '"
                                    + functionStarter.projectName.getText() + "' , " + status.getGeoLocation().getLatitude() + ", "
                                    + status.getGeoLocation().getLongitude() + ")";

                            dbOperations.runOperations(sql, settingFileOpen.getDbHostUrl(),
                                    settingFileOpen.getDbHostPort(), settingFileOpen.getDbName(),
                                    settingFileOpen.getDbUser(), settingFileOpen.getDbUserPassword(), "Insert");
                        }
                    }

                    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                    }

                    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                        System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
                    }

                    public void onScrubGeo(long userId, long upToStatusId) {
                        System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
                    }

                    public void onStallWarning(StallWarning warning) {
                        System.out.println("Got stall warning:" + warning);
                    }

                    public void onException(Exception ex) {
                        ex.printStackTrace();
                    }
                };

                twitterStream.addListener(listener);

                double fLat1 = Double.parseDouble(functionStarter.lat1.getText()); // 39.9
                double fLon1 = Double.parseDouble(functionStarter.lon1.getText()); // 25.07
                double fLat2 = Double.parseDouble(functionStarter.lat2.getText()); // 42.05
                double fLon2 = Double.parseDouble(functionStarter.lon2.getText()); // 44.6

                double[][] bb = {{fLon1, fLat1}, {fLon2, fLat2}};

                FilterQuery fq = new FilterQuery();
                fq.locations(bb);

                twitterStream.filter(fq);
            }
        });


    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(11, 5, new Insets(0, 0, 0, 0), -1, -1));
        consumerKey = new JTextField();
        panel1.add(consumerKey, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        consumerSecret = new JTextField();
        panel1.add(consumerSecret, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        accessTokken = new JTextField();
        panel1.add(accessTokken, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        accessTokkenSecret = new JTextField();
        panel1.add(accessTokkenSecret, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        dbHostUrl = new JTextField();
        panel1.add(dbHostUrl, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        dbHostPort = new JTextField();
        panel1.add(dbHostPort, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        dbName = new JTextField();
        panel1.add(dbName, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        dbUser = new JTextField();
        panel1.add(dbUser, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        dbUserPassword = new JTextField();
        dbUserPassword.setText("");
        panel1.add(dbUserPassword, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Consumer Key");
        panel1.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Consumer Secret");
        panel1.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Access Tokken");
        panel1.add(label3, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Access Tokken Screet");
        panel1.add(label4, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("DB Host");
        panel1.add(label5, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("DB Port");
        panel1.add(label6, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("DB Name");
        panel1.add(label7, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("DB User");
        panel1.add(label8, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("DB Password");
        panel1.add(label9, new com.intellij.uiDesigner.core.GridConstraints(8, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        projectName = new JTextField();
        panel1.add(projectName, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        lat1 = new JTextField();
        panel1.add(lat1, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        lon1 = new JTextField();
        panel1.add(lon1, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Project Name");
        panel1.add(label10, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Left Down lat/lon");
        panel1.add(label11, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lat2 = new JTextField();
        panel1.add(lat2, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        lon2 = new JTextField();
        panel1.add(lon2, new com.intellij.uiDesigner.core.GridConstraints(2, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Right Up lat/lon");
        panel1.add(label12, new com.intellij.uiDesigner.core.GridConstraints(2, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tweetUser = new JTextField();
        panel1.add(tweetUser, new com.intellij.uiDesigner.core.GridConstraints(10, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tweet = new JTextField();
        panel1.add(tweet, new com.intellij.uiDesigner.core.GridConstraints(10, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tweetLat = new JTextField();
        panel1.add(tweetLat, new com.intellij.uiDesigner.core.GridConstraints(10, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tweetLon = new JTextField();
        panel1.add(tweetLon, new com.intellij.uiDesigner.core.GridConstraints(10, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("User Name");
        panel1.add(label13, new com.intellij.uiDesigner.core.GridConstraints(9, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("Tweet");
        panel1.add(label14, new com.intellij.uiDesigner.core.GridConstraints(9, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("Latitude");
        panel1.add(label15, new com.intellij.uiDesigner.core.GridConstraints(9, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label16 = new JLabel();
        label16.setText("Longitude");
        panel1.add(label16, new com.intellij.uiDesigner.core.GridConstraints(9, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        updateSettings = new JButton();
        updateSettings.setText("Update Settings");
        panel1.add(updateSettings, new com.intellij.uiDesigner.core.GridConstraints(5, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnRun = new JButton();
        btnRun.setFont(new Font(btnRun.getFont().getName(), Font.BOLD | Font.ITALIC, btnRun.getFont().getSize()));
        btnRun.setText("RUN!");
        panel1.add(btnRun, new com.intellij.uiDesigner.core.GridConstraints(6, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label17 = new JLabel();
        label17.setText("Functions");
        panel1.add(label17, new com.intellij.uiDesigner.core.GridConstraints(4, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
