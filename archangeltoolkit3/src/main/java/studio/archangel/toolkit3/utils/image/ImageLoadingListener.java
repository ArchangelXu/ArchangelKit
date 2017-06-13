package studio.archangel.toolkit3.utils.image;

import com.facebook.imagepipeline.image.ImageInfo;

/**
 * Created by Michael on 2015/5/18.
 */
public interface ImageLoadingListener {
	void onImageLoaded(ImageInfo imageInfo);

	void onFailure();
}
