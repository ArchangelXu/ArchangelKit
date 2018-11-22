package studio.archangel.toolkit3.utils.networking;

import android.net.Uri;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.badoo.mobile.util.WeakHandler;
import com.facebook.common.internal.Objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import studio.archangel.toolkit3.AngelApplication;
import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.utils.CommonUtil;
import studio.archangel.toolkit3.utils.Logger;

/**
 * Created by Administrator on 2015/10/14.
 */
public class AngelNet implements Executor {

	public enum Method {
		GET, POST, PUT, DELETE, DOWNLOAD
	}

	public static final int LOW_LEVEL_STATUS_CODE = -1;
	public static final String LOW_LEVEL_ERROR_CODE = "network_layer_error";
	public static final String META_USE_RAW = "[meta]use_raw";
	public static final String FILE_PREFIX = "[file]";
	public static final String URI_PREFIX = "[uri]";
	public static final String LIST_PREFIX = "[list]";
	public static final int time_out = 30;//in second
	OkHttpClient client;
	AngelNetLogger logger;
	AngelNetConfig config;

	public synchronized OkHttpClient getClient() {
		if (client == null) {
			client = new OkHttpClient.Builder()
					.addInterceptor(new GzipRequestInterceptor())

//					.connectTimeout(time_out, TimeUnit.SECONDS)
//					.writeTimeout(10, TimeUnit.SECONDS)
//					.readTimeout(30, TimeUnit.SECONDS)
//					.cache(cache)
//					.addInterceptor(interceptor)
					.build();
		}
		return client;
	}

	public synchronized OkHttpClient getUploader() {
		return new OkHttpClient.Builder()
				.addInterceptor(new GzipRequestInterceptor())
				.connectTimeout(0, TimeUnit.SECONDS)
				.writeTimeout(0, TimeUnit.SECONDS)
				.readTimeout(0, TimeUnit.SECONDS)
				.build();
	}

	public synchronized OkHttpClient getDownloader(final AngelNetDownloadCallBack callBack) {
		return new OkHttpClient.Builder()
				.addNetworkInterceptor(new Interceptor() {
					@Override
					public Response intercept(Chain chain) throws IOException {
						Response originalResponse = chain.proceed(chain.request());
						return originalResponse.newBuilder()
								.body(new AngelNetProgressResponseBody(originalResponse.body(), callBack, AngelNet.this))
								.build();
					}
				})
				.build();
	}

	public synchronized OkHttpClient getContinuableDownloader(long downloaded_size, final AngelNetDownloadCallBack callBack) {
		return new OkHttpClient.Builder()
				.addNetworkInterceptor(new Interceptor() {
					@Override
					public Response intercept(Chain chain) throws IOException {
						Response originalResponse = chain.proceed(chain.request());
						return originalResponse.newBuilder()
								.body(new AngelNetProgressResponseBody(originalResponse.body(), downloaded_size, callBack, AngelNet.this))
								.build();
					}
				})
				.build();
	}

	WeakHandler handler = null;

	@Override
	public void execute(@NonNull Runnable r) {
		handler.post(r);
	}

	public void execute(@NonNull Runnable r, long delay) {
		handler.postDelayed(r, delay);
	}

	public AngelNet(AngelNetConfig c) {
//		client = new OkHttpClient();
		client = getClient();
		logger = new AngelNetLogger();
		config = c;
		handler = new WeakHandler(Looper.getMainLooper());
	}

	public AngelNetConfig getConfig() {
		return config;
	}

	public void setLogger(AngelNetLogger logger) {
		this.logger = logger;
	}

	public String doGetUrl(String url) {
		return config.getServerRoot() + url;
	}

	public Call doGet(String url, HashMap<String, String> headers, final AngelNetCallBack callback) {
		return doGet(url, null, headers, callback);
	}

	public Call doPost(String url, HashMap<String, String> headers, final AngelNetCallBack callback) {
		return doPost(url, null, headers, callback);
	}

	public Call doGet(String url, String key, Object value, HashMap<String, String> headers, final AngelNetCallBack callback) {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put(key, value);
		return doGet(url, parameters, headers, callback);
	}

	public Call doPost(String url, String key, Object value, HashMap<String, String> headers, final AngelNetCallBack callback) {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put(key, value);
		return doPost(url, parameters, headers, callback);
	}


