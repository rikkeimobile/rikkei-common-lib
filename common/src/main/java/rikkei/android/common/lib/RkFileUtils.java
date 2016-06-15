/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package rikkei.android.common.lib;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Supporting file access
 * Created by NamHV on 5/4/2016.
 */
public class RkFileUtils {
    private static final String LOG_TAG = "RkFileUtils";
    private static final String NEW_LINE = "\n";
    // With file in assets

    /**
     * Get list file from assets folder by path
     */
    private static String[] getListFileFromAssets(@NonNull Context context, @Nullable String path) {
        try {
            String pathStr = TextUtils.isEmpty(path) ? "" : path;
            return context.getAssets().list(pathStr);
        } catch (IOException e) {
            RkLogger.e(LOG_TAG, String.format(Locale.US, "File: %s not found", path));
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return a String array of all the assets at the given path.
     *
     * @param path    A relative path within the assets, i.e., "docs/home.html".
     * @param context Application's context
     * @return String[] Array of strings, one for each asset.  These file
     * names are relative to 'path'.  You can open the file by
     * concatenating 'path' and a name in the returned string (via
     * File) and passing that to open().
     */
    public static String[] getListFileFromAssetsByPath(@NonNull Context context, @Nullable String path) {
        return getListFileFromAssets(context, path);
    }

    /**
     * Return a String array of all the assets
     *
     * @param context Application's context
     * @return String[] Array of strings, one for each asset.
     */
    public static String[] getListFileFromAsset(@NonNull Context context) {
        return getListFileFromAssets(context, null);
    }

    /**
     * Return a String is file content of give path
     *
     * @param context  Application's context
     * @param fileName File name (or path with filename)
     * @param encoding File encoding
     * @return String file content
     */
    public static String readAssetStringFile(@NonNull Context context, @NonNull String fileName, @Nullable String encoding) {
        if (TextUtils.isEmpty(fileName)) {
            throw new NullPointerException("fileName must not null");
        }
        InputStream inputStream = getInputStreamFromAsset(context, fileName);
        return readStringFromInputStream(inputStream, encoding);

    }

    /**
     * Return a Bitmap off image file with give @filePath
     *
     * @param context  Application's context
     * @param filePath File name (or path with filename)
     * @return Bitmap Image's bitmap
     */
    public static Bitmap getBitmapFromAsset(@NonNull Context context, @NonNull String filePath) {
        InputStream is = getInputStreamFromAsset(context, filePath);
        if (null != is) {
            return BitmapFactory.decodeStream(is);
        }
        return null;
    }

    /**
     * Return a InputStream is file input stream of give path
     *
     * @param context  Application's context
     * @param filePath File name (or path with filename)
     * @return InputStream file content in input stream type
     */
    public static InputStream getInputStreamFromAsset(@NonNull Context context, @NonNull String filePath) {
        AssetManager assetManager = context.getAssets();
        try {
            return assetManager.open(filePath);
        } catch (IOException e) {
            RkLogger.e(LOG_TAG, String.format(Locale.US, "File: %s not found", filePath));
            e.printStackTrace();
        }
        return null;
    }

    // With file in sdCard

    /**
     * Get list file from sdcard folder by path
     */
    public static File[] getListFileFromSdCard(String path) {
        File fileDir;
        if (TextUtils.isEmpty(path)) {
            fileDir = Environment.getExternalStorageDirectory();
        } else {
            File sdCardFile = Environment.getExternalStorageDirectory();
            //Get the text file
            fileDir = new File(sdCardFile, path);
        }
        return fileDir.listFiles();
    }

    /**
     * Read file in sdcard as string.
     *
     * @param filepath File path. Example: /Files/hello.txt . You don't have to add sdcard link.
     * @return File content in string
     */
    public static String readStringFromSdCard(@NonNull String filepath) {
        InputStream targetStream = getInputStreamFromFileInSdCard(filepath);
        return readStringFromInputStream(targetStream, null);
    }

    /**
     * Read file in sdcard as string.
     *
     * @param filepath File path. Example: /Files/hello.txt . You don't have to add sdcard link.
     * @return File content in string
     */
    public static String readStringFromSdCard(@NonNull File filepath) {
        InputStream targetStream = getInputStreamFromFileInSdCard(filepath);
        return readStringFromInputStream(targetStream, null);
    }

    /**
     * Read bitmap from sdcard by give @filePath
     * @param filePath Path to file
     * @return File's bitmap or null
     */
    public static Bitmap getBitmapFromSdCard(@NonNull String filePath) {
        InputStream is = getInputStreamFromFileInSdCard(filePath);
        if (null != is) {
            return BitmapFactory.decodeStream(is);
        }
        return null;
    }

    /**
     * Read bitmap from sdcard by give @file
     * @param file File to retrieve bitmap
     * @return File's bitmap or null
     */
    public static Bitmap getBitmapFromSdCard(@NonNull File file) {
        InputStream is = getInputStreamFromFileInSdCard(file);
        if (null != is) {
            return BitmapFactory.decodeStream(is);
        }
        return null;
    }

    /**
     * Return a InputStream is file input stream of give path
     *
     * @param filePath File path. Example: /Files/hello.txt . You don't have to add sdcard link.
     * @return InputStream file content in input stream type
     */
    public static InputStream getInputStreamFromFileInSdCard(@NonNull String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("filePath must not null");
        }
        File sdCardFile = Environment.getExternalStorageDirectory();
        //Get the text file
        File file = new File(sdCardFile, filePath);
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            RkLogger.e(LOG_TAG, String.format(Locale.US, "File: %s not found", filePath));
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return a InputStream is file input stream of give path
     *
     * @param filePath File path. Example: /Files/hello.txt . You don't have to add sdcard link.
     * @return InputStream file content in input stream type
     */
    public static InputStream getInputStreamFromFileInSdCard(@NonNull File filePath) {
        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            RkLogger.e(LOG_TAG, String.format(Locale.US, "File: %s not found", filePath.getAbsolutePath()));
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create a new file in SdCard with give file @fileName
     *
     * @param fileName New file's name
     * @throws IllegalArgumentException When give @fileNam is null or Empty
     */
    public static boolean createFileInSdCard(@NonNull String fileName, byte[] data) {
        if (TextUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("fileName must not null");
        }
        File file = getFullFileFromPath(fileName);
        File parentDir = getDirectorFromFilePath(file);
        return (parentDir.exists() || createDirectorInSdCard(parentDir)) && createFileInSdCard(file, data);
    }

    /**
     * Create a new file in SdCard with give file @fileName
     *
     * @param fileName New file's file
     * @throws IllegalArgumentException When give @fileNam is null or Empty
     */
    public static boolean createFileInSdCard(@NonNull File fileName, byte[] data) {
        File parentDir = getDirectorFromFilePath(fileName);
        if (parentDir.exists() || (!parentDir.exists() && parentDir.mkdir())) {
            try {
                if (fileName.createNewFile()) {
                    FileOutputStream fileOS = new FileOutputStream(fileName);
                    fileOS.write(data);
                    fileOS.flush();
                    fileOS.close();
                    return true;
                } else {
                    RkLogger.e(LOG_TAG, "Create file failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Create a new file in SdCard with give file @folderName
     *
     * @param folderName New directory's name
     * @throws IllegalArgumentException When give @folderName is null or Empty
     */
    public static boolean createDirectorInSdCard(@NonNull String folderName) {
        if (TextUtils.isEmpty(folderName)) {
            throw new IllegalArgumentException("folderName must not null");
        }
        File dirFile = getFullFileFromPath(folderName);
        return createDirectorInSdCard(dirFile);
    }

    /**
     * Create a new file in SdCard with give file @folderName
     *
     * @param folderName New directory's file
     * @throws IllegalArgumentException When give @folderName is null or Empty
     */
    public static boolean createDirectorInSdCard(@NonNull File folderName) {
        if (!folderName.exists()) {
            return folderName.mkdir();
        } else {
            RkLogger.i(LOG_TAG, "This folder already exist");
            return true;
        }
    }


    /**
     * Copy file from @srcPath to @desPath
     *
     * @param srsPath Source file path
     * @param desPath Destination file path
     * @return True if success, false if else
     */
    public static boolean copyFileInSdCard(@NonNull String srsPath, @NonNull String desPath) {
        if (TextUtils.isEmpty(srsPath) || TextUtils.isEmpty(desPath)) {
            throw new IllegalArgumentException("File path must not null");
        }
        File src = getFullFileFromPath(srsPath);
        File des = getFullFileFromPath(desPath);
        return copyFileInSdCard(src, des);
    }

    /**
     * Copy file from @srsFile to @desFile
     *
     * @param srsFile Source file file
     * @param desFile Destination file file
     * @return True if success, false if else
     */
    public static boolean copyFileInSdCard(@NonNull File srsFile, @NonNull File desFile) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(srsFile);
            out = new FileOutputStream(desFile);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Common
    /*
     * Read input stream as string
     */
    private static String readStringFromInputStream(InputStream inputStream, @Nullable String encoding) {
        if (null == inputStream) {
            return null;
        }
        String encode = TextUtils.isEmpty(encoding) ? "UTF-8" : encoding;
        StringBuilder buf = new StringBuilder();
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(inputStream, encode));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(NEW_LINE + str);
            }
            in.close();
            return buf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String normalizationFilePath(@NonNull String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            // Remove space at begin and last
            filePath = filePath.trim();

            // Check file path has separator at begin
            while (filePath.charAt(0) == File.separatorChar) {
                filePath = filePath.substring(1, filePath.length());
            }
        }

        return filePath;
    }

    private static String getFullLink(@NonNull String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + normalizationFilePath(path);
    }

    private static File getFullFileFromPath(@NonNull String path) {
        String fullPath = getFullLink(path);
        if (TextUtils.isEmpty(fullPath)) {
            throw new IllegalArgumentException("Invalid path");
        }
        return new File(fullPath);
    }

    private static File getDirectorFromFilePath(@NonNull File filePath) {
        return filePath.getParentFile();
    }

}
