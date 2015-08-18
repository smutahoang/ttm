package hoang.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataTool {

	private static DateFormat dateFormat;

	private static String setConnectionString() {
		// String connectionString =
		// "jdbc:mysql://10.0.106.72:3306?user=tahoang&password=LARCdata8102";
		String connectionString = "jdbc:mysql://10.0.106.62:3306?user=tanh&password=123456";
		return connectionString;
	}

	private static void getTweetInOneDay(Connection connection, Date date,
			String outputPath) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		dateFormat = new SimpleDateFormat("yyyy_MM");
		// String sqlQuery =
		// "SELECT status_id, user_id, published_time_GMT, content FROM kpopdb.tweet_"

		String sqlQuery = "SELECT status_id, user_id, published_time_GMT, content FROM us_political_tweet.tweet_"

				+ dateFormat.format(date);

		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sqlQuery = sqlQuery + " Where published_time_GMT>='"
				+ dateFormat.format(date) + "'";

		c.add(Calendar.DATE, 1);
		sqlQuery = sqlQuery + " and published_time_GMT < '"
				+ dateFormat.format(c.getTime()) + "'";
		System.out.println(sqlQuery);
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sqlQuery);
			try {
				dateFormat = new SimpleDateFormat("MM_dd");
				String fileName = outputPath + SystemTool.pathSeparator
						+ "tweet_" + dateFormat.format(date) + ".txt";
				File file = new File(fileName);
				if (!file.exists()) {
					file.createNewFile();
				}
				BufferedWriter bw = new BufferedWriter(new FileWriter(
						file.getAbsoluteFile()));

				while (resultSet.next()) {
					String tweetID = resultSet.getString("status_id");
					String userID = resultSet.getString("user_id");
					String time = resultSet.getString("published_time_GMT");
					String content = resultSet.getString("content");
					content = content.replace("\n", " ");

					bw.write(tweetID + "\t" + userID + "\t" + time + "\t"
							+ content + "\n");
				}
				bw.close();
			} catch (Exception e) {
				System.out
						.println("Error in writing out tweet topics to file!");
				e.printStackTrace();
				System.exit(0);
			}

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
	}

	public static void main(String[] argv) {
		System.out.println("START");
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String OutputPath = "F:" + SystemTool.pathSeparator + "Users"
				+ SystemTool.pathSeparator + "tahoang"
				+ SystemTool.pathSeparator + "Data" + SystemTool.pathSeparator
				// + "kpopdb";
				+ "us_political_tweet";
		try {

			Connection connection = DriverManager
					.getConnection(setConnectionString());

			String startDateString = "2012-08-15";
			String endDateString = "2012-09-15";

			try {

				Date startDate = dateFormat.parse(startDateString);
				Date endDate = dateFormat.parse(endDateString);
				Date date = startDate;
				while (date.compareTo(endDate) <= 0) {
					getTweetInOneDay(connection, date, OutputPath);
					Calendar c = Calendar.getInstance();
					c.setTime(date);
					c.add(Calendar.DATE, 1);
					date = c.getTime();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("DONE");
	}

}