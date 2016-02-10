package com.zestedesavoir.zestwriter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	static List<String> fileList;

	public static void zipContent(String srcPathFolder, String destZipPathFile) {
		fileList = new ArrayList<String>();
		generateFileList(srcPathFolder, new File(srcPathFolder));
		zipIt(srcPathFolder, destZipPathFile);
	}

	/**
	 * Zip it
	 *
	 * @param zipFile
	 *            output ZIP file location
	 */
	public static void zipIt(String folder, String zipFile) {

		byte[] buffer = new byte[1024];

		try {

			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);


			for (String file : fileList) {

				ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);

				FileInputStream in = new FileInputStream(folder + File.separator + file);

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
			}

			zos.closeEntry();
			// remember close it
			zos.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Traverse a directory and get all files, and add the file into fileList
	 *
	 * @param node
	 *            file or directory
	 */
	public static void generateFileList(String srcFolder, File node) {

		// add file only
		if (node.isFile()) {
			fileList.add(generateZipEntry(srcFolder, node.getAbsoluteFile().toString()));
		}

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				generateFileList(srcFolder, new File(node, filename));
			}
		}

	}

	/**
	 * Format the file path for zip
	 *
	 * @param file
	 *            file path
	 * @return Formatted file path
	 */
	private static String generateZipEntry(String srcFolder, String file) {
		return file.substring(srcFolder.length() + 1, file.length());
	}
}
