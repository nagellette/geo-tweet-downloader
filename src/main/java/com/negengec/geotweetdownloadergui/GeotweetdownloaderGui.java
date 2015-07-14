package com.negengec.geotweetdownloadergui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.negengec.geotweetdownloader.CheckInitialDbState;
import com.negengec.geotweetdownloader.DbOperations;
import com.negengec.geotweetdownloader.SettingsReader;
import com.negengec.geotweetdownloader.SettingsWriter;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class GeotweetdownloaderGui {

	private JFrame frame;
	private JTextField consumerKey;
	private JTextField consumerSecret;
	private JTextField accessTokken;
	private JTextField accessTokkenSecret;
	private JTextField dbName;
	private JTextField dbUser;
	private JTextField dbUserPassword;
	private JTextField lat2;
	private JTextField lon2;
	private JTextField lat1;
	private JTextField lon1;
	private JTextField projectName;
	private JLabel lblNewLabel;
	private JButton btnRun;
	private JTextField tweetUser;
	private JTextField tweet;
	private JTextField tweetLat;
	private JTextField tweetLon;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JLabel lblNewLabel_4;
	private JTextField dbHostPort;
	private JTextField dbHostUrl;
	private JLabel lblNewLabel_5;
	private JLabel lblNewLabel_6;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GeotweetdownloaderGui window = new GeotweetdownloaderGui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GeotweetdownloaderGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// open file to get initial settings from properties fi
		SettingsReader readInitialSettings = new SettingsReader();
		readInitialSettings.readSettings();
		;

		frame = new JFrame();
		frame.getContentPane().setFont(new Font("Tahoma", Font.BOLD, 11));
		frame.setBounds(100, 100, 875, 355);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		consumerKey = new JTextField();
		consumerKey.setBounds(10, 11, 392, 20);
		frame.getContentPane().add(consumerKey);
		consumerKey.setColumns(10);
		consumerKey.setText(readInitialSettings.getConsumerKey());

		consumerSecret = new JTextField();
		consumerSecret.setColumns(10);
		consumerSecret.setBounds(10, 42, 392, 20);
		frame.getContentPane().add(consumerSecret);
		consumerSecret.setText(readInitialSettings.getConsumerSecret());

		accessTokken = new JTextField();
		accessTokken.setColumns(10);
		accessTokken.setBounds(10, 73, 392, 20);
		frame.getContentPane().add(accessTokken);
		accessTokken.setText(readInitialSettings.getAccessTokken());

		accessTokkenSecret = new JTextField();
		accessTokkenSecret.setColumns(10);
		accessTokkenSecret.setBounds(10, 104, 392, 20);
		frame.getContentPane().add(accessTokkenSecret);
		accessTokkenSecret.setText(readInitialSettings.getAccessTokkenSecret());

		dbName = new JTextField();
		dbName.setBounds(10, 188, 132, 20);
		frame.getContentPane().add(dbName);
		dbName.setColumns(10);
		dbName.setText(readInitialSettings.getDbName());

		dbUser = new JTextField();
		dbUser.setColumns(10);
		dbUser.setBounds(10, 216, 132, 20);
		frame.getContentPane().add(dbUser);
		dbUser.setText(readInitialSettings.getDbUser());

		dbUserPassword = new JTextField();
		dbUserPassword.setColumns(10);
		dbUserPassword.setBounds(10, 246, 132, 20);
		frame.getContentPane().add(dbUserPassword);
		dbUserPassword.setText(readInitialSettings.getDbUserPassword());

		dbHostUrl = new JTextField();
		dbHostUrl.setColumns(10);
		dbHostUrl.setBounds(10, 135, 132, 20);
		frame.getContentPane().add(dbHostUrl);
		dbHostUrl.setText(readInitialSettings.getDbHostUrl());

		dbHostPort = new JTextField();
		dbHostPort.setBounds(10, 159, 132, 20);
		frame.getContentPane().add(dbHostPort);
		dbHostPort.setColumns(10);
		dbHostPort.setText(readInitialSettings.getDbHostPort());

		JLabel lblConsumerKey = new JLabel("Consumer Key");
		lblConsumerKey.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblConsumerKey.setLabelFor(consumerKey);
		lblConsumerKey.setBounds(412, 11, 95, 14);
		frame.getContentPane().add(lblConsumerKey);

		JLabel lblConsumerSecret = new JLabel("Consumer Secret");
		lblConsumerSecret.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblConsumerSecret.setLabelFor(consumerSecret);
		lblConsumerSecret.setBounds(412, 42, 115, 14);
		frame.getContentPane().add(lblConsumerSecret);

		JLabel lblAccessTokken = new JLabel("Access Tokken");
		lblAccessTokken.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAccessTokken.setLabelFor(accessTokken);
		lblAccessTokken.setBounds(412, 73, 95, 14);
		frame.getContentPane().add(lblAccessTokken);

		JLabel lblAccessTokkenSecret = new JLabel("Access Tokken Secret");
		lblAccessTokkenSecret.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAccessTokkenSecret.setLabelFor(accessTokkenSecret);
		lblAccessTokkenSecret.setBounds(412, 104, 132, 14);
		frame.getContentPane().add(lblAccessTokkenSecret);

		JLabel lblDatabaseName = new JLabel("Database Name");
		lblDatabaseName.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDatabaseName.setLabelFor(dbName);
		lblDatabaseName.setBounds(152, 191, 95, 14);
		frame.getContentPane().add(lblDatabaseName);

		JLabel lblDatabaseUser = new JLabel("Database User");
		lblDatabaseUser.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDatabaseUser.setLabelFor(dbUser);
		lblDatabaseUser.setBounds(152, 219, 95, 14);
		frame.getContentPane().add(lblDatabaseUser);

		JLabel lblUserPassword = new JLabel("User Password");
		lblUserPassword.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblUserPassword.setLabelFor(dbUserPassword);
		lblUserPassword.setBounds(152, 249, 95, 14);
		frame.getContentPane().add(lblUserPassword);

		lat2 = new JTextField();
		lat2.setBounds(562, 73, 86, 20);
		frame.getContentPane().add(lat2);
		lat2.setColumns(10);
		lat2.setText("42.05");

		lon2 = new JTextField();
		lon2.setBounds(658, 73, 86, 20);
		frame.getContentPane().add(lon2);
		lon2.setColumns(10);
		lon2.setText("44.6");

		lat1 = new JTextField();
		lat1.setColumns(10);
		lat1.setBounds(562, 44, 86, 20);
		frame.getContentPane().add(lat1);
		lat1.setText("39.9");

		lon1 = new JTextField();
		lon1.setColumns(10);
		lon1.setBounds(658, 44, 86, 20);
		frame.getContentPane().add(lon1);
		lon1.setText("25.07");

		JLabel lblRightDownLatlon = new JLabel("Left down lat/lon");
		lblRightDownLatlon.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblRightDownLatlon.setLabelFor(lat1);
		lblRightDownLatlon.setBounds(754, 45, 108, 14);
		frame.getContentPane().add(lblRightDownLatlon);

		JLabel lblLeftUpLatlon = new JLabel("Right up lat/lon");
		lblLeftUpLatlon.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblLeftUpLatlon.setBounds(754, 76, 108, 14);
		frame.getContentPane().add(lblLeftUpLatlon);

		JButton updateSettings = new JButton("Update \r\nSettings");
		updateSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					SettingsWriter settingWriter = new SettingsWriter();
					settingWriter.writeSettings(consumerKey.getText(), consumerSecret.getText(), accessTokken.getText(),
							accessTokkenSecret.getText(), dbHostUrl.getText(), dbHostPort.getText(), dbName.getText(),
							dbUser.getText(), dbUserPassword.getText());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		updateSettings.setFont(new Font("Tahoma", Font.BOLD, 11));
		updateSettings.setVerticalTextPosition(SwingConstants.TOP);
		updateSettings.setVerticalAlignment(SwingConstants.TOP);
		updateSettings.setBounds(323, 203, 132, 23);
		frame.getContentPane().add(updateSettings);

		lblNewLabel = new JLabel("Project Name");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setBounds(754, 14, 95, 14);
		frame.getContentPane().add(lblNewLabel);

		btnRun = new JButton("Run!");
		btnRun.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnRun.addActionListener(new ActionListener() {
			private PrintStream out;

			public void actionPerformed(ActionEvent arg0) {

				CheckInitialDbState checkState = new CheckInitialDbState();
				if (!checkState.checkInitialState(dbHostUrl.getText(), dbHostPort.getText(), dbName.getText(),
						dbUser.getText(), dbUserPassword.getText())) {
					checkState.createInitialState(dbHostUrl.getText(), dbHostPort.getText(), dbName.getText(),
							dbUser.getText(), dbUserPassword.getText());
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
					// TODO Auto-generated catch block
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

							tweetUser.setText(status.getUser().getScreenName());
							tweet.setText(tweetText);
							Double tempLatDouble = status.getGeoLocation().getLatitude();
							Double tempLonDouble = status.getGeoLocation().getLongitude();
							tweetLat.setText(tempLatDouble.toString());
							tweetLon.setText(tempLonDouble.toString());

							String sql = "INSERT INTO twitter_stream"
									+ " (TWITTERUSER, TWEET, PROJECTNAME, LAT, LON) VALUES ('"
									+ status.getUser().getScreenName() + "' , '" + tweetText + "' , '"
									+ projectName.getText() + "' , " + status.getGeoLocation().getLatitude() + ", "
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

				double fLat1 = Double.parseDouble(lat1.getText()); // 39.9
				double fLon1 = Double.parseDouble(lon1.getText()); // 25.07
				double fLat2 = Double.parseDouble(lat2.getText()); // 42.05
				double fLon2 = Double.parseDouble(lon2.getText()); // 44.6

				double[][] bb = { { fLon1, fLat1 }, { fLon2, fLat2 } };

				FilterQuery fq = new FilterQuery();
				fq.locations(bb);

				twitterStream.filter(fq);
			}
		});

		projectName = new JTextField();
		projectName.setBounds(562, 11, 182, 20);
		frame.getContentPane().add(projectName);
		projectName.setColumns(10);
		lblNewLabel.setLabelFor(projectName);
		lblNewLabel.setLabelFor(projectName);
		btnRun.setBounds(754, 203, 91, 23);
		frame.getContentPane().add(btnRun);

		tweetUser = new JTextField();
		tweetUser.setBounds(10, 302, 86, 20);
		frame.getContentPane().add(tweetUser);
		tweetUser.setColumns(10);

		tweet = new JTextField();
		tweet.setBounds(106, 302, 542, 20);
		frame.getContentPane().add(tweet);
		tweet.setColumns(10);

		tweetLat = new JTextField();
		tweetLat.setBounds(658, 302, 86, 20);
		frame.getContentPane().add(tweetLat);
		tweetLat.setColumns(10);

		tweetLon = new JTextField();
		tweetLon.setBounds(754, 302, 86, 20);
		frame.getContentPane().add(tweetLon);
		tweetLon.setColumns(10);

		lblNewLabel_1 = new JLabel("User Name");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1.setBounds(10, 277, 86, 14);
		frame.getContentPane().add(lblNewLabel_1);

		lblNewLabel_2 = new JLabel("Tweet");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_2.setBounds(106, 277, 108, 14);
		frame.getContentPane().add(lblNewLabel_2);

		lblNewLabel_3 = new JLabel("Lat");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_3.setBounds(658, 277, 46, 14);
		frame.getContentPane().add(lblNewLabel_3);

		lblNewLabel_4 = new JLabel("Lon");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_4.setBounds(754, 277, 46, 14);
		frame.getContentPane().add(lblNewLabel_4);

		lblNewLabel_5 = new JLabel("DB Host URL");
		lblNewLabel_5.setLabelFor(dbHostUrl);
		lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_5.setBounds(152, 138, 95, 14);
		frame.getContentPane().add(lblNewLabel_5);

		lblNewLabel_6 = new JLabel("DB Host Port");
		lblNewLabel_6.setLabelFor(dbHostPort);
		lblNewLabel_6.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_6.setBounds(152, 162, 95, 14);
		frame.getContentPane().add(lblNewLabel_6);

		SettingsReader settingFileRead = new SettingsReader();
		settingFileRead.readSettings();
	}
}
