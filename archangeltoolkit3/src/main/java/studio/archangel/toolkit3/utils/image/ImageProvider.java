package studio.archangel.toolkit3.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import studio.archangel.toolkit3.AngelApplication;
import studio.archangel.toolkit3.models.FileInfo;
import studio.archangel.toolkit3.utils.CommonUtil;
import studio.archangel.toolkit3.utils.Logger;
import studio.archangel.toolkit3.utils.text.AmountProvider;

/**
 * Created by Michael on 2015/5/15.
 */
public class ImageProvider {
	public static void load(View v, Object res) {
		int width = v.getWidth();
		int height = v.getHeight();
		if (width <= 0 || height <= 0) {
//			v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//				@Override
//				public void onGlobalLayout() {
//					int width = v.getWidth();
//					int height = v.getHeight();
//					if (width > 0 && height > 0) {
//						v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//						load(v, res, width, height);
//					}
//				}
//			});
			load(v, res, 0, 0);
		} else {
			load(v, res, width, height);
		}
//			load(v, res, 0, 0);
	}

	public static void load(View v, Object res, int width, int height) {
		ImageRequestBuilder request_builder = null;
		if (res instanceof Integer) {
			request_builder = ImageRequestBuilder.newBuilderWithResourceId((Integer) res);
		} else if (res instanceof String) {
			request_builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse((String) res));
		} else if (res instanceof Uri) {
			request_builder = ImageRequestBuilder.newBuilderWithSource((Uri) res);
		}
		if (request_builder == null) {
			Logger.errSimple("Failed to create image request builder for res:" + (res == null ? null : res.toString()));
			return;
		}
		request_builder.setAutoRotateEnabled(true)
				.setRotationOptions(RotationOptions.autoRotate())
				.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
				.setProgressiveRenderingEnabled(false);
		if (width > 0 && height > 0) {
			request_builder.setResizeOptions(new ResizeOptions(width, height));
		}
		if (!(v instanceof ImageView)) {
			DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline().
					fetchDecodedImage(request_builder.build(), AngelApplication.getInstance());

			dataSource.subscribe(new BaseBitmapDataSubscriber() {
				@Override
				public void onNewResultImpl(@Nullable final Bitmap bitmap) {
					v.post(new Runnable() {
						public void run() {

							v.setBackground(new BitmapDrawable(v.getResources(), bitmap));
						}
					});
				}

				@Override
				public void onFailureImpl(DataSource dataSource) {
					// No cleanup required here.
				}
			}, CallerThreadExecutor.getInstance());
		} else {
			if (v instanceof SimpleDraweeView) {
				final SimpleDraweeView iv = (SimpleDraweeView) v;
				PipelineDraweeControllerBuilder controller_builder = Fresco.newDraweeControllerBuilder();
				controller_builder.setOldController(iv.getController())
						.setAutoPlayAnimations(true)
						.setImageRequest(request_builder.build());

				iv.setController(controller_builder.build());
			} else {
				final ImageView iv = (ImageView) v;
				DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline().
						fetchDecodedImage(request_builder.build(), AngelApplication.getInstance());

				dataSource.subscribe(new BaseBitmapDataSubscriber() {
					@Override
					public void onNewResultImpl(@Nullable final Bitmap bitmap) {
						iv.post(new Runnable() {
							public void run() {

								iv.setImageBitmap(bitmap);
							}
						});
					}

					@Override
					public void onFailureImpl(DataSource dataSource) {
						// No cleanup required here.
					}
				}, CallerThreadExecutor.getInstance());
			}
		}
	}

	public static void load(View v, Object res, final ImageLoadingListener l) {
		ImageRequestBuilder request_builder = null;
		if (res instanceof Integer) {
			request_builder = ImageRequestBuilder.newBuilderWithResourceId((Integer) res);

		} else if (res instanceof String) {
			request_builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse((String) res));
		}
		if (request_builder == null) {
			Logger.out("Failed to create image request builder.");
			return;
		}
		request_builder
				.setRotationOptions(RotationOptions.autoRotate())
				.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
				.setProgressiveRenderingEnabled(false);
		if (!(v instanceof ImageView)) {
			Logger.out("Target view is not a ImageView.");
			return;
		}
		if (v instanceof SimpleDraweeView) {
			final SimpleDraweeView iv = (SimpleDraweeView) v;
			PipelineDraweeControllerBuilder controller_builder = Fresco.newDraweeControllerBuilder();
			controller_builder.setOldController(iv.getController())
					.setAutoPlayAnimations(true)
					.setImageRequest(request_builder.build());
			if (l != null) {
				ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
					@Override
					public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
						l.onImageLoaded(imageInfo);
						if (imageInfo == null) {
							return;
						}
						QualityInfo qualityInfo = imageInfo.getQualityInfo();
						Logger.out(String.format("Final image received! Size %d x %d Quality level %d, good enough: %s, full quality: %s",
								imageInfo.getWidth(),
								imageInfo.getHeight(),
								qualityInfo.getQuality(),
								qualityInfo.isOfGoodEnoughQuality(),
								qualityInfo.isOfFullQuality()));
					}

					@Override
					public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
						l.onImageLoaded(imageInfo);
					}

					@Override
					public void onFailure(String id, Throwable throwable) {
						l.onFailure(throwable);
					}
				};
				controller_builder.setControllerListener(controllerListener);
			}
			iv.setController(controller_builder.build());
		} else {
			final ImageView iv = (ImageView) v;
			DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline().
					fetchDecodedImage(request_builder.build(), AngelApplication.getInstance());

			dataSource.subscribe(new BaseBitmapDataSubscriber() {
				@Override
				public void onNewResultImpl(@Nullable final Bitmap bitmap) {
					iv.post(new Runnable() {
						public void run() {
							if (l != null) {
								l.onImageLoaded(null);
							}
							iv.setImageBitmap(bitmap);
						}
					});
				}

				@Override
				public void onFailureImpl(DataSource dataSource) {
					// No cleanup required here.
				}
			}, CallerThreadExecutor.getInstance());
		}
	}

	/**
	 * 将图片预加载到缓存中
	 */
	public static void preload(Object res) {
		ImageRequestBuilder builder = null;
		if (res instanceof Integer) {
			builder = ImageRequestBuilder.newBuilderWithResourceId((Integer) res);

		} else if (res instanceof String) {
			builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse((String) res));
		}
		if (builder == null) {
			Logger.out("Failed to create image request builder.");
			return;
		}
		builder.setAutoRotateEnabled(true)
				.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)

				.setProgressiveRenderingEnabled(false);

		Fresco.getImagePipeline().prefetchToBitmapCache(builder.build(), AngelApplication.getInstance());
	}

	public static boolean isInCache(Object res) {
		ImageRequestBuilder builder = null;
		if (res instanceof Integer) {
			builder = ImageRequestBuilder.newBuilderWithResourceId((Integer) res);

		} else if (res instanceof String) {
			builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse((String) res));
		}
		if (builder == null) {
			Logger.out("Failed to create image request builder.");
			return false;
		}
		builder.setAutoRotateEnabled(true)
				.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)

				.setProgressiveRenderingEnabled(false);
		return Fresco.getImagePipeline().isInBitmapMemoryCache(builder.build());
	}

	/**
	 * 生成缩略图
	 *
	 * @param uri 目标图片的URI
	 * @return 生成的缩略图的文件信息
	 */
	public static FileInfo getSmallPic(Uri uri) {
		FileInfo info = new FileInfo();
		Bitmap bitmap = null;
		String path = CommonUtil.getPath(AngelApplication.getInstance(), uri);

		bitmap = getBitmap(path);
		String thumb_path = null;
		Bitmap thumb = bitmap;
		if (thumb == null) {
			return null;
		}
		FileOutputStream out = null;
		File file = new File(AngelApplication.getInstance().getDir("temp", Context.MODE_PRIVATE).getAbsolutePath() + "/temp_" + System.currentTimeMillis() + ".jpg");
		thumb_path = file.getAbsolutePath();
		try {
			out = new FileOutputStream(file);
			thumb.compress(Bitmap.CompressFormat.JPEG, 80, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			thumb.recycle();
		}
		info.width = thumb.getWidth();
		info.height = thumb.getHeight();
		info.path = thumb_path;
		Logger.out(thumb_path + " file size:" + AmountProvider.getReadableFileSize(file));
		return info;
	}

	static Bitmap getBitmap(String path) {
		Logger.out("getBitmap from path=" + path);
		if (path == null) {
			return null;
		}
		InputStream in = null;
		try {
			final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
			in = new FileInputStream(path);
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();


			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {

				scale++;
			}
			Logger.out("scale = " + scale + ", orig-width: " + o.outWidth + ",orig - height: " + o.outHeight);

			Bitmap b = null;
			in = new FileInputStream(path);
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);

				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();
				Logger.out("1th scale operation dimenions - width: " + width + ",height: " + height);

				double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
				double x = (y / height) * width;

				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
				b.recycle();
				b = scaledBitmap;

				System.gc();
			} else {
				b = BitmapFactory.decodeStream(in);
			}
			in.close();
			ExifInterface exif = null;
			if (b != null) {
				try {
					exif = new ExifInterface(path);
					int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
					if (orientation != ExifInterface.ORIENTATION_UNDEFINED) {
						b = rotateBitmap(b, orientation);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				Logger.out("bitmap size - width: " + b.getWidth() + ", height: " + b.getHeight());
			} else {
				Logger.out("bitmap is null!");
			}
			return b;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

		Matrix matrix = new Matrix();
		switch (orientation) {
			case ExifInterface.ORIENTATION_NORMAL:
				return bitmap;
			case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
				matrix.setScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				matrix.setRotate(180);
				break;
			case ExifInterface.ORIENTATION_FLIP_VERTICAL:
				matrix.setRotate(180);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_TRANSPOSE:
				matrix.setRotate(90);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				matrix.setRotate(90);
				break;
			case ExifInterface.ORIENTATION_TRANSVERSE:
				matrix.setRotate(-90);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				matrix.setRotate(-90);
				break;
			default:
				return bitmap;
		}
		try {
			Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			bitmap.recycle();
			return bmRotated;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void loadBitmapFromNetwork(@NonNull Context context, @NonNull String url, @NonNull onBitmapLoadedCallback callback) {
		ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
		requestBuilder.setAutoRotateEnabled(true)
				.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
				.setProgressiveRenderingEnabled(false);

		DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline().fetchDecodedImage(requestBuilder.build(), context.getApplicationContext());
		dataSource.subscribe(new BaseDataSubscriber<CloseableReference<CloseableImage>>() {

			@Override
			public void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
				if (!dataSource.isFinished()) {
					return;
				}
				CloseableReference<CloseableImage> ref = dataSource.getResult();
				if (ref != null) {
					final CloseableImage result = ref.get();
					Bitmap bitmap = ((CloseableBitmap) result).getUnderlyingBitmap();
					callback.onBitmapLoaded(bitmap);
				}
			}

			@Override
			public void onFailureImpl(DataSource dataSource) {
				Throwable t = dataSource.getFailureCause();
				if (t != null) {
					t.printStackTrace();
					callback.onBitmapLoaded(null);
				}
			}
		}, CallerThreadExecutor.getInstance());
	}

	public static Bitmap loadBitmapFromCache(Context context, String url) {
		ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
		ImageRequest request = requestBuilder.build();
		Bitmap bitmap = null;
		DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline().fetchImageFromBitmapCache(request, context.getApplicationContext());
		CloseableReference<CloseableImage> ref = dataSource.getResult();
		try {
			if (ref != null) {
				final CloseableImage result = ref.get();
				if (result instanceof CloseableBitmap) {
					bitmap = ((CloseableBitmap) result).getUnderlyingBitmap();
				} else {
					Logger.err("result is not a CloseableBitmap");
				}
			}
		} finally {
			CloseableReference.closeSafely(ref);
			dataSource.close();
		}
		return bitmap;
	}

	public interface onBitmapLoadedCallback {
		void onBitmapLoaded(@Nullable Bitmap bitmap);
	}

}
