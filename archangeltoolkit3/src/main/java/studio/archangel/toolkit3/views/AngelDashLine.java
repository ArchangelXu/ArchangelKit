package studio.archangel.toolkit3.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.utils.UIUtil;


/**
 * Created by Michael on 2015/6/9.
 */
public class AngelDashLine extends View {
	private Paint paint;
	private Path path;
	private PathEffect effects;
	private int color;
	private int width;
	private int length;
	private int gap;

	public AngelDashLine(Context context) {
		this(context, null);
	}

	public AngelDashLine(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AngelDashLine(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		path = new Path();
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AngelDashLine);
		color = a.getColor(R.styleable.AngelDashLine_adl_dash_color, getResources().getColor(R.color.grey_light));
		paint.setColor(color);
		width = a.getDimensionPixelOffset(R.styleable.AngelDashLine_adl_line_width, 1);
		paint.setStrokeWidth(width);
		length = a.getDimensionPixelOffset(R.styleable.AngelDashLine_adl_dash_length, UIUtil.getPX(context, 4));
		gap = a.getDimensionPixelOffset(R.styleable.AngelDashLine_adl_dash_gap, UIUtil.getPX(context, 2));
		effects = new DashPathEffect(new float[]{length, gap, length, gap}, 0);
		int res = a.getResourceId(R.styleable.AngelDashLine_adl_background, R.color.trans);
		setBackgroundResource(res);
		a.recycle();
	}

	public void setLineColor(int color) {
		this.color = color;
		paint.setColor(color);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paint.setPathEffect(effects);
		int height = getMeasuredHeight();
		int width = getMeasuredWidth();
		if (height <= width) {
			// horizontal
			path.moveTo(0, (float) (height / 2.0));
			path.lineTo(width, (float) (height / 2.0));
			canvas.drawPath(path, paint);
		} else {
			// vertical
			path.moveTo((float) (width / 2.0), 0);
			path.lineTo((float) (width / 2.0), height);
			canvas.drawPath(path, paint);
		}

	}
}