	public Call doGet(String url, HashMap<String, Object> parameters, HashMap<String, String> headers, final AngelNetCallBack callback) {
		return call(Method.GET, url, parameters, headers, callback);
	}

	public Call doPost(String url, HashMap<String, Object> parameters, HashMap<String, String> headers, final AngelNetCallBack callback) {
		return call(Method.POST, url, parameters, headers, callback);
	}

	public Call doPut(String url, HashMap<String, Object> parameters, HashMap<String, String> headers, final AngelNetCallBack callback) {
		return call(Method.PUT, url, parameters, headers, callback);
	}

	public Call doDelete(String url, HashMap<String, Object> parameters, HashMap<String, String> headers, final AngelNetCallBack callback) {
		return call(Method.DELETE, url, parameters, headers, callback);
	}

	public String doGetSync(String url) throws IOException {
		return doGetSync(url, null, null);
	}

	public String doPostSync(String url) throws IOException {
		return doPostSync(url, null, null);
	}

	public String doGetSync(String url, String key, Object value, HashMap<String, String> headers) throws IOException {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put(key, value);
		return doGetSync(url, parameters, headers);
	}

	public String doPostSync(String url, String key, Object value, HashMap<String, String> headers) throws IOException {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put(key, value);
		return doPostSync(url, parameters, headers);
	}

	public String doGetSync(String url, HashMap<String, Object> parameters, HashMap<String, String> headers) throws IOException {
		return callSync(Method.GET, url, parameters, headers);
	}

	public String doPostSync(String url, HashMap<String, Object> parameters, HashMap<String, String> headers) throws IOException {
		return callSync(Method.POST, url, parameters, headers);
	}

	public Call doDownloadFile(String url, File file, AngelNetDownloadCallBack callBack) {
		return doDownloadFile(url, "", file, callBack);
	}

	public Call doDownloadContinuableFile(String url, File file, String etag, AngelNetDownloadCallBack callBack) {
		return doDownloadContinuableFile(url, "", file, etag, callBack);
	}

	public Call doDownloadFile(String url, String token, File file, AngelNetDownloadCallBack callBack) {
		return downloadFile(url, token, file, callBack);
	}

	public Call doDownloadContinuableFile(String url, String token, File file, String etag, AngelNetDownloadCallBack callBack) {
		return downloadContinuableFile(url, token, file, etag, callBack);
	}

	public boolean doDownloadFileSync(String url, File file) throws IOException {
		return downloadFileSync(url, "", file);
	}

