package ru.devhead.gfzuchet;


        import android.content.Context;
        import android.graphics.Rect;
        import android.view.Gravity;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.WindowManager;
        import android.view.View.OnTouchListener;
        import android.widget.ImageView;
        import android.widget.PopupWindow;

/**
 * @author anshu
 *
 */
public class Settings implements OnTouchListener {

    private View triggerView;
    private PopupWindow window;
    protected final WindowManager windowManager;

    public Settings(View triggerView) {
        this.triggerView = triggerView;
        window = new PopupWindow(triggerView.getContext());
        window.setTouchable(true);
        window.setTouchInterceptor(this);
        windowManager = (WindowManager) triggerView.getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        ImageView imageView = new ImageView(triggerView.getContext());
        //imageView.setImageResource(R.drawable.);
        window.setContentView(imageView);
        window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        window.setTouchable(true);
        window.setFocusable(true);
        window.setOutsideTouchable(true);
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            this.window.dismiss();
            return true;
        }
        return false;
    }

    public void show() {
        int[] location = new int[2];
        triggerView.getLocationOnScreen(location);
        window.showAtLocation(triggerView, Gravity.NO_GRAVITY,
                location[0] + 50, location[1] + (triggerView.getHeight() / 2));
    }

}