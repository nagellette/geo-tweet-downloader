package com.negengec.geotweetdownloader;

public class CheckInitialDbState {

	public boolean checkInitialState(String dbHostUrl, String dbHostPort,
			String dbName, String dbUser, String dbUserPassword) {

		final SettingsReader settingFileOpen = new SettingsReader();
		settingFileOpen.readSettings();
		final DbOperations dbOperations = new DbOperations();

		String queryTablesSql = "select count(*) from pg_tables where schemaname = 'public' and tablename = 'twitter_stream' group by tablename";

		if(!dbOperations.runOperations(queryTablesSql, dbHostUrl, dbHostPort,
				dbName, dbUser, dbUserPassword, "Select")){
			return false;
		} else {
			return true;
		}
	}

	public void createInitialState(String dbHostUrl, String dbHostPort,
			String dbName, String dbUser, String dbUserPassword) {

		final SettingsReader settingFileOpen = new SettingsReader();
		settingFileOpen.readSettings();
		final DbOperations dbOperations = new DbOperations();

		String createTableSql = "CREATE OR REPLACE FUNCTION public.twitter_stream_coordinate_trigger_function()\n"
				+ "  RETURNS trigger AS \n"
				+ "$BODY$\n"
				+ "BEGIN\n"
				+ "  IF NEW.lat IS NOT NULL AND NEW.lon IS NOT NULL THEN\n"
				+ "    NEW.geom := ST_SetSRID(ST_MakePoint(NEW.lon, NEW.lat),4326);\n"
				+ "  END IF;\n"
				+ "  RETURN NEW;\n"
				+ "END$BODY$\n"
				+ "  LANGUAGE plpgsql VOLATILE\n"
				+ "  COST 100;\n\n"
				+ "CREATE SEQUENCE public.twitter_stream_id_seq\n"
				+ " INCREMENT 1\n"
				+ " MINVALUE 1\n"
				+ " MAXVALUE 9223372036854775807\n"
				+ " START 244821\n"
				+ " CACHE 1;\n\n"
				+ "CREATE TABLE public.twitter_stream"
				+ "\n(\n"
				+ "  id integer NOT NULL DEFAULT nextval('"
				+ "twitter_stream"
				+ "_id_seq'::regclass),\n"
				+ "  twitteruser text NOT NULL,\n"
				+ "  tweet text NOT NULL,\n"
				+ "  projectname text NOT NULL,\n"
				+ "  lat double precision,\n"
				+ "  lon double precision,\n"
				+ "  inserttime timestamp without time zone DEFAULT now(),\n"
				+ "  geom geometry(Point,4326),\n"
				+ "  CONSTRAINT twitter_stream_id_unique UNIQUE (id)\n"
				+ ")\n"
				+ "WITH (\n"
				+ "  OIDS=FALSE\n"
				+ ");\n\n"
				+ "CREATE TRIGGER twitter_stream_coordinate_trg  BEFORE INSERT\n"
				+ "  ON public.twitter_stream"
				+ "\nFOR EACH ROW\n"
				+ "EXECUTE PROCEDURE public.twitter_stream_coordinate_trigger_function();";

		dbOperations.runOperations(createTableSql, dbHostUrl, dbHostPort,
				dbName, dbUser, dbUserPassword, "Create");

	}
}
