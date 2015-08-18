package hoang.tool;

import java.io.File;

public class SystemTool {
	//public static String pathSeparator = "\\";
	public static String pathSeparator = "/";

	public static void createFolder(String baseFolderName, String subFolderName) {
		try {
			String folderName = baseFolderName + pathSeparator + subFolderName;
			File newFolder = new File(folderName);
			// if the directory does not exist, create it
			if (!newFolder.exists()) {
				System.out.println("creating directory: " + folderName);
				boolean result = newFolder.mkdir();
				if (!result) {
					System.out.println("Error in creating the new folder");
					// System.out.println("DIR created");
					System.exit(0);
				}
			}
		} catch (Exception e) {
			System.out.println("Error in creating the new folder");
			e.printStackTrace();
			System.exit(0);
		}
	}
}
