package com.lsh2017.dontgetsick;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;

/**
 * Created by 이소희 on 2017-08-18.
 */

public class NMapCalloutBasicOverlay extends NMapCalloutOverlay {


    public NMapCalloutBasicOverlay(NMapOverlay nMapOverlay, NMapOverlayItem nMapOverlayItem, Rect rect) {
        super(nMapOverlay, nMapOverlayItem, rect);
    }

    @Override
    protected boolean isTitleTruncated() {
        return false;
    }

    @Override
    protected int getMarginX() {
        return 0;
    }

    @Override
    protected Rect getBounds(NMapView nMapView) {
        return null;
    }

    @Override
    protected PointF getSclaingPivot() {
        return null;
    }

    @Override
    protected void drawCallout(Canvas canvas, NMapView nMapView, boolean b, long l) {

    }

    @Override
    protected Drawable getDrawable(int i, int i1) {
        return null;
    }
}
