package com.triposo.barone;

// http://stackoverflow.com/a/4937713/804479

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Wraps its children.
 */
public class FlowLayout extends ViewGroup {
	public static final int DEFAULT_HORIZONTAL_SPACING = 5;
	public static final int DEFAULT_VERTICAL_SPACING = 5;
	private final int horizontalSpacing;
	private final int verticalSpacing;
	private int max_lines = 0;
	private List<RowMeasurement> currentRows = Collections.emptyList();
	boolean ellipsized = false;
	OnEllipsizeListener listener;

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
		horizontalSpacing = styledAttributes.getDimensionPixelSize(R.styleable.FlowLayout_fl_horizontalSpacing,
				DEFAULT_HORIZONTAL_SPACING);
		verticalSpacing = styledAttributes.getDimensionPixelSize(R.styleable.FlowLayout_fl_verticalSpacing,
				DEFAULT_VERTICAL_SPACING);
		max_lines = styledAttributes.getInteger(R.styleable.FlowLayout_fl_maxLines, 0);
		styledAttributes.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		final int maxInternalWidth = MeasureSpec.getSize(widthMeasureSpec) - getHorizontalPadding();
		final int maxInternalHeight = MeasureSpec.getSize(heightMeasureSpec) - getVerticalPadding();
		List<RowMeasurement> rows = new ArrayList<RowMeasurement>();
		RowMeasurement currentRow = new RowMeasurement(maxInternalWidth, widthMode);
		rows.add(currentRow);
		int lines = 1;
		for (View child : getLayoutChildren()) {
			LayoutParams childLayoutParams = child.getLayoutParams();
			int childWidthSpec = createChildMeasureSpec(childLayoutParams.width, maxInternalWidth, widthMode);
			int childHeightSpec = createChildMeasureSpec(childLayoutParams.height, maxInternalHeight, heightMode);
			child.measure(childWidthSpec, childHeightSpec);
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();
			if (currentRow.wouldExceedMax(childWidth)) {
				lines++;
				if (!(max_lines > 0 && lines > max_lines)) {
					currentRow = new RowMeasurement(maxInternalWidth, widthMode);
					rows.add(currentRow);
				}
			}
			currentRow.addChildDimensions(childWidth, childHeight);
		}

		int longestRowWidth = 0;
		int totalRowHeight = 0;
		for (int index = 0; index < rows.size(); index++) {
			RowMeasurement row = rows.get(index);
			totalRowHeight += row.getHeight();
			if (index < rows.size() - 1) {
				totalRowHeight += verticalSpacing;
			}
			longestRowWidth = Math.max(longestRowWidth, row.getWidth());
		}
		setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? MeasureSpec.getSize(widthMeasureSpec) : longestRowWidth
				+ getHorizontalPadding(), heightMode == MeasureSpec.EXACTLY ? MeasureSpec.getSize(heightMeasureSpec)
				: totalRowHeight + getVerticalPadding());
		currentRows = Collections.unmodifiableList(rows);
	}

	private int createChildMeasureSpec(int childLayoutParam, int max, int parentMode) {
		int spec;
		if (childLayoutParam == LayoutParams.FILL_PARENT) {
			spec = MeasureSpec.makeMeasureSpec(max, MeasureSpec.EXACTLY);
		} else if (childLayoutParam == LayoutParams.WRAP_CONTENT) {
			spec = MeasureSpec.makeMeasureSpec(max, parentMode == MeasureSpec.UNSPECIFIED ? MeasureSpec.UNSPECIFIED
					: MeasureSpec.AT_MOST);
		} else {
			spec = MeasureSpec.makeMeasureSpec(childLayoutParam, MeasureSpec.EXACTLY);
		}
		return spec;
	}

	public int getMaxLines() {
		return max_lines;
	}

	public void setMaxLines(int max_lines) {
		this.max_lines = max_lines;
	}

	public void setOnEllipsizeListener(OnEllipsizeListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onLayout(boolean changed, int leftPosition, int topPosition, int rightPosition, int bottomPosition) {
		final int widthOffset = getMeasuredWidth() - getPaddingRight();
		int x = getPaddingLeft();
		int y = getPaddingTop();
		int lines = 1;
		boolean intercepted = false;
//		if (((Integer) getTag()) == 2063) {
//		System.out.println("FlowLayout " + hashCode() + "widthOffset=" + widthOffset);
//		}
		Iterator<RowMeasurement> rowIterator = currentRows.iterator();
		RowMeasurement currentRow = rowIterator.next();
		List<View> children = getLayoutChildren();
		int i = 0;
		for (; i < children.size(); i++) {
			View child = children.get(i);
			final int childWidth = child.getMeasuredWidth();
			final int childHeight = child.getMeasuredHeight();
			if (x + childWidth > widthOffset) {
				if (childWidth > widthOffset) {//this single view's width greater than layout's width, skip
					children.get(i).layout(0, 0, 0, 0);
					continue;
				}
				if (max_lines > 0 && lines >= max_lines) {
					intercepted = true;
				} else {
					lines++;
					x = getPaddingLeft();
					y += currentRow.height + verticalSpacing;
					if (rowIterator.hasNext()) {
						currentRow = rowIterator.next();
					}
				}
			}
			if (intercepted) {
				break;
			}
			// Align the child vertically.
			int childY = y + (currentRow.height - childHeight) / 2;
			child.layout(x, childY, x + childWidth, childY + childHeight);
//			CharSequence text = ((TextView) child).getText();
//			if (((Integer) getTag()) == 2063) {
//			System.out.println("FlowLayout " + hashCode() + " " + text + " x=" + x + " width=" + childWidth);
//			}
			x += childWidth + horizontalSpacing;
		}
		boolean last_ellipsized = this.ellipsized;
		this.ellipsized = intercepted;
		if (last_ellipsized != ellipsized) {
			if (listener != null) {
				listener.onEllipsizeStateChanged(ellipsized);
			}
		}
		for (; i < children.size(); i++) {
//			children.get(i).setVisibility(View.GONE);
			children.get(i).layout(0, 0, 0, 0);
		}
	}

	public boolean isEllipsized() {
		return ellipsized;
	}

	private List<View> getLayoutChildren() {
		List<View> children = new ArrayList<View>();
		for (int index = 0; index < getChildCount(); index++) {
			View child = getChildAt(index);
			if (child.getVisibility() != View.GONE) {
				children.add(child);
			}
		}
		return children;
	}

	protected int getVerticalPadding() {
		return getPaddingTop() + getPaddingBottom();
	}

	protected int getHorizontalPadding() {
		return getPaddingLeft() + getPaddingRight();
	}

	private final class RowMeasurement {
		private final int maxWidth;
		private final int widthMode;
		private int width;
		private int height;

		public RowMeasurement(int maxWidth, int widthMode) {
			this.maxWidth = maxWidth;
			this.widthMode = widthMode;
		}

		public int getHeight() {
			return height;
		}

		public int getWidth() {
			return width;
		}

		public boolean wouldExceedMax(int childWidth) {
			return widthMode == MeasureSpec.UNSPECIFIED ? false : getNewWidth(childWidth) > maxWidth;
		}

		public void addChildDimensions(int childWidth, int childHeight) {
			width = getNewWidth(childWidth);
			height = Math.max(height, childHeight);
		}

		private int getNewWidth(int childWidth) {
			return width == 0 ? childWidth : width + horizontalSpacing + childWidth;
		}
	}

	public interface OnEllipsizeListener {
		void onEllipsizeStateChanged(boolean ellipsized);
	}
}