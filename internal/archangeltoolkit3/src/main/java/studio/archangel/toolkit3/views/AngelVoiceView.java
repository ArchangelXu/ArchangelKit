package studio.archangel.toolkit3.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import studio.archangel.toolkit3.R;


/**
 * Created by Michael on 2014/12/10.
 */
public class AngelVoiceView extends RelativeLayout {
	//    onVolumeChangeListener l;
	static int post_frames = 60;
	int remain_frame = post_frames;
	Paint paint = new Paint();
	int volume = 0;
	boolean fade_start = false;
	boolean draw_effect = false;
	int color1, color2;

	public AngelVoiceView(Context context) {
		super(context);
	}

	public AngelVoiceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		color1 = getResources().getColor(R.color.red);
		color2 = getResources().getColor(R.color.blue);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (draw_effect) {
			Rect src = new Rect(0, 0, getWidth(), getHeight());
			Rect dst = new Rect(0, 0, getWidth(), getHeight());

			if (fade_start) {
				if (remain_frame > 0) {
					remain_frame--;
					paint.setColor(color1);
					canvas.drawBitmap(getWaveBitmap(), src, dst, null);

				} else {
					remain_frame = post_frames;
					fade_start = false;
					draw_effect = false;
				}
			} else {
				paint.setColor(color2);

				canvas.drawBitmap(getWaveBitmap(), src, dst, null);

			}
			invalidate();
		}


	}

	public void setVolume(int v) {
		volume = v;
		draw_effect = true;
		fade_start = false;
	}
//
//    public void setonVolumeChangeListener(onVolumeChangeListener listener) {
//        l = listener;
//    }

	Bitmap getWaveBitmap() {
		Bitmap output = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawCircle(0, 0, volume, paint);
		return output;
	}
//
//    public interface onVolumeChangeListener {
//        public void onVolumeChanged(int volume);
//    }
}