	private Request buildRequest(final Method method, final String url, final HashMap<String, Object> parameters, HashMap<String, String> headers, final AngelNetCallBack callback) {
		Request request = null;
		switch (method) {
			case GET: {
				HttpUrl.Builder url_builder = HttpUrl.parse(url).newBuilder();
				if (parameters != null) {
//					for (Map.Entry<String, Object> en : parameters.entrySet()) {
//						Object value = en.getValue();
//						if (value instanceof File) {
//							throw new IllegalArgumentException("You can not use GET method to upload file.");
//						}
//						url_builder.addQueryParameter(en.getKey(), value.toString());
//					}
					addParametersToBuilder(parameters, url_builder);
				}
				Request.Builder request_builder = new Request.Builder();
				request_builder = request_builder.url(url_builder.build().url());
				if (headers != null) {
					Set<Map.Entry<String, String>> set = headers.entrySet();
					for (Map.Entry<String, String> en : set) {
						request_builder = request_builder.addHeader(en.getKey(), en.getValue());
					}
				}
//				if (AngelApplication.isDebug()) {
//					request_builder = request_builder.cacheControl(CacheControl.FORCE_NETWORK);
//					Logger.out("FORCE_NETWORK");
//				}
				request = request_builder.build();
				break;
			}
			case DELETE: {
				HttpUrl.Builder url_builder = HttpUrl.parse(url).newBuilder();
				if (parameters != null) {
					for (Map.Entry<String, Object> en : parameters.entrySet()) {
						Object value = en.getValue();
						if (value instanceof File) {
							throw new IllegalArgumentException("You can not use GET method to upload file.");
						}
						url_builder.addQueryParameter(en.getKey(), value.toString());
					}
				}
				Request.Builder request_builder = new Request.Builder();
				request_builder = request_builder.url(url_builder.build().url()).delete();
				if (headers != null) {
					Set<Map.Entry<String, String>> set = headers.entrySet();
					for (Map.Entry<String, String> en : set) {
						request_builder = request_builder.addHeader(en.getKey(), en.getValue());
					}
				}
//				if (AngelApplication.isDebug()) {
//					request_builder = request_builder.cacheControl(CacheControl.FORCE_NETWORK);
//					Logger.out("FORCE_NETWORK");
//				}
				request = request_builder.build();
				break;
			}
			case POST: {
				MultipartBody.Builder multi_builder = new MultipartBody.Builder();
				RequestBody body = null;
				if (parameters != null) {
					boolean use_raw_format = false;
					if (parameters.containsKey(META_USE_RAW)) {
						try {
							use_raw_format = (boolean) parameters.get(META_USE_RAW);
						} catch (Exception e) {
							Logger.err(e);//e.printStackTrace();
						}
						parameters.remove(META_USE_RAW);
					}
					if (use_raw_format) {
						JSONObject jo = new JSONObject();
						Collection<Object> values = parameters.values();
						File file = null;
						Uri uri = null;
						for (Object value : values) {
							if (value instanceof File) {
								file = (File) value;
								break;
							} else if (value instanceof Uri) {
								uri = (Uri) value;
								break;
							}
						}
						if (file != null || uri != null) {
							if (file != null) {
								RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data; charset=utf-8"), file);
								body = new AngelNetProgressRequestBody(requestBody, callback);
							} else if (uri != null) {
								File f = new File(uri.getPath());
								RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data; charset=utf-8"), f);
								body = new AngelNetProgressRequestBody(requestBody, callback);
							}
						} else {
							for (Map.Entry<String, Object> en : parameters.entrySet()) {
								Object value = en.getValue();
								if (value instanceof File) {
									Logger.out("skipped file parameter.");
								} else {
									try {
										jo.put(en.getKey(), en.getValue());
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}
							body = RequestBody.create(MediaType.parse("text; charset=utf-8"), jo.toString());
						}
					} else {
//						boolean has_file = false;
						addParametersToBuilder(parameters, callback, multi_builder);
						multi_builder.setType(MediaType.parse("multipart/form-data; charset=utf-8"));
						body = multi_builder.build();
					}
				} else {
					body = RequestBody.create(MediaType.parse("text; charset=utf-8"), "");
				}
				Request.Builder request_builder = new Request.Builder();
				request_builder = request_builder.url(url).post(body);
				if (headers != null) {
					Set<Map.Entry<String, String>> set = headers.entrySet();
					for (Map.Entry<String, String> en : set) {
						request_builder = request_builder.addHeader(en.getKey(), en.getValue());
					}
				}
				request = request_builder.build();
				break;
			}
			case PUT: {
				MultipartBody.Builder multi_builder = new MultipartBody.Builder();
				RequestBody body = null;
				if (parameters != null) {
					boolean use_raw_format = false;
					if (parameters.containsKey(META_USE_RAW)) {
						try {
							use_raw_format = (boolean) parameters.get(META_USE_RAW);
						} catch (Exception e) {
							Logger.err(e);//e.printStackTrace();
						}
						parameters.remove(META_USE_RAW);
					}
					if (use_raw_format) {
						JSONObject jo = new JSONObject();
						Collection<Object> values = parameters.values();
						File file = null;
						Uri uri = null;
						for (Object value : values) {
							if (value instanceof File) {
								file = (File) value;
								break;
							} else if (value instanceof Uri) {
								uri = (Uri) value;
								break;
							}
						}
						if (file != null || uri != null) {
							if (file != null) {
								RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data; charset=utf-8"), file);
								body = new AngelNetProgressRequestBody(requestBody, callback);
							} else if (uri != null) {
								File f = new File(uri.getPath());
								RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data; charset=utf-8"), f);
								body = new AngelNetProgressRequestBody(requestBody, callback);
							}
						} else {
							for (Map.Entry<String, Object> en : parameters.entrySet()) {
								Object value = en.getValue();
								if (value instanceof File) {
									Logger.out("skipped file parameter.");
								} else {
									try {
										jo.put(en.getKey(), en.getValue());
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}
							body = RequestBody.create(MediaType.parse("text; charset=utf-8"), jo.toString());
						}
					} else {
//						boolean has_file = false;
						addParametersToBuilder(parameters, callback, multi_builder);
						multi_builder.setType(MediaType.parse("multipart/form-data; charset=utf-8"));
						body = multi_builder.build();
					}
				} else {
					body = RequestBody.create(MediaType.parse("text; charset=utf-8"), "");
				}
				Request.Builder request_builder = new Request.Builder();
				request_builder = request_builder.url(url).put(body);
				if (headers != null) {
					Set<Map.Entry<String, String>> set = headers.entrySet();
					for (Map.Entry<String, String> en : set) {
						request_builder = request_builder.addHeader(en.getKey(), en.getValue());
					}
				}
				request = request_builder.build();
				break;
			}
		}
		return request;
	}

	private void addParametersToBuilder(HashMap<String, Object> parameters, AngelNetCallBack callback, MultipartBody.Builder multi_builder) {
//		boolean has_file;
		for (Map.Entry<String, Object> en : parameters.entrySet()) {
			Object value = en.getValue();
			if (value instanceof File) {
//				has_file = true;
				RequestBody requestBody = RequestBody.create(MediaType.parse("text/x-markdown; charset=utf-8"), (File) value);
				AngelNetProgressRequestBody body = new AngelNetProgressRequestBody(requestBody, callback);
				multi_builder.addFormDataPart(en.getKey(), ((File) value).getName(), body);
			} else if (value instanceof Uri) {
//				has_file = true;
				RequestBody requestBody = RequestBody.create(MediaType.parse("text/x-markdown; charset=utf-8"), new File(((Uri) value).getPath()));
				AngelNetProgressRequestBody body = new AngelNetProgressRequestBody(requestBody, callback);
				multi_builder.addFormDataPart(en.getKey(), ((Uri) value).getLastPathSegment(), body);
			} else if (value instanceof List) {
				List list = (List) value;
				String key = en.getKey();
				if (!key.endsWith("[]")) {
					key = key + "[]";
//				} else if (list.size() == 1 && key.endsWith("[]")) {
//					key = key.substring(0, key.lastIndexOf("[]"));
				}
				for (Object o : list) {
					multi_builder.addFormDataPart(key, o.toString());
				}
			} else {
				multi_builder.addFormDataPart(en.getKey(), value.toString());
			}
		}
	}

	private void addParametersToBuilder(HashMap<String, Object> parameters, HttpUrl.Builder multi_builder) {
		for (Map.Entry<String, Object> en : parameters.entrySet()) {
			Object value = en.getValue();
			if (value instanceof File) {
				throw new IllegalArgumentException("You can not use GET method to upload file.");
			} else if (value instanceof List) {
				List list = (List) value;
				String key = en.getKey();
				if (!key.endsWith("[]")) {
					key = key + "[]";
				}
				for (Object o : list) {
					multi_builder.addQueryParameter(key, o.toString());
				}
			} else {
				multi_builder.addQueryParameter(en.getKey(), value.toString());
			}
		}
	}

	private boolean downloadFileSync(final String url, String token, final File file) throws IOException {
		Request request = new Request.Builder().url(url)
				.addHeader("X-CSRFToken", token)
				.addHeader("Content-Type", "application/json")
				.build();
		Response response = getDownloader(null).newCall(request).execute();
		if (!response.isSuccessful()) {
			return false;
		}
		try (BufferedSink sink = Okio.buffer(Okio.sink(file))) {
			sink.writeAll(response.body().source());
		} catch (Exception e) {
			Logger.err(e);
			return false;
		}
		return true;
	}

	private Call downloadFile(final String url, String token, final File file, final AngelNetDownloadCallBack callback) {
		if (callback != null) {
			execute(new Runnable() {
				@Override
				public void run() {
					callback.onStart();
				}
			});
		}
		if (!CommonUtil.isNetWorkAvailable()) {
			if (callback != null) {
				execute(new Runnable() {
					@Override
					public void run() {
						callback.onFailure(AngelApplication.getInstance().getString(R.string.error_no_network));
					}
				});
			}
			return null;
		}
		Request request = null;
		try {
			request = new Request.Builder().url(url)
					.addHeader("X-CSRFToken", token)
					.addHeader("Content-Type", "application/json")
					.build();
		} catch (Exception e) {
			Logger.err(e);//e.printStackTrace();
			if (callback != null) {
				execute(new Runnable() {
					@Override
					public void run() {
						callback.onFailure(AngelApplication.getInstance().getString(R.string.error_other), e.toString());
					}
				});
			}
			return null;
		}
		final Call call = getDownloader(callback).newCall(request);
		if (callback != null) {
			execute(new Runnable() {
				@Override
				public void run() {
					callback.onPrepared(call);
				}
			});
		}
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				logger.log(url, Method.DOWNLOAD, null, null);
				e.printStackTrace();
				if (callback != null) {
					execute(new Runnable() {
						@Override
						public void run() {
							callback.onFailure(config.getErrorMsg(), e.toString());
						}
					});
				}
				if (e.toString().contains("ENETUNREACH") || e.toString().contains("ECONNREFUSED")) {
					Logger.out("okhttp client reset!");
					client = null;
				}
			}

			@Override
			public void onResponse(final Call call, Response response) throws IOException {
				logger.log(url, Method.DOWNLOAD, null, null);
				BufferedSink sink = Okio.buffer(Okio.sink(file));
				try {
					sink.writeAll(response.body().source());
				} catch (final IOException e) {
					if (call.isCanceled()) {
						if (callback != null) {
							execute(new Runnable() {
								@Override
								public void run() {
									callback.onCancel();
								}
							});
						}
					} else {
						e.printStackTrace();
						if (callback != null) {
							execute(new Runnable() {
								@Override
								public void run() {
									callback.onFailure(config.getErrorMsg(), e.toString());
								}
							});
						}
					}
					return;
				} finally {
					sink.close();
				}
				if (callback != null) {
					execute(new Runnable() {
						@Override
						public void run() {
							callback.onSuccess(file);
						}
					});
				}
			}
		});
		return call;
	}

	private Call downloadContinuableFile(final String url, String token, final File file, String etag, final AngelNetDownloadCallBack callback) {
		if (callback != null) {
			execute(new Runnable() {
				@Override
				public void run() {
					callback.onStart();
				}
			});
		}
		if (!CommonUtil.isNetWorkAvailable()) {
			if (callback != null) {
				execute(new Runnable() {
					@Override
					public void run() {
						callback.onFailure(AngelApplication.getInstance().getString(R.string.error_no_network));
					}
				});
			}
			return null;
		}
		Request request = null;
		long length = file.length();
		HashMap<String, String> headers = new HashMap<>();
		try {
			Request.Builder builder = new Request.Builder().url(url)
					.addHeader("X-CSRFToken", token)
					.addHeader("Content-Type", "application/json");
			if (!file.exists() || length == 0) {//init download
				builder.addHeader("Range", "bytes=0-");
			} else {
				if (!CommonUtil.isEmptyString(etag)) {//has etag
					builder.addHeader("Range", "bytes=" + length + "-")
							.addHeader("if-Range", etag);
					Logger.out("onStart " + length);
				} else {//no etag, resolve as init download
					file.delete();
					builder.addHeader("Range", "bytes=0-");
				}
			}
			request = builder.build();
			Set<String> names = request.headers().names();
			for (String name : names) {
				headers.put(name, request.header(name));
			}
		} catch (Exception e) {
			Logger.err(e);//e.printStackTrace();
			if (callback != null) {
				execute(new Runnable() {
					@Override
					public void run() {
						callback.onFailure(AngelApplication.getInstance().getString(R.string.error_other), e.toString());
					}
				});
			}
			return null;
		}
		final Call call = getContinuableDownloader(length, callback).newCall(request);
		if (callback != null) {
			execute(new Runnable() {
				@Override
				public void run() {
					callback.onPrepared(call);
				}
			});
		}
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				logger.log(url, Method.DOWNLOAD, null, headers);
				e.printStackTrace();
				if (callback != null) {
					execute(new Runnable() {
						@Override
						public void run() {
							callback.onFailure(config.getErrorMsg(), e.toString());
						}
					});
				}
				if (e.toString().contains("ENETUNREACH") || e.toString().contains("ECONNREFUSED")) {
					Logger.out("okhttp client reset!");
					client = null;
				}
			}

			@Override
			public void onResponse(final Call call, Response response) throws IOException {
				logger.log(url, Method.DOWNLOAD, null, headers);
				BufferedSink sink;
				String res_etag = response.header("etag");
				String cl = response.header("Content-Length");
				Logger.out("onResponse " + cl);
//				if (res_etag != null) {
//					res_etag = res_etag.replace("\"", "");
//				}
				if (!Objects.equal(etag, res_etag)) {
					if (callback != null) {
						execute(new Runnable() {
							@Override
							public void run() {
								callback.onEtagChange(res_etag);
							}
						});
					}
				}
				if (response.code() == 206 || response.code() == 200) {
					sink = Okio.buffer(Okio.appendingSink(file));
				} else if (response.code() == 412) {
					file.delete();
					sink = Okio.buffer(Okio.sink(file));
				} else {
					sink = Okio.buffer(Okio.sink(file));
				}
				try {
					Buffer buffer = sink.buffer();
					int bufferSize = 1024 * 1024; //1M
					BufferedSource source = response.body().source();
					while (source.read(buffer, bufferSize) != -1) {
						sink.emit();
					}
				} catch (final IOException e) {
					if (call.isCanceled()) {
//						sink.emit();
						sink.flush();
						if (callback != null) {
							execute(new Runnable() {
								@Override
								public void run() {
									callback.onCancel();
									Logger.out("onCancel " + file.length());
								}
							});
						}
					} else {
						e.printStackTrace();
						if (callback != null) {
							execute(new Runnable() {
								@Override
								public void run() {
									callback.onFailure(config.getErrorMsg(), e.toString());
								}
							});
						}
					}
					return;
				} finally {
					sink.close();
				}
				if (callback != null) {
					execute(new Runnable() {
						@Override
						public void run() {
							callback.onSuccess(file);
						}
					});
				}
			}
		});
		return call;
	}

	private String callSync(final Method method, final String url, final HashMap<String, Object> parameters, HashMap<String, String> headers) throws IOException {
		Request request = buildRequest(method, url, parameters, null, null);
		Response response = getClient().newCall(request).execute();
		return response.body().string();
	}

	private Call call(final Method method, final String url, final HashMap<String, Object> parameters, final HashMap<String, String> headers, final AngelNetCallBack callback) {

		if (callback != null) {
			execute(new Runnable() {
				@Override
				public void run() {
					callback.onStart();
				}
			});
		}
		if (!CommonUtil.isNetWorkAvailable()) {
			if (callback != null) {
				execute(new Runnable() {
					@Override
					public void run() {
						if (!callback.onNoNetwork()) {
							callback.onFailure(AngelApplication.getInstance().getResources().getString(R.string.error_no_network));
						}
					}
				});
			}
			return null;
		}
		Request request = buildRequest(method, url, parameters, headers, callback);
		OkHttpClient client = getClient();
		if (parameters != null) {
			for (Map.Entry<String, Object> en : parameters.entrySet()) {
				Object value = en.getValue();
				if (value instanceof File) {
					client = getUploader();
					break;
				}
			}
		}
		Call call = client.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				logger.log(url, method, parameters, headers);
				e.printStackTrace();
				if (callback != null) {
					execute(new Runnable() {
						@Override
						public void run() {
							callback.onFailure(config.getErrorMsg(), e.toString());
						}
					});
				}
				if (e.toString().contains("ENETUNREACH") || e.toString().contains("ECONNREFUSED")) {
					Logger.out("okhttp client reset!");
					AngelNet.this.client = null;
				}
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				logger.log(url, method, parameters, headers);
				final String s;
				try {
					s = response.body().string();
				} catch (IOException e) {
					e.printStackTrace();
					if (callback != null) {
						execute(new Runnable() {
							@Override
							public void run() {
								callback.onFailure(config.getErrorMsg(), e.toString());
							}
						});
					}
					return;
				}
				if (callback != null) {
					callback.onSuccess(AngelNet.this, s, config);
				}
			}
		});
		return call;
	}

	public interface AngelNetConfig {
		String getServerRoot();

		String getErrorMsg();

		String getErrorCode();

		String getReturnCodeName();

		String getReturnDataName();

		String getReturnMsgName();
	}

}
