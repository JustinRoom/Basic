package jsc.org.lib.basic.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.StringDef;
import androidx.appcompat.widget.ActionMenuView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import jsc.org.lib.basic.R;

public class CustomTitleBar extends RelativeLayout {

    /**
     * type-{@link LinearLayout}
     */
    public final static String BACK_CONTAINER = "backContainer";

    /**
     * type-{@link ImageView}
     */
    public final static String BACK_ICON = "backIcon";

    /**
     * type-{@link TextView}
     */
    public final static String BACK_TEXT = "backText";

    /**
     * type-{@link TextView}
     */
    public final static String TITLE = "title";

    /**
     * type-{@link LinearLayout}
     */
    public final static String MENU_CONTAINER = "menuContainer";

    /**
     * type-{@link ActionMenuView}
     */
    public final static String MENU = "menu";

    @StringDef({BACK_CONTAINER, BACK_ICON, BACK_TEXT, TITLE, MENU_CONTAINER, MENU})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SubViewKey {
    }

    private final Map<String, View> viewCache = new HashMap<>();

    public CustomTitleBar(Context context) {
        this(context, null);
    }

    public CustomTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.drawable.base_title_bar_background);
        initViews(context);
    }

    private void initViews(Context context) {
        //back container
        LayoutParams bParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        bParams.addRule(ALIGN_PARENT_LEFT);
        bParams.addRule(CENTER_VERTICAL);
        LinearLayout backContainer = new LinearLayout(context);
        backContainer.setOrientation(LinearLayout.HORIZONTAL);
        addView(backContainer, bParams);
        viewCache.put(BACK_CONTAINER, backContainer);
        //back icon
        int hPadding = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics()) + .5f);
        ImageView backIcon = new ImageView(context);
        backIcon.setPadding(hPadding, 0, hPadding, 0);
        backIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        backIcon.setImageResource(R.mipmap.base_ico_back);
        backContainer.addView(backIcon, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        viewCache.put(BACK_ICON, backIcon);
        //back text
        TextView backText = new TextView(context);
        backText.setGravity(Gravity.CENTER_VERTICAL);
        backText.setTextColor(Color.WHITE);
        backText.setText("返回");
        backContainer.addView(backText, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        viewCache.put(BACK_TEXT, backText);

        //title
        LayoutParams tParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tParams.addRule(CENTER_IN_PARENT);
        TextView title = new TextView(context);
        title.setGravity(Gravity.CENTER_VERTICAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            title.setTextAppearance(androidx.appcompat.R.style.TextAppearance_AppCompat_Subhead);
        }
        title.setTextColor(Color.WHITE);
        title.setText("Custom Title");
        addView(title, tParams);
        viewCache.put(TITLE, title);

        //menu container
        LayoutParams mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mParams.addRule(ALIGN_PARENT_RIGHT);
        mParams.addRule(CENTER_VERTICAL);
        LinearLayout menuContainer = new LinearLayout(context);
        menuContainer.setOrientation(LinearLayout.HORIZONTAL);
        menuContainer.setGravity(Gravity.CENTER_VERTICAL);
        addView(menuContainer, mParams);
        viewCache.put(MENU_CONTAINER, menuContainer);
        //action menu
        ActionMenuView actionMenu = new ActionMenuView(context);
        menuContainer.addView(actionMenu, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        viewCache.put(MENU, actionMenu);
    }

    private int navigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        //Xiaomi Redmi K30 navigation_bar_height = 44
        return Math.max(resources.getDimensionPixelSize(resourceId), 144);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(navigationBarHeight(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public View findChildByKey(@SubViewKey String viewKey) {
        return viewCache.get(viewKey);
    }

    public void visible(@SubViewKey String viewKey, int visibility) {
        View view = findChildByKey(viewKey);
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    public void setTitle(CharSequence title) {
        View view = findChildByKey(TITLE);
        if (view instanceof TextView) {
            ((TextView) view).setText(title);
        }
    }

    public Menu menu() {
        return ((ActionMenuView) findChildByKey(MENU)).getMenu();
    }
}
