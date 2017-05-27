package studio.archangel.toolkit3.utils.networking;

/**
 * Collect and modify from http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0904/3416.html
 */
public abstract class AngelNetProgressCallBack extends AngelNetCallBack {
	public abstract void onProgress(long bytesRead, long contentLength, boolean done);
}