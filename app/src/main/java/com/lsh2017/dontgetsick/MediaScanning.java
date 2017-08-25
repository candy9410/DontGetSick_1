package com.lsh2017.dontgetsick;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

/**
 * Created by 이소희 on 2017-08-07.
 */

public class MediaScanning implements MediaScannerConnection.MediaScannerConnectionClient {

    MediaScannerConnection conn;
    File targetFile;

    public MediaScanning(Context context, File targetFile) {
        this.targetFile = targetFile;
        conn = new MediaScannerConnection(context, this);
        conn.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        conn.scanFile(targetFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String s, Uri uri) {
        conn.disconnect();
    }
}
