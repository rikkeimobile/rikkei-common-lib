package rikkei.android.http.lib.params;

import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by tuyenpx on 12/05/2016.
 * <p/>
 * This class support for Multi-Part HTTP request ,
 * this one already defined in https://www.ietf.org/rfc/rfc2388.txt .
 */
public class RkHttpMultipartParam extends RkHttpParam {
    private static final String MULTIPART_CONTENT_TYPE_REQUEST = "multipart/form-data; charset=%s; boundary=%s";

    private String boundary;
    private static final String CRLF = "\r\n";


    private static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String COLON_SPACE = ": ";
    private static final String FORM_DATA = "form-data; name=\"%s\"";
    private static final String FILENAME = "filename=\"%s\"";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String SEMICOLON_SPACE = "; ";
    private static final String CONTENT_TYPE_OCTET_STREAM = "application/octet-stream";
    private static final String HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
    private static final String BINARY = "binary";
    private static final String DEFAULT_BOUNDARY = "*******************";
    private static final String TWO_HYPHENS = "--";

    private static final String CONTENT_TYPE_TEXT = "text/plain";


    public RkHttpMultipartParam() {
        super();
        this.boundary = DEFAULT_BOUNDARY;
    }

    /**
     * @return return boundary of multi-part http request .
     */
    public String getBoundary() {
        return boundary;
    }

    /**
     * @param boundary : set boundary for multi-part http request .
     */

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }


    /**
     * @param connection : URLConnection object need to be set body .
     * @param listener   : listener use to update progress upload file .
     *                   This method use to set multi-params for HTTP request after added params and file need to be upload to server.
     * @filekey filekey : to upload file to server ,
     */

    public void setBodyToRequest(URLConnection connection, IRkUpLoadCallBack listener) throws IOException {
        PrintWriter writer;

        OutputStream out = connection.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(out, getParamsEncoding()), true);
        for (String key : requestParams.keySet()) {
            Object param = requestParams.get(key);
            if (param == null) {
                continue;
            }

            if (param instanceof File) {
                throw new RuntimeException("to upload file must use ArrayList<File>");
            }


            if (param instanceof ArrayList) {

                ArrayList<File> mListFile2Upload = (ArrayList<File>) param;

                for (File f : mListFile2Upload) {
                    if (!f.exists()) {
                        throw new IOException(String.format("File not found: %s", f.getAbsolutePath()));
                    }
                    if (f.isDirectory()) {
                        throw new IOException(String.format("File is a directory: %s", f.getAbsolutePath()));
                    }

                    String contenttype = getMimeType(f.getPath());
                    if (contenttype == null) {
                        contenttype = CONTENT_TYPE_OCTET_STREAM;
                    }
                    writer.append(TWO_HYPHENS + boundary);
                    writer.append(CRLF)
                            .append(String.format(HEADER_CONTENT_DISPOSITION + COLON_SPACE + FORM_DATA + SEMICOLON_SPACE + FILENAME, key, f.getName()))
                            .append(CRLF).append(HEADER_CONTENT_TYPE + COLON_SPACE + contenttype).append(CRLF)
                            .append(HEADER_CONTENT_TRANSFER_ENCODING + COLON_SPACE + BINARY).append(CRLF).append(CRLF).flush();


                    FileInputStream fis = new FileInputStream(f);
                    int transferredBytes = 0;
                    int totalSize = (int) f.length();
                    BufferedInputStream input = new BufferedInputStream(fis);
                    int bufferLength;

                    byte[] buffer = new byte[1024];
                    while ((bufferLength = input.read(buffer)) > 0) {
                        out.write(buffer, 0, bufferLength);
                        transferredBytes += bufferLength;
                        if (listener != null) {
                            listener.onProgress(transferredBytes, totalSize);
                        }
                    }
                    out.flush();
                    if (input != null)
                        input.close();
                    writer.append(CRLF).flush();
                }
            } else {
                writer.append(TWO_HYPHENS + boundary).append(CRLF).append(String.format(HEADER_CONTENT_DISPOSITION + COLON_SPACE + FORM_DATA, key)).append(CRLF)
                        .append(HEADER_CONTENT_TYPE + COLON_SPACE + CONTENT_TYPE_TEXT).append(CRLF).append(CRLF).append(String.valueOf(param)).append(CRLF).flush();

            }
        }
        // End of multipart/form-data.
        writer.append(TWO_HYPHENS + boundary + TWO_HYPHENS).append(CRLF).flush();
        out.close();
        writer.close();
    }

    @Override
    public String getBody() {
        throw new RuntimeException("this method can't call in RkHTTPMultipartParams");
    }

    @Override
    public String getContentType() {
        Log.e("tuyenpx", "contentype = " + String.format(MULTIPART_CONTENT_TYPE_REQUEST, getParamsEncoding(), getBoundary()));
        return String.format(MULTIPART_CONTENT_TYPE_REQUEST, getParamsEncoding(), boundary);
    }

    /**
     * This function already tested , and it worked well .
     */

    private String getMimeType(String pathFile) {
        String type = null;
        String extension = pathFile.substring(pathFile.lastIndexOf(".") + 1);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
