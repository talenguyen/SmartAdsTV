package com.anvy.projects.dssviewer.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.os.Environment;

public class FileUtil {

	private FileUtil() {
	}

	public static void mkdirs(String dir) {
		final File fileDir = new File(dir);
		if (fileDir.exists()) {
			return;
		}
		fileDir.mkdirs();
	}

	public static boolean exists(String dir) {
		return new File(dir).exists();
	}

	public static boolean isExternalStorageWriteable() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return true;
		}
		return false;
	}

	public static boolean isExternalStorageAvailable() {
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment
				.getExternalStorageState())) {
			return true;
		}
		return false;
	}

	public static void unZip(String zipFile) throws ZipException, IOException {
		int BUFFER = 2048;
		File file = new File(zipFile);

		ZipFile zip = new ZipFile(file);
		String newPath = zipFile.substring(0, zipFile.length() - 4);

		new File(newPath).mkdir();
		Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

		// Process each entry
		while (zipFileEntries.hasMoreElements()) {
			// grab a zip file entry
			ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
			String currentEntry = entry.getName();
			File destFile = new File(newPath, currentEntry);
			// destFile = new File(newPath, destFile.getName());
			File destinationParent = destFile.getParentFile();

			// create the parent directory structure if needed
			destinationParent.mkdirs();

			if (!entry.isDirectory()) {
				BufferedInputStream is = new BufferedInputStream(
						zip.getInputStream(entry));
				int currentByte;
				// establish buffer for writing file
				byte data[] = new byte[BUFFER];

				// write the current file to disk
				FileOutputStream fos = new FileOutputStream(destFile);
				BufferedOutputStream dest = new BufferedOutputStream(fos,
						BUFFER);

				// read and write until last byte is encountered
				while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, currentByte);
				}
				dest.flush();
				dest.close();
				is.close();
			}

		}
	}

	public static boolean delete(String filePath) {
		final File file = new File(filePath);
		if (file.exists()) {
			return file.delete();
		}
		return true;
	}

	public static void write(InputStream inStream, String output)
			throws IOException {
		FileOutputStream outStream = new FileOutputStream(new File(output));
		byte[] buf = new byte[1024];
		int l;
		while ((l = inStream.read(buf)) >= 0) {
			outStream.write(buf, 0, l);
		}
		inStream.close();
		outStream.close();
	}

}
