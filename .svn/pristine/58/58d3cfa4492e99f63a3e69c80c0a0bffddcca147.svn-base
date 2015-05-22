package com.negengec.geotweetdownloader;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class GetGeotagedTweets {

	public static void main(final String[] args) {
		final SettingsReader settingFileOpen = new SettingsReader();
		settingFileOpen.readSettings();
		final DbOperations dbOperations = new DbOperations();
		final String tableName = args[0];
		
		String createTableSql = "CREATE OR REPLACE FUNCTION public."+ tableName +"_coordinate_trigger_function()\n"
				+"  RETURNS trigger AS \n"
				+"$BODY$\n"
				+"BEGIN\n"
				+"  IF NEW.lat IS NOT NULL AND NEW.lon IS NOT NULL THEN\n"
				+"    NEW.geom := ST_SetSRID(ST_MakePoint(NEW.lon, NEW.lat),4326);\n"
				+"  END IF;\n"
				+"  RETURN NEW;\n"
				+"END$BODY$\n"
				+"  LANGUAGE plpgsql VOLATILE\n"
				+"  COST 100;\n\n"
				+"CREATE SEQUENCE public."+tableName+"_id_seq\n"
				+" INCREMENT 1\n"
				+" MINVALUE 1\n"
				+" MAXVALUE 9223372036854775807\n"
				+" START 244821\n"
				+" CACHE 1;\n\n"				
				+"CREATE TABLE public."+tableName
				+"\n(\n"
				+"  id integer NOT NULL DEFAULT nextval('"+tableName+"_id_seq'::regclass),\n"
				+ "  twitteruser text NOT NULL,\n"
				+ "  tweet text NOT NULL,\n"
				+ "  lat double precision,\n"
				+ "  lon double precision,\n"
				+ "  inserttime timestamp without time zone DEFAULT now(),\n"
				+ "  geom geometry(Point,4326),\n"
				+ "  CONSTRAINT "+ tableName +"_id_unique UNIQUE (id)\n"
				+ ")\n"
				+ "WITH (\n"
				+ "  OIDS=FALSE\n"
				+");\n\n"
				+"CREATE TRIGGER "+tableName+"_coordinate_trg  BEFORE INSERT\n"
				+"  ON public."+tableName
				+"\nFOR EACH ROW\n"
				+"EXECUTE PROCEDURE public."+tableName+"_coordinate_trigger_function();";
				
		// System.out.println(createTableSql);
		
		dbOperations.runOperations(createTableSql, settingFileOpen.getDbHostUrl(), settingFileOpen.getDbHostPort(),settingFileOpen.getDbName(),
				settingFileOpen.getDbUser(),
				settingFileOpen.getDbUserPassword(), "Create");
				

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey(settingFileOpen.getConsumerKey())
				.setOAuthConsumerSecret(settingFileOpen.getConsumerSecret())
				.setOAuthAccessToken(settingFileOpen.getAccessTokken())
				.setOAuthAccessTokenSecret(
						settingFileOpen.getAccessTokkenSecret());

		TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
				.getInstance();
		StatusListener listener = new StatusListener() {
			public void onStatus(Status status) {


				if (status.getGeoLocation() != null) {
					System.out.println("@" + status.getUser().getScreenName()
							+ " - " + status.getText() + "location:"
							+ status.getGeoLocation());

					String tweetText = status.getText();
					if (tweetText.contains("'")) {
						String tweetText1 = tweetText.replaceAll("'", "''");
						tweetText = tweetText1;
					}

					String sql = "INSERT INTO " + tableName
							+ " (TWITTERUSER, TWEET, LAT, LON) VALUES ('"
							+ status.getUser().getScreenName() + "' , '"
							+ tweetText + "' , "
							+ status.getGeoLocation().getLatitude() + ", "
							+ status.getGeoLocation().getLongitude() + ")";

					dbOperations.runOperations(sql, settingFileOpen.getDbHostUrl(), settingFileOpen.getDbHostPort(),settingFileOpen.getDbName(),
							settingFileOpen.getDbUser(),
							settingFileOpen.getDbUserPassword(), "Insert");
				}
			}

			public void onDeletionNotice(
					StatusDeletionNotice statusDeletionNotice) {
			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:"
						+ numberOfLimitedStatuses);
			}

			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId
						+ " upToStatusId:" + upToStatusId);
			}

			public void onStallWarning(StallWarning warning) {
				System.out.println("Got stall warning:" + warning);
			}

			public void onException(Exception ex) {
				ex.printStackTrace();
			}
		};

		twitterStream.addListener(listener);

		double lat1 = Double.parseDouble(args[1]); // 39.9
		double lon1 = Double.parseDouble(args[2]); // 25.07
		double lat2 = Double.parseDouble(args[3]); // 42.05
		double lon2 = Double.parseDouble(args[4]); // 44.6
		
		System.out.println(args[1]+" "+ args[2]+" "+args[3]+" "+args[4]);

		double[][] bb = { { lon1, lat1 }, { lon2, lat2 } };

		FilterQuery fq = new FilterQuery();
		fq.locations(bb);

		twitterStream.filter(fq);
	}
}
