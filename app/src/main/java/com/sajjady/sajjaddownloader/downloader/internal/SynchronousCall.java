package com.sajjady.sajjaddownloader.downloader.internal;

import com.sajjady.sajjaddownloader.downloader.Response;
import com.sajjady.sajjaddownloader.downloader.request.DownloadRequest;

public class SynchronousCall {

    public final DownloadRequest request;

    public SynchronousCall(DownloadRequest request) {
        this.request = request;
    }

    public Response execute() {
        DownloadTask downloadTask = DownloadTask.create(request);
        return downloadTask.run();
    }

}
