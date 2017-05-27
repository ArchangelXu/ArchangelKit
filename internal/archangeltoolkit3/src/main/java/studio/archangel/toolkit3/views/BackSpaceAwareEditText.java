package studio.archangel.toolkit3.views;

/**
 * Created by xmk on 2017/3/16.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

public class BackSpaceAwareEditText extends EditText {
	OnBackSpacePressedListener listener;

	public BackSpaceAwareEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BackSpaceAwareEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BackSpaceAwareEditText(Context context) {
		super(context);
	}

	public void setOnBackSpacePressedListener(OnBackSpacePressedListener listener) {
		this.listener = listener;
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		return new BackSpaceAwareInputConnection(super.onCreateInputConnection(outAttrs),
				true);
	}

	private class BackSpaceAwareInputConnection extends InputConnectionWrapper {

		public BackSpaceAwareInputConnection(InputConnection target, boolean mutable) {
			super(target, mutable);
		}

		@Override
		public boolean sendKeyEvent(KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
				if (listener != null && !listener.onBackSpacePressed(getText().length())) {
					return false;
				}
			}
			return super.sendKeyEvent(event);
		}


		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) {
			// magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
			if (beforeLength == 1 && afterLength == 0) {
				// backspace
				return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
						&& sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
			}

			return super.deleteSurroundingText(beforeLength, afterLength);
		}
	}

	public interface OnBackSpacePressedListener {
		/**
		 * @return false if you wish to cancel the backspace
		 */
		boolean onBackSpacePressed(int char_count_before_delete);
	}
}