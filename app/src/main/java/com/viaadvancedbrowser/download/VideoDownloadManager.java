package com.viaadvancedbrowser.download;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import java.util.*;

public class VideoDownloadManager {
    
    private Context context;
    private DownloadManager downloadManager;
    private Set<String> supportedFormats;
    
    public VideoDownloadManager(Context context) {
        this.context = context;
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        this.supportedFormats = new HashSet<>(Arrays.asList(
            "mp4", "webm", "mkv", "avi", "mov", "flv", "3gp"
        ));
    }
    
    public void detectAndDownload(String url) {
        if (isVideoUrl(url)) {
            downloadVideo(url, generateFilename(url));
        }
    }
    
    private boolean isVideoUrl(String url) {
        if (url == null) return false;
        
        String path = Uri.parse(url).getPath();
        if (path != null) {
            String extension = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
            return supportedFormats.contains(extension);
        }
        
        return url.contains("video/") || url.contains(".m3u8") || url.contains(".mpd");
    }
    
    public void downloadVideo(String videoUrl, String filename) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videoUrl));
        
        request.setTitle(filename);
        request.setDescription("Downloading video");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        
        downloadManager.enqueue(request);
    }
    
    private String generateFilename(String url) {
        return "video_" + System.currentTimeMillis() + ".mp4";
    }
